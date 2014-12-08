package com.newcircle

import com.newcircle.util.Output

// Overridden version of Output, for testing.
trait MockOutput extends Output {
  var messages: Seq[String] = Seq()

  override def println(o: Any) = messages = messages :+ o.toString
}
