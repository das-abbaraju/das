package com.newcircle.objects

// THIS ISN'T RIGHT. FIX IT.


object PlayingCardSuit extends Enumeration {
  val Clubs = Value("\u2663")
  val Diamonds = Value("\u2666")
  val Hearts = Value("\u2665")
  val Spades = Value("\u2660")


  for (c <- PlayingCardSuit.values) println(c.id + ": " + c)

}


