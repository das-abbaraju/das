package com.newcircle.tests.higher

import com.newcircle.higher.ReduceLeft
import com.newcircle.UnitSpec

import scala.util.Random

class ReduceLeftSpec extends UnitSpec {
  "max" should "return the maximum value of a shuffled collection" in {
    ReduceLeft.max(Random.shuffle(-100 to 100): _*) should equal (100)
  }
}
