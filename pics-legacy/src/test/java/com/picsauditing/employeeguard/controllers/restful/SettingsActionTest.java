package com.picsauditing.employeeguard.controllers.restful;

import com.picsauditing.PicsActionTest;
import com.picsauditing.employeeguard.EGTestDataUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.google.gson.GsonBuilder;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.models.MSettingsManager;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import com.picsauditing.employeeguard.services.SettingsService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.util.PhotoUtil;
import com.picsauditing.employeeguard.validators.employee.EmployeePhotoFormValidator;
import com.picsauditing.employeeguard.validators.profile.ProfileEditFormValidator;
import org.mockito.internal.util.reflection.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.GsonBuilder;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.models.MSettingsManager;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import com.picsauditing.employeeguard.services.SettingsService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.util.PhotoUtil;
import com.picsauditing.employeeguard.validators.employee.EmployeePhotoFormValidator;
import com.picsauditing.employeeguard.validators.profile.ProfileEditFormValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class SettingsActionTest extends PicsActionTest {

	@Mock
	SettingsService settingsService;

	private MSettingsManager.MSettings mSettings;

	private SettingsAction settingsAction;
	private	String data = "{\"language\":{\"id\":\"es\",\"name\":\"EspaÃ±ol\"},\"dialect\":{\"id\":\"CR\",\"name\":\"Costa Rica\"}}";

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		settingsAction = new SettingsAction();
		super.setUp(settingsAction);

		Whitebox.setInternalState(settingsAction, "settingsService", settingsService);
		settingsAction.setData(data);

		when(permissions.getAppUserID()).thenReturn(EGTestDataUtil.APP_USER_ID);

		mSettings = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(data, MSettingsManager.MSettings.class);

		when(settingsService.extractSettings(EGTestDataUtil.APP_USER_ID)).thenReturn(mSettings);


	}

	@Test
	public void testIndex() throws Exception {

		settingsAction.index();

		assertNotNull(settingsAction.getJsonString());

	}

	@Test
	public void testInsert() throws Exception {
		settingsAction.insert();

		verify(settingsService).updateSettings(any(MSettingsManager.MSettings.class),anyInt());
	}
}
