package com.newcircle.higher

object Values {

  def values(fun: (Int) => Int, low: Int, high: Int)  = {

    (low to high).map{ i =>(i,fun(i))}.toArray

  }

}
