package x.y.z

import collection.mutable.Stack
import org.scalatest._
import Matchers._

class DummyTest extends FlatSpec {
  "a falsy express" should " fail" in {
    1  should be > 2
  }
}


