package com.newcircle.tests.files

import com.newcircle.files.NonCommentLines
import com.newcircle.UnitSpec
import scala.io.Source
import java.io.{File, PrintWriter}

class NonCommentLinesSpec extends UnitSpec {
  "NonCommentLines" should "skip comments in a file" in {
    val lines = Array("Line 1",
                      "Line 2",
                      "#Line 3",
                      "",
                      "#           Line 5",
                      "                      # Line 6")

    withTempFile("NonCommentLines", ".txt") { f =>
      writeLines(f, lines)
      val lines2 = NonCommentLines.readNonCommentLines(f)
      lines2 should equal(Array("Line 1", "Line 2", ""))
    }
  }
}
