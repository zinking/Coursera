package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._
import scala.math._
import scala.util.Random

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  property("min1") = forAll { a: Int =>
    val h = insert(a, empty)
    findMin(h) == a
  }
  
  property("isEmpty1") = {
    isEmpty(empty)
  }
  
  property("insert1") = forAll { a: Int =>
    val h = insert( a, empty )
    !isEmpty(h)
  }
  
  property("deleteMin1") = forAll { a: Int =>
    val h = insert( a, empty )
    val j = deleteMin( h );
    isEmpty(j)
  }
  
  property("deleteMin2") = forAll { (m: Int, n: Int) =>
    val h = insert( m, empty )
    val p = insert( n, empty )
    val q = meld( h,p )
    val r = deleteMin( q )
    findMin( r ) == max( m, n )
  }
  
  property("meld1") = forAll { (m: Int, n: Int) =>
    val h = insert( m, empty )
    val p = insert( n, empty )
    val q = meld( h,p )
    !isEmpty( q ) &&
    	findMin(q) == min(m,n)
  }
  
  property("findMin1") = forAll { n: Int =>
    val tsq = Seq.fill(10)(Random.nextInt)
    //println( tsq )
    var h = empty //side effects
    tsq.foreach( x => {
      h = insert( x, h ) 
    });
    
    val ssq = tsq.sorted
    //println( ssq )
    var allminimum:Boolean = true;
    
    ssq.foreach( x=>{
    	val m = findMin( h )
    	allminimum = allminimum && (x==m);
    	h=deleteMin(h)
    })
    allminimum
  }
  

  lazy val genHeap: Gen[H] = ???

  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)
  
  abstract class Sheap[A] extends {
    	//val spq = new PriorityQueue[A]();
    
    	def empty: H // the empty heap
	 	def isEmpty(h: H): Boolean // whether the given heap h is empty
	
	 	def insert(x: A, h: H): H // the heap resulting from inserting x into h
	 	def meld(h1: H, h2: H): H // the heap resulting from merging h1 and h2
	
	 	def findMin(h: H): A // a minimum of the heap h
	 	def deleteMin(h: H): H // a heap resulting from deleting a minimum of h
    
  };
  

}


