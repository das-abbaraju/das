package com.picsauditing.PICS;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.picsauditing.jpa.entities.AppTranslation;
import com.picsauditing.jpa.entities.TranslationQualityRating;

public class I18nCacheTest {

	I18nCache i18nCache;

	@Before
	public void setup() {
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
				+ key + "', '" + locale + "', " + "'" + expectedValue + "', 2, NULL, 1, 1, NOW(), NOW(), NOW(), 0, 0)";

		translationToTest.setKey(key);
		translationToTest.setLocale(locale);
		translationToTest.setValue(value);
		translationToTest.setQualityRating(TranslationQualityRating.Good);

		assertEquals(expectedQuery, Whitebox.invokeMethod(i18nCache, "buildInsertStatement", translationToTest));
	}
}