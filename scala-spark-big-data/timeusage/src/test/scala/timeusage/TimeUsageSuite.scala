package timeusage

import org.apache.spark.sql.{Column, ColumnName, DataFrame, Row}
import org.apache.spark.sql.types.{
  DoubleType,
  StringType,
  StructField,
  StructType
}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.util.Random

@RunWith(classOf[JUnitRunner])
class TimeUsageSuite extends FunSuite with BeforeAndAfterAll {

  test("classified column should work") {
    val columnNames = List("t18010")
    val (g1, g2, g3) = TimeUsage.classifiedColumns(columnNames)

    assert( g1 === List(new Column("t18010")))
    assert( g2 === List.empty)
    assert( g3 === List.empty)

  }

}
