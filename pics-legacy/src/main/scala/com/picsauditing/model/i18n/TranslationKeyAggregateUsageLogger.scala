package com.picsauditing.model.i18n

import com.picsauditing.i18n.model

class TranslationKeyAggregateUsageLogger extends TranslationUsageLogger with com.picsauditing.i18n.model.logging.TranslationUsageLogger {

  // this is not feature toggled because it is only used by the new TranslationServiceAdapter
  def logTranslationUsage(usage: TranslationLookupData) {
    val usageLogger: TranslationUsageLogCommand  = new TranslationUsageLogCommand(usage)
    usageLogger.queue
  }

  def logTranslationUsage(usage: model.TranslationLookupData) = {
    val picsUsage = new TranslationLookupData()
    picsUsage.setLocaleRequest(usage.getLocaleRequest)
    picsUsage.setLocaleResponse(usage.getLocaleResponse)
    picsUsage.setMsgKey(usage.getMsgKey)
    picsUsage.setEnvironment(usage.getEnvironment)
    picsUsage.setPageName(usage.getPageName)
    picsUsage.setRequestDate(usage.getRequestDate)
    picsUsage.setRetrievedByWildcard(usage.isRetrievedByWildcard)

    val usageLogger: TranslationUsageLogCommand  = new TranslationUsageLogCommand(picsUsage)
    usageLogger.queue

  }
}
