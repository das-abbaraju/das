package com.picsauditing.model.i18n;

import com.picsauditing.model.events.i18n.TranslationLookupData;

public interface TranslationUsageLogger {
    void logTranslationUsage(TranslationLookupData usage);
}
