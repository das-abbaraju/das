package com.picsauditing.i18n.service;

import com.picsauditing.i18n.model.database.TranslationUsage;
import com.picsauditing.i18n.service.commands.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.joda.time.DateTime;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import scala.Option;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

/*
    TranslateRestClient now basically only proxies to Hystrix Commands... so that's really all we have to test.
 */
public class TranslateRestClientTest {
    private static final String TEST_KEY = "Test.Key";
    private static final String TEST_LOCALE = "en";
    private static final String TEST_TRANSLATION = "Testing testing... is this thing on?";
    private static final String TEST_PAGENAME = "TestPage";
    private static final String TEST_ENV = "testing environment";
    private static final String TEST_BATCH_NUM = "12345";
    private static final String TEST_COMMAND_KEY = "COMMAND KEY";

    private TranslateRestClient translateRestClient;
    private List<String> languages;
    private List<TranslationUsage> lookupData;

    @Mock
    private TranslateCommand translateCommand;
    @Mock
    private TranslateWildcardCommand translateWildcardCommand;
    @Mock
    private SaveTranslationCommand saveTranslationCommand;
    @Mock
    private AllLocalesForKeyCommand allLocalesForKeyCommand;
    @Mock
    private UpdateTranslationUsageLogCommand updateTranslationUsageLogCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        translateRestClient = new TranslateRestClient();

        languages = new ArrayList<>();
        languages.add(TEST_LOCALE);

        lookupData = new ArrayList<>();
        lookupData.add(translationUsage());

        Whitebox.setInternalState(translateRestClient, "translateCommand", translateCommand);
        Whitebox.setInternalState(translateRestClient, "translateWildcardCommand", translateWildcardCommand);
        Whitebox.setInternalState(translateRestClient, "saveTranslationCommand", saveTranslationCommand);
        Whitebox.setInternalState(translateRestClient, "allLocalesForKeyCommand", allLocalesForKeyCommand);
        Whitebox.setInternalState(translateRestClient, "updateTranslationUsageLogCommand", updateTranslationUsageLogCommand);
    }

    @After
    public void tearDown() throws Exception {
        Whitebox.setInternalState(translateRestClient, "translateCommand", (TranslateCommand)null);
        Whitebox.setInternalState(translateRestClient, "translateWildcardCommand", (TranslateWildcardCommand)null);
        Whitebox.setInternalState(translateRestClient, "saveTranslationCommand", (SaveTranslationCommand)null);
        Whitebox.setInternalState(translateRestClient, "allLocalesForKeyCommand", (AllLocalesForKeyCommand)null);
        Whitebox.setInternalState(translateRestClient, "updateTranslationUsageLogCommand", (UpdateTranslationUsageLogCommand)null);
    }

    @Test
    public void testTranslateRestClient_ProxiesToTranslateCommand() throws Exception {
        translateRestClient.translationFromWebResource(TEST_KEY, TEST_LOCALE);
        verify(translateCommand).execute();
    }

    @Test
    public void testTranslateRestClient_ProxiesToTranslateWildcardCommand() throws Exception {
        translateRestClient.translationsFromWebResourceByWildcard(TEST_KEY, TEST_LOCALE);
        verify(translateWildcardCommand).execute();
    }

    @Test
    public void testTranslateRestClient_ProxiesToSaveTranslationCommand() throws Exception {
        translateRestClient.saveTranslation(TEST_KEY, TEST_TRANSLATION, languages);
        verify(saveTranslationCommand).execute();
    }

    @Test
    public void testTranslateRestClient_ProxiesToAllLocalesForKeyCommand() throws Exception {
        translateRestClient.allLocalesForKey(TEST_KEY);
        verify(allLocalesForKeyCommand).execute();
    }

    @Test
    public void testTranslateRestClient_ProxiesToUpdateTranslationUsageLogCommand() throws Exception {
        translateRestClient.updateTranslationLog(lookupData);
        verify(updateTranslationUsageLogCommand).execute();
    }

    @Test
    public void testTranslateCommand_CustomCommandKeySetsProperlyInRealyCommandObject() throws Exception {
        translateRestClient = new TranslateRestClient(TEST_COMMAND_KEY);

        TranslateCommand command = Whitebox.invokeMethod(translateRestClient, "translateCommand", TEST_KEY, TEST_LOCALE);

        assertTrue(TEST_COMMAND_KEY.equals(command.getCommandKey().name()));
    }

    private TranslationUsage translationUsage() {
        Date now = now();
        Date yesterday = yesterday();

        return new TranslationUsage(
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
    }

    private Date now() {
        return new Date();
    }

    private Date yesterday() {
        return new DateTime().minusDays(1).toDate();
    }

}
