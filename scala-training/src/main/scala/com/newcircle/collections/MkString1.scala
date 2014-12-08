package com.newcircle.collections

object MkString1 {

  /** Given a collection coll containing elements of type T,
    * coll.mkString(" ") == MkString1.mkString1(coll, " ")
    *  
    */
  def mkString1[T](coll: Iterable[T], sep: String = " "): String = {
    // Implement this function using reduceLeft(). Hint: You may also need
    // to use map().
    coll map {_.toString} reduceLeft(_  + sep + _)
  }
}
