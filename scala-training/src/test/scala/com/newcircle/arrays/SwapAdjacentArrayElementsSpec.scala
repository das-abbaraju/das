package com.newcircle.tests.arrays

import com.newcircle.arrays.SwapAdjacentArrayElements
import com.newcircle.UnitSpec

class SwapAdjacentArrayElementsSpec extends UnitSpec {
  val swapper = new SwapAdjacentArrayElements

  "swapInPlace" should "swap an even-length array" in {
    val array = Array(1, 2, 3, 4)
    swapper.swapInPlace(array)
    array should equal (Array(2, 1, 4, 3))
  }

  "swapInPlace" should "swap an odd-length array" in {
    val array = Array(1, 2, 3, 4, 5)
    swapper.swapInPlace(array)
    array should equal (Array(2, 1, 4, 3, 5))
  }

  "swapToNewArray" should "swap an even-length array" in {
    val array = Array(1, 2, 3, 4)
    val array2 = swapper.swapToNewArray(array)
    array2 should === (Array(2, 1, 4, 3))
  }

  it should "swap an odd-length array" in {
    val array = Array(1, 2, 3, 4, 5)
    val array2 = swapper.swapToNewArray(array)
    array2 should === (Array(2, 1, 4, 3, 5))
  }

  it should "return a new array" in {
    val array = Array(1, 2, 3, 4)
    val array2 = swapper.swapToNewArray(array)
    array should not equal (array2)
  }
}

    
