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
  case object ReplicaTimeout

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
  

  var packs = Map.empty[ Long , (ActorRef,Operation)] // id , requestor of the operation, operation 
  var racks = Map.empty[ Long , (ActorRef,Operation)]
 
  ////////////////////////////////////////////////////////////////////////////////////////////////
  
  var kv = Map.empty[String, String]
  var secondaries = Map.empty[ActorRef, ActorRef]
  var replicators = Set.empty[ActorRef]
  
  val scheduledRetryPersist = context.system.scheduler.schedule(Duration.Zero, 100 milliseconds, self, RetryPersist)
  val scheduledRetryReplica = context.system.scheduler.schedule(Duration.Zero, 100 milliseconds, self, RetryReplica)
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
      log.info(s"Persisted ack +1 $sender $id")
      packs -= id
    }
    
	case Replicated(k,id) =>{
	  log.info(s"Replicated ack +1 $sender $id")
	  racks -= id
	}
	
	case RetryPersist     => {
      
      packs.foreach({ 
        case( k,v ) =>{
           log.info(s"Retry Persist $k $v")
           persistor ! v._2
        }
      })  
	}
	
	case RetryReplica     => {
	  
      val replica_cks = racks.filter({
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
	  if ( packs.size == 0 && racks.size ==0 ) {
	    requester ! OperationAck(id)
	    log.info(s"Told requester $requester ACK $id")
	  }
	  else {
	    requester ! OperationFailed(id)
	    packs = Map.empty[ Long , (ActorRef,Operation)]
	    racks = Map.empty[ Long , (ActorRef,Operation)]
	    log.info(s"Told requester $requester OP Fail $id")
	  }
	  log.info("Switching back to leader");
	  context.become( leader )
	  unstashAll()
	}
	
	//case Replicas( replicas ) => handle_replica_message(replicas)
	
	case ReplicaTimeout     => {
		  log.info("Replica timeout Reached")
		  if ( racks.size == 0 ) {
		    requester ! OperationAck(id)
		    log.info(s"Told requester $requester ACK $id")
		  }
		  else {
		    requester ! OperationFailed(id)
		    racks = Map.empty[ Long , (ActorRef,Operation)]
		    log.info(s"Told requester $requester OP Fail $id")
		  }
		  log.info("Switching back to leader");
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
      packs += ( id -> (persistor,pmsg) )
      log.info(s"sending persist message for primary: $persistor <- $pmsg");
      replicators.foreach( replicator =>{
	    val rmsg = Replicate(k,v,id)
	    replicator ! rmsg
	    racks += ( id -> (replicator,rmsg) )
	    log.info(s"sending replicate message to replicator $replicator <- $rmsg");
      })

      context.system.scheduler.scheduleOnce( 1 second, self, RetryTimeout)
  }
  
  def handle_replica_message( replicas:Set[ActorRef])={
      log.info(s"replica request received  for $replicas");
      var rps = Set.empty[ActorRef]
      var expired_replica = Set.empty[ActorRef]
      replicas.foreach( {
        case r:ActorRef =>{
          if( r != self  ){
              log.info(s"replication for $r");
	          val replicator = context.actorOf( Replicator.props(r)  )
	          rps += replicator
          }
        }
        
      })
      
      replicators.foreach( or => {
        if ( !rps.contains(or) ){
          racks.foreach({
            case (k,v) => {
              if( v._1 == or ) racks -= k//remove their acknowledgements
              log.info(s"remove pending RACK for $v._1 <- $v._2");
            }
          })
          
          packs.foreach({
            case (k,v) => {
              if( v._1 == or ) packs -= k//remove their acknowledgements
              log.info(s"remove pending PACK for $v._1 <- $v._2");
            }
          })
          or ! PoisonPill
        }
      })
      
      replicators = rps
      
      val id = System.nanoTime
      begin_replica_process(self,id);
    }
  
  
  def begin_replica_process( sender:ActorRef, id:Long)={
	  context.become( leader_in_replication(sender,id) )
	  log.info("Switching to replication");
	  log.info("Replicate primary to all secondary");
      replicators.foreach( replicator =>{
    	  kv.foreach({
	        case (k,v)=>{
	          //val id = System.nanoTime
	          val rmsg = Replicate(k,Some(v),id)
	          racks += ( id -> (replicator,rmsg) )
	          replicator ! Replicate(k,Some(v),id)
	          log.info(s"sending replicate message to replicator $replicator <- $rmsg");
	        }
	      })
      })

      context.system.scheduler.scheduleOnce( 1 second, self, ReplicaTimeout)
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
      packs += ( id -> (sender,pmsg) ) //DIFF between primary and secondary, primary have context to track requestor while secondary 
      	
      log.info(s"sending persist message for secondary $pmsg");
      //val scheduledRetryPersist = context.system.scheduler.schedule(Duration.Zero, 100 milliseconds, self, RetryPersist)
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
      val osender = packs(id)._1
      osender ! msg
      packs -= id
      log.info(s"Secondary ACK persitence with $msg");
    }
    
    case RetryPersist     => {
      
      packs.foreach({ 
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
