package com.newcircle.classes

/**
 * Created by dasabbaraju on 02/12/14.
 */
object Accounts {
  private var lastNumber = 0
  def newUniqueNumber() = { lastNumber += 1; lastNumber }
}