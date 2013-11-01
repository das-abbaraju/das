package com.picsauditing.service.i18n;

import com.sun.jersey.api.client.ClientResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class AllLocalesForKeyCommandTest extends TranslationCommandsCommon {
    private AllLocalesForKeyCommand allLocalesForKeyCommand;

    @Before
    public void setUp() throws Exception {
        allLocalesForKeyCommand = new AllLocalesForKeyCommand(TEST_KEY);
    }

    @Test
    public void testAllLocalesForKeyCommand_Happy() {
        when(response.getEntity(String.class)).thenReturn("{\"locales\":[\"de\",\"en\",\"es\",\"fi\",\"fr\",\"nl\",\"no\",\"pt\",\"sv\"]}");

        List<String> result = allLocalesForKeyCommand.execute();

        assertTrue(result.contains("de"));
        assertTrue(result.contains("fi"));
        assertTrue(result.contains("sv"));
        assertTrue(result.size() == 9);
    }

    @Test
    public void testAllLocalesForKeyCommand_ExceptionInExcecuteReturnsEmptyList() {
        doThrow(new RuntimeException()).when(webResource).path(anyString());

        // this will happen from the fallback
        List<String> result = allLocalesForKeyCommand.execute();

        assertTrue(result.isEmpty());
    }

    @Test
    public void testAllLocalesForKeyCommand_NullResponseReturnsEmptyList() throws Exception {
        when(builder.get(ClientResponse.class)).thenReturn(null);

        List<String> result = allLocalesForKeyCommand.execute();

        assertTrue(result.isEmpty());
    }

    @Test
    public void testAllLocalesForKeyCommand_Non200ResponseReturnsEmptyList() throws Exception {
        when(response.getStatus()).thenReturn(500);

        List<String> result = allLocalesForKeyCommand.execute();

        assertTrue(result.isEmpty());
    }

}