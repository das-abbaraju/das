package com.newcircle.tests.maptuple

import com.newcircle.maptuple.WordCounter
import com.newcircle.{MockOutput, UnitSpec}

class CountWordsSpec extends UnitSpec {
  "countWordsMutable" should "handle the red sky" in {
    val sentence = "Red sky at night, sailors delight, red sky at morning, sailor take warning"
    val expected = Seq("at 2",
                       "delight 1",
                       "morning 1",
                       "night 1",
                       "red 2",
                       "sailor 1",
                       "sailors 1",
                       "sky 2",
                       "take 1",
                       "warning 1")
    val obj = new WordCounter with MockOutput

    obj.countWordsMutable(sentence)
    obj.messages should equal (expected)
  }

  it should "handle non-drinking horses" in {
    val sentence = "If you can lead it to water and force it to drink, it is not a horse."

    val expected = Seq("a 1",
                       "and 1",
                       "can 1",
                       "drink 1",
                       "force 1",
                       "horse 1",
                       "if 1",
                       "is 1",
                       "it 3",
                       "lead 1",
                       "not 1",
                       "to 2",
                       "water 1",
                       "you 1")

    val obj = new WordCounter with MockOutput

    obj.countWordsMutable(sentence)
    obj.messages should equal (expected)
  }

  "countWordsImmutable" should "handle the red sky" in {
    val sentence = "Red sky at night, sailors delight, red sky at morning, sailor take warning"
    val expected = Seq("at 2",
                       "delight 1",
                       "morning 1",
                       "night 1",
                       "red 2",
                       "sailor 1",
                       "sailors 1",
                       "sky 2",
                       "take 1",
                       "warning 1")
    val obj = new WordCounter with MockOutput

    obj.countWordsImmutable(sentence)
    obj.messages should equal (expected)
  }


}

    
