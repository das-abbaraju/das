package com.newcircle.tests.maptuple

import com.newcircle.maptuple.Stats
import com.newcircle.UnitSpec

class StatsSpec extends UnitSpec {
  "statsFor" should "handle an empty array" in {
    val stats = Stats.statsFor(Array.empty[Int])
    stats should equal ((0, 0, 0, 0.0, 0.0))
  }

  it should "work properly on an even-length array" in {
    val data = Array(30, 19, 11, 5, 24, 15, 27, 9, 23, 4,
                     19, 14, 23, 1, 23, 17,  9, 6,  4, 6)

    val (min, max, mode, mean, median) = Stats.statsFor(data)
    min should equal (1)
    max should equal (30)
    median should equal (14.5)
    mode should equal (23)
    mean should equal (14.45)
  }

  it should "work properly on an odd-length array" in {
    val data = Array(21, 18, 12, 13, 33, 12, 29, 14, 22, 47, 
                     45, 49, 48, 15, 27, 24, 16,  8,  3)

    val (min, max, mode, mean, median) = Stats.statsFor(data)
    min should equal (3)
    max should equal (49)
    median should equal (21.0)
    mode should equal (12)
    mean should equal (24.0)
  }

  it should "bail if there's more than one mode" in {

    val data = Array(31, 39, 21, 45, 19, 38, 14, 24, 7,  5,
                     25,  5,  9, 17, 22, 24,  9, 29, 29, 5,
                     43, 15, 24, 36, 39,  4, 28, 43, 35, 16,
                      8, 21, 13, 17, 38, 23, 25, 30, 43, 10)

    intercept[IllegalArgumentException] {
      Stats.statsFor(data)
    }
  }
}

