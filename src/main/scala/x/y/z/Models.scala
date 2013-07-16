package x.y.z

import java.sql.Timestamp
import org.scalatest.events.{Event, SuiteStarting}

/**
 * Created by zane.wang on 7/16/13.
 */
case class TestSuite(suiteName: String,
                     className: Option[String],
                     timeStamp: Option[Timestamp],
                     successes: Int,
                     failures: Int = 0,
                     ignores: Int = 0,
                     pendings:Int = 0,
                     duration: Option[Long])

/**
 *
 * @param testName
 * @param className
 * @param timeStamp
 * @param duration
 * @param status failure = 0, success = 1, pending = 2, canceled = 3, ignored = 4
 * @param message
 */
case class TestCase(testName: String,
                    className: Option[String],
                    timeStamp: Timestamp,
                    duration: Option[Long] = None,
                    status: Option[Int] = None,
                    message: Option[String] = None)

/**
 * This class is copied from org.scalatest.tools.SuiteResult, because it's a package private model
 * @param suiteId
 * @param suiteName
 * @param suiteClass
 * @param duration
 * @param startEvent
 * @param endEvent
 * @param eventList
 * @param testsSucceededCount
 * @param testsFailedCount
 * @param testsIgnoredCount
 * @param testsPendingCount
 * @param testsCanceledCount
 * @param scopesPendingCount
 * @param isCompleted
 */
case class SuiteResult(suiteId: String,
                       suiteName: String,
                       suiteClass: Option[String],
                       duration: Option[Long],
                       startEvent: SuiteStarting,
                       endEvent: Event,
                       eventList: collection.immutable.IndexedSeq[Event],
                       testsSucceededCount: Int,
                       testsFailedCount: Int,
                       testsIgnoredCount: Int,
                       testsPendingCount: Int,
                       testsCanceledCount: Int,
                       scopesPendingCount: Int,
                       isCompleted: Boolean)

