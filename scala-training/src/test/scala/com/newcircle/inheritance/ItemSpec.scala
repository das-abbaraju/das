package com.newcircle.tests.inheritance

import com.newcircle.inheritance.{Item, SimpleItem, Bundle}
import com.newcircle.UnitSpec

class ItemSpec extends UnitSpec {
  "SimpleItem" should "support price and description constructor params" in {
    val item = new SimpleItem("banana", 0.15)
    item.description should equal ("banana")
    item.price should equal (0.15)
  }

  "Bundle" should "take items on the constructor" in {
    val bundle = testBundle
    bundle.totalItems should equal (3)
  }

  it should "have a price that's the sum of its item prices" in {
    val bundle = testBundle
    bundle.price should equal (9.84)
    bundle.description should equal ("Essentials")
  }

  it should "support an addItem method" in {
    val bundle = testBundle
    val initialPrice = bundle.price
    bundle addItem (new SimpleItem("pear", 0.65))
    bundle.price should equal (initialPrice + 0.65)
  }

  private def testBundle = {
    new Bundle("Essentials", Array(new SimpleItem("banana", 0.15),
                                   new SimpleItem("apple", 0.70),
                                   new SimpleItem("bag of coffee", 8.99)))
  }
}
