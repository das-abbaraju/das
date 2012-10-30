package com.picsauditing.util;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;

public class LocaleControllerTest {

	@Test
	public void testIsLocaleSupported() throws Exception {
		assertTrue(LocaleController.isLocaleSupported(Locale.ENGLISH));
		assertTrue(LocaleController.isLocaleSupported(new Locale("pt")));
	}

	@Test
	public void testKoreanFallbackToEnglish() throws Exception {
		assertEquals(Locale.ENGLISH, LocaleController.getNearestSupportedLocale(Locale.KOREAN));
	}

	@Test
	public void testFrenchCanadianPermissions() throws Exception {
		Permissions permissions = EntityFactory.makePermission();
		permissions.setLocale(Locale.CANADA_FRENCH);
		LocaleController.setLocaleOfNearestSupported(permissions);

		assertEquals(Locale.CANADA_FRENCH, permissions.getLocale());
	}

	@Test
	public void testUSEnglish() throws Exception {
		assertEquals(Locale.US, LocaleController.getNearestSupportedLocale(Locale.US));
	}

	@Test
	public void testgetValidLocale_sameLocaleIfValid() {
		assertEquals(Locale.FRENCH, LocaleController.getValidLocale(new Locale("fr")));
	}

	@Test
	public void testgetValidLocale_defaultsToEnglishIfEmpty() {
		assertEquals(Locale.ENGLISH, LocaleController.getValidLocale(new Locale("")));
	}

	@Test
	public void testSanitizeLocale_defaultsToEnglishIfInvalidLocale() {
		assertEquals(Locale.ENGLISH, LocaleController.getValidLocale(new Locale("NOT_A_VALID_LOCALE")));
	}
}
