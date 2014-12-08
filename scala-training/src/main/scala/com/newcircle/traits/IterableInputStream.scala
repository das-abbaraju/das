package com.newcircle.traits

import java.io.InputStream
import scala.collection.{Iterable, Iterator}

/** Implement a class called IterableInputStream that mixes
  * java.io.InputStream with scala.collection.Iterable.
  */
class IterableInputStream(str: InputStream) extends Iterable[Byte] /* ... */ {
  // You need to provide an implementation of this method.
  def iterator: Iterator[Byte] = {
    Array.empty[Byte].iterator // replace with actual implementation
  }

  /** If you don't provide this, the default toString() method will run your
    * iterator.
    */
  override def toString = {
    s"IterableInputStream<${System.identityHashCode(this)}>"
  }
}

class InputStreamIterator(stream: InputStream) extends Iterator[Byte] {
  def hasNext: Boolean = {
    false // replace with actual implementation
  }
  def next(): Byte = {
    0 // replace with actual implementation
  }
}

