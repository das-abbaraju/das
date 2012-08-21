package com.picsauditing.PICS;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.search.Database;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ActionContext.class})
public class I18nCacheTest {

	I18nCache i18nCache;

	private final Locale FOREIGN_LOCALE = Locale.CANADA;

	@Mock private Database databaseForTesting;
	@Mock private Permissions permissions;
	@Mock private ActionContext actionContext;
	Map<String, Object> fakeSession;


	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(ActionContext.class);
		PowerMockito.when(ActionContext.getContext()).thenReturn(actionContext);

		fakeSession = new HashMap<String, Object>();
		fakeSession.put("permissions", permissions);
		when(actionContext.getSession()).thenReturn(fakeSession);
		when(permissions.getUserId()).thenReturn(1);

		when(actionContext.getParameters()).thenReturn(fakeSession);

		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);

		i18nCache = I18nCache.getInstance();
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
	public void testGetText_MissingKeyPicsShouldReturnKey() {
		when(permissions.isPicsEmployee()).thenReturn(true);
		String key = "INVALID_KEY";

		String value = i18nCache.getText(key, Locale.ENGLISH);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_MissingKeyNotPicsShouldReturnBlank() {
		when(permissions.isPicsEmployee()).thenReturn(false);
		String key = "INVALID_KEY";

		String value = i18nCache.getText(key, Locale.ENGLISH);

		assertEquals("", value);
	}

	@Test
	public void testGetText_MissingKeyForeignLocalePicsShouldReturnKey() {
		when(permissions.isPicsEmployee()).thenReturn(true);
		String key = "INVALID_KEY";

		String value = i18nCache.getText(key, FOREIGN_LOCALE);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_MissingKeyForeignLocaleNotPicsShouldReturnBlank() {
		when(permissions.isPicsEmployee()).thenReturn(false);
		String key = "INVALID_KEY";

		String value = i18nCache.getText(key, FOREIGN_LOCALE);

		assertEquals("", value);
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

		i18nCache.getText(key, FOREIGN_LOCALE);

		assertFalse(i18nCache.hasKey(key, FOREIGN_LOCALE));
	}

	@Test
	public void testGetText_ValidEnglishAndMissingForeignShouldReturnEnglish() {
		String key = "VALID_KEY";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(key, Locale.ENGLISH.toString(), "VALID_TRANSLATION");
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String englishValue = i18nCache.getText(key, Locale.ENGLISH);
		String foriegnValue = i18nCache.getText(key, FOREIGN_LOCALE);

		assertEquals(englishValue, foriegnValue);
	}

	@Test
	public void testGetText_DefaultEnglishPicsShouldReturnKey() {
		String key = "VALID_KEY";
		when(permissions.isPicsEmployee()).thenReturn(true);
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(key, Locale.ENGLISH.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(key, Locale.ENGLISH);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_DefaultEnglishAndMissingForeignPicsShouldReturnKey() {
		String key = "VALID_KEY";
		when(permissions.isPicsEmployee()).thenReturn(true);
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(key, Locale.ENGLISH.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(key, FOREIGN_LOCALE);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_ValidEnglishAndDefaultForeignShouldReturnEnglish() {
		String key = "VALID_KEY";
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(key, Locale.ENGLISH.toString(), "VALID_TRANSLATION");
		cache.put(key, FOREIGN_LOCALE.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String foreignValue = i18nCache.getText(key, FOREIGN_LOCALE);
		String englishValue = i18nCache.getText(key, Locale.ENGLISH);

		assertEquals(englishValue, foreignValue);
	}

	@Test
	public void testGetText_DefaultEnglishAndDefaultForeignEnglishPicsShouldReturnKey() {
		String key = "VALID_KEY";
		when(permissions.isPicsEmployee()).thenReturn(true);
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(key, Locale.ENGLISH.toString(), I18nCache.DEFAULT_TRANSLATION);
		cache.put(key, FOREIGN_LOCALE.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(key, Locale.ENGLISH);

		assertEquals(key, value);
	}

	@Test
	public void testGetText_DefaultEnglishAndDefaultForeignNotEnglishPicsShouldReturnKey() {
		String key = "VALID_KEY";
		when(permissions.isPicsEmployee()).thenReturn(true);
		Table<String, String, String> cache = TreeBasedTable.create();
		cache.put(key, Locale.ENGLISH.toString(), I18nCache.DEFAULT_TRANSLATION);
		cache.put(key, FOREIGN_LOCALE.toString(), I18nCache.DEFAULT_TRANSLATION);
		Whitebox.setInternalState(i18nCache, "cache", cache);

		String value = i18nCache.getText(key, FOREIGN_LOCALE);

		assertEquals(key, value);
	}
}