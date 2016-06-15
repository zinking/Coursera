package reductions

import scala.annotation._
import org.scalameter._
import common._

object ParallelParenthesesBalancingRunner {

  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 120,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val length = 100000000
    val chars = new Array[Char](length)
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime ms")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime ms")
    println(s"speedup: ${seqtime / fjtime}")
  }
}

object ParallelParenthesesBalancing {

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def balance(chars: Array[Char]): Boolean = {
    chars.foldLeft(0)((b:Int,c:Char)=>{
      c match {
        case '(' => b+1
        case ')' =>
          if (b-1 < 0)
            return false
          else
            b-1
        case _ => b
      }
    }) == 0
  }

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def parBalance(chars: Array[Char], threshold: Int): Boolean = {

    /**
     * parallel traverse the subsequence and return the blance status
     * @param idx from which index
     * @param until until which index
     * @param l number of initial brackets opening to the left
     * @param r number of initial brackets opening to the right
     * @return numer of brackets opening to the left and right respctively
     */
    def traverse(idx: Int, until: Int, l: Int, r: Int):(Int,Int) = {
      chars.slice(idx,until).foldLeft((l,r)){ (p,c) =>
        val (l,r) = p
        c match {
          case '(' => (l,r+1)
          case ')' if r>0 => (l,r-1)
          case ')' if r<=0 => (l+1,r)
          case _ => p
        }
      }
    }

    def reduce(from: Int, until: Int):(Int,Int) = {
      if (until - from <= threshold) {
        traverse(from,until,0,0)
      } else {
        val m = (from + until)/2
        val (rr1,rr2) = parallel(
          reduce(from,m),
          reduce(m,until)
        )
        val (l1,r1) = rr1
        val (l2,r2) = rr2
        val d = r1-l2
        if (d>0) {
          (l1,d+r2)
        } else {
          (l1-d,r2)
        }
      }
    }

    reduce(0, chars.length) == (0,0)
  }

  // For those who want more:
  // Prove that your reduction operator is associative!

}
