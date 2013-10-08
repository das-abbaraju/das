package com.picsauditing.model.i18n

import org.springframework.beans.factory.annotation.Autowired
import com.picsauditing.dao.TranslationUsageDAO
import com.picsauditing.model.events.i18n.TranslationLookupData

class TranslationKeyAggregateUsageLogger extends TranslationUsageLogger {

  @Autowired
  val usageLogger: TranslationUsageDAO = null;

  def logTranslationUsage(usage: TranslationLookupData) {
    usageLogger.logKeyUsage(usage)
  }

}
