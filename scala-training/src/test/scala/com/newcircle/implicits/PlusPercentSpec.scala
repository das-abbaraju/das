package com.newcircle.tests.implicits

import com.newcircle.implicits.PlusPercent._
import com.newcircle.UnitSpec

class PlusPercentSpec extends UnitSpec {
  "+%" should "handle 0 percent" in {
    (100 +% 0) should equal (100)
  }

  it should "handle 1 percent" in {
    (100 +% 1) should equal (101)
    (1 +% 1) should equal (1)
    (101 +% 1) should equal (102)
  }

  it should "handle a negative percent" in {
    (100 +% -1) should equal (99)
  }

  it should "handle 100%" in {
    (33 +% 100) should equal (33 * 2)
  }
}
