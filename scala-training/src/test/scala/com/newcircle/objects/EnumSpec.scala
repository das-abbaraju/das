package com.newcircle.tests.objects

import com.newcircle.objects.PlayingCardSuit
import com.newcircle.UnitSpec

class EnumSpec extends UnitSpec {
  "PlayingCardSuit" should "should handle clubs" in {
    PlayingCardSuit.Clubs.toString should equal ("\u2663")
  }

  it should "should handle diamonds" in {
    PlayingCardSuit.Diamonds.toString should equal ("\u2666")
  }

  it should "should handle hearts" in {
    PlayingCardSuit.Hearts.toString should equal ("\u2665")
  }

  it should "should handle spades" in {
    PlayingCardSuit.Spades.toString should equal ("\u2660")
  }
}
