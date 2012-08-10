package com.picsauditing.actions;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTest;

public class TranslationActionSupportTest extends PicsTest {

	private TranslationActionSupport translationActionSupport;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);

		translationActionSupport = new TranslationActionSupport();
	}

	@Test
	public void testSortTranslationsByLocaleDisplayNames() throws Exception {
		Map<Locale, String> sortedTranslationMap = new HashMap<Locale, String>();

		sortedTranslationMap.put(Locale.ENGLISH, "Hello");
		sortedTranslationMap.put(Locale.FRENCH, "Hello");
		sortedTranslationMap.put(Locale.GERMAN, "Hello");
		sortedTranslationMap.put(new Locale("es"), "Hello");
		sortedTranslationMap.put(new Locale("sv"), "Hello");
		sortedTranslationMap.put(new Locale("nl"), "Hello");
		sortedTranslationMap.put(new Locale("no"), "Hello");
		sortedTranslationMap.put(new Locale("pt"), "Hello");

		Map<Locale, String> supposedlySortedTranslationMap = Whitebox
				.invokeMethod(translationActionSupport,
						"sortTranslationsByLocaleDisplayNames",
						sortedTranslationMap);

		String[] expectedValues = new String[] { "Dutch", "English", "French",
				"German", "Norwegian", "Portuguese", "Spanish", "Swedish" };
		int counter = 0;
		for (Map.Entry<Locale, String> entry : supposedlySortedTranslationMap
				.entrySet()) {

			assertEquals(expectedValues[counter++], entry.getKey()
					.getDisplayLanguage());
		}
	}

	@Test
	public void testConvertStringToLocale_JustLanguage() throws Exception {
		Locale locale = Whitebox.invokeMethod(translationActionSupport,
				"convertStringToLocale", "en");

		assertEquals(Locale.ENGLISH, locale);
	}
	
	@Test
	public void testConvertStringToLocale_LanguageAndCountry() throws Exception {
		Locale locale = Whitebox.invokeMethod(translationActionSupport,
				"convertStringToLocale", "en_GB");

		assertEquals(Locale.UK, locale);
	}
	
	@Test
	public void testConvertStringToLocale_LanguageCountryAndExtraJunk() throws Exception {
		Locale locale = Whitebox.invokeMethod(translationActionSupport,
				"convertStringToLocale", "en_GB_junk_yeah");

		assertEquals(Locale.UK, locale);
	}
}
