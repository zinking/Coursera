package reductions

import org.scalameter._
import common._

object LineOfSightRunner {
  
  val standardConfig = config(
    Key.exec.minWarmupRuns -> 20,
    Key.exec.maxWarmupRuns -> 40,
    Key.exec.benchRuns -> 80,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]) {
    val length = 10000000
    val input = (0 until length).map(_ % 100 * 1.0f).toArray
    val output = new Array[Float](length + 1)
    val seqtime = standardConfig measure {
      LineOfSight.lineOfSight(input, output)
    }
    println(s"sequential time: $seqtime ms")

    val partime = standardConfig measure {
      LineOfSight.parLineOfSight(input, output, 10000)
    }
    println(s"parallel time: $partime ms")
    println(s"speedup: ${seqtime / partime}")
  }
}

object LineOfSight {

  def max(a: Float, b: Float): Float = if (a > b) a else b

  def lineOfSight(input: Array[Float], output: Array[Float]): Unit = {
    input.zipWithIndex.foldLeft(0.0f){ (prevMax,c)=>
      val (e,i)=c
      if (i==0) {
        output(i) = 0.0f
        prevMax
      } else {
        val current:Float = e/i
        output(i) = max(prevMax,current)
        output(i)
      }
    }
  }

  sealed abstract class Tree {
    def maxPrevious: Float
  }

  case class Node(left: Tree, right: Tree) extends Tree {
    val maxPrevious = max(left.maxPrevious, right.maxPrevious)
  }

  case class Leaf(from: Int, until: Int, maxPrevious: Float) extends Tree

  /** Traverses the specified part of the array and returns the maximum angle.
   */
  def upsweepSequential(input: Array[Float], from: Int, until: Int): Float = {
    (from-1 to until-1).foldLeft(0.0f){ (p,ci)=>
      if (ci == 0) {
        p
      } else {
        val e = input(ci)
        val r = e/ci
        max(p,r)
      }
    }
  }

  /** Traverses the part of the array starting at `from` and until `end`, and
   *  returns the reduction tree for that part of the array.
   *
   *  The reduction tree is a `Leaf` if the length of the specified part of the
   *  array is smaller or equal to `threshold`, and a `Node` otherwise.
   *  If the specified part of the array is longer than `threshold`, then the
   *  work is divided and done recursively in parallel.
   */
  def upsweep(input: Array[Float], from: Int, end: Int, threshold: Int): Tree = {
    if (end - from <= threshold) {
      val maxPrev = upsweepSequential(input, from, end)
      Leaf(from, end, maxPrev)
    } else {
      val md = (from+end)/2

      val (tL, tR) = parallel(
        upsweep(input,from,md,threshold),
        upsweep(input,md,end,threshold)
      )
      Node(tL, tR)
    }

  }

  /** Traverses the part of the `input` array starting at `from` and until
   *  `until`, and computes the maximum angle for each entry of the output array,
   *  given the `startingAngle`.
   */
  def downsweepSequential(input: Array[Float], output: Array[Float],
    startingAngle: Float, from: Int, until: Int): Unit = {
    if (until >= from ) {
      (from to until).map{ i =>
        val c = upsweepSequential(input,from,i)
        output(i-1) = max(c, startingAngle)
      }
    }
  }

  /** Pushes the maximum angle in the prefix of the array to each leaf of the
   *  reduction `tree` in parallel, and then calls `downsweepTraverse` to write
   *  the `output` angles.
   */
  def downsweep(input: Array[Float], output: Array[Float], startingAngle: Float, tree: Tree): Unit = {
    tree match {
      case Leaf(f,u,m) =>
        downsweepSequential(input,output,startingAngle,f,u)
      case Node(l,r) =>
        parallel(
          downsweep(input,output,startingAngle,l),
          downsweep(input,output,max(startingAngle, l.maxPrevious),r)
        )
    }
  }

  /** Compute the line-of-sight in parallel. */
  def parLineOfSight(input: Array[Float], output: Array[Float], threshold: Int): Unit = {
    val tRes = upsweep(input,1,input.length,threshold)
    downsweep(input,output,0,tRes)
  }
}
