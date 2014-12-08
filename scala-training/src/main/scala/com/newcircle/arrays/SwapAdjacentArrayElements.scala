package com.newcircle.arrays

class SwapAdjacentArrayElements {

  // Fill in this procedure.
  def swapInPlace(array: Array[Int]): Unit = {
    for(i <- 0 until array.length - 1 by 2){
      var temp  = array(i)
      array(i) = array(i+1)
      array(i+1) = temp

    }

  }

  // Fill in this function.
  def swapToNewArray(array: Array[Int]): Unit = {

    val newArray = new Array[Int](array.size)
    Array.copy(array, 0, newArray, 0, array.size)


    for(i <- 0 until newArray.length - 1 by 2){
      var temp  = newArray(i)
      newArray(i) = newArray(i+1)
      newArray(i+1) = temp

    }

    for(i <- 0 until newArray.length - 1){
      array(i) = newArray(i)
    }





  }


}

