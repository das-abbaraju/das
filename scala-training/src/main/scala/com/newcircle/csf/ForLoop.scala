package com.newcircle.csf

import com.newcircle.util.Output

class ForLoop extends Output {

  /** Write a function, in Scala, that does the equivalent of this Java
    * for loop:
    * 
    *   for (int i = 10; i >= 0; i --) System.out.println(i);
    *
    * Use the `println` function inherited from `Output`.
    */


  def forLoop(upper: Int, lower: Int): Unit = {
    var a =0
    for(a <- upper to lower){
      println(a);
    }
  }
}
