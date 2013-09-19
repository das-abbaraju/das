package com.picsauditing.model.events;

import com.picsauditing.messaging.Publisher;
import com.picsauditing.model.i18n.TranslationLookupData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;

public class TranslationLookupEventListener implements ApplicationListener<TranslationLookupEvent> {

    @Autowired
    @Qualifier("TranslationUsagePublisher")
    private Publisher translationUsagePublisher;

    @Override
    public void onApplicationEvent(TranslationLookupEvent event) {
        translationUsagePublisher.publish(event.getSource());
    }
}
