package com.picsauditing.i18n.service.commands;

import com.picsauditing.i18n.model.TranslationQualityRating;
import com.picsauditing.i18n.model.TranslationWrapper;
import com.sun.jersey.api.client.ClientResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class TranslateWildcardCommandTest extends TranslationCommandsCommon {
    private TranslateWildcardCommand translateWildcardCommand;

    @Before
    public void setUp() throws Exception {
        translateWildcardCommand = new TranslateWildcardCommand(TEST_KEY, TEST_LOCALE);
    }

    @Test
    public void testTranslateWildcardCommand_BadResponseGivesErrorTranslation() throws Exception {
        when(response.getStatus()).thenReturn(500);

        List<TranslationWrapper> translations = translateWildcardCommand.run();
        TranslationWrapper translation = translations.get(0);

        verifyErrorTranslation(translation);
    }

    @Test
    public void testTranslateWildcardCommand_Happy() throws Exception {
        TranslationResponse responseO = new TranslationResponse(TEST_KEY, TEST_TRANSLATION, TEST_LOCALE, TranslationQualityRating.Good);
        List<TranslationResponse> responses = new ArrayList();
        responses.add(responseO);
        responses.add(responseO);
        when(response.getEntity(String.class)).thenReturn(makeJson(responses));

        List<TranslationWrapper> translations = translateWildcardCommand.run();

        for (TranslationWrapper translation : translations) {
            assertTrue(TEST_KEY.equals(translation.getKey()));
            assertTrue(TEST_TRANSLATION.equals(translation.getTranslation()));
            assertTrue("en".equals(translation.getLocale()));
        }
    }

    @Test
    public void testTranslateWildcardCommand_ExceptionInExcecuteReturnsErrorTranslation() {
        doThrow(new RuntimeException()).when(webResource).path(anyString());

        List<TranslationWrapper> translations = translateWildcardCommand.execute();
        TranslationWrapper translation = translations.get(0);

        verifyErrorTranslation(translation);
    }

    @Test
    public void testTranslateWildcardCommand_NullResponseReturnsErrorTranslation() throws Exception {
        when(builder.get(ClientResponse.class)).thenReturn(null);

        List<TranslationWrapper> translations = translateWildcardCommand.run();

        TranslationWrapper translation = translations.get(0);
        verifyErrorTranslation(translation);
    }

}
