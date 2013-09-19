package com.picsauditing.model.events;

import com.picsauditing.messaging.Publisher;
import com.picsauditing.model.i18n.TranslationLookupData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TranslationLookupEventListenerTest {
    private TranslationLookupEventListener translationLookupEventListener;
    private TranslationLookupData translationLookupData;

    @Mock
    private Publisher translationUsagePublisher;
    @Mock
    private TranslationLookupEvent event;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        translationLookupEventListener = new TranslationLookupEventListener();
        translationLookupData = new TranslationLookupData();

        when(event.getSource()).thenReturn(translationLookupData);
        Whitebox.setInternalState(translationLookupEventListener, "translationUsagePublisher", translationUsagePublisher);
    }

    @Test
    public void testOnApplicationEvent_ProxiesToPublisher() throws Exception {
        translationLookupEventListener.onApplicationEvent(event);
        verify(translationUsagePublisher).publish(translationLookupData);
    }
}
