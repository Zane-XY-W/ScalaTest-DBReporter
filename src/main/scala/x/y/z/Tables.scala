package x.y.z
import scala.slick.driver.H2Driver.simple._
import java.sql.Timestamp
import scala.slick.jdbc.meta.MTable

import scala.slick.jdbc.{StaticQuery => Q}

/**
 * Created by zane.wang on 7/16/13.
 */
object TestCases extends Table[TestCase]("TESTCASES") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

  def testName = column[String]("NAME")

  def className = column[String]("CLASSNAME")

  def timeStamp = column[Timestamp]("TIMESTAMP")

  def duration = column[Long]("DURATION")

  def status = column[Int]("STATUS")

  def message = column[String]("MESSAGE")

  def * = testName ~ className.? ~ timeStamp ~ duration.? ~ status.? ~ message.? <>(TestCase, TestCase.unapply _)

}

object TestSuites extends Table[TestSuite]("TESTSUITES") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

  def suiteName = column[String]("SUITENAME")

  def className = column[String]("CLASSNAME")

  def timeStamp = column[Timestamp]("TIMESTAMP")

  def successes = column[Int]("SUCCESSES")

  def failures = column[Int]("FAILURES")

  def ignores = column[Int]("IGNORES")

  def pendings = column[Int]("PENDINGS")

  def duration = column[Long]("DURATION")

  def * = suiteName ~ className.? ~ timeStamp.? ~ successes ~ failures ~ ignores ~ pendings ~ duration.? <>(TestSuite, TestSuite.unapply _)

  def createIfNotExist(implicit session: Session) = {
    if (!MTable.getTables.list.exists(_.name.name == TestSuites.tableName))
      TestSuites.ddl.create
  }

  def save(testSuite: TestSuite)(implicit session: Session) = TestSuites.insert(testSuite)

}
