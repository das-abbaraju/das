package com.picsauditing.model.i18n;

public class TranslationKeyDoNothingLogger implements TranslationUsageLogger {

    @Override
    public void logTranslationUsage(TranslationLookupData usage) {
        // do nothing
    }
}
