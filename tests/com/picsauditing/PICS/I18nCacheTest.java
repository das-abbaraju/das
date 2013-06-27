package com.picsauditing.PICS;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.PicsActionTest;
import com.picsauditing.dao.AppTranslationDAO;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.Strings;
import com.picsauditing.util.TranslationUtil;

public class I18nCacheTest extends PicsActionTest {

	private I18nCache i18nCache;

	private final Locale FOREIGN_LOCALE = Locale.CANADA;

	private static AppTranslationDAO appTranslationDAO = Mockito.mock(AppTranslationDAO.class);
	private static FeatureToggle featureToggle = Mockito.mock(FeatureToggle.class);

	@BeforeClass
	public static void classSetUp() {
		Whitebox.setInternalState(I18nCache.class, "appTranslationDAO", appTranslationDAO);
		Whitebox.setInternalState(I18nCache.class, "featureToggle", featureToggle);
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "appTranslationDAO", (AppTranslationDAO) null);
		Whitebox.setInternalState(I18nCache.class, "featureToggle", (AppTranslationDAO) null);
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		super.setupMocks();
		Mockito.reset(appTranslationDAO);
		Mockito.reset(featureToggle);

		when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_EMPTY_STRING_IS_VALID_TRANSLATION)).thenReturn(false);

		i18nCache = I18nCache.getInstance();
	}

	@After
	public void tearDown() {
		I18nCache.getInstance().clear();
	}

	// This is a locking test
	@Test
	public void testDefaultTranslationIsEmptyString() {
		// The fallbacks for missing/invalid translations depend on the default
		// translation
		// being the empty string.
		// If you need to change the default translation, please be sure you are
		// changing it for a good reason.
		assertEquals(Strings.EMPTY_STRING, I18nCache.DEFAULT_TRANSLATION);
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
		cache.put(TranslationUtil.prepareKeyForCache(key), Locale.ENGLISH.toString(), "VALID_TRANSLATION");
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String englishValue = i18nCache.getText(key, Locale.ENGLISH);
		String foriegnValue = i18nCache.getText(key, FOREIGN_LOCALE);

		assertEquals(englishValue, foriegnValue);
	}

	@Test
	public void testGetText_DefaultEnglish_ShouldReturnKey() {
		String key = "VALID_KEY";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(TranslationUtil.prepareKeyForCache(key), Locale.ENGLISH.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(key, Locale.ENGLISH);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_DefaultEnglishAndMissingForeign_ShouldReturnKey() {
		String key = "VALID_KEY";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(TranslationUtil.prepareKeyForCache(key), Locale.ENGLISH.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(key, FOREIGN_LOCALE);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_ValidEnglishAndDefaultForeign_RequestNotEnglish_ShouldReturnEnglishTranslation() {
		String key = "VALID_KEY";
		String englishTranslation = "ENGLISH_TRANSLATION";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(TranslationUtil.prepareKeyForCache(key), Locale.ENGLISH.toString(), englishTranslation);
		cache.put(TranslationUtil.prepareKeyForCache(key), FOREIGN_LOCALE.toString(), I18nCache.DEFAULT_TRANSLATION);
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
		cache.put(TranslationUtil.prepareKeyForCache(key), Locale.ENGLISH.toString(), I18nCache.DEFAULT_TRANSLATION);
		cache.put(TranslationUtil.prepareKeyForCache(key), FOREIGN_LOCALE.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(key, Locale.ENGLISH);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_DefaultEnglishAndDefaultForeign_RequestNotEnglish_ShouldReturnKey() {
		String key = "VALID_KEY";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(TranslationUtil.prepareKeyForCache(key), Locale.ENGLISH.toString(), I18nCache.DEFAULT_TRANSLATION);
		cache.put(TranslationUtil.prepareKeyForCache(key), FOREIGN_LOCALE.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(key, FOREIGN_LOCALE);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_DefaultEnglish_KeyEndsWithHelpText_ShouldReturnBlank() {
		String keyHelpText = "VALID_KEY.helpText";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(TranslationUtil.prepareKeyForCache(keyHelpText), Locale.ENGLISH.toString(),
				I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(keyHelpText, Locale.ENGLISH);

		assertEquals("", value);
	}

	@Test
	public void testGetText_ValidEnglish_KeyEndsWithHelpText_ShouldReturnTranslation() {
		String keyHelpText = "VALID_KEY.helpText";
		String validTranslation = "VALID_TRANSLATION";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(TranslationUtil.prepareKeyForCache(keyHelpText), Locale.ENGLISH.toString(), validTranslation);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(keyHelpText, Locale.ENGLISH);

		assertEquals(validTranslation, value);
	}

	@Test
	public void testGetText_ValidEnglish_KeyEndsWithHelpText_ShouldReturnTranslation_ToggleOn() {
		when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_EMPTY_STRING_IS_VALID_TRANSLATION)).thenReturn(true);
		String keyHelpText = "VALID_KEY.helpText";
		String validTranslation = "";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(TranslationUtil.prepareKeyForCache(keyHelpText), Locale.ENGLISH.toString(), validTranslation);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(keyHelpText, Locale.ENGLISH);

		assertEquals(validTranslation, value);
	}

	/**
	 * The purpose of this test is to verify that in the event a SQLException is
	 * thrown while reading data from the database, the cache in the i18n cache
	 * will not be null.
	 * 
	 * This test is purposely sets the internal INSTANCE field to "null" and
	 * adds in a specific mock database, overriding the behavior of the setup()
	 * method and the super class's setupMocks() method in order to verify that
	 * a non-null cache instance is created.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testFailedCacheBuildCreatesEmptyCache() throws SQLException {
		setupForSqlExceptionOnLoadFromDatabase();

		I18nCache.getInstance();

		verifyI18nCacheIsNotNull();
	}

	private void setupForSqlExceptionOnLoadFromDatabase() throws SQLException {
		Whitebox.setInternalState(I18nCache.class, "INSTANCE", (I18nCache) null);
		when(appTranslationDAO.getTranslationsForI18nCache()).thenThrow(new SQLException("SQL Error for JUnit test"));
	}

	private void verifyI18nCacheIsNotNull() {
		Table<String, String, String> cache = Whitebox.getInternalState(i18nCache, "cache");
		assertNotNull(cache);
		assertEquals(0, cache.size());
	}
}