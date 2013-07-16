package x.y.z

/**
 * Created by zane.wang on 7/16/13.
 */

import org.scalatest._
import scala.collection.mutable.ListBuffer
import org.scalatest.events._
import scala.slick.session.{Database, Session}
import java.util.ResourceBundle
import java.text.MessageFormat
import org.scalatest.events.ScopePending
import org.scalatest.events.MarkupProvided
import org.scalatest.events.SuiteAborted
import org.scalatest.events.ScopeClosed
import scala.Some
import org.scalatest.events.RunAborted
import org.scalatest.events.SuiteStarting
import org.scalatest.events.RunStarting
import org.scalatest.events.TestIgnored
import org.scalatest.events.RunCompleted
import org.scalatest.events.DiscoveryStarting
import org.scalatest.events.ScopeOpened
import org.scalatest.events.TestPending
import org.scalatest.events.SuiteCompleted
import org.scalatest.events.TestCanceled
import org.scalatest.events.TestStarting
import org.scalatest.events.RunStopped
import org.scalatest.events.TestSucceeded
import org.scalatest.events.InfoProvided
import org.scalatest.events.TestFailed
import org.scalatest.events.DiscoveryCompleted
import java.sql.Timestamp


class DBReporter extends ResourcefulReporter {

  private val results: ListBuffer[SuiteResult] = ListBuffer.empty
  private var eventList: ListBuffer[Event] = ListBuffer.empty
  private var runEndEvent: Option[Event] = None

  import DBReporter.db
  private def saveSuites(suiteResult: SuiteResult) {
    db withSession {
      implicit session: Session =>
        val testSuite = TestSuite(suiteResult.suiteName,
          suiteResult.suiteClass,
          Some(new Timestamp(suiteResult.startEvent.timeStamp)),
          suiteResult.testsSucceededCount,
          suiteResult.testsFailedCount,
          suiteResult.testsIgnoredCount,
          suiteResult.testsPendingCount,
          suiteResult.duration)
        TestSuites.createIfNotExist
        TestSuites.save(testSuite)
    }
  }

  /**
   * You can use this method to collect TestCase model and persist them if needed.
   * @param suiteResult
   * @return
   */
  private def collectTestCases(suiteResult: SuiteResult): IndexedSeq[TestCase] = suiteResult.eventList.collect {
    case TestSucceeded(_, _, _, suiteClass, testName, testText, _, duration, _, _, _, payload, _, timeStamp) =>
      TestCase(testName, suiteClass, new Timestamp(timeStamp), duration, Some(1), Some(testText))
    case TestFailed(_, message, _, _, suiteClass, testName, testText, _, throwable, duration, _, _, _, payload, _, timeStamp) =>
      TestCase(testName, suiteClass, new Timestamp(timeStamp), duration, Some(0), Some(testText))
    case TestIgnored(_, _, _, suiteClass, testName, testText, _, _, payload, _, timeStamp) =>
      TestCase(testName, suiteClass, new Timestamp(timeStamp), None, Some(4), Some(testText))
    case TestPending(_, _, _, suiteClass, testName, testText, _, duration, _, _, payload, _, timeStamp) =>
      TestCase(testName, suiteClass, new Timestamp(timeStamp), None, Some(2), Some(testText))
    case TestCanceled(_, message, _, _, suiteClass, testName, testText, _, throwable, duration, _, _, payload, _, timeStamp) =>
      TestCase(testName, suiteClass, new Timestamp(timeStamp), None, Some(3), Some(testText))
  }

  def collectSuiteResult(events: ListBuffer[Event], seed: SuiteResult): SuiteResult = events.foldLeft(seed) {
    case (r, e) =>
      e match {
        case testSucceeded: TestSucceeded => r.copy(testsSucceededCount = r.testsSucceededCount + 1)
        case testFailed: TestFailed => r.copy(testsFailedCount = r.testsFailedCount + 1)
        case testIgnored: TestIgnored => r.copy(testsIgnoredCount = r.testsIgnoredCount + 1)
        case testPending: TestPending => r.copy(testsPendingCount = r.testsPendingCount + 1)
        case testCanceled: TestCanceled => r.copy(testsCanceledCount = r.testsCanceledCount + 1)
        case scopePending: ScopePending => r.copy(scopesPendingCount = r.scopesPendingCount + 1)
        case _ => r
      }
  }

