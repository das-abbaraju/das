package com.picsauditing.dao

import scala.slick.driver.MySQLDriver.simple._
import com.picsauditing.models.database.TranslationUsages
import com.picsauditing.i18n.model.database.TranslationUsage
import java.util.Date
import Database.threadLocalSession
import java.sql.SQLIntegrityConstraintViolationException
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation
import org.slf4j.{LoggerFactory, Logger}
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
import com.picsauditing.i18n.model.TranslationLookupData

class TranslationUsageDAO extends PICSDataAccess {
  private val logger: Logger = LoggerFactory.getLogger(classOf[TranslationUsageDAO])

  def usageEtlSProc(msgKey: String, msgLocale: String, pageName: String, pageOrder: String,  environment: String) = sqlu"{ call etlTranslationUsage($msgKey, $msgLocale, $pageName, $pageOrder, $environment) }".first

  private val findIdByKeyLocalePageEnv = for {
    (msgKey, msgLocale, pageName, environment)  <- Parameters[(String, String, String, String)]
    t <- TranslationUsages
      if t.msgKey === msgKey &&
        t.msgLocale === msgLocale &&
        t.pageName === pageName &&
        t.environment === environment
  } yield t.id

  def logKeyUsage(keyUsage: TranslationLookupData) = {
    // I'm leaving in the regular slick db stuff as an example or in case we decide to go back to it should the sproc
    // prove not to be faster
    // doLogKeyUsageByInsert(keyUsage)
    doLogKeyUsageBySProc(keyUsage)
  }

  def doLogKeyUsageBySProc(keyUsage: TranslationLookupData) = {
    db withSession {
      try {
        usageEtlSProc(keyUsage.getMsgKey, keyUsage.getLocaleResponse, keyUsage.getPageName, keyUsage.getPageOrder, keyUsage.getEnvironment)
      } catch {
        case e: MySQLIntegrityConstraintViolationException => {
          // we are going to do nothing because this is a race condition inside the sproc we're calling.
        }
        case e: Throwable => {
          logger.error(e.getMessage)
        }
      }
    }
  }

  def doLogKeyUsageByInsert(keyUsage: TranslationLookupData) = {
    db withSession {
      findIdByKeyLocalePageEnv(keyUsage.getMsgKey, keyUsage.getLocaleResponse, keyUsage.getPageName, keyUsage.getEnvironment).firstOption match {
        case None => { insertNewTranslationKeyUsage(keyUsage) }
        case Some(id) => { updateTranslationKeyUsageTimeById(id) }
      }
    }
  }

  def updateTranslationKeyUsageTimeById(id: Long): Int = {
    val now = new Date()
    val keyUsage = for {
        usage <- TranslationUsages
        if usage.id === id && usage.lastUsed < new java.sql.Date(now.getTime)
      } yield usage.lastUsed ~ usage.synchronizedBatch ~ usage.synchronizedDate
    keyUsage update(now, null, null)
  }

  def insertNewTranslationKeyUsage(keyUsage: TranslationLookupData) = {
    try {
      TranslationUsages.insert(
        TranslationUsage(
          None,
          keyUsage.getMsgKey,
          keyUsage.getLocaleResponse,
          keyUsage.getPageName,
          Some(keyUsage.getPageOrder),
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

  implicit private[this] def dateToDate(x: java.util.Date): java.sql.Date = new java.sql.Date(x.getTime)
}
