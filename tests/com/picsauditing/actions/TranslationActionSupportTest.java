package com.picsauditing.actions;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTest;

public class TranslationActionSupportTest extends PicsTest {
	@Mock
	private AppPropertyDAO appPropertyDAO;

	private TranslationActionSupport translationActionSupport;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		MockitoAnnotations.initMocks(this);

		translationActionSupport = new TranslationActionSupport();
		Whitebox.setInternalState(translationActionSupport, "propertyDAO", appPropertyDAO);
	}

	@Test
	public void testGetSupportedLocales() throws Exception {
		Locale[] supportedLocales = TranslationActionSupport.getSupportedLocales();

		assertTrue(supportedLocales.length == 9);
		boolean found = false;
		Locale searchFor = new Locale("fi");
		for (int i = 0; i < supportedLocales.length; i++) {
			if (supportedLocales[i].equals(searchFor)) {
				found = true;
				break;
			}
		}
		assertTrue(found);
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

	@Test
	public void testIsLanguageFullySupported_FullySupportedLanguages() {
		AppProperty appProperty = new AppProperty(AppProperty.FULLY_SUPPORTED_LANGUAGES, "en,fr,de");
		when(appPropertyDAO.find(AppProperty.FULLY_SUPPORTED_LANGUAGES)).thenReturn(appProperty);
		// This is going to change periodically, need to maintain this list
		ActionContext previousContext = ActionContext.getContext();

		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put(ActionContext.LOCALE, Locale.FRENCH);
		ActionContext.setContext(new ActionContext(context));

		assertTrue(translationActionSupport.isLanguageFullySupported());

		context.put(ActionContext.LOCALE, Locale.GERMAN);
		assertTrue(translationActionSupport.isLanguageFullySupported());

		context.put(ActionContext.LOCALE, Locale.ENGLISH);
		assertTrue(translationActionSupport.isLanguageFullySupported());

		ActionContext.setContext(previousContext);
	}

	@Test
	public void testIsLanguageFullySupported_NonFullySupportedLanguages() {
		AppProperty appProperty = new AppProperty(AppProperty.FULLY_SUPPORTED_LANGUAGES, "en,fr,de");
		when(appPropertyDAO.find(AppProperty.FULLY_SUPPORTED_LANGUAGES)).thenReturn(appProperty);
		// This is going to change periodically, need to maintain this list
		ActionContext previousContext = ActionContext.getContext();

		HashMap<String, Object> context = new HashMap<String, Object>();
		context.put(ActionContext.LOCALE, new Locale("zh"));
		ActionContext.setContext(new ActionContext(context));

		assertFalse(translationActionSupport.isLanguageFullySupported());

		context.put(ActionContext.LOCALE, new Locale("af"));
		assertFalse(translationActionSupport.isLanguageFullySupported());

		context.put(ActionContext.LOCALE, new Locale("bn"));
		assertFalse(translationActionSupport.isLanguageFullySupported());

		ActionContext.setContext(previousContext);
	}
}
