package reductions

import org.scalameter._
import common._

object ParallelCountChangeRunner {

  @volatile var seqResult = 0

  @volatile var parResult = 0

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 20,
    Key.exec.maxWarmupRuns -> 40,
    Key.exec.benchRuns -> 80,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val amount = 250
    val coins = List(1, 2, 5, 10, 20, 50)
    val seqtime = standardConfig measure {
      seqResult = ParallelCountChange.countChange(amount, coins)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential count time: $seqtime ms")

    def measureParallelCountChange(threshold: ParallelCountChange.Threshold): Unit = {
      val fjtime = standardConfig measure {
        parResult = ParallelCountChange.parCountChange(amount, coins, threshold)
      }
      println(s"parallel result = $parResult")
      println(s"parallel count time: $fjtime ms")
      println(s"speedup: ${seqtime / fjtime}")
    }

    //measureParallelCountChange(ParallelCountChange.moneyThreshold(amount))
    measureParallelCountChange(ParallelCountChange.totalCoinsThreshold(coins.length))
    measureParallelCountChange(ParallelCountChange.combinedThreshold(amount, coins))
  }
}

object ParallelCountChange {

  //val rcache = scala.collection.mutable.HashMap[Int,Int]()
  /** Returns the number of ways change can be made from the specified list of
   *  coins for the specified amount of money.
   */
  def countChange(m: Int, coins: List[Int]): Int = coins match {
    case Nil => if (m == 0) 1 else 0
    case c::rs => (0 to m/c) map(k => countChange(m-k*c,rs)) sum
  }


  type Threshold = (Int, List[Int]) => Boolean

  /** In parallel, counts the number of ways change can be made from the
   *  specified list of coins for the specified amount of money.
   */
  def parCountChange1(m: Int, coins: List[Int], threshold: Threshold): Int = {
    if (threshold(m,coins)) {
      countChange(m,coins)
    } else {
      coins match {
        case Nil => 0
        case c::rs =>
          val from = 0
          val til = m/c
          val md = (from+til)/2

          val (sum1,sum2) = parallel(
            (from to md) map{k=>
              parCountChange(m-k*c,rs,threshold)
            } sum,
            (md+1 to til) map{k=>
              parCountChange(m-k*c,rs,threshold)
            } sum
          )
          sum1+sum2
      }
    }
  }

  def parCountChange(m: Int, coins: List[Int], threshold: Threshold): Int = {
    if (m<0 || coins.length == 0 ) return 0
    if (threshold(m,coins)) {
      countChange(m,coins)
    } else {
      coins match {
        case Nil => 0
        case c::rs =>
          val til = m/c
          val (sum1,sum2) = parallel(
            parCountChange(m-c,coins,threshold),
            parCountChange(m,rs,threshold)
          )
          sum1+sum2
      }
    }
  }

  /** Threshold heuristic based on the starting money. */
  def moneyThreshold(startingMoney: Int): Threshold =
    (m:Int,_) => m <= (2*startingMoney)/3

  /** Threshold heuristic based on the total number of initial coins. */
  def totalCoinsThreshold(totalCoins: Int): Threshold =
    //(_,cs:List[Int])=> totalCoins/(cs.length+1) >= 3
    (_,cs:List[Int])=> cs.length <= (2*totalCoins)/3


  /** Threshold heuristic based on the starting money and the initial list of coins. */
  def combinedThreshold(startingMoney: Int, allCoins: List[Int]): Threshold = {
    (m,cs) => {
      val csn = cs.length
      val tn = allCoins.length
      m*csn <= (startingMoney*tn)/2
    }

  }
}
