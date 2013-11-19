package com.picsauditing.i18n.service.commands;

import com.picsauditing.i18n.model.TranslationQualityRating;
import com.picsauditing.i18n.model.TranslationWrapper;
import com.sun.jersey.api.client.ClientResponse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class TranslateCommandTest extends TranslationCommandsCommon {
    private static final String TEST_COMMAND_KEY = "TestHystrixCommandKey";
    private TranslateCommand translateCommand;

    @Before
    public void setUp() throws Exception {
        translateCommand = new TranslateCommand(TEST_KEY, TEST_LOCALE);
    }

    @Test
    public void testTranslationFromWebResource_Happy() throws Exception {
        TranslationResponse responseO = new TranslationResponse(TEST_KEY, TEST_TRANSLATION, TEST_LOCALE, TranslationQualityRating.Good);
        when(response.getEntity(String.class)).thenReturn(makeJson(responseO, responseO));

        TranslationWrapper translation = translateCommand.run();

        assertTrue(TEST_KEY.equals(translation.getKey()));
        assertTrue(TEST_TRANSLATION.equals(translation.getTranslation()));
        assertTrue(TEST_LOCALE.equals(translation.getLocale()));
        assertTrue(TranslationQualityRating.Good.equals(translation.getQualityRating()));
    }

    @Test
    public void testTranslationFromWebResource_CreatesTranslationFromBest() throws Exception {
        TranslationResponse best = new TranslationResponse(TEST_KEY, TEST_TRANSLATION, TEST_LOCALE, TranslationQualityRating.Good);
        TranslationResponse requested = new TranslationResponse(TEST_KEY, "French " + TEST_TRANSLATION, "fr", TranslationQualityRating.Bad);
        when(response.getEntity(String.class)).thenReturn(makeJson(best, requested));

        TranslationWrapper translation = translateCommand.run();

        assertTrue(TEST_KEY.equals(translation.getKey()));
        assertTrue(TEST_TRANSLATION.equals(translation.getTranslation()));
        assertTrue(TEST_LOCALE.equals(translation.getLocale()));
        assertTrue(TranslationQualityRating.Good.equals(translation.getQualityRating()));
    }

    @Test
    public void testTranslationFromWebResource_BadResponseGivesErrorTranslation() throws Exception {
        when(client.resource(anyString())).thenReturn(webResource);
        when(response.getStatus()).thenReturn(500);

        TranslationWrapper translation = translateCommand.run();

        verifyErrorTranslation(translation);
    }

    @Test
    public void testTranslateWildcardCommand_ExceptionInExcecuteReturnsErrorTranslation() {
        doThrow(new RuntimeException()).when(webResource).path(anyString());

        TranslationWrapper translation = translateCommand.execute();

        verifyErrorTranslation(translation);
    }

    @Test
    public void testTranslateWildcardCommand_NullResponseReturnsErrorTranslation() throws Exception {
        when(builder.get(ClientResponse.class)).thenReturn(null);

        TranslationWrapper translation = translateCommand.run();

        verifyErrorTranslation(translation);
    }

    @Test
    public void testConstructorSetsCommandKey() throws Exception {
        translateCommand = new TranslateCommand(TEST_KEY, TEST_LOCALE, TEST_COMMAND_KEY);

        String keyName = translateCommand.getCommandKey().name();
        assertTrue(TEST_COMMAND_KEY.equals(keyName));
    }

    @Test
    public void testTranslationFromWebResource_OldFormatNoQualityRatingReturnsGood() throws Exception {
        TranslationResponse best = new TranslationResponse();
        best.setKey(TEST_KEY);
        best.setValue(TEST_TRANSLATION);
        best.setLocale(TEST_LOCALE);
        when(response.getEntity(String.class)).thenReturn(makeJsonOldFormat(best));

        TranslationWrapper translation = translateCommand.run();

        assertTrue(TranslationQualityRating.Good.equals(translation.getQualityRating()));
    }

}
