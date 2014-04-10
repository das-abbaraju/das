package com.picsauditing.persistence.provider

import com.picsauditing.persistence.model.{TranslationUsage, Profile, TranslationUsageAccess}
import scala.slick.jdbc.{StaticQuery => Q}
import Q.interpolation

class TranslationUsageProvider extends TranslationUsageAccess { this: Profile =>
  import profile.simple._

  private def usageEtlSProc(msgKey: String, msgLocale: String, pageName: String, pageOrder: String,  environment: String) = sqlu"{ call etlTranslationUsage($msgKey, $msgLocale, $pageName, $pageOrder, $environment) }".first

  private val findTranslationByKeyLocalPageEnv = Compiled {
    (msgKey: Column[String], msgLocale: Column[String], pageName: Column[String], environment: Column[String]) =>
      for {
        t <- translationUsages if t.msgKey === msgKey && t.msgLocale === msgLocale && t.pageName === pageName && t.environment === environment
      } yield t
  }


  def insertNewTranslationKeyUsage(keyUsage: TranslationLookupData) = {
    try {
      translationUsages.insert(
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

}
