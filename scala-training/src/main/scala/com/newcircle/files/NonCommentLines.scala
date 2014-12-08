package com.newcircle.files

import java.io.File

import scala.io.Source

object NonCommentLines {
  def readNonCommentLines(file: File): Array[String] = {

    val numPattern = """""^\s*""".r


    val fileData = Source.fromFile(file, "UTF-8").getLines().toArray


    var array = Array.empty[String]

    for (line <- fileData) {

      val text = numPattern findFirstIn line
      if (text == None) {
        array :+= array + line
      }


    }
    array

  }
}

