package com.newcircle.arrays

/**
 * Created by dasabbaraju on 01/12/14.
 */


  object HelloWorld {
    def main(args: Array[String]) {
      println("Hello, world!")
      increment(10,2)
    }

  def increment(start: Int, finish: Int) = {
    for (i <- start to finish by -2) {
      println("Current value (increasing from "+start+" to "+finish+") is "+i)
    }
  }
  }

