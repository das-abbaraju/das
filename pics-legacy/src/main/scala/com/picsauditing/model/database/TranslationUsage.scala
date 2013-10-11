package com.picsauditing.models.database

import java.sql.Date
import scala.slick.driver.MySQLDriver.simple._
import scala.beans.BeanProperty

case class TranslationUsage(
        id: Option[Long],
        @BeanProperty msgKey: String,
        @BeanProperty msgLocale: String,
        @BeanProperty pageName: String,
        @BeanProperty environment: String,
        firstUsed: Option[Date],
        lastUsed: Option[Date],
        synchronizedBatch: Option[String],
        synchronizedDate: Option[Date],
        ipAddress: Option[String]
) {
  def getId() = { id.getOrElse(null) }
  def getFirstUsed() = { firstUsed.getOrElse(null) }
  def getLastUsed() = { lastUsed.getOrElse(null) }
  def getSynchronizedBatch() = { synchronizedBatch.getOrElse(null) }
  def getSynchronizedDate() = { synchronizedDate.getOrElse(null) }
}

object TranslationUsages extends Table[TranslationUsage]("translation_usage") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def msgKey = column[String]("msgKey")

  def msgLocale = column[String]("msgLocale")

  def pageName = column[String]("pageName")

  def environment = column[String]("environment")

  def firstUsed = column[Date]("firstUsed", O.DBType("Date"))

  def lastUsed = column[Date]("lastUsed", O.DBType("Date"))

  def synchronizedBatch = column[String]("synchronizedBatch")

  def synchronizedDate = column[Date]("synchronizedDate", O.DBType("Date"))

  def ipAddress = column[String]("ipAddress")

  def * = id.? ~ msgKey ~ msgLocale ~ pageName ~ environment ~ firstUsed.? ~ lastUsed.? ~ synchronizedBatch.? ~ synchronizedDate.? ~ ipAddress.? <>
    (TranslationUsage, TranslationUsage.unapply _)
}
