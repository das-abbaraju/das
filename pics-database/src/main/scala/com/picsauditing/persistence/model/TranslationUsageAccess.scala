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
   firstUsed : Option[java.sql.Date],
   lastUsed : Option[java.sql.Date],
   synchronizedBatch : Option[String],
   synchronizedDate : Option[java.sql.Date]
)

trait TranslationUsageAccess { this: Profile =>
  import simple._
  val translationUsageTableName = "translation_usage"


  class TranslationUsageSchema(tag: Tag) extends Table[TranslationUsage](tag, translationUsageTableName) {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def msgKey = column[String]("msgKey")
    def msgLocale = column[String]("msgLocale")
    def pageName = column[String]("pageName")
    def pageOrder = column[String]("pageOrder")
    def environment = column[String]("environment")
    def firstUsed = column[java.sql.Date]("firstUsed", O.DBType("Date"))
    def lastUsed = column[java.sql.Date]("lastUsed", O.DBType("Date"))
    def synchronizedBatch = column[String]("synchronizedBatch")
    def synchronizedDate = column[java.sql.Date]("synchronizedDate", O.DBType("Date"))

    def * = (id.?, msgKey, msgLocale, pageName, pageOrder.?, environment, firstUsed.?, lastUsed.?, synchronizedBatch.?, synchronizedDate.?) <>
      (TranslationUsage.tupled, TranslationUsage.unapply)

    implicit val convertFromSQLDate = MappedColumnType.base[java.util.Date, java.sql.Date](
      { utilDate => new java.sql.Date(utilDate.getTime)},
      { sqlDate => new java.util.Date(sqlDate.getTime)}
    )


  }

  protected[persistence] val translationUsages = TableQuery[TranslationUsageSchema]

}
