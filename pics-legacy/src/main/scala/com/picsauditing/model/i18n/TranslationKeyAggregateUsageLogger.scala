package com.picsauditing.model.i18n

import org.springframework.beans.factory.annotation.Autowired
import com.picsauditing.dao.TranslationUsageDAO
import com.picsauditing.model.events.i18n.TranslationLookupData
import com.picsauditing.toggle.FeatureToggle

class TranslationKeyAggregateUsageLogger extends TranslationUsageLogger {
  private final val featureToggleName = FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER

  @Autowired
  val usageLogger: TranslationUsageDAO = null

  @Autowired
  val featureToggleChecker: FeatureToggle = null

  def logTranslationUsage(usage: TranslationLookupData) {
    if (featureToggleChecker.isFeatureEnabled(featureToggleName))
      usageLogger.logKeyUsage(usage)
  }

}
