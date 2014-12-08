package com.newcircle.traits

import scala.collection.mutable.ArrayBuffer

trait IntQueue {
  /** Remove and return the first element of the queue. It's okay to
    * throw an exception if the queue is empty.
    */
  def get(): Int

  /** Put an element on the end of the queue.
    *
    * @param x  the element to add
    */
  def put(x: Int)

  /** Get the entire contents of the queue as an Array.
    *
    * @return an array containing (a copy of) the queue's contents.
    */
  def contents: Array[Int]
}

/** Basic queue implementation.
  */
class BasicIntQueue extends IntQueue {
  private val buffer = ArrayBuffer[Int]()

  def get() = ???
  def put(x: Int) {
  }

  // Get the queue's entire contents as an array.
  def contents = Array.empty[Int]
}

// Implement this trait. It should behave exactly like a regular IntQueue,
// but it should square each integer element that gets put into the queue.
trait SquaringIntQueue /* ... */

// Implement this trait. It should behave exactly like a regular IntQueue,
// but it should negate each integer element that gets put into the queue.
trait NegatingIntQueue /* ... */
