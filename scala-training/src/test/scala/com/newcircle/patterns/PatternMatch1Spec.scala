package com.newcircle.tests.patterns

import com.newcircle.patterns.PatternMatch1.swapFirstTwo
import com.newcircle.UnitSpec

class PatternMatch1Spec extends UnitSpec {
  "PatternMatch1Spec.swapFirstTwo" should "handle an empty array" in {
    swapFirstTwo(Array[Int]()) should equal (Array[Int]())
  }

  it should "handle a 1-element array" in {
    swapFirstTwo(Array(100)) should equal (Array(100))
  }

  it should "handle a 2-element array" in {
    swapFirstTwo(Array("abc", "def")) should equal (Array("def", "abc"))
  }

  it should "handle a 3-element array" in {
    swapFirstTwo(
      Array("abc", "def", "ghi")
    ) should equal (
      Array("def", "abc", "ghi")
    )
  }

  it should "handle an n-element array" in {
    swapFirstTwo(
      Array("abc", "def", "ghi", "jkl")
    ) should equal (
      Array("def", "abc", "ghi", "jkl")
    )
  }
}
