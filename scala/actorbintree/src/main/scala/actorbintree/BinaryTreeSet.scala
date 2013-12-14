/**
 * Copyright (C) 2009-2013 Typesafe Inc. <http://www.typesafe.com>
 */
package actorbintree

import akka.actor._
import akka.actor.Stash
import scala.collection.immutable.Queue
import akka.event.LoggingReceive

object BinaryTreeSet {

  trait Operation {
    def requester: ActorRef
    def id: Int
    def elem: Int
  }

  trait OperationReply {
    def id: Int
  }

  /** Request with identifier `id` to insert an element `elem` into the tree.
    * The actor at reference `requester` should be notified when this operation
    * is completed.
    */
  case class Insert(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request with identifier `id` to check whether an element `elem` is present
    * in the tree. The actor at reference `requester` should be notified when
    * this operation is completed.
    */
  case class Contains(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request with identifier `id` to remove the element `elem` from the tree.
    * The actor at reference `requester` should be notified when this operation
    * is completed.
    */
  case class Remove(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request to perform garbage collection*/
  case object GC

  /** Holds the answer to the Contains request with identifier `id`.
    * `result` is true if and only if the element is present in the tree.
    */
  case class ContainsResult(id: Int, result: Boolean) extends OperationReply
  
  /** Message to signal successful completion of an insert or remove operation. */
  case class OperationFinished(id: Int) extends OperationReply

}


class BinaryTreeSet extends Actor with Stash {
  import BinaryTreeSet._
  import BinaryTreeNode._

  def createRoot: ActorRef = context.actorOf(BinaryTreeNode.props(0, initiallyRemoved = true))

  var root = createRoot

  // optional
  var pendingQueue = Queue.empty[Operation]

  // optional
  def receive = normal

  // optional
  /** Accepts `Operation` and `GC` messages. */
  val normal: Receive = LoggingReceive {
    //case Insert(requester, id, elem ) 	=> root ! Insert(requester, id, elem )
    //case Contains(requester, id, elem ) => root ! Contains(requester, id, elem )
    //case Remove(requester, id, elem ) 	=> root ! Remove(requester, id, elem )
    case operation: Operation => root ! operation
    case GC => {
      val newRoot = context.actorOf(BinaryTreeNode.props(0, initiallyRemoved = true))
      context.become(
    		  garbageCollecting(newRoot) 
      )
      root ! CopyTo( newRoot )
      
      
    }
    //case _ => sender ! OperationFinished
    }

  // optional
  /** Handles messages while garbage collection is performed.
    * `newRoot` is the root of the new binary tree where we want to copy
    * all non-removed elements into.
    */
  def garbageCollecting(newRoot: ActorRef): Receive = LoggingReceive {
    
    case CopyFinished => {
      root ! PoisonPill
      root = newRoot
      
//      while( !pendingQueue.isEmpty){
//        val op = pendingQueue.dequeue
//        root ! op
//      }
      context.become(normal)
      unstashAll()
      
    }

    case op:Operation => stash()
//    case op:Operation => {
//      pendingQueue.enqueue(op)
//    }
  }

}

object BinaryTreeNode {
  trait Position

  case object Left extends Position
  case object Right extends Position

  case class CopyTo(treeNode: ActorRef)
  case object CopyFinished

  def props(elem: Int, initiallyRemoved: Boolean) = Props(classOf[BinaryTreeNode],  elem, initiallyRemoved)
}

class BinaryTreeNode(val elem: Int, initiallyRemoved: Boolean) extends Actor {
  import BinaryTreeNode._
  import BinaryTreeSet._

  var subtrees = Map[Position, ActorRef]()
  var removed = initiallyRemoved

  // optional
  def receive = normal
  
  def l ={
    subtrees(Left);
  }
  
  def r ={
    subtrees(Right)
  }

  def hasLeft = {
    subtrees.contains(Left)
  }
  
  def hasRight = {
    subtrees.contains(Right)
  }
  
  def isLeaf = {
    !hasLeft && !hasRight
  }
  
  // optional
  /** Handles `Operation` messages and `CopyTo` requests. */
  val normal: Receive =  LoggingReceive  { 
    case Insert(requester, id, elem ) 	=> {
      if( elem < this.elem ){
        if( hasLeft ){
          l ! Insert(requester, id, elem )
        }
        else{
          subtrees += Left -> context.actorOf(BinaryTreeNode.props(elem, initiallyRemoved = false))
          requester ! OperationFinished(id)
        }
      }
      else if( elem == this.elem ){
        removed = false;
        requester ! OperationFinished(id)
      }
      else{
        if( hasRight ){
          r ! Insert(requester, id, elem )
        }
        else{
          subtrees += Right -> context.actorOf(BinaryTreeNode.props(elem, initiallyRemoved = false))
          requester ! OperationFinished(id)
        }
      }
    }
    
    case Contains(requester, id, elem ) =>{
      if( elem < this.elem ){
        if( hasLeft ){
          l ! Contains(requester, id, elem )
        }
        else requester !  ContainsResult( id, false )
      }
      else if ( elem == this.elem  ){
        //if( !removed ) requester ! ContainsResult( id, true )
        //else requester ! ContainsResult( id, false )
        requester ! ContainsResult( id, !removed )
      }
      else {
        if( hasRight ){
          r ! Contains(requester, id, elem )
        }
        else requester !  ContainsResult( id, false )
      }
      //requester !  ContainsResult( id, false )
    }
    
    case Remove(requester, id, elem ) 	=>{
      if( elem < this.elem ){
        if( hasLeft ){
          l ! Remove(requester, id, elem )
        }
        else requester !  OperationFinished(id)
      }
      else if ( elem == this.elem ){
        removed = true
        requester ! OperationFinished(id)
      }
      else {
        if( hasRight ){
          r ! Remove(requester, id, elem )
        }
        else requester !  OperationFinished(id)
      }
    }
    
    case CopyTo( newRoot ) => {
      
      if( !removed ){
        newRoot ! Insert(self, elem, elem )
      }
      val children = subtrees.values.toSet
      children foreach { _ ! CopyTo(newRoot) }
      context.become(copying(children, insertConfirmed=removed))
//      if( hasLeft ){
//        l ! CopyTo(newRoot)
//      }
//      
//      if( hasRight ){
//        r ! CopyTo(newRoot)
//      }
//      
//      sender ! CopyFinished

    }
      
    case _ => OperationFinished 
    }

  // optional
  /** `expected` is the set of ActorRefs whose replies we are waiting for,
    * `insertConfirmed` tracks whether the copy of this node to the new tree has been confirmed.
    */
  def copying(expected: Set[ActorRef], insertConfirmed: Boolean): Receive =  {
    if (expected.isEmpty && insertConfirmed) {
      context.parent ! CopyFinished
      normal
    } else {
      LoggingReceive {
        case OperationFinished(x) if x == elem => context.become(copying(expected, insertConfirmed = true))
        case CopyFinished => context.become(copying(expected - sender, insertConfirmed))
      }
    }
  }

}
