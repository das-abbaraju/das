package com.newcircle.tests.files

import com.newcircle.files.ReverseFile
import com.newcircle.UnitSpec
import scala.io.Source
import java.io.File

class ReverseFileSpec extends UnitSpec {
  "ReverseFile" should "reverse a file" in {
    val lines = Array("Line 1", "Line 2", "Line 3", "", "Line 5")

    withTempFile("ReverseFile", ".txt") { f =>
      writeLines(f, lines)

      val reversed = ReverseFile.reverseFile(f)
      reversed should equal(lines.reverse)
    }
  }
}
