package com.newcircle.inheritance

abstract class Item(/* ... */) {
  def description: String
  def price: BigDecimal
}

/* Create a SimpleItem subclass whose price and description are
   constructor parameters. */
class SimpleItem(val description: String, val price: BigDecimal) extends Item

/* Create a Bundle class that contains one or more items. Its price method
   must be the sum of all the items in the bundle. It should have a suitable
   description field. It should accept an array of items as a constructor
   parameter. It should also have an addItem() method to add more items. */
class Bundle(_description: String, var items: Array[Item]) {
  val description = _description // THERE'S A BETTER WAY TO DO THIS
  def addItem(item: Item): Unit = {
    items = items :+ item

  }
  def totalItems: Int = {
    items.length
  }
  def price: BigDecimal = {
    items.map{_.price}.sum
  }
}
