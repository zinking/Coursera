package reductions

import java.util.concurrent._
import scala.collection._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common._
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory

@RunWith(classOf[JUnitRunner]) 
class LineOfSightSuite extends FunSuite {
  import LineOfSight._
  test("lineOfSight should correctly handle an array of size 4") {
    val output = new Array[Float](4)
    lineOfSight(Array[Float](0f, 1f, 8f, 9f), output)
    assert(output.toList == List(0f, 1f, 4f, 4f))
  }


  test("upsweepSequential should correctly handle the chunk 1 until 4 of an array of 4 elements") {
    val res = upsweepSequential(Array[Float](0f, 1f, 8f, 9f), 1, 4)
    assert(res == 4f)
  }


  test("downsweepSequential should correctly handle a 4 element array when the starting angle is zero") {
    val output = new Array[Float](4)
    downsweepSequential(Array[Float](0f, 1f, 8f, 9f), output, 0f, 1, 4)
    assert(output.toList == List(0f, 1f, 4f, 4f))
  }

  test("downsweepSequential should correctly handle empty chunks. " +
      "E.g. if from >= end, downsweepSequential should not write anything into the output array") {
    val output = new Array[Float](5)
    downsweepSequential(Array[Float](0f, 1f, 8f, 9f, 10f), output, 0f, 11, 1)
    assert(output.toList == List(0f, 0f, 0f, 0f, 0f))
  }

  test("downsweep should correctly compute the output for a non-zero starting angle") {
    val output = new Array[Float](4)
    downsweepSequential(Array[Float](1f, 1f, 8f, 9f), output, 0f, 1, 4)
    assert(output.toList == List(1f, 1f, 4f, 4f))
  }

  test("parLineOfSight should correctly handle an array of size 4") {
    val output = new Array[Float](4)
    parLineOfSight(Array[Float](0f, 1f, 8f, 9f), output, 2)
    assert(output.toList == List(0f, 1f, 4f, 4f))
  }

  test("parallel calling count") {
    parallelCallCount = 0
    val input = 1.0f to 17.0f by 1.0f
    val output = new Array[Float](17)
    parLineOfSight(input.toArray,output,1)
    assert(parallelCallCount == 30)

  }

}

