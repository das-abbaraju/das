package com.picsauditing.i18n.model.database

import java.sql.Date
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
        synchronizedDate: Option[Date]
) {
  def getId() = { id.getOrElse(null) }
  def getFirstUsed() = { firstUsed.getOrElse(null) }
  def getLastUsed() = { lastUsed.getOrElse(null) }
  def getSynchronizedBatch() = { synchronizedBatch.getOrElse(null) }
  def getSynchronizedDate() = { synchronizedDate.getOrElse(null) }
}
