package com.picsauditing.service.i18n;

import com.picsauditing.model.i18n.TranslationWrapper;

public class NonLoggingTranslationServiceAdapter extends TranslationServiceAdapter {
    private static TranslationService INSTANCE;

    private NonLoggingTranslationServiceAdapter() {
        super();
    }

    public static TranslationService getInstance() {
        TranslationService service = INSTANCE;
        if (service == null) {
            synchronized (TranslationServiceAdapter.class) {
                service = INSTANCE;
                if (service == null) {
                    registerTranslationTransformStrategy();
                    INSTANCE = new NonLoggingTranslationServiceAdapter();
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public String getText(String key, String locale) {
        TranslationWrapper translation = getTextForKey(key, locale);
        return (translation == null) ? DEFAULT_TRANSLATION : translation.getTranslation();
    }

}
