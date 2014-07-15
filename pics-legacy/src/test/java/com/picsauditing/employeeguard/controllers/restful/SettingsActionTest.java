package com.picsauditing.employeeguard.controllers.restful;

import com.google.gson.GsonBuilder;
import com.picsauditing.PicsActionTest;
import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.models.MSettingsManager;
import com.picsauditing.employeeguard.services.SettingsService;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class SettingsActionTest extends PicsActionTest {

	@Mock
	SettingsService settingsService;
	@Mock
	private SessionInfoProvider sessionInfoProvider;

	private MSettingsManager.MSettings mSettings;

	private SettingsAction settingsAction;
	private String data = "{\"language\":{\"id\":\"es\",\"name\":\"EspaÃ±ol\"},\"dialect\":{\"id\":\"CR\",\"name\":\"Costa Rica\"}}";

	private MSettingsManager.MSettings populateSettings() {
		return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(data, MSettingsManager.MSettings.class);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		settingsAction = new SettingsAction();
		super.setUp(settingsAction);

		Whitebox.setInternalState(settingsAction, "settingsService", settingsService);
		settingsAction.setmSettings(populateSettings());

		when(permissions.getAppUserID()).thenReturn(EGTestDataUtil.APP_USER_ID);

		mSettings = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(data, MSettingsManager.MSettings.class);

		when(settingsService.extractSettings(EGTestDataUtil.APP_USER_ID)).thenReturn(mSettings);

		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
	}

	@Test
	public void testIndex() throws Exception {

		settingsAction.index();

		assertNotNull(settingsAction.getJsonString());

	}

	@Test
	public void testInsert() throws Exception {
		when(super.request.getReader()).thenReturn(new BufferedReader(new StringReader(data)));

		settingsAction.insert();

		verify(settingsService).updateSettings(any(MSettingsManager.MSettings.class), anyInt());
		verify(sessionInfoProvider).putInSession(anyString(), anyString());
	}
}
