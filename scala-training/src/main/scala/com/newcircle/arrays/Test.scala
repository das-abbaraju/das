package com.newcircle.arrays

/**
 * Created by dasabbaraju on 01/12/14.
 */
class Test extends  App {

  def increment(start: Int, finish: Int) = {
    for (i <- start to finish) {
      println("Current value (increasing from " + start + " to " + finish + ") is " + i)
    }
  }

  object Test {
    def main(args:Array[String]) = {
      println("Program execution start.")

      println("Program execution end.")
    }
  }



}
