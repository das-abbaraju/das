package com.picsauditing.employeeguard.services;

import com.google.gson.GsonBuilder;
import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.models.MSettingsManager;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.user.UserService;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class SettingsServiceTest {

	@Mock
	private ProfileEntityService profileEntityService;
	@Mock
	private SessionInfoProvider sessionInfoProvider;
	@Mock
	private UserService userService;

	private Map<String, Object> requestMap = new HashMap<>();

	private EGTestDataUtil egTestDataUtil = new EGTestDataUtil();

	private SettingsService settingsService;

	private Profile profile;
	private User user;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		profile = egTestDataUtil.buildFakeProfileWithSettings();
		user = egTestDataUtil.buildFakeUserWithSettings();
		when(profileEntityService.findByAppUserId(anyInt())).thenReturn(profile);
		when(userService.findByAppUserId(EGTestDataUtil.APP_USER_ID)).thenReturn(user);

		settingsService = new SettingsService();
		Whitebox.setInternalState(settingsService, "profileEntityService", profileEntityService);
		Whitebox.setInternalState(settingsService, "userService", userService);

		org.powermock.reflect.Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getRequest()).thenReturn(requestMap);
	}

	@Test
	public void testExtractSettings_FromProfile() throws Exception {
		MSettingsManager.MSettings mSettings = settingsService.extractSettings(EGTestDataUtil.APP_USER_ID);

		Locale localeInSettings = profile.getSettings().getLocale();
		assertNotNull(mSettings);

		Locale copiedLocale = mSettings.prepareLocale();

		assertEquals(localeInSettings.getLanguage(), copiedLocale.getLanguage());
		assertEquals(localeInSettings.getCountry(), copiedLocale.getCountry());
	}

	@Test
	public void testExtractSettings_FromUser() throws Exception {
		MSettingsManager.MSettings mSettings = settingsService.extractSettings(EGTestDataUtil.APP_USER_ID);

		Locale localeInSettings = user.getLocale();
		assertNotNull(mSettings);

		Locale copiedLocale = mSettings.prepareLocale();

		assertEquals(localeInSettings.getLanguage(), copiedLocale.getLanguage());
		assertEquals(localeInSettings.getCountry(), copiedLocale.getCountry());
	}

	@Test
	public void testUpdateSettings() throws Exception {
		String data = "{\"language\":{\"id\":\"es\",\"name\":\"EspaÃ±ol\"},\"dialect\":{\"id\":\"CR\",\"name\":\"Costa Rica\"}}";
		MSettingsManager.MSettings mSettings = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(data, MSettingsManager.MSettings.class);

		settingsService.updateSettings(mSettings, EGTestDataUtil.APP_USER_ID);
		when(profileEntityService.update(any(Profile.class), any(EntityAuditInfo.class))).thenReturn(profile);

		assertEquals(mSettings.prepareLocale(), profile.getSettings().getLocale());
	}
}
