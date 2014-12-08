package com.newcircle.objects

class Point(val x: Int, val y: Int)

object Point {
  def apply(x: Int, y: Int): Point = {
    new Point(0, 0)
  }
}

