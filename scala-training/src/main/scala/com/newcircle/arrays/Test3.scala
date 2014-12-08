package com.newcircle.arrays

/**
 * Created by dasabbaraju on 02/12/14.
 */


  object Main {
    def main(args: Array[String]) {
      val res = for (a <- args) yield a.toUpperCase
      println(res)
      println("Arguments: " + res.toString)

      for(i<- 10 to 1 by -1){
        println(i)
      }
    }
  }


