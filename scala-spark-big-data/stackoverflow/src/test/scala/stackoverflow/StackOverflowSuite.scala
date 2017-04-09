package stackoverflow

import org.scalatest.{FunSuite, BeforeAndAfterAll}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import java.io.File

@RunWith(classOf[JUnitRunner])
class StackOverflowSuite extends FunSuite with BeforeAndAfterAll {


  lazy val testObject = new StackOverflow {
    override val langs =
      List(
        "JavaScript", "Java", "PHP", "Python", "C#", "C++", "Ruby", "CSS",
        "Objective-C", "Perl", "Scala", "Haskell", "MATLAB", "Clojure", "Groovy")
    override def langSpread = 50000
    override def kmeansKernels = 45
    override def kmeansEta: Double = 20.0D
    override def kmeansMaxIterations = 120
  }

  test("testObject can be instantiated") {
    val instantiatable = try {
      testObject
      true
    } catch {
      case _: Throwable => false
    }
    assert(instantiatable, "Can't instantiate a StackOverflow object")
  }

  import StackOverflow._

  def initializeStackOverflow(): Boolean = {
    try {
      testObject
      true
    } catch {
      case ex: Throwable =>
        println(ex.getMessage)
        ex.printStackTrace()
        false
    }
  }

  override def afterAll(): Unit = {
    assert(initializeStackOverflow(), " -- did you fill in all the values in WikipediaRanking (conf, sc, wikiRdd)?")
    sc.stop()
  }

  test("clusterResults") {
    val centers = Array((0, 0), (100000, 0))

    val rdd = sc.parallelize(List(
      (0, 1000),
      (0, 23),
      (0, 234),
      (0, 0),
      (0, 1),
      (0, 1),
      (50000, 2),
      (50000, 10),
      (100000, 2),
      (100000, 5),
      (100000, 10),
      (200000, 100)))

    val results = testObject.clusterResults(centers, rdd)
    testObject.printResults(results)
    val expected = Array(("PHP", 75.0, 4, 5), ("JavaScript", 75.0, 8, 12))
    // note the use of === to test equality
    assert(results === expected, "Failed test did not get expected results")
  }


}
