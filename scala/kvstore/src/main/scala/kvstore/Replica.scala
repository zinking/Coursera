package kvstore

import akka.actor.{ OneForOneStrategy, Props, ActorRef, Actor }
import kvstore.Arbiter._
import scala.collection.immutable.Queue
import akka.actor.SupervisorStrategy.Restart
import scala.annotation.tailrec
import akka.pattern.{ ask, pipe }
import akka.actor.Terminated
import scala.concurrent.duration._
import akka.actor.PoisonPill
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy
import akka.util.Timeout
import akka.actor.Cancellable
import akka.actor.Stash

object Replica {
  trait Operation {
    def key: String
    def id: Long
  }
  case class Insert(key: String, value: String, id: Long) extends Operation
  case class Remove(key: String, id: Long) extends Operation
  case class Get(key: String, id: Long) extends Operation

  sealed trait OperationReply
  case class OperationAck(id: Long) extends OperationReply
  case class OperationFailed(id: Long) extends OperationReply
  case class GetResult(key: String, valueOption: Option[String], id: Long) extends OperationReply

  def props(arbiter: ActorRef, persistenceProps: Props): Props = Props(new Replica(arbiter, persistenceProps))
}

class Replica(val arbiter: ActorRef, persistenceProps: Props) extends Actor with Stash {
  import Replica._
  import Replicator._
  import Persistence._
  import context.dispatcher

  /*
   * The contents of this actor is just a suggestion, you can implement it in any way you like.
   */
  val persistor = context.actorOf( persistenceProps, "Persistence" )
  
  override val supervisorStrategy = OneForOneStrategy(){
    case _ : PersistenceException => Restart
  }
  
//  context.system.scheduler.schedule(0 milliseconds, 100 milliseconds )(reassure_persistence)
  
  var acks = Map.empty[(Long,ActorRef), Operation]
  
  /*
   * This section deal with the primary global broadcast
   */
  //var schedule_broadcast:Cancellable = Nothing ;
  
  //var acks4replicate = Map.empty[(Long,ActorRef), (Replicate, Long)]
  //def reassure_globalbroadcast = {
  //def decide_globalbroadcast =
  
  def broadcast_primary_op( k:String, v:Option[String], id:Long ) = {
    //val rps_only = replicators
    replicators.foreach( replicator =>{
	    val msg = Replicate(k,v,id)
	    replicator ! msg
	    acks += ( (id,replicator) -> msg )
    })
  }
  
  def broadcast_primary_all( ) = {
	  kv.foreach({
	    case (k,v)=> {
	      val id = System.currentTimeMillis
	      broadcast_primary_op(k,Some(v),id)
	    }
	  })
   }  
  ////////////////////////////////////////////////////////////////////////////////////////////////
  
  var kv = Map.empty[String, String]
  // a map from secondary replicas to replicators
  var secondaries = Map.empty[ActorRef, ActorRef]
  // the current set of replicators
  var replicators = Set.empty[ActorRef]
  
  arbiter ! Join

  def receive = {
    case JoinedPrimary   => {
      context.become(leader)

    }
    case JoinedSecondary => {
      context.become(replica)
    }
  }
  
  def ask_for_persist( ss:ActorRef, k:String,  v:Option[String],  id:Long ):Cancellable={
    val msg = Persist(k,v,id)
    persistor ! msg
    acks += ( (id,ss)-> msg )
    
    return context.system.scheduler.schedule(0 milliseconds, 100 milliseconds )({
      val persist_cks = acks.filter({
        case (k,v) =>{
          v.isInstanceOf[Persist]
        }
      })//filter persist messages
      
      persist_cks.foreach({ 
        case( k,v ) =>{
           persistor ! msg
        }
      })//
    })//end of check_persist
   
  }
  
  //def broad_cast_update( )

  /* TODO Behavior for  the leader role. */
  val leader_in_broadcast: Receive = {
    case Persisted(k,id)=> {
      //val senderA = acks(id)._1
      //senderA ! OperationAck(id)
      val k = ( id,sender )
      acks -= k
    }
    
    //case Persisted(k,id)  =>
	case Replicated(k,id) =>{
	  val k = ( id,sender )
	  acks -= k
	}
    
    case _=> stash();
  }

  val leader: Receive = {
    case Insert(k,v,id) => {
      kv += ( k->v )
      //p_actor ! Persist(k,Some(v),id)
      //sender ! OperationAck(id)
      context.become( leader_in_broadcast )
      val ck_persist = ask_for_persist( sender, k,Some(v), id)
      broadcast_primary_op(k,Some(v),id)
      context.system.scheduler.scheduleOnce(1 seconds )({
    	  ck_persist.cancel()
		  if ( acks.size == 0 ) sender ! OperationAck(id)
		  else {
		    sender ! OperationFailed(id)
		    acks = Map.empty[(Long,ActorRef), Operation]
		  }
		  context.become( leader )
		  unstashAll()
      })
      
    }
    case Remove(k,id)   => {
      kv -= k
      context.become( leader_in_broadcast )
      val ck_persist = ask_for_persist( sender, k,None, id)
      broadcast_primary_op(k,None,id)
      context.system.scheduler.scheduleOnce(1 seconds )({
    	  ck_persist.cancel()
		  if ( acks.size == 0 ) sender ! OperationAck(id)
		  else {
		    sender ! OperationFailed(id)
		    acks = Map.empty[(Long,ActorRef), Operation]
		  }
		  context.become( leader )
		  unstashAll()
      })
    }
    case Get(k,id)      => {
      sender ! GetResult(k,kv.get(k),id)
    }
    
    
    
    //case class Replicas(replicas: Set[ActorRef])
    case Replicas( replicas ) =>{
      var rps = Set.empty[ActorRef]
      replicas.foreach( {
        case r:Replica =>{
          if( r != this ){
	          val replicator = context.actorOf( Replicator.props(r)  )
	          r.secondaries += ( r->replicator )
	          rps += replicator
          }
        }
        
      })
      replicators = rps
      //schedule_broadcast = context.system.scheduler.schedule(0 milliseconds, 100 milliseconds )(reassure_globalbroadcast)
      //scheduleOnce
      
      broadcast_primary_all()
      context.system.scheduler.scheduleOnce(1 seconds )({
		    if ( acks.size == 0 ) arbiter ! OperationAck
		    else {
		      arbiter ! OperationFailed
		      acks = Map.empty[(Long,ActorRef), Operation]
		    }
		    context.become( leader )
		    unstashAll()
      })
      context.become( leader_in_broadcast )
    }
    

    case _ =>
  }
  /////////////////////////////////////////////////////////////////////////////// 
  
  
  ///////////////////////////////////////////////////////////////////////////////

  var cur_seq = 0L
  /* TODO Behavior for the replica role. */
  val replica: Receive = {
    case Snapshot(k,v,seq) => {
      if( cur_seq == seq ){
         v match {
           case Some(vv) => kv += ( k->vv )
           case None => kv -= k
         }
         //sender ! SnapshotAck( k, seq )
         ask_for_persist( sender, k,v, seq)//assumption seq and id won't mix
         cur_seq += 1
      }
      else if( seq < cur_seq ){
        sender ! SnapshotAck( k, seq )
        cur_seq = seq + 1
      }
    }
    
    case Persisted(k,seq)=> {
      val msg = SnapshotAck( k, seq )
      secondaries(self) ! msg
    }
    
    case Get(k,id)      => {
      sender ! GetResult(k,kv.get(k),id)
    }
    case _ => 
  }

}
