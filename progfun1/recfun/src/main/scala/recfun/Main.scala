package recfun

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
  }

  /**
   * Exercise 1
   */
    def pascal(c: Int, r: Int): Int = {
      if (c == 0) return 1
      if (c == 1) return r
      pascal(c-1,r-1) + pascal(c,r-1)
    }
  /**
   * Exercise 2
   */
    def balance(chars: List[Char]): Boolean = {
      def bracketBalance(chars: List[Char], bal:Int): Boolean = {
        if (chars.isEmpty && bal == 0) return true
        if (bal<0) return false
        if (chars.isEmpty) return false
        val newBal = chars.head match {
          case '(' => bal+1
          case ')' => bal-1
          case _ => bal
        }
        bracketBalance(chars.tail,newBal)
      }
      bracketBalance(chars,0)
    }
  
  /**
   * Exercise 3
   */
    def countChange(money: Int, coins: List[Int]): Int = {
      if (money < 0) return 0
      if (money == 0) return 1
      coins match {
        case c::cs =>
          val ts:Int = money / c
          (0 until ts+1) map { t=>
            countChange(money - t*c, cs)
          } sum
        case _ =>
          0
      }
    }
  }
