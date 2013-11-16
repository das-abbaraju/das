package com.picsauditing.i18n.model.logging;

import com.picsauditing.i18n.model.TranslationLookupData;

public class TranslationKeyDoNothingLogger implements TranslationUsageLogger {

    @Override
    public void logTranslationUsage(TranslationLookupData usage) {
        // do nothing
    }
}
