package com.newcircle

import org.scalatest._
import java.io.{File, PrintWriter}

abstract class UnitSpec extends FlatSpec with Matchers {

  /** Helper: Run a block of code with a temporary file. Then, delete the
    * file.
    */
  def withTempFile[T](prefix: String, suffix: String)(code: File => T): T = {
    val f = File.createTempFile(prefix, suffix)
    try {
      f.deleteOnExit()
      code(f)
    }
    finally {
      f.delete()
    }
  }

  /** Helper: Write an array of lines to a file.
    */
  def writeLines(f: File, lines: Array[String]) {
    val out = new PrintWriter(f)
    try {
      for (line <- lines)
        out.println(line)
    }
    finally {
      out.close()
    }
  }
}

