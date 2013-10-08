package com.picsauditing.model.i18n;

import com.picsauditing.model.events.i18n.TranslationLookupData;
import com.picsauditing.service.i18n.TranslateRestClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TranslationUsageLogSynchronizerTest {
    private TranslationUsageLogSynchronizer translationUsageLogSynchronizer;

    @Mock
    private TranslateRestClient translateRestClient;
    @Mock
    private TranslationLookupData lookupData;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        translationUsageLogSynchronizer = new TranslationUsageLogSynchronizer();

        Whitebox.setInternalState(translationUsageLogSynchronizer, "translateRestClient", translateRestClient);
    }


}
