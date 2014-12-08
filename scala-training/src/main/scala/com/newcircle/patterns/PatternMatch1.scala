package com.newcircle.patterns

// The Manifest bit is Scala reflection "magic", allowing type information
// that Java normally erases to be accessed at runtime. Don't worry about it
// right now. We'll see more of it shortly.
object PatternMatch1 {

  def swapFirstTwo[T: Manifest](a: Array[T]): Array[T] = {

    a match {
      case Array(first, second, rest@_*) => Array(second, first) ++ Array(rest: _*)
      case _                             => a
    }
  }


  def swapFirstTwo2[T: Manifest](a: Array[T]): Array[T] = {
    a.splitAt(2) match {
      case  (Array(first, second), Array(rest @_*)) => Array(second, first) ++ rest
      case _ => a

    }
  }

}
