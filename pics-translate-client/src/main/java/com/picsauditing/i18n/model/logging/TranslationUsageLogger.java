package com.picsauditing.i18n.model.logging;

import com.picsauditing.i18n.model.TranslationLookupData;

public interface TranslationUsageLogger {
    void logTranslationUsage(TranslationLookupData usage);
}
