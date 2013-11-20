package com.picsauditing.models.database

import java.sql.Date
import scala.slick.driver.MySQLDriver.simple._
import com.picsauditing.i18n.model.database.TranslationUsage

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

  def * = id.? ~ msgKey ~ msgLocale ~ pageName ~ environment ~ firstUsed.? ~ lastUsed.? ~ synchronizedBatch.? ~ synchronizedDate.? <>
    (TranslationUsage, TranslationUsage.unapply _)
}
