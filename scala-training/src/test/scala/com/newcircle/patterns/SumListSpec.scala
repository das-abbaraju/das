package com.newcircle.tests.patterns

import com.newcircle.patterns.SumList
import com.newcircle.UnitSpec

class SumListSpec extends UnitSpec {
  "sum" should "handle an empty list" in {
    SumList.sum(List.empty[Option[Int]]) should equal (0)
  }

  it should "handle a list with one None element" in {
    SumList.sum(List[Option[Int]](None)) should equal (0)
  }

  it should "handle a list with one Some element" in {
    SumList.sum(List(Some(100))) should equal (100)
  }

  it should "handle a list with many Some elements" in {
    SumList.sum(List(Some(1001), Some(-3))) should equal (998)
  }

  it should "handle a list with many None elements" in {
    SumList.sum(List[Option[Int]](None, None, None)) should equal (0)
  }

  it should "handle a mixed list" in {
    SumList.sum(List(None, 
                     Some(-1),
                     Some(100),
                     None,
                     Some(20))) should equal (119)
  }
}
