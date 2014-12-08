package com.newcircle.tests.collections

import com.newcircle.collections.MkString1
import com.newcircle.UnitSpec

class MkStringSpec1 extends UnitSpec {
  "mkstring2" should "join a collection of Strings with a space" in {
    val coll = List("John", "Jim", "Noelle", "Jenna", "Barbara")
    MkString1.mkString1(coll, " ") should equal (coll.mkString(" "))
  }

  it should "handle a collection of Integers with commas" in {
    val coll = 1 to 30
    MkString1.mkString1(coll, ", ") should equal (coll.mkString(", "))
  }

  it should "handle a collection of Sets" in {
    val coll = List(Set(1 to 9: _*), Set(15 to 30: _*))
    MkString1.mkString1(coll, " ") should equal (coll.mkString(" "))
  }
}
