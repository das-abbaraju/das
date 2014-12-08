package com.newcircle.collections

import scala.collection.mutable

object Props {

  /** Get the system properties (`System.properties`) as an immutable Scala map.
    */
  def properties: mutable.Map[String, String] = {
    import scala.collection.JavaConversions._
    val props: scala.collection.mutable.Map[String, String] = System.getProperties()
    props
  }
}
