package com.picsauditing.model.events;

import com.picsauditing.messaging.Publisher;
import com.picsauditing.model.i18n.TranslationLookupData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TranslationLookupEventListener implements ApplicationListener<TranslationLookupEvent> {
    private static List<TranslationLookupData> lookups  = new ArrayList<>();
    private static final int NUMBER_TO_SEND_AT_ONCE = 5000;

    @Autowired
    @Qualifier("TranslationUsagePublisher")
    private Publisher translationUsagePublisher;

    @Override
    public void onApplicationEvent(TranslationLookupEvent event) {
        TranslationLookupData lookupData = (TranslationLookupData) event.getSource();
        aggregateLookupDataAndSendIfWeHaveEnough(lookupData);
    }

    private synchronized void aggregateLookupDataAndSendIfWeHaveEnough(TranslationLookupData lookupData) {
        lookups.add(lookupData);
        if (lookups.size() > NUMBER_TO_SEND_AT_ONCE) {
            translationUsagePublisher.publish(lookups);
            lookups.clear();
        }
    }
}
