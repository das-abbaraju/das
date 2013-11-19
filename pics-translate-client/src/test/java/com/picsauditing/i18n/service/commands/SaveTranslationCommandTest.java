package com.picsauditing.i18n.service.commands;

import com.picsauditing.i18n.model.TranslationQualityRating;
import com.sun.jersey.api.client.ClientResponse;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class SaveTranslationCommandTest extends TranslationCommandsCommon {
    private SaveTranslationCommand saveTranslationCommand;

    private List<String> requiredLanguages;

    @Before
    public void setUp() throws Exception {
        requiredLanguages = new ArrayList<>();
        requiredLanguages.add(TEST_LOCALE);

        saveTranslationCommand = new SaveTranslationCommand(TEST_KEY, TEST_TRANSLATION, requiredLanguages);

        when(builder.type(TranslateRestApiSupport.APPLICATION_JSON_VALUE)).thenReturn(builder);
        when(builder.post(any(Class.class), any())).thenReturn(response);
    }

    @Test
    public void testSaveTranslationCommand_PostsWithGoodQuality() throws Exception {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(builder.post(ClientResponse.class, captor)).thenReturn(response);

        saveTranslationCommand.run();

        verify(builder).post(any(Class.class), captor.capture());
        JSONObject keySaved = (JSONObject) JSONValue.parse(captor.getValue());
        assert(TranslationQualityRating.Good.equals(TranslationQualityRating.getRatingFromOrdinal(Integer.parseInt((String) keySaved.get("qualityRating")))));
    }

    @Test
    public void testSaveTranslationCommand_PostsForEachLocaleInRequiredLanguages() throws Exception {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(builder.post(ClientResponse.class, captor)).thenReturn(response);
        requiredLanguages.add("fr");

        saveTranslationCommand.run();

        verify(builder, times(2)).post(any(Class.class), captor.capture());
        Set<String> locales = new HashSet<>();
        for (String value : captor.getAllValues()) {
            JSONObject keySaved = (JSONObject) JSONValue.parse(value);
            locales.add((String)keySaved.get("locale"));
        }
        assertTrue(locales.contains(TEST_LOCALE));
        assertTrue(locales.contains("fr"));
    }

    @Test
    public void testSaveTranslationCommand_ExceptionInExcecuteReturnsFalse() {
        doThrow(new RuntimeException()).when(webResource).path(anyString());

        Boolean result = saveTranslationCommand.execute();

        assertFalse(result);
    }

    @Test
    public void testSaveTranslationCommand_NullResponseReturnsFalse() throws Exception {
        when(builder.post(any(Class.class), any())).thenReturn(null);

        Boolean result = saveTranslationCommand.run();

        assertFalse(result);
    }

    @Test
    public void testSaveTranslationCommand_Non200ResponseReturnsFalse() throws Exception {
        when(response.getStatus()).thenReturn(500);

        Boolean result = saveTranslationCommand.run();

        assertFalse(result);
    }

}
