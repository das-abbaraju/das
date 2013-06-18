package com.picsauditing.PICS;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.PicsActionTest;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.search.Database;

public class I18nCacheTest extends PicsActionTest {

	private I18nCache i18nCache;

	private final Locale FOREIGN_LOCALE = Locale.CANADA;

    @BeforeClass
    public static void preSetup() {
        Whitebox.setInternalState(I18nCache.class, "INSTANCE", (I18nCache) null);
    }

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		super.setupMocks();
		i18nCache = I18nCache.getInstance();
	}

	@After
	public void tearDown() {
		Whitebox.setInternalState(I18nCache.class, "INSTANCE", (I18nCache) null);
	}

	@Test
	public void testDefaultTranslationIsEmptyString() {
		// The fallbacks for missing/invalid translations depend on the default translation
		// being the empty string.
		// If you need to change the default translation, please be sure you are
		// changing it for a good reason.
		assertEquals("", I18nCache.DEFAULT_TRANSLATION);
	}

	@Test
	public void testBuildInsertStatement_withSingleQuote() throws Exception {
		AppTranslation translationToTest = new AppTranslation();

		String key = "AuditQuestion.54.name";
		String locale = "en";
		String value = "Lucas' Insurance Requirements";
		String expectedValue = "Lucas'' Insurance Requirements";

		String expectedQuery = "INSERT INTO app_translation "
				+ "(msgKey, locale, msgValue, qualityRating, sourceLanguage, "
				+ "createdBy, updatedBy, creationDate, updateDate, lastUsed, " + "contentDriven, applicable) VALUES ('"
				+ key + "', '" + locale + "', " + "'" + expectedValue + "', 2, NULL, 1, 1, NOW(), NOW(), NOW(), 0, 0) "
				+ "ON DUPLICATE KEY UPDATE msgValue = '" + expectedValue + "', qualityRating = 2, "
				+ "updateDate = NOW(), contentDriven = 0, applicable = 0";

		translationToTest.setKey(key);
		translationToTest.setLocale(locale);
		translationToTest.setValue(value);
		translationToTest.setQualityRating(TranslationQualityRating.Good);

		assertEquals(expectedQuery, Whitebox.invokeMethod(i18nCache, "buildInsertStatement", translationToTest));
	}

	@Test
	public void testGetText_MissingKey_ShouldNotReturnNull() {
		String key = "INVALID_KEY";

		String value = i18nCache.getText(key, Locale.ENGLISH);

		assertNotNull(value);
	}

	@Test
	public void testGetText_MissingKey_ShouldReturnKey() {
		String key = "INVALID_KEY";

		String value = i18nCache.getText(key, Locale.ENGLISH);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_MissingKeyForeignLocale_ShouldReturnKey() {
		String key = "INVALID_KEY";

		String value = i18nCache.getText(key, FOREIGN_LOCALE);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_MissingKey_ShouldNotInsertIntoDatabase() {
		String key = "INVALID_KEY";

		i18nCache.getText(key, Locale.ENGLISH);

		assertFalse(i18nCache.hasKey(key, Locale.ENGLISH));
	}

	@Test
	public void testGetText_MissingKeyForeignLocale_ShouldNotInsertIntoDatabase() {
		String key = "INVALID_KEY";

		i18nCache.getText(key, FOREIGN_LOCALE);

		assertFalse(i18nCache.hasKey(key, FOREIGN_LOCALE));
	}

	@Test
	public void testGetText_ValidEnglishAndMissingForeign_ShouldReturnEnglish() {
		String key = "VALID_KEY";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(i18nCache.prepareKeyForCache(key), Locale.ENGLISH.toString(), "VALID_TRANSLATION");
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String englishValue = i18nCache.getText(key, Locale.ENGLISH);
		String foriegnValue = i18nCache.getText(key, FOREIGN_LOCALE);

		assertEquals(englishValue, foriegnValue);
	}

	@Test
	public void testGetText_DefaultEnglish_ShouldReturnKey() {
		String key = "VALID_KEY";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(i18nCache.prepareKeyForCache(key), Locale.ENGLISH.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(key, Locale.ENGLISH);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_DefaultEnglishAndMissingForeign_ShouldReturnKey() {
		String key = "VALID_KEY";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(i18nCache.prepareKeyForCache(key), Locale.ENGLISH.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(key, FOREIGN_LOCALE);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_ValidEnglishAndDefaultForeign_RequestNotEnglish_ShouldReturnEnglishTranslation() {
		String key = "VALID_KEY";
		String englishTranslation = "ENGLISH_TRANSLATION";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(i18nCache.prepareKeyForCache(key), Locale.ENGLISH.toString(), englishTranslation);
		cache.put(i18nCache.prepareKeyForCache(key), FOREIGN_LOCALE.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String foreignValue = i18nCache.getText(key, FOREIGN_LOCALE);
		String englishValue = i18nCache.getText(key, Locale.ENGLISH);

		assertEquals(englishTranslation, englishValue);
		assertEquals(englishTranslation, foreignValue);
	}

	@Test
	public void testGetText_DefaultEnglishAndDefaultForeign_RequestEnglish_ShouldReturnKey() {
		String key = "VALID_KEY";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(i18nCache.prepareKeyForCache(key), Locale.ENGLISH.toString(), I18nCache.DEFAULT_TRANSLATION);
		cache.put(i18nCache.prepareKeyForCache(key), FOREIGN_LOCALE.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(key, Locale.ENGLISH);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_DefaultEnglishAndDefaultForeign_RequestNotEnglish_ShouldReturnKey() {
		String key = "VALID_KEY";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(i18nCache.prepareKeyForCache(key), Locale.ENGLISH.toString(), I18nCache.DEFAULT_TRANSLATION);
		cache.put(i18nCache.prepareKeyForCache(key), FOREIGN_LOCALE.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(key, FOREIGN_LOCALE);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_DefaultEnglish_KeyEndsWithHelpText_ShouldReturnBlank() {
		String keyHelpText = "VALID_KEY.helpText";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(i18nCache.prepareKeyForCache(keyHelpText), Locale.ENGLISH.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(keyHelpText, Locale.ENGLISH);

		assertEquals("", value);
	}

	@Test
	public void testGetText_ValidEnglish_KeyEndsWithHelpText_ShouldReturnTranslation() {
		String keyHelpText = "VALID_KEY.helpText";
		String validTranslation = "VALID_TRANSLATION";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(i18nCache.prepareKeyForCache(keyHelpText), Locale.ENGLISH.toString(), validTranslation);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(keyHelpText, Locale.ENGLISH);

		assertEquals(validTranslation, value);
	}

	/**
	 * The purpose of this test is to verify that in the event a SQLException is thrown while
	 * reading data from the database, the cache in the i18n cache will not be null.
	 *
	 * This test is purposely sets the internal INSTANCE field to "null" and adds in a specific
	 * mock database, overriding the behavior of the setup() method and the super class's setupMocks()
	 * method in order to verify that a non-null cache instance is created.
	 *
	 * @throws SQLException
	 */
	@Test
	public void testFailedCacheBuildCreatesEmptyCache() throws SQLException {
		Whitebox.setInternalState(I18nCache.class, "INSTANCE", (I18nCache) null);

		Database mockDatabase = Mockito.mock(Database.class);
		when(mockDatabase.select(anyString(), anyBoolean())).thenThrow(new SQLException("SQL Error for JUnit test"));
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", mockDatabase);

		Table<String, String, String> cache = Whitebox.getInternalState(i18nCache, "cache");

		assertNotNull(cache);
		assertEquals(0, cache.size());
	}
}