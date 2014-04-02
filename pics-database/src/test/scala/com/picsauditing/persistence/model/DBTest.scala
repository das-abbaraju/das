package com.picsauditing.persistence.model

import scala.slick.driver.H2Driver.simple._

trait DBTest[A] {
  val dbName: String
  val service: A

  lazy val dataSource = Database.forURL("jdbc:h2:mem:" + dbName, driver = "org.h2.Driver")

  def queryTest( f:(Session, A) => Unit ) = dataSource withSession { session =>
    f(session, service)
  }

}

