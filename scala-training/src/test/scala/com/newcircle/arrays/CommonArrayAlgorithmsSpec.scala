package com.newcircle.tests.arrays

import com.newcircle.arrays.CommonArrayAlgorithms
import com.newcircle.UnitSpec
import scala.util.Random

class CommonArrayAlgorithmsSpec extends UnitSpec {
  val obj = new CommonArrayAlgorithms

  "average" should "compute the average of some doubles" in {
    obj.average(Array(10.0, 11.0, 12.0)) should equal (11.0)
  }

  it should "compute the average of some more doubles" in {
    obj.average(Array(101.0, 15.0, 19.0)) should equal (45.0)
  }

  it should "handle an empty array" in {
    obj.average(Array.empty[Double]) should equal (0)
  }

  "reverseSort" should "sort an array of integers in reverse order" in {
    val array = Random.shuffle(1 to 15).toArray
    
    obj.reverseSort(array) should equal ((15 to (1, -1)).toArray)
  }
}

    
