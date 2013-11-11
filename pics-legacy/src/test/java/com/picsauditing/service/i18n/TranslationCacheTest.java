package com.picsauditing.service.i18n;

import com.picsauditing.model.i18n.TranslationWrapper;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TranslationCacheTest {
    private static final String TEST_KEY = "Test.Key";
    private static final String TEST_OTHER_KEY = "Test.Other.Key";
    private static final String TEST_LOCALE = "en";
    private static final String TEST_OTHER_LOCALE = "fr";
    private static final String TEST_TRANSLATION_TEXT = "Test Text and a clever one too!";

    private TranslationCache translationCache;
    private Cache cache;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        translationCache = new TranslationCache();
        // cache.get is final, so we cannot stub it in a mock. This will have to use the real cache
        cache = Whitebox.getInternalState(TranslationCache.class, "cache");

        cache.removeAll();
    }

    @Test
    public void testGet_KeyInCacheForDifferentLocaleReturnsNull() {
        translationCache.put(testTranslation(TEST_KEY, TEST_OTHER_LOCALE, TEST_TRANSLATION_TEXT));

        TranslationWrapper translation = translationCache.get(TEST_KEY, TEST_LOCALE);

        assertThat(translation, nullValue());
    }

    @Test
    public void testGet_KeyNotInCacheReturnsNull() {
        TranslationWrapper translation = translationCache.get(TEST_KEY, TEST_LOCALE);

        assertThat(translation, nullValue());
    }

    @Test
    public void testGet_HasKeyForLocaleInCache() throws Exception {
        TranslationWrapper putTranslation = testTranslation(TEST_KEY, TEST_LOCALE, TEST_TRANSLATION_TEXT);
        translationCache.put(putTranslation);

        TranslationWrapper getTranslation = translationCache.get(TEST_KEY, TEST_LOCALE);

        assertEquals(putTranslation.getKey(), getTranslation.getKey());
        assertEquals(putTranslation.getLocale(), getTranslation.getLocale());
        assertEquals(putTranslation.getTranslation(), getTranslation.getTranslation());
    }

    @Test
    public void testClear() throws Exception {
        // this isn't the right format, but works for this test
        cache.put(new Element(TEST_KEY, TEST_OTHER_LOCALE));

        translationCache.clear();

        assertTrue(cache.getSize() == 0);
    }

    @Test
    public void testGet_Remove() {
        translationCache.put(testTranslation(TEST_KEY, TEST_LOCALE, TEST_TRANSLATION_TEXT));
        translationCache.put(testTranslation(TEST_OTHER_KEY, TEST_LOCALE, TEST_TRANSLATION_TEXT));

        translationCache.remove(TEST_KEY);

        assertTrue(cache.getSize() == 1);
        assertThat(cache.get(TEST_OTHER_KEY), not(nullValue()));
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
