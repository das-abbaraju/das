package com.picsauditing.dao

import javax.sql.DataSource
import org.springframework.beans.factory.annotation.Autowired
import scala.slick.driver.MySQLDriver.simple._
import com.picsauditing.models.database.{TranslationUsages, TranslationUsage}
import java.util.Date
import Database.threadLocalSession
import java.sql.SQLIntegrityConstraintViolationException
import com.picsauditing.model.events.i18n.TranslationLookupData
import java.text.SimpleDateFormat

class TranslationUsageDAO {
  @Autowired
  private val dataSource:  DataSource = null;

  private val findIdByKeyLocalePageEnv = for {
    (msgKey, msgLocale, pageName, environment)  <- Parameters[(String, String, String, String)]
    t <- TranslationUsages
      if t.msgKey === msgKey &&
        t.msgLocale === msgLocale &&
        t.pageName === pageName &&
        t.environment === environment
  } yield t.id

  def logKeyUsage(keyUsage: TranslationLookupData) = {
    Database.forDataSource(dataSource) withSession {
      findIdByKeyLocalePageEnv(keyUsage.getMsgKey, keyUsage.getLocaleResponse, keyUsage.getPageName, keyUsage.getEnvironment).firstOption match {
        case None => { insertNewTranslationKeyUsage(keyUsage) }
        case Some(id) => { updateTranslationKeyUsageTimeById(id) }
      }
    }
  }

  def updateTranslationKeyUsageTimeById(id: Long): Int = {
    val now = new Date()
    (for {
      usage <- TranslationUsages if usage.id === id && usage.lastUsed < new java.sql.Date(now.getTime)
    } yield (usage.lastUsed ~ usage.synchronizedBatch ~ usage.synchronizedDate)).update(now, null, null)
  }

  def insertNewTranslationKeyUsage(keyUsage: TranslationLookupData) = {
    try {
      TranslationUsages.insert(
        TranslationUsage(
          None,
          keyUsage.getMsgKey,
          keyUsage.getLocaleResponse,
          keyUsage.getPageName,
          keyUsage.getEnvironment,
          Some(new Date()),
          Some(new Date()),
          None,
          None
        )
      )
    } catch {
      case e: SQLIntegrityConstraintViolationException => {
        // ignore this exception - since we are async, there's a chance that this will be inserted between our
        // select and this insert. This isn't a problem. The granularity on this logging is per day. So consider
        // our work done if we end up here
      }
    }
  }

  implicit def dateToDate(x: java.util.Date): java.sql.Date = new java.sql.Date(x.getTime)
}
