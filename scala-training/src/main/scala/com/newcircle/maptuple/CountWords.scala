package com.newcircle.maptuple

import com.newcircle.util.Output

import scala.collection.mutable.{Map => MutableMap}

class WordCounter extends Output {

  def countWordsMutable(sentence: String) {

    val split = splitSentence(sentence.toLowerCase)

    var map = MutableMap.empty[String, Int]
    for (i <- split) {

      var value = map.getOrElse(i, 0) +1

      map += i -> value

    }

    for( word <- map.keySet.toArray.sorted){
      println(s"$word ${map.get(word)}")
    }






    // Use a mutable map here
  }

  def countWordsImmutable(sentence: String) {
    // Use an immutable map here
  }

  private def splitSentence(s: String): Array[String] = {
    // We'll explain this later...
    """[\s\W]+""".r.split(s)
  }
}
