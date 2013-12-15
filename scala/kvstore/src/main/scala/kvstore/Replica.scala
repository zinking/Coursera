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
import akka.event.Logging

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
  
  case object RetryTimeout

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
  val persistor = context.actorOf( persistenceProps )
  
  override val supervisorStrategy = OneForOneStrategy(){
    case _  => Restart
  }
  

  var acks = Map.empty[ Long , (ActorRef,Operation)] // id , requestor of the operation, operation 
  
 
  ////////////////////////////////////////////////////////////////////////////////////////////////
  
  var kv = Map.empty[String, String]
  var secondaries = Map.empty[ActorRef, ActorRef]
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

  def leader_in_replication( requester:ActorRef, id:Long ): Receive = {
    case Persisted(k,id)  => {
      log.info("Persisted ack +1",id)
      acks -= id
    }
    
	case Replicated(k,id) =>{
	  log.info("Replicated ack +1",id)
	  acks -= id
	}
	
	case RetryPersist     => {
	  
      val persist_cks = acks.filter({
        case (k,v) =>{
          v._2.isInstanceOf[Persist]
        }
      })
      
      persist_cks.foreach({ 
        case( k,v ) =>{
           log.info(s"Retry Persist $k $v")
           persistor ! v._2
        }
      })  
	}
	
	case RetryReplica     => {
	  
      val replica_cks = acks.filter({
        case (k,v) =>{
          v._2.isInstanceOf[Replicate]
        }
      })
      
      replica_cks.foreach({ 
        case( k,v ) =>{
           log.info(s"Retry replicate $k $v")
           v._1 ! v._2
        }
      })  
	}
	
	case RetryTimeout     => {
	  log.info("Primary timeout Reached")
	  if ( acks.size == 0 ) requester ! OperationAck(id)
	  else {
	    sender ! OperationFailed(id)
	    acks = Map.empty[ Long , (ActorRef,Operation)]
	  }
	  context.become( leader )
	  unstashAll()
	}
	
    
    case _=> stash();
  }
  
  def begin_replication_process( sender:ActorRef, k:String, v:Option[String], id:Long)={
	  context.become( leader_in_replication(sender,id) )
	  log.info("Switching to replication");
      val pmsg = Persist(k,v,id)
      persistor ! pmsg
      acks += ( id -> (persistor,pmsg) )
      log.info("sending persist message for primary",pmsg);
      replicators.foreach( replicator =>{
	    val rmsg = Replicate(k,v,id)
	    replicator ! rmsg
	    acks += ( id -> (replicator,rmsg) )
	    log.info("sending replicate message to replicator",rmsg);
      })
      val scheduledRetryPersist = context.system.scheduler.schedule(Duration.Zero, 100 milliseconds, self, RetryPersist)
      val scheduledRetryReplica = context.system.scheduler.schedule(Duration.Zero, 100 milliseconds, self, RetryReplica)
      context.system.scheduler.schedule(Duration.Zero, 1 second, self, RetryTimeout)
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
    
    case Replicas( replicas ) =>{
      var rps = Set.empty[ActorRef]
      replicas.foreach( {
        case r:Replica =>{
          if( r != this ){
              log.debug("replication for ",r);
	          val replicator = context.actorOf( Replicator.props(r)  )
	          r.secondaries += ( r->replicator )
	          rps += replicator
          }
        }
        
      })
      replicators = rps
      
      kv.foreach({
        case (k,v)=>{
          val id = System.nanoTime
          self ! Insert(k,v,id)
        }
      })
    }
    

    case _ =>
  }
  /////////////////////////////////////////////////////////////////////////////// 
  
  
  ///////////////////////////////////////////////////////////////////////////////
  
  def begin_persist_process( sender:ActorRef, k:String, v:Option[String], seq:Long)={
	  log.info(s"Secondary wait for persist");
	  //val id = System.nanoTime
	  val id = seq
      val pmsg = Persist(k,v,id)
      persistor ! pmsg 
      acks += ( id -> (sender,pmsg) ) //DIFF between primary and secondary, primary have context to track requestor while secondary 
      	
      log.info(s"sending persist message for secondary $pmsg");
      val scheduledRetryPersist = context.system.scheduler.schedule(Duration.Zero, 100 milliseconds, self, RetryPersist)
  }

  var cur_seq = 0L
  /* TODO Behavior for the replica role. */
  val replica: Receive = {
    case Snapshot(k,v,seq) => {
      if( cur_seq == seq ){
         v match {
           case Some(vv) => kv += ( k->vv )
           case None => kv -= k
         }
         begin_persist_process(sender,k,v,seq)
         cur_seq += 1
      }
      else if( seq < cur_seq ){
        sender ! SnapshotAck( k, seq )
        cur_seq = seq + 1
      }
    }
    
    case Persisted(k,id)=> {
      val msg = SnapshotAck(k,id)
      val osender = acks(id)._1
      osender ! msg
      acks -= id
      log.info(s"Secondary ACK persitence with $msg");
    }
    
    case RetryPersist     => {
	  
      val persist_cks = acks.filter({
        case (k,v) =>{
          v._2.isInstanceOf[Persist]
        }
      })
      
      persist_cks.foreach({ 
        case( k,v ) =>{
           log.info(s"Retry Persist for secondary $k $v")
           persistor ! v._2
        }
      })  
	}
    
    case Get(k,id)      => {
      sender ! GetResult(k,kv.get(k),id)
    }
    case _ => 
  }

}
