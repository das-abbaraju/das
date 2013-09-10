package com.picsauditing.service.i18n;

import com.picsauditing.model.i18n.TranslationWrapper;

public class NonLoggingTranslationServiceAdapter extends TranslationServiceAdapter {
    private static final TranslationService INSTANCE = new NonLoggingTranslationServiceAdapter();

    private NonLoggingTranslationServiceAdapter() {
        super();
    }

    public static TranslationService getInstance() {
        return INSTANCE;
    }

    @Override
    public String getText(String key, String locale) {
        TranslationWrapper translation = getTextForKey(key, locale);
        return (translation == null) ? DEFAULT_TRANSLATION : translation.getTranslation();
    }

}
