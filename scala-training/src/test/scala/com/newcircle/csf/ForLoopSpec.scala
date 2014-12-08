package com.newcircle.tests.csf

import com.newcircle.csf.ForLoop
import com.newcircle.{MockOutput, UnitSpec}

class ForLoopSpec extends UnitSpec {
  "ForLoop" should "loop like Java" in {
    val looper = new ForLoop with MockOutput
    looper.forLoop(10, 1)
    val expected = Seq("10", "9", "8", "7", "6", "5", "4", "3", "2", "1")
    looper.messages should equal (expected)
    print( "actual" +looper.messages);
  }
}
