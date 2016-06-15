package scalashop

import org.scalameter._
import common._

object HorizontalBoxBlurRunner {

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 5,
    Key.exec.maxWarmupRuns -> 10,
    Key.exec.benchRuns -> 10,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val radius = 3
    val width = 1920
    val height = 1080
    val src = new Img(width, height)
    val dst = new Img(width, height)
    val seqtime = standardConfig measure {
      HorizontalBoxBlur.blur(src, dst, 0, height, radius)
    }
    println(s"sequential blur time: $seqtime ms")

    val numTasks = 32
    val partime = standardConfig measure {
      HorizontalBoxBlur.parBlur(src, dst, numTasks, radius)
    }
    println(s"fork/join blur time: $partime ms")
    println(s"speedup: ${seqtime / partime}")
  }
}


/** A simple, trivially parallelizable computation. */
object HorizontalBoxBlur {

  /** Blurs the rows of the source image `src` into the destination image `dst`,
   *  starting with `from` and ending with `end` (non-inclusive).
   *
   *  Within each row, `blur` traverses the pixels by going from left to right.
   */
  def blur(src: Img, dst: Img, from: Int, end: Int, radius: Int): Unit = {
  // TODO implement this method using the `boxBlurKernel` method
    val w = src.width
    val h = src.height
    for (
      xx <- Array.range(0,w);
      yy <- Array.range(from,end)
    ) {
      val blured = boxBlurKernel(src,xx,yy,radius)
      dst.update(xx,yy,blured)
    }
  }

  /** Blurs the rows of the source image in parallel using `numTasks` tasks.
   *
   *  Parallelization is done by stripping the source image `src` into
   *  `numTasks` separate strips, where each strip is composed of some number of
   *  rows.
   */
  def parBlur(src: Img, dst: Img, numTasks: Int, radius: Int): Unit = {
  // TODO implement using the `task` construct and the `blur` method
    val w = src.width
    val h = src.height
    parBlurFT(src,dst,numTasks,radius,0,h)
  }

  def parBlurFT(src: Img, dst: Img, numTasks: Int, radius: Int, from: Int, to:Int): Unit = {
    if (numTasks<=1) {
      println(s"from $from to $to")
      blur(src,dst,from,to,radius)
    } else {
      val m=(from+to)/2
      parallel(
        parBlurFT(src,dst,numTasks/2,radius,from,m),
        parBlurFT(src,dst,numTasks/2,radius,m,to)
      )
    }

  }

}