  def apply(event: Event): Unit = {

    event match {
      case _: DiscoveryStarting | _: DiscoveryCompleted | _: RunStarting =>

      case e@(_: RunCompleted | _: RunStopped | _: RunAborted) => runEndEvent = Some(e)

      case SuiteCompleted(_, suiteName, suiteId, suiteClass, duration, _, _, _, _, _, _) =>
        val (suiteEvents, otherEvents) = extractSuiteEvents(suiteId)
        eventList = otherEvents
        val sortedSuiteEvents = suiteEvents.sorted

        sortedSuiteEvents.head match {
          case suiteStarting: SuiteStarting =>
            val suiteResult = collectSuiteResult(sortedSuiteEvents, SuiteResult(suiteId, suiteName, suiteClass, duration, suiteStarting, event, Vector.empty ++ sortedSuiteEvents.tail, 0, 0, 0, 0, 0, 0, true))
            results += suiteResult
          case _ =>
        }

      case SuiteAborted(_, _, suiteName, suiteId, suiteClass, _, duration, _, _, _, _, _, _) =>
        val (suiteEvents, otherEvents) = extractSuiteEvents(suiteId)
        eventList = otherEvents
        val sortedSuiteEvents = suiteEvents.sorted

        sortedSuiteEvents.head match {
          case suiteStarting: SuiteStarting =>
            val suiteResult = collectSuiteResult(sortedSuiteEvents, SuiteResult(suiteId, suiteName, suiteClass, duration, suiteStarting, event, Vector.empty ++ sortedSuiteEvents.tail, 0, 0, 0, 0, 0, 0, false))
            results += suiteResult
          case other =>
        }

      case _ => eventList += event
    }
  }

  def extractSuiteEvents(suiteId: String) = {
    def eventHasSameSuiteId[T <: {val suiteId : String}](e: Event) = e.asInstanceOf[T].suiteId == suiteId
    def nameInfoOptHasSameSuiteId[T <: {val nameInfo : Option[NameInfo]}](e: Event) = e.asInstanceOf[T].nameInfo.map(_.suiteId == suiteId).getOrElse(false)
    def nameInfoHasSameSuiteId[T <: {val nameInfo : NameInfo}](e: Event) = e.asInstanceOf[T].nameInfo.suiteId == suiteId
    eventList partition {
      case e@(_: TestStarting | _: TestSucceeded | _: TestIgnored | _: TestFailed | _: TestPending
              | _: TestCanceled | _: SuiteStarting) => eventHasSameSuiteId(e)
      case e@(_: InfoProvided | _: MarkupProvided) => nameInfoOptHasSameSuiteId(e)
      case e@(_: ScopeOpened | _: ScopeClosed | _: ScopePending) => nameInfoHasSameSuiteId(e)
      case _ => false
    }
  }


  def dispose() {
    results.foreach(saveSuites)
  }
}

object Resources {

  lazy val resourceBundle = ResourceBundle.getBundle("org.scalatest.ScalaTestBundle")

  def apply(resourceName: String): String = resourceBundle.getString(resourceName)

  private def makeString(resourceName: String, argArray: Array[Object]): String = {
    val raw = apply(resourceName)
    val msgFmt = new MessageFormat(raw)
    msgFmt.format(argArray)
  }

  def apply(resourceName: String, o1: AnyRef*): String = makeString(resourceName, o1.toArray)

  def bigProblems(ex: Throwable) = {
    val message = if (ex.getMessage == null) "" else ex.getMessage.trim
    if (message.length > 0) Resources("bigProblemsWithMessage", message) else Resources("bigProblems")
  }
}

object DBReporter {
  private val db: Database = Database.forURL("jdbc:h2:mem:testsuites", driver = "org.h2.Driver")
  val create = db withSession(TestSuites.createIfNotExist)
}
