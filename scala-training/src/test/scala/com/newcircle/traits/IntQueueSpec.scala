package com.newcircle.tests.traits

import com.newcircle.traits.{BasicIntQueue, NegatingIntQueue,
                               SquaringIntQueue}
import com.newcircle.UnitSpec

class IntQueueSpec extends UnitSpec {
  "BasicIntQueue" should "work" in {
    val queue = new BasicIntQueue

    queue.put(10)
    queue.put(20)

    queue.contents should equal (Array(10, 20))
  }

  "NegatingIntQueue" should "negate whatever's put in it" in {
    val queue = new BasicIntQueue with NegatingIntQueue
    queue.put(10)
    queue.put(20)
    queue.put(-30)

    queue.contents should equal (Array(-10, -20, 30))
  }

  "SquaringIntQueue" should "square whatever's put in it" in {
    val queue = new BasicIntQueue with SquaringIntQueue
    queue.put(10)
    queue.put(20)

    queue.contents should equal (Array(100, 400))
  }

  "Squaring then Negating positives" should "result in negative numbers" in {
    val queue = new BasicIntQueue with NegatingIntQueue with SquaringIntQueue
    queue.put(10)
    queue.put(20)

    queue.contents should equal (Array(-100, -400))
  }

  "Negating then Squaring positives" should "result in positive numbers" in {
    val queue = new BasicIntQueue with SquaringIntQueue with NegatingIntQueue
    queue.put(10)
    queue.put(20)

    queue.contents should equal (Array(100, 400))
  }
}
