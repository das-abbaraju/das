package com.newcircle.tests.collections

import com.newcircle.collections.Props
import com.newcircle.UnitSpec
import scala.io.Source

class PropMapSpec extends UnitSpec {
  "PropMap.properties" should "return an immutable map" in {
    val props = Props.properties
    props shouldBe a [Map[String,String]]
    props shouldBe an [Immutable]
  }

  it should "return all the properties" in {
    import scala.collection.JavaConverters._
    val myProps = System.getProperties.asScala.toMap
    myProps should equal (Props.properties)
  }

}
