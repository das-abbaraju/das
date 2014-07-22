package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.entities.Profile;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MProfileManagerTest extends MManagersTest {


	@Before
	public void setUp() throws Exception {
		super.setUp();

	}

	@Test
	public void testCopyProfile() throws Exception {
		Profile profile = egTestDataUtil.buildFakeProfileWithSettings();
		Locale localeInSettings = profile.getSettings().getLocale();

		MModels.fetchProfileManager().operations().copyId().copyFirstName().copyLastName().copyEmail().copyPhone().copyAppUserId();
		MProfileManager.MProfile mProfile = MModels.fetchProfileManager().copyProfile(profile);

		assertNotNull(mProfile);

		assertTrue(profile.getId()==mProfile.getId());
		assertTrue(profile.getUserId() == mProfile.getAppUserId());
		assertEquals(profile.getFirstName() , mProfile.getFirstName());
		assertEquals(profile.getLastName() , mProfile.getLastName());
		assertEquals(profile.getEmail() , mProfile.getEmail());
		assertEquals(profile.getPhone() , mProfile.getPhone());
	}
}
