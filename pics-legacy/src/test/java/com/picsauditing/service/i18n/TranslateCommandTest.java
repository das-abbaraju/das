package com.picsauditing.service.i18n;

import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.model.i18n.TranslationWrapper;
import com.sun.jersey.api.client.ClientResponse;
import org.junit.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class TranslateCommandTest extends TranslationCommandsCommon {
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

}
