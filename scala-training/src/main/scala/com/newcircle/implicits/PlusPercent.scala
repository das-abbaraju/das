package com.newcircle.implicits

object PlusPercent {
  // Create a MyRichInt class to which an Int can be implicitly converted.
  // It must provide a +% function that adds a percentage to the integer.
  // Thus, 120 +% 10 should return 132.

  implicit class MyRichInt(i: Int) {
    def +%(percent: Int): Int = {
      (i+ (i*percent)/100)
    }
  }
}
