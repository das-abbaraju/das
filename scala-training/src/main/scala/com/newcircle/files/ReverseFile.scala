package com.newcircle.files

import java.io.File

import scala.io.Source

object ReverseFile {
  def reverseFile(file: File): Array[String] = {


    Source.fromFile(file, "UTF-8").getLines().toArray.reverse

  }
}

