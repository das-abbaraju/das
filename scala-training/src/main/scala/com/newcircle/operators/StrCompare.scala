package com.newcircle.operators

object StringOperators {

  implicit class MyRichString(s: String) {
    // Define the <=> operator here
    def <=>(s2: String) = {
      s.compareTo(s2)
    }
  }
}
