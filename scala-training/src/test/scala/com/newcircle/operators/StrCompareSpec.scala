package com.newcircle.tests.operators

import com.newcircle.operators.StringOperators.MyRichString
import com.newcircle.UnitSpec

class StrCompareSpec extends UnitSpec {
  "<=>" should """properly compare "abc" and "def" """ in {
    ("abc" <=> "def") should equal ("abc".compareTo("def"))
    ("def" <=> "abc") should equal ("def".compareTo("abc"))
  }

  it should "properly compare a string to itself" in {
    val s = "dafaolikajsdf"
    (s <=> s) should equal (0)
  }
}
