package com.picsauditing.model.i18n

import com.picsauditing.i18n.model.logging.TranslationUsageLogger
import com.picsauditing.i18n.model

class TranslationKeyAggregateUsageLogger extends TranslationUsageLogger {

  // this is not feature toggled because it is only used by the new TranslationServiceAdapter
  def logTranslationUsage(usage: model.TranslationLookupData)   {
    val usageLogger: TranslationUsageLogCommand  = new TranslationUsageLogCommand(usage)
    usageLogger.queue
  }
}
