package kvstore

import akka.actor.Props
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.PoisonPill
import akka.actor.Terminated
import scala.concurrent.duration._
import akka.event.Logging
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
  val log = Logging(context.system, this)
  var acks = Map.empty[Long, (ActorRef, Replicate)] // this is acks for snapshot
  var pending = Vector.empty[Snapshot]
  
  var _seqCounter = 0L
  def nextSeq = {
    val ret = _seqCounter
    _seqCounter += 1
    ret
  }
  
  context.system.scheduler.schedule(100 millis, 100 millis, context.self, "Retry")
  
  /* TODO Behavior for the Replicator. */
  def receive: Receive = {
    case rep @ Replicate(key, valueOpt, id) => {
      val seq = nextSeq
      acks += seq -> (sender, rep) 
      replica ! Snapshot(key, valueOpt, seq)
    }
    case SnapshotAck(key, seq) => {
      acks.get(seq).map{entry =>
        val (primary, command) = entry
        primary ! Replicated(key, command.id)
      }
      acks -= seq 
    }
    case "Retry" => {
      acks.foreach(entry => {
        val (seq, (primary, replicate)) = entry
        replica ! Snapshot(replicate.key, replicate.valueOption, seq)
      })
    }
  }
  

}
