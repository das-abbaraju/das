package com.picsauditing.i18n.service;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.i18n.model.TinyTranslation;
import com.picsauditing.i18n.model.TranslationWrapper;
import com.picsauditing.i18n.model.logging.TranslationUsageLogger;
import com.picsauditing.i18n.service.cache.TranslationCache;
import com.picsauditing.i18n.service.cache.TranslationWildcardCache;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TranslationServiceAdapterTest {
    private static final String TEST_KEY = "TestKey";
    private static final String TEST_TRANSLATION_VALUE = "This is a test string";
    private static final Locale TEST_LOCALE = Locale.ENGLISH;
    private static final Locale TEST_LOCALE_OTHER = Locale.FRENCH;
    private static final TranslationWrapper TEST_TRANSLALATION = new TranslationWrapper.Builder().key(TEST_KEY).locale(TEST_LOCALE.toString()).translation(TEST_TRANSLATION_VALUE).build();
    private static final TranslationWrapper TEST_TRANSLALATION_OTHER = new TranslationWrapper.Builder().key(TEST_KEY).locale(TEST_LOCALE_OTHER.toString()).translation(TEST_TRANSLATION_VALUE).build();

    private TranslationServiceAdapter translationService;

    private Map<String, Object> context;
    private TranslationServiceProperties properties;

    @Mock
    private TranslateRestClient client;
    @Mock
    protected HttpServletRequest request;
    @Mock
    private TranslationUsageLogger usageLogger;
    @Mock
    private TranslationCache cache;
    @Mock
    private TranslationWildcardCache wildcardCache;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        properties = new TranslationServiceProperties.Builder().build();
        translationService = new TranslationServiceAdapter(properties);
        Whitebox.setInternalState(translationService, "translateRestClient", client);
        Whitebox.setInternalState(translationService, "cache", cache);
        Whitebox.setInternalState(translationService, "wildcardCache", wildcardCache);
    }

    @After
    public void tearDown() throws Exception {
        translationService.clear();
    }

    @Test
    public void testGetText_NotCachedResultsInServiceCall() throws Exception {

        translationService.getText(TEST_KEY, TEST_LOCALE.toString());

        verify(client).translationFromWebResource(TEST_KEY, TEST_LOCALE.toString());
    }

    @Test
    public void testGetText_NotCachedResultsInOneServiceCallForMultipleCalls() throws Exception {
        TranslationWrapper translation = testTranslation(TEST_TRANSLATION_VALUE);
        when(client.translationFromWebResource(TEST_KEY, TEST_LOCALE.toString())).thenReturn(TEST_TRANSLALATION);
        when(cache.get(TEST_KEY, TEST_LOCALE.toString())).thenReturn(null).thenReturn(translation);

        translationService.getText(TEST_KEY, TEST_LOCALE.toString());
        translationService.getText(TEST_KEY, TEST_LOCALE.toString());

        verify(client, times(1)).translationFromWebResource(TEST_KEY, TEST_LOCALE.toString());
    }

    @Test
    public void testGetText_CachedDoesNotResultInServiceCall() throws Exception {
        cacheTestTranslation();

        translationService.getText(TEST_KEY, TEST_LOCALE.toString());

        verify(client, never()).translationFromWebResource(TEST_KEY, TEST_LOCALE.toString());    }

    @Test
    public void testGetText_CachedKeyButLocaleCacheMissResultsInServiceCall() throws Exception {
        cacheTestTranslation();

        translationService.getText(TEST_KEY, TEST_LOCALE_OTHER.toString());

        verify(client).translationFromWebResource(TEST_KEY, TEST_LOCALE_OTHER.toString());
    }

    private void cacheTestTranslation() throws Exception {
        TranslationWrapper translation = testTranslation(TEST_TRANSLATION_VALUE);
        when(cache.get(TEST_KEY, TEST_LOCALE.toString())).thenReturn(translation);
    }

    private List<TranslationWrapper> englishTranslations() {
        List<TranslationWrapper> translations = new ArrayList<>();
        translations.add(new TranslationWrapper.Builder().key(TEST_KEY + "1").locale("en").translation("One").build());
        translations.add(new TranslationWrapper.Builder().key(TEST_KEY + "2").locale("en").translation("Two").build());
        translations.add(new TranslationWrapper.Builder().key(TEST_KEY + "3").locale("en").translation("Three").build());
        return translations;
    }

    private List<TranslationWrapper> frenchTranslations() {
        List<TranslationWrapper> frTranslations = new ArrayList<>();
        frTranslations.add(new TranslationWrapper.Builder().key(TEST_KEY + "1").locale("fr").translation("Un").build());
        frTranslations.add(new TranslationWrapper.Builder().key(TEST_KEY + "2").locale("fr").translation("Deux").build());
        frTranslations.add(new TranslationWrapper.Builder().key(TEST_KEY + "3").locale("fr").translation("Trois").build());
        return frTranslations;
    }

    @Test
    public void testGetText_WithArgs_GetProperlyFormatted() throws Exception {
        String value = "This is {0} and this is {1}";
        String expected = "This is One and this is Two";
        String[] args = new String[] {"One", "Two"};
        TranslationWrapper translation = testTranslation(value);

        when(client.translationFromWebResource(TEST_KEY, TEST_LOCALE.toString())).thenReturn(translation);

        String returned = translationService.getText(TEST_KEY, TEST_LOCALE, args);

        assertTrue(expected.equals(returned));
    }

    @Test
    public void testGetText_StringLocale_WithArgs_GetProperlyFormatted() throws Exception {
        String value = "This is {0} and this is {1}";
        String expected = "This is One and this is Two";
        String[] args = new String[] {"One", "Two"};
        TranslationWrapper translation = testTranslation(value);

        when(client.translationFromWebResource(TEST_KEY, TEST_LOCALE.toString())).thenReturn(translation);

        String returned = translationService.getText(TEST_KEY, TEST_LOCALE.toString(), args);

        assertTrue(expected.equals(returned));
    }

    private TranslationWrapper testTranslation(String value) {
        return new TranslationWrapper.Builder()
                .key(TEST_KEY)
                .locale(TEST_LOCALE.toString())
                .translation(value)
                .build();
    }

    @Test
    public void testGetText__KeyOnly_NothingInCacheCallsServiceForEachLocale() throws Exception {
        List<String> allLocalesForKey = new ArrayList() {{ add("en"); add("fr"); add("de"); }};
        when(client.allLocalesForKey(TEST_KEY)).thenReturn(allLocalesForKey);

        Map<String,String> allTranslationsForKey = translationService.getText(TEST_KEY);

        for (String locale : allLocalesForKey) {
            verify(client).translationFromWebResource(TEST_KEY, locale);
        }
    }

    @Test
    public void testGetText_KeyOnly_FirstCallCachesAllLocales() throws Exception {
        List<String> allLocalesForKey = new ArrayList() {{ add("en"); add("fr"); add("de"); }};
        when(client.allLocalesForKey(TEST_KEY)).thenReturn(allLocalesForKey);
        TranslationWrapper translation = testTranslation(TEST_TRANSLATION_VALUE);
        for (String locale : allLocalesForKey) {
            when(client.translationFromWebResource(TEST_KEY, locale)).thenReturn(
                    new TranslationWrapper.Builder().key(TEST_KEY).locale(locale).translation(locale+TEST_TRANSLATION_VALUE).build()
            );
            when(cache.get(TEST_KEY, locale.toString()))
                    .thenReturn(null)
                    .thenReturn(translation);
        }

        translationService.getText(TEST_KEY);
        translationService.getText(TEST_KEY);
        translationService.getText(TEST_KEY);

        verify(client, times(allLocalesForKey.size())).translationFromWebResource(eq(TEST_KEY), anyString());
    }

    @Test
    public void testGetText_KeyOnly_Happy() throws Exception {
        List<String> allLocalesForKey = new ArrayList() {{ add("en"); add("fr"); add("de"); }};
        when(client.allLocalesForKey(TEST_KEY)).thenReturn(allLocalesForKey);
        TranslationWrapper translation = testTranslation(TEST_TRANSLATION_VALUE);
        Table<String, String, TinyTranslation> requestedlocaleToReturnedLocaleToText = TreeBasedTable.create();
        for (String locale : allLocalesForKey) {
            when(client.translationFromWebResource(TEST_KEY, locale)).thenReturn(
                    new TranslationWrapper.Builder().key(TEST_KEY).locale(locale).translation(locale+TEST_TRANSLATION_VALUE).build()
            );
            when(cache.get(TEST_KEY, locale.toString()))
                    .thenReturn(null)
                    .thenReturn(translation);

            TinyTranslation tinyTranslation = new TinyTranslation();
            tinyTranslation.text = locale+TEST_TRANSLATION_VALUE;

            requestedlocaleToReturnedLocaleToText.put(locale, locale, tinyTranslation);
        }
        when(cache.get(TEST_KEY)).thenReturn(requestedlocaleToReturnedLocaleToText);

        Map<String, String> translations = translationService.getText(TEST_KEY);

        for (String locale : allLocalesForKey) {
            assertTrue(translations.containsValue(locale+TEST_TRANSLATION_VALUE));
        }
    }

    @Test
    public void testGetText_NoArgs_ReturnsUnformatted() throws Exception {
        String value = "This is {0} and this is {1}";
        TranslationWrapper translation = testTranslation(value);

        when(client.translationFromWebResource(TEST_KEY, TEST_LOCALE.toString())).thenReturn(translation);

        String returned = translationService.getText(TEST_KEY, TEST_LOCALE, null);

        assertTrue(value.equals(returned));
    }

    @Test
    public void testGetTextLike_Happy() throws Exception {
        List<TranslationWrapper> englishTranslations = englishTranslations();
        when(client.translationsFromWebResourceByWildcard(TEST_KEY, Locale.ENGLISH.toString())).thenReturn(englishTranslations);

        Map<String, String> returned = translationService.getTextLike(TEST_KEY, Locale.ENGLISH.toString());

        for (TranslationWrapper translation : englishTranslations) {
            assertTrue(returned.keySet().contains(translation.getKey()));
        }
    }

    @Test
    public void testSaveTranslation_ProxiesToClient() throws Exception {
        List<String> requiredLanguages = new ArrayList() {{ add("en"); add("fr"); add("de"); }};
        when(client.saveTranslation(TEST_KEY, TEST_TRANSLATION_VALUE, requiredLanguages)).thenReturn(true);

        translationService.saveTranslation(TEST_KEY, TEST_TRANSLATION_VALUE, requiredLanguages);

        verify(client).saveTranslation(TEST_KEY, TEST_TRANSLATION_VALUE, requiredLanguages);
    }

    @Test
    public void testSaveTranslation_OkRemovesKeyFromCache() throws Exception {
        cacheTestTranslation();
        List<String> requiredLanguages = new ArrayList() {{ add("en"); add("fr"); add("de"); }};
        when(client.saveTranslation(TEST_KEY, TEST_TRANSLATION_VALUE, requiredLanguages)).thenReturn(true);

        translationService.saveTranslation(TEST_KEY, TEST_TRANSLATION_VALUE, requiredLanguages);

        verify(cache).remove(TEST_KEY);
    }

    @Test
    public void testSaveTranslation_NotOkDoesNotRemoveKeyFromCache() throws Exception {
        cacheTestTranslation();
        List<String> requiredLanguages = new ArrayList() {{ add("en"); add("fr"); add("de"); }};
        when(client.saveTranslation(TEST_KEY, TEST_TRANSLATION_VALUE, requiredLanguages)).thenReturn(false);

        translationService.saveTranslation(TEST_KEY, TEST_TRANSLATION_VALUE, requiredLanguages);

        verify(cache, never()).remove(TEST_KEY);
    }

    @Test
    public void testClear_SetsLastClearedTime() throws Exception {
        Date now = new Date();
        translationService.clear();
        Date cleared = translationService.getLastCleared();
        Seconds s = Seconds.secondsBetween(new DateTime(now), new DateTime(cleared));
        assertTrue(s.getSeconds() < 10);
    }

    @Test
    public void testHasKey_EmptyKeyReturnsFalse() throws Exception {
        assertFalse(translationService.hasKey(null, TEST_LOCALE));
        assertFalse(translationService.hasKey("", TEST_LOCALE));
    }

    @Test
    public void testHasKey_KeyContainingSpaceReturnsFalse() throws Exception {
        assertFalse(translationService.hasKey("This Key Has Several Spaces", TEST_LOCALE));
    }

    @Test
    public void testHasKey_EmptyTranslationReturnsTrue() throws Exception {
        when(client.translationFromWebResource(TEST_KEY, TEST_LOCALE.toString())).thenReturn(
                new TranslationWrapper.Builder()
                        .key(TEST_KEY)
                        .locale(TEST_LOCALE.toString())
                        .translation("")
                        .build()
        );
        assertTrue(translationService.hasKey(TEST_KEY, TEST_LOCALE));
    }

    @Test
    public void testHasKeyInLocale_HasKey() throws Exception {
        when(client.translationFromWebResource(TEST_KEY, TEST_LOCALE.toString())).thenReturn(TEST_TRANSLALATION);
        assertTrue(translationService.hasKeyInLocale(TEST_KEY, TEST_LOCALE.toString()));
    }

    @Test
    public void testHasKeyInLocale_TranslationCannotBeReturned() throws Exception {
        when(client.translationFromWebResource(TEST_KEY, TEST_LOCALE.toString())).thenReturn(TEST_TRANSLALATION);
        assertFalse(translationService.hasKeyInLocale(TEST_KEY, TEST_LOCALE_OTHER.toString()));
    }

    @Test
    public void testHasKeyInLocale_DoesNotHaveLocaleButReturnsEnglish() throws Exception {
        when(client.translationFromWebResource(TEST_KEY, TEST_LOCALE_OTHER.toString())).thenReturn(TEST_TRANSLALATION);
        assertFalse(translationService.hasKeyInLocale(TEST_KEY, TEST_LOCALE_OTHER.toString()));
    }

    @Test
    public void testHasKeyInLocale_EmptyLocaleReturnsFalse() throws Exception {
        assertFalse(translationService.hasKeyInLocale(TEST_TRANSLATION_VALUE, null));
        assertFalse(translationService.hasKeyInLocale(TEST_TRANSLATION_VALUE, ""));
    }

    @Test
    public void testHasKey_HasGoodGoodKeyTranslationReturnsTrue() throws Exception {
        when(client.translationFromWebResource(TEST_KEY, TEST_LOCALE.toString())).thenReturn(TEST_TRANSLALATION);
        assertTrue(translationService.hasKey(TEST_KEY, TEST_LOCALE));
    }

    @Test
    public void testGetTranslationsForJS_ValidateArguments() throws Exception {
        Set<String> locales = new HashSet() {{ add("en"); add("fr"); add("de"); }};
        assertTrue(translationService.getTranslationsForJS(null, "execute", locales).isEmpty());
        assertTrue(translationService.getTranslationsForJS("Action", null, locales).isEmpty());
        assertTrue(translationService.getTranslationsForJS("Action", "execute", null).isEmpty());
        assertTrue(translationService.getTranslationsForJS("Action", "execute", new HashSet()).isEmpty());
    }

    @Test
    public void testGetTranslationsForJS_CheckProxyToClient() throws Exception {
        Set<String> locales = new HashSet() {{ add("en"); add("fr"); add("de"); }};
        List<Map<String, String>> returned = translationService.getTranslationsForJS("Action", "execute", locales);

        for(String locale : locales) {
            verify(client).translationsFromWebResourceByWildcard("Action.execute", locale);
            verify(client).translationsFromWebResourceByWildcard("Action."+TranslationService.ACTION_TRANSLATION_KEYWORD, locale);
        }
    }

}
