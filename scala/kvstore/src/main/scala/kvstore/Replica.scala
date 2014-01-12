package kvstore

import akka.actor.{ OneForOneStrategy, Props, ActorRef, Actor, AllForOneStrategy }
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
import akka.event.Logging
import akka.actor.ActorKilledException
import akka.actor.SupervisorStrategy.{Restart, Stop}

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
  
  case object RetryPersist 
  case object RetryReplica 
  
  case class RetryTimeout( req:ActorRef, id:Long)

  def props(arbiter: ActorRef, persistenceProps: Props): Props = Props(new Replica(arbiter, persistenceProps))
}


class Replica(val arbiter: ActorRef, persistenceProps: Props) extends Actor with Stash {
  import Replica._
  import Replicator._
  import Persistence._
  import context.dispatcher
  val log = Logging(context.system, this)
  /*
   * The contents of this actor is just a suggestion, you can implement it in any way you like.
   */
  //val persistor = context.actorOf( persistenceProps )
  arbiter ! Join
  val persistance = context.actorOf(persistenceProps)
  //val persistance = context.actorFor(persistenceProps)
  
  override val supervisorStrategy = AllForOneStrategy(){
    case _:PersistenceException => Restart
    case _:ActorKilledException => Stop 
  }

  
  var acks = Map.empty[Long,(ActorRef,Map[ActorRef,Operation])]
  
  ////////////////////////////////////////////////////////////////////////////////////////////////
  
  var kv = Map.empty[String, String]
  var secondaries = Map.empty[ActorRef, ActorRef]
  var replicators = Set.empty[ActorRef]
  
  context.system.scheduler.schedule(Duration.Zero, 100 milliseconds, self, "Retry")


  def receive = {
    case JoinedPrimary   => {
      context.become(leader)
    }
    case JoinedSecondary => {
      context.become(replica)
    }
  }
  
  def nextId = {
    System.nanoTime
  }

  
  def begin_replication_process( sender:ActorRef, k:String, v:Option[String], id:Long)={
      val pmsg = Persist(k,v,id)
      persistance ! pmsg
      var ackm = Map.empty[ActorRef,Operation] 
      ackm += ( persistance -> pmsg )
      log.info(s"Primary sending persist message: $persistance <- $pmsg");
      val rmsg = Replicate(k,v,id)
      replicators.foreach( replicator =>{
	    replicator ! rmsg
	    ackm += ( replicator -> rmsg )
	    log.info(s"sending replicate message to replicator $replicator <- $rmsg");
      })
      
      acks += ( id -> ( sender,ackm ))
      log.info(s"ACKs content $acks")
      context.system.scheduler.scheduleOnce( 1 second, self, RetryTimeout(sender,id))
  }
  
  def handle_replica_message( replicas:Set[ActorRef])={
      log.info(s"replica request received  for $replicas");
      
      val nreplicas = replicas
      val oreplicas = secondaries.keySet
      
      val nadded = nreplicas -- oreplicas - self
      val oremov = oreplicas -- nreplicas
     
      
      log.info(s"add replica : $nadded");
      log.info(s"rmv replica : $oremov");
      
      //secondaries = Map.empty[ActorRef, ActorRef]
      var nreps = Set.empty[ActorRef]
      nadded.foreach( { // these are replicas
        case r:ActorRef =>{
          if( r != self  ){
              log.info(s"add replica for $r");
              // replica -> replicator 
	          //val replicator = secondaries.getOrElse(r, context.actorOf( Replicator.props(r)) )
              val nreplicator = context.actorOf( Replicator.props(r))
	          secondaries += ( r -> nreplicator )
	          replicators += nreplicator
	          nreps += nreplicator
          }
        }
      })
      
      val id=nextId
      var ackm = Map.empty[ActorRef,Operation]
      kv.foreach({
        case (k,v) => {
        	  val rmsg = Replicate(k,Some(v),id)
		      nreps.foreach( replicator =>{
			    replicator ! rmsg
			    ackm += ( replicator -> rmsg )
			    log.info(s"Primary Replica:  $replicator <- $rmsg");
		      })
        }
      })
      acks += ( id -> ( sender,ackm ))
      

      oremov.foreach( oreplica => {
          val replicator = secondaries( oreplica )
          log.info(s"remove pending ACK for $replicator");
          acks.foreach({
            case (k,(sender,ackm)) =>{
              acks += ( k -> (sender,ackm - replicator ))
            }
          })

          replicator ! PoisonPill
      })
      
      acks.keySet.foreach( id=>checkIfOk2ACK(id))
      
    }
  
  def checkIfOk2ACK( id:Long ){
    acks.get(id).map( entry=>{
      val ( requester, ackm ) = entry
      if( ackm.size == 0 ) {
        requester ! OperationAck(id)
        acks -= id
        log.info(s"Told requester $requester OP ACK $id")
      }
      else log.info(s"There are still $ackm so cannot ACK $id")
    })
  }
  
  def begin_replica_process( sender:ActorRef, id:Long)={
	  log.info("Replicate primary to all secondary");
      
      kv.foreach({
	        case (k,v)=>{
	          val rmsg = Replicate(k,Some(v),id)
	          var ackm = Map.empty[ActorRef,Operation]
	          replicators.foreach( rtor => {
	            rtor ! rmsg 
	            ackm += ( rtor -> rmsg)
	          })
	          acks += ( id -> ( sender, ackm ))
	          log.info(s"sending replicate message to replicator $replicators <- $rmsg");
	        }
      })

  }

  val leader: Receive = {
    case Insert(k,v,id) => {
      kv += ( k->v )
      begin_replication_process(sender,k,Some(v),id)
    }
    case Remove(k,id)   => {
      kv -= k
      begin_replication_process(sender,k,None,id)
    }
    case Get(k,id)      => {
      sender ! GetResult(k,kv.get(k),id)
    }
    
    case Replicas( replicas ) => handle_replica_message(replicas)
    
    
    case Persisted(k,id)  => {
      log.info(s"Persisted ack +1 $sender $id")
      acks.get(id).map( entry=>{
        val (requester, ackm ) = entry
        acks += ( id->(requester,ackm-persistance))
      })
      checkIfOk2ACK(id)
    }
    
	case Replicated(k,id) =>{
	  log.info(s"Replicated ack +1 $sender $id")
	  acks.get(id).map( entry=>{
        val (requester, ackm ) = entry
        acks += ( id->(requester,ackm-sender))
      })
      checkIfOk2ACK(id)
	}
	
	case "Retry" =>{
	  acks.values.foreach(entry=>{
	    val ( _, ackm ) = entry
	    ackm.foreach( {
	      case ( actor:ActorRef, msg:Operation)=> {
	        actor ! msg 
	        log.info(s"Primary Retry message $msg -> $actor")
	      }
	    })
	  })
	}
	
	case RetryTimeout(requester,id)     => {
	  //log.info(s"Primary timeout Reached for OP:$id")
	  
	  acks.get(id).map( entry=>{
	    val (requester,ackm) = entry
	    if( ackm.size != 0 ) {
	      requester ! OperationFailed(id)
	      acks -= id
	      log.info(s"Told requester $requester OP Fail $id")
	    }
	  })
	  
	}
	
	
    

    case _ =>
  }
  /////////////////////////////////////////////////////////////////////////////// 
  
  
  ///////////////////////////////////////////////////////////////////////////////
  
  def begin_persist_process( sender:ActorRef, k:String, v:Option[String], seq:Long)={
	  val id = seq
      val pmsg = Persist(k,v,id)
      persistance ! pmsg 
      var ackm = Map.empty[ActorRef,Operation]
	  ackm += ( persistance -> pmsg )
      acks += ( id -> (sender, ackm ))
      log.info(s"Secondary send $pmsg -> $persistance");
  }

  var cur_seq = 0L
  /* TODO Behavior for the replica role. */
  val replica: Receive = {
    case msg@Snapshot(k,v,seq) => {
      if( cur_seq == seq ){
         v match {
           case Some(vv) => kv += ( k->vv )
           case None => kv -= k
         }
         log.info(s"Secondary received $msg and waiting for persist");
         begin_persist_process(sender,k,v,seq)
         //cur_seq += 1
      }
      else if( seq < cur_seq ){
        log.info(s"Secondary reset sequence from $cur_seq to ${seq+1}");
        sender ! SnapshotAck( k, seq )
        //cur_seq = seq + 1
      }
    }
    
    case Persisted(k,id)=> {
      val msg = SnapshotAck(k,id)
      acks.get(id).map( entry=>{
        val (requester, ackm ) = entry
        acks -= id
        requester ! msg
        cur_seq += 1
        log.info(s"Secondary persisted change,  SnapshotACK $requester <- $msg");
        
      })
      
      
      
    }
    
    case "Retry" =>{
      acks.values.foreach(entry=>{
	    val ( _, ackm ) = entry
	    ackm.foreach( {
	      case ( actor:ActorRef, msg:Operation)=> {
	        actor ! msg 
	        log.info(s"Secondary Retry message $msg -> $actor")
	      }
	    })
	  })
    }
    
    
    case Get(k,id)      => {
      sender ! GetResult(k,kv.get(k),id)
    }
    case _ => 
  }

}
