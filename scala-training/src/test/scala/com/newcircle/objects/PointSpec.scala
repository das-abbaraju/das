package com.newcircle.tests.objects

import com.newcircle.objects._
import com.newcircle.UnitSpec

class PointSpec extends UnitSpec {
  "Point" should "be able to be created with 'new'" in {
    val point = new Point(10, 30)
    point.x should equal (10)
    point.y should equal (30)
  }

  it should "permit creation without 'new'" in {
    val point = Point(2, 3)
    point.x should equal (2)
    point.y should equal (3)

  }
}
