package com.picsauditing.persistence.provider

import com.picsauditing.persistence.model.{Profile, TranslationUsageAccess}
import scala.slick.jdbc.{StaticQuery => Q}
import Q.interpolation
import scala.collection.JavaConversions._

class TranslationUsageProvider extends TranslationUsageAccess { this: Profile =>
  import simple._

  def translationsUsedSince(date: java.sql.Date)(implicit session: Session) = {
    val q = for { t <- translationUsages if t.lastUsed > date } yield t
    q.list.groupBy( _.msgKey ) map { case (k, v) => k -> setAsJavaSet(v map { _.msgLocale } toSet) }
  }

  def logKeyUsage(msgKey: String, msgLocale: String, pageName: String, pageOrder: String, environment: String)(session: Session) = {
    usageEtlSProc(msgKey, msgLocale, pageName, pageOrder, environment)(session)
  }

  private def usageEtlSProc(msgKey: String, msgLocale: String, pageName: String, pageOrder: String, environment: String)(implicit session: Session) =
    sqlu"{ call etlTranslationUsage($msgKey, $msgLocale, $pageName, $pageOrder, $environment) }".first

  /*
    What follows represents the original implimentation code prior to using the stored procedure,
    converted to slick 2 syntax. If you're interested, or need to see the original, it can be found
    in `pics-legacy/src/main/scala/com/picsauditing/dao/TranslationUsageDAO.scala`'s git history.
   */
//
//  private def getCurrentTime = new java.sql.Date(new java.util.Date().getTime)
//
//  def insertNewTranslationKeyUsage(msgKey: String, localeResponse: String, pageName: String, pageOrder: Option[String], environment: String) = {
//    implicit session: Session => {
//      val now = getCurrentTime
//      translationUsages.insert(
//        TranslationUsage(
//          None,
//          msgKey,
//          localeResponse,
//          pageName,
//          pageOrder,
//          environment,
//          Some(now),
//          Some(now),
//          None,
//          None
//        )
//      )
//    }
//  }
//
//  def updateTranslationKeyUsageTime(translationUsageId: Long) = { implicit session: Session =>
//    val now = getCurrentTime
//    (
//      for {
//        usage <- translationUsages if usage.id === translationUsageId && usage.lastUsed < now
//      } yield (usage.lastUsed, usage.synchronizedBatch, usage.synchronizedDate)
//    ) update(now, null, null)
//  }
//
//
//  private val findTranslationUsageByKeyLocalPageEnv = Compiled {
//    (msgKey: Column[String], msgLocale: Column[String], pageName: Column[String], environment: Column[String]) =>
//      for {
//        t <- translationUsages if t.msgKey === msgKey && t.msgLocale === msgLocale && t.pageName === pageName && t.environment === environment
//      } yield t
//  }

}
