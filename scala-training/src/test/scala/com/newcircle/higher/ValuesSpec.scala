package com.newcircle.tests.higher

import com.newcircle.higher.Values._
import com.newcircle.UnitSpec

class ValuesSpec extends UnitSpec {
  "values" should "properly handle a function that squares its argument" in {
    values(x => x * x, -5, 4) should equal (
      Array((-5, 25), (-4, 16), (-3, 9), (-2, 4), (-1, 1),
            ( 0,  0), ( 1,  1), ( 2, 4), ( 3, 9), ( 4, 16))
    )
  }

  it should "properly handle a function that negates its argument" in {
    values(x => -x, -3, 3) should equal (
      Array((-3,3), (-2,2), (-1,1), (0,0), (1,-1), (2,-2), (3,-3))
    )
  }
}
