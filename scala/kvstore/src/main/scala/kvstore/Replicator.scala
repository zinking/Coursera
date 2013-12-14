package kvstore

import akka.actor.Props
import akka.actor.Actor
import akka.actor.ActorRef
import scala.concurrent.duration._

object Replicator {
  import Replica._
  case class Replicate(key: String, valueOption: Option[String], id: Long) extends Operation
  case class Replicated(key: String, id: Long) 
  
  case class Snapshot(key: String, valueOption: Option[String], seq: Long) 
  case class SnapshotAck(key: String, seq: Long) 

  def props(replica: ActorRef): Props = Props(new Replicator(replica))
}

class Replicator(val replica: ActorRef) extends Actor {
  import Replicator._
  import Replica._
  import context.dispatcher
  
  /*
   * The contents of this actor is just a suggestion, you can implement it in any way you like.
   */

  // map from sequence number to pair of sender and request
  var acks = Map.empty[Long, (ActorRef, Replicate)] // this is acks for snapshot
  // a sequence of not-yet-sent snapshots (you can disregard this if not implementing batching)
  var pending = Vector.empty[Snapshot]
  
  var _seqCounter = 0L
  def nextSeq = {
    val ret = _seqCounter
    _seqCounter += 1
    ret
  }
  
  /* TODO Behavior for the Replicator. */
  def receive: Receive = {
    case Replicate( k,v,id)=>{
      replica ! Snapshot(k,v,_seqCounter)
      acks += ( _seqCounter -> (sender, Replicate( k,v,id) ))
      //sender ! Replicated(k,id)
    }
    case SnapshotAck(k,seq)=>{
      val iv = acks(seq)
      val primary = iv._1
      val id = iv._2.id
      primary ! Replicated(k,id)//repsond primary that this is replciated
      acks -= seq
      nextSeq
    }
    case _ =>
  }
  
  //import akka.actor.Scheduler
  //Scheduler.schedule(actor, Message(), 0L, 5L, TimeUnit.MINUTES)
  //schedule(0 milliseconds, 100 milliseconds, reassure_replica )
  context.system.scheduler.schedule(0 milliseconds, 100 milliseconds )(reassure_replica)


  def reassure_replica = {
    acks.foreach({ item =>
      val r = item._2._2;
      replica ! Snapshot( r.key, r.valueOption, r.id )
    })
  }

}
