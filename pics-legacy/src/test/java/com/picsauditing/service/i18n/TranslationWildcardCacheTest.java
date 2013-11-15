package com.picsauditing.service.i18n;

import com.picsauditing.model.i18n.TranslationWrapper;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TranslationWildcardCacheTest {
    private static final String TEST_KEY_WILDCARD = "Test.Key.%";
    private static final String TEST_KEY = "Test.Key.";
    private static final String TEST_LOCALE = "en";
    private static final String TEST_OTHER_LOCALE = "fr";
    private static final String TEST_TRANSLATION_TEXT = "Test Text";
    private static final int NUM_TEST_TRANSLATIONS = 10;

    private TranslationWildcardCache translationCache;
    private Cache wildcardCache;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        translationCache = new TranslationWildcardCache();
        // cache.get is final, so we cannot stub it in a mock. This will have to use the real cache
        wildcardCache = Whitebox.getInternalState(TranslationWildcardCache.class, "wildcardCache");

        wildcardCache.removeAll();
    }

    @Test
    public void testGetOutWhatWePutIn() throws Exception {
        List<TranslationWrapper> putTranslations = translations(0);
        translationCache.put(TEST_KEY_WILDCARD, TEST_LOCALE, putTranslations);

        List<TranslationWrapper> getTranslations = translationCache.get(TEST_KEY_WILDCARD, TEST_LOCALE);

        assertSame(putTranslations, getTranslations);
    }

    @Test
    public void testGetOutWhatWePutInWhenWePutTwice() throws Exception {
        List<TranslationWrapper> putTranslations = translations(0);
        translationCache.put(TEST_KEY_WILDCARD, TEST_LOCALE, putTranslations);
        translationCache.put(TEST_KEY_WILDCARD, TEST_LOCALE, putTranslations);

        List<TranslationWrapper> getTranslations = translationCache.get(TEST_KEY_WILDCARD, TEST_LOCALE);

        assertSame(putTranslations, getTranslations);
    }

    @Test
    public void test_EmptyTranslationsReturnsEmptyBack() throws Exception {
        List<TranslationWrapper> putTranslations = new ArrayList();
        translationCache.put(TEST_KEY_WILDCARD, TEST_LOCALE, putTranslations);

        List<TranslationWrapper> getTranslations = translationCache.get(TEST_KEY_WILDCARD, TEST_LOCALE);

        assertTrue(getTranslations.size() == 0);
    }

    @Test
    public void test_NullTranslationsReturnsEmptyList() throws Exception {
        translationCache.put(TEST_KEY_WILDCARD, TEST_LOCALE, null);

        List<TranslationWrapper> getTranslations = translationCache.get(TEST_KEY_WILDCARD, TEST_LOCALE);

        assertTrue(getTranslations.size() == 0);
    }

    @Test
    public void testClear() throws Exception {
        // this isn't the right format, but works for this test
        wildcardCache.put(new Element(TEST_KEY, TEST_OTHER_LOCALE));

        translationCache.clear();

        assertTrue(wildcardCache.getSize() == 0);
    }

    private void assertSame(List<TranslationWrapper> translations, List<TranslationWrapper> cachedTranslations) {
        int found = 0;
        for (TranslationWrapper translation : translations) {
            for (TranslationWrapper cachedTranslation : cachedTranslations) {
                if (translation.getKey() == cachedTranslation.getKey() &&
                        translation.getTranslation() == cachedTranslation.getTranslation() &&
                        translation.getLocale() == cachedTranslation.getLocale()) {
                    found++;
                }
            }
        }
        assertTrue(found == translations.size());
    }

    private List<TranslationWrapper> translations(int startCount) throws Exception {
        List<TranslationWrapper> translations = new ArrayList<>();
        for (int i = startCount; i < NUM_TEST_TRANSLATIONS; i++) {
            translations.add(testTranslation(TEST_KEY+i, TEST_LOCALE, i+" "+TEST_TRANSLATION_TEXT));
        }
        return translations;
    }

    private TranslationWrapper testTranslation(String key, String locale, String value) {
        return new TranslationWrapper.Builder()
                .key(key)
                .locale(locale)
                .requestedLocale(locale)
                .translation(value)
                .build();
    }

}
