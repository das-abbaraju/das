package com.newcircle.tests.collections

import com.newcircle.collections.LineNumbers
import com.newcircle.{MockOutput, UnitSpec}
import scala.io.Source

class LineNumbersSpec extends UnitSpec {
  "printNumberedLines" should "print line numbers" in {
    val nums = new LineNumbers with MockOutput
    val lines = (1 to 20).map { n => s"Line $n" }
    val source = Source.fromString(lines.mkString("\n"))
    val expected = lines.zipWithIndex.map { case (s, n) => s"${n + 1}: $s" }
    nums.printNumberedLines(source)
    nums.messages should equal (expected.toSeq)
  }
}
