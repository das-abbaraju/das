package com.picsauditing.i18n.service.commands;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.picsauditing.i18n.model.database.TranslationUsage;
import com.sun.jersey.api.client.ClientResponse;
import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import scala.Option;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateTranslationUsageLogCommandTest extends TranslationCommandsCommon {
    private static final String TEST_PAGENAME = "TestPage";
    private static final String TEST_ENV = "testing environment";
    private static final String TEST_BATCH_NUM = "12345";

    private UpdateTranslationUsageLogCommand updateTranslationUsageLogCommand;

    private List<TranslationUsage> lookupData;
    private SimpleDateFormat format;

    @Mock
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        lookupData = new ArrayList<>();
        updateTranslationUsageLogCommand = new UpdateTranslationUsageLogCommand(lookupData);

        format = new SimpleDateFormat("yyyy-MM-dd");

        when(builder.type(TranslateRestApiSupport.APPLICATION_JSON_VALUE)).thenReturn(builder);
        when(builder.post(any(Class.class), any())).thenReturn(response);
    }

    @Test
    public void testPostedContent() throws Exception {
        // this is kind of a stupid test - but I'm leaving it in case we have any JIRA/Issues/Modifications that actually
        // require a posted content test

        Date now = now();
        Date yesterday = yesterday();

        TranslationUsage usage = new TranslationUsage(
                Option.empty(),
                TEST_KEY,
                TEST_LOCALE,
                TEST_PAGENAME,
                TEST_ENV,
                Option.apply(new java.sql.Date(yesterday.getTime())),
                Option.apply(new java.sql.Date(now.getTime())),
                Option.apply(TEST_BATCH_NUM),
                Option.apply(new java.sql.Date(now.getTime()))
        );
        lookupData.add(usage);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        when(builder.post(ClientResponse.class, captor)).thenReturn(response);

        updateTranslationUsageLogCommand.run();

        verify(builder).post(any(Class.class), captor.capture());
        JSONArray logged = (JSONArray) JSONValue.parse(captor.getValue());
        // [{"id":null,"msgKey":"Test.Key","pageName":"TestPage","synchronizedDate":"2013-11-01","environment":"testing environment","synchronizedBatch":"12345","msgLocale":"en","lastUsed":"2013-11-01","firstUsed":"2013-10-31"}]
        JSONObject usageSent = (JSONObject)logged.get(0);
        assertEquals(TEST_KEY, usageSent.get("msgKey"));
        assertEquals(TEST_LOCALE, usageSent.get("msgLocale"));
        assertEquals(TEST_PAGENAME, usageSent.get("pageName"));
        assertEquals(TEST_ENV, usageSent.get("environment"));
        assertEquals(format.format(yesterday), usageSent.get("firstUsed"));
        assertEquals(format.format(now), usageSent.get("lastUsed"));
    }

    private Date now() {
        return new Date();
    }

    private Date yesterday() {
        return new DateTime().minusDays(1).toDate();
    }

    @Test
    public void testUpdateTranslationUsageLogCommand_ExceptionInExcecuteReturnsFalse() {
        doThrow(new RuntimeException()).when(webResource).path(anyString());

        Boolean result = updateTranslationUsageLogCommand.execute();

        assertFalse(result);
    }

    @Test
    public void testUpdateTranslationUsageLogCommand_NullResponseReturnsFalse() throws Exception {
        when(builder.post(any(Class.class), any())).thenReturn(null);

        Boolean result = updateTranslationUsageLogCommand.run();

        assertFalse(result);
    }

    @Test
    public void testUpdateTranslationUsageLogCommand_Non200ResponseReturnsFalse() throws Exception {
        when(response.getStatus()).thenReturn(500);

        Boolean result = updateTranslationUsageLogCommand.run();

        assertFalse(result);
    }

    @Test
    public void testUpdateTranslationUsageLogCommand_JsonMapperErrorReturnsFalse() throws Exception {
        doThrow(JsonMappingException.fromUnexpectedIOE(new IOException("testing"))).when(mapper).writeValueAsString(any());
        UpdateTranslationUsageLogCommand.registerJsonMapper(mapper);

        Boolean result = updateTranslationUsageLogCommand.run();

        assertFalse(result);
        // put it back like it was
        UpdateTranslationUsageLogCommand.registerJsonMapper(new ObjectMapper());
    }
}