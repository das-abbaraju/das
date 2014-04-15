package com.picsauditing.dao

import java.util.Date
import java.sql.SQLIntegrityConstraintViolationException
import com.picsauditing.i18n.model.TranslationLookupData
import scala.collection.JavaConversions._
import com.picsauditing.persistence.provider.TranslationUsageProvider
import com.picsauditing.persistence.model.MySQLProfile

class SpringConfiguredTranslationUsageDAO extends TranslationUsageDAO with SpringProvidedDataConnection

class TranslationUsageDAO (
  usageProvider: TranslationUsageProvider = new TranslationUsageProvider with MySQLProfile
) { this: SlickDatabaseAccessor =>


  def logKeyUsage(keyUsage: TranslationLookupData) = {
    try {
      db withSession( session => usageProvider.logKeyUsage(
        keyUsage.getMsgKey,
        keyUsage.getLocaleResponse,
        keyUsage.getPageName,
        keyUsage.getPageOrder,
        keyUsage.getEnvironment
      )(session))
    } catch {
      case e: SQLIntegrityConstraintViolationException => {
        // ignore this exception - since we are async, there's a chance that this will be inserted between our
        // select and this insert. This isn't a problem. The granularity on this logging is per day. So consider
        // our work done if we end up here
      }
    }
  }

  def translationsUsedSince(date: Date): java.util.Map[String, java.util.Set[String]] = {
    db withSession { implicit session => usageProvider.translationsUsedSince( new java.sql.Date(date.getTime))}
  }

}
