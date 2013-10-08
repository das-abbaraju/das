package com.picsauditing.model.events.i18n;

import com.picsauditing.model.i18n.TranslationUsageLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

public class TranslationLookupEventListener implements ApplicationListener<TranslationLookupEvent> {

    @Autowired
    private TranslationUsageLogger usageLogger;

    @Override
    public void onApplicationEvent(TranslationLookupEvent event) {
        usageLogger.logTranslationUsage((TranslationLookupData)event.getSource());
    }
}
