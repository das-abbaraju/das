package com.picsauditing.model.i18n;

import com.picsauditing.service.i18n.TranslateRestClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TranslationLoggerTest {
    private TranslationLogger translationLogger;

    @Mock
    private TranslateRestClient translateRestClient;
    @Mock
    private TranslationLookupData lookupData;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        translationLogger = new TranslationLogger();

        Whitebox.setInternalState(translationLogger, "translateRestClient", translateRestClient);
    }

    @Test
    public void testHandle() throws Exception {
        when(translateRestClient.updateTranslationLog(lookupData)).thenReturn(true);

        translationLogger.handle(lookupData);

        verify(translateRestClient).updateTranslationLog(lookupData);
    }

    @Test(expected=Exception.class)
    public void testHandle_UnableToUpdate() throws Exception {
        when(translateRestClient.updateTranslationLog(lookupData)).thenReturn(false);

        translationLogger.handle(lookupData);
    }

}
