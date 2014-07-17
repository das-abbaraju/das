package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.entities.Profile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import static org.junit.Assert.*;

public class MSettingsManagerTest extends MManagersTest {


	@Before
	public void setUp() throws Exception {
		super.setUp();

	}

	@Test
	public void testCopyProfile() throws Exception {
		Profile profile = egTestDataUtil.buildFakeProfileWithSettings();
		Locale localeInSettings = profile.getSettings().getLocale();

		MModels.fetchSettingsManager().operations().copyLocale();
		MSettingsManager.MSettings mSettings = MModels.fetchSettingsManager().copyProfile(profile);

		assertNotNull(mSettings);

		Locale copiedLocale = mSettings.prepareLocale();

		assertEquals(localeInSettings.getLanguage(), copiedLocale.getLanguage());
		assertEquals(localeInSettings.getCountry(), copiedLocale.getCountry());
	}
}
