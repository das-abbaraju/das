package com.picsauditing.PICS;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.search.Database;

public class I18nCacheTest {

	I18nCache i18nCache;

	private final Locale UNSUPPORTED_LOCALE = Locale.CANADA;

	@Mock
	private Database databaseForTesting;

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);

		i18nCache = I18nCache.getInstance();
	}

	@Test
	public void testBuildInsertStatement_withSingleQuote() throws Exception {
		AppTranslation translationToTest = new AppTranslation();

		String key = "AuditQuestion.54.name";
		String locale = "en";
		String value = "Lucas' Insurance Requirements";
		String expectedValue = "Lucas\\' Insurance Requirements";

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
	public void testGetText_MissingKeyShouldNotReturnNull() {
		String key = "INVALID_KEY";

		String value = i18nCache.getText(key, Locale.ENGLISH);

		assertNotNull(value);
	}

	@Test
	public void testGetText_MissingKeyShouldReturnKey() {
		String key = "INVALID_KEY";

		String value = i18nCache.getText(key, Locale.ENGLISH);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_MissingKeyForeignLocaleShouldReturnKey() {
		String key = "INVALID_KEY";

		String value = i18nCache.getText(key, UNSUPPORTED_LOCALE);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_MissingKeyShouldNotInsertIntoDatabase() {
		String key = "INVALID_KEY";

		i18nCache.getText(key, Locale.ENGLISH);

		assertFalse(i18nCache.hasKey(key, Locale.ENGLISH));
	}

	@Test
	public void testGetText_MissingKeyForeignLocaleShouldNotInsertIntoDatabase() {
		String key = "INVALID_KEY";

		i18nCache.getText(key, UNSUPPORTED_LOCALE);

		assertFalse(i18nCache.hasKey(key, UNSUPPORTED_LOCALE));
	}

	@Test
	public void testGetText_ValidEnglishAndMissingForeignShouldReturnEnglish() {
		String key = "VALID_KEY";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(key, Locale.ENGLISH.toString(), "VALID_TRANSLATION");
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String englishValue = i18nCache.getText(key, Locale.ENGLISH);
		String foriegnValue = i18nCache.getText(key, UNSUPPORTED_LOCALE);

		assertEquals(englishValue, foriegnValue);
	}

	@Test
	public void testGetText_DefaultEnglishShouldReturnKey() {
		String key = "VALID_KEY";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(key, Locale.ENGLISH.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(key, Locale.ENGLISH);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_DefaultEnglishAndMissingForeignShouldReturnKey() {
		String key = "VALID_KEY";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(key, Locale.ENGLISH.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(key, UNSUPPORTED_LOCALE);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_ValidEnglishAndDefaultForeignShouldReturnEnglish() {
		String key = "VALID_KEY";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(key, Locale.ENGLISH.toString(), "VALID_TRANSLATION");
		cache.put(key, UNSUPPORTED_LOCALE.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String foreignValue = i18nCache.getText(key, UNSUPPORTED_LOCALE);
		String englishValue = i18nCache.getText(key, Locale.ENGLISH);

		assertEquals(englishValue, foreignValue);
	}

	@Test
	public void testGetText_DefaultEnglishAndDefaultForeignEnglishShouldReturnKey() {
		String key = "VALID_KEY";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(key, Locale.ENGLISH.toString(), I18nCache.DEFAULT_TRANSLATION);
		cache.put(key, UNSUPPORTED_LOCALE.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(key, Locale.ENGLISH);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_DefaultEnglishAndDefaultForeignNotEnglishShouldReturnKey() {
		String key = "VALID_KEY";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(key, Locale.ENGLISH.toString(), I18nCache.DEFAULT_TRANSLATION);
		cache.put(key, UNSUPPORTED_LOCALE.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(key, UNSUPPORTED_LOCALE);

		assertEquals(key, value);
	}
}