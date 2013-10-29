package com.picsauditing.model.i18n

class TranslationKeyAggregateUsageLogger extends TranslationUsageLogger {

  // this is not feature toggled because it is only used by the new TranslationServiceAdapter
  def logTranslationUsage(usage: TranslationLookupData) {
    val usageLogger: TranslationUsageLogCommand  = new TranslationUsageLogCommand(usage)
    usageLogger.queue
  }

}
