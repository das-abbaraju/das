package com.picsauditing.persistence.model

import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.meta.MTable

trait DBTest[A] {
  val dbName: String
  val service: A

  lazy val dataSource = Database.forURL("jdbc:h2:mem:" + dbName, driver = "org.h2.Driver")

  def queryTest( f:(Session, A) => Unit ) = dataSource withSession { session =>
    f(session, service)
  }

  def createTable(tableName: String, tableFunction: => Unit )(implicit session: Session) =
    if (!MTable.getTables.list.exists( _.name.name == tableName)) tableFunction

}

