package com.picsauditing.util;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;

public class LocaleControllerTest {

	@Test
	public void testKoreanFallbackToEnglish() throws Exception {
		Assert.assertEquals(Locale.ENGLISH, LocaleController.getNearestSupportedLocale(Locale.KOREAN));
	}

	@Test
	public void testFrenchCanadianPermissions() throws Exception {
		Permissions permissions = EntityFactory.makePermission();
		permissions.setLocale(Locale.CANADA_FRENCH);
		LocaleController.setLocaleOfNearestSupported(permissions);
		Assert.assertEquals(Locale.CANADA_FRENCH, permissions.getLocale());
	}

	@Test
	public void testUSEnglish() throws Exception {
		Assert.assertEquals(Locale.US, LocaleController.getNearestSupportedLocale(Locale.US));
	}
}
