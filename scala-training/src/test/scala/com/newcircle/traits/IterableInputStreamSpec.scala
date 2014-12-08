package com.newcircle.tests.traits

import com.newcircle.traits.IterableInputStream
import java.io.ByteArrayInputStream
import com.newcircle.UnitSpec

class IterableInputStreamSpec extends UnitSpec {
  "IterableInputStream" should "loop over an input stream" in {

    val testData = "Lorem ipsum dolor sit amet, consectetur adipisicing " + 
                   "elit, sed do eiusmod tempor incididunt ut labore et " +
                   "dolore magna aliqua. Ut enim ad minim veniam, quis " +
                   "nostrud exercitation ullamco laboris nisi ut aliquip ex " +
                   "ea commodo consequat. Duis aute irure dolor in " +
                   "reprehenderit in voluptate velit esse cillum dolore eu " +
                   "fugiat nulla pariatur. Excepteur sint occaecat cupidatat " +
                   "non proident, sunt in culpa qui officia deserunt mollit " +
                   "anim id est laborum."

    val iterable = new IterableInputStream(
      new ByteArrayInputStream(testData.getBytes)
    )

    val result = (for (b <- iterable) yield b.asInstanceOf[Char]).mkString("")
    result should equal (testData)
  }
}
