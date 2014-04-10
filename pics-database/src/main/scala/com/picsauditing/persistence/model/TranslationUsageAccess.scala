package com.picsauditing.persistence.model

import scala.beans.BeanProperty

case class TranslationUsage(
   id : Option[Long],
   @BeanProperty
   msgKey : String,
   @BeanProperty
   msgLocale : String,
   @BeanProperty
   pageName : String,
   pageOrder : Option[String],
   @BeanProperty
   environment : String,
   firstUsed : Option[java.util.Date],
   lastUsed : Option[java.util.Date],
   synchronizedBatch : Option[String],
   synchronizedDate : Option[java.util.Date]
)

trait TranslationUsageAccess { this: Profile =>
  import profile.simple._
  val translationUsageTableName = "translation_usage"

  class TranslationUsageSchema(tag: Tag) extends Table[TranslationUsage](tag, translationUsageTableName) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def msgKey = column[String]("msgKey")
    def msgLocale = column[String]("msgLocale")
    def pageName = column[String]("pageName")
    def pageOrder = column[String]("pageOrder")
    def environment = column[String]("environment")
    def firstUsed = column[java.util.Date]("firstUsed", O.DBType("Date"))(convertFromSQLDate)
    def lastUsed = column[java.util.Date]("lastUsed", O.DBType("Date"))(convertFromSQLDate)
    def synchronizedBatch = column[String]("synchronizedBatch")
    def synchronizedDate = column[java.util.Date]("synchronizedDate", O.DBType("Date"))(convertFromSQLDate)

    def * = (id.?, msgKey, msgLocale, pageName, pageOrder.?, environment, firstUsed.?, lastUsed.?, synchronizedBatch.?, synchronizedDate.?) <>
      (TranslationUsage.tupled, TranslationUsage.unapply)
  }

  protected[persistence] val translationUsages = TableQuery[TranslationUsageSchema]

}
