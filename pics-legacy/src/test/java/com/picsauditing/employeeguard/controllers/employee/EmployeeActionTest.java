package com.picsauditing.employeeguard.controllers.employee;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.forms.employee.EmployeeProfileEditForm;
import com.picsauditing.employeeguard.forms.employee.EmployeeProfileForm;
import com.picsauditing.employeeguard.forms.employee.ProfilePhotoForm;
import com.picsauditing.employeeguard.forms.factory.EmployeeProfileEditFormBuilder;
import com.picsauditing.employeeguard.forms.factory.EmployeeProfileFormBuilder;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import com.picsauditing.employeeguard.services.ProfileService;
import com.picsauditing.employeeguard.services.factory.ProfileDocumentServiceFactory;
import com.picsauditing.employeeguard.services.factory.ProfileServiceFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmployeeActionTest extends PicsActionTest {
	public static final String ID = "ID";
	private EmployeeAction employeeAction;

	private FormBuilderFactory formBuilderFactory;
	private ProfileService profileService;
	private ProfileDocumentService profileDocumentService;

	@Mock
	private EmployeeProfileForm employeeProfileForm;
	@Mock
	private EmployeeProfileFormBuilder employeeProfileFormBuilder;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeAction = new EmployeeAction();
		formBuilderFactory = new FormBuilderFactory();
		profileService = ProfileServiceFactory.getProfileService();
		profileDocumentService = ProfileDocumentServiceFactory.getProfileDocumentService();

		super.setUp(employeeAction);

		Whitebox.setInternalState(employeeAction, "formBuilderFactory", formBuilderFactory);
		Whitebox.setInternalState(employeeAction, "profileService", profileService);
		Whitebox.setInternalState(employeeAction, "profileDocumentService", profileDocumentService);
		Whitebox.setInternalState(formBuilderFactory, "employeeProfileFormBuilder", employeeProfileFormBuilder);
		Whitebox.setInternalState(formBuilderFactory, "employeeProfileEditFormBuilder", new EmployeeProfileEditFormBuilder());

		when(employeeProfileFormBuilder.build(any(Profile.class))).thenReturn(employeeProfileForm);
		when(permissions.getAppUserID()).thenReturn(Identifiable.SYSTEM);
	}

	@Test
	public void testShow() throws Exception {
		employeeAction.setId(ID);
		assertEquals(PicsRestActionSupport.SHOW, employeeAction.show());
		assertNotNull(employeeAction.getProfile());
		assertNotNull(employeeAction.getEmployeeProfileForm());
		verify(profileService).findById(ID);
	}

	@Test
	public void testEditPersonalSection() throws Exception {
		employeeAction.setId(ID);
		assertEquals("personal-form", employeeAction.editPersonalSection());
		assertNotNull(employeeAction.getProfile());
		assertNotNull(employeeAction.getPersonalInfo());
		verify(profileService).findById(ID);
	}

	@Test
	public void testUpdate() throws Exception {
		employeeAction.setId(ID);
		assertEquals(PicsActionSupport.REDIRECT, employeeAction.update());
		assertNull(employeeAction.getProfile());
		assertTrue(employeeAction.getUrl().startsWith("/employee-guard/employee/profile/"));
		verify(profileService).findById(ID);
	}

	@Test
	public void testUpdate_Personal() throws Exception {
		employeeAction.setId(ID);
		employeeAction.setPersonalInfo(new EmployeeProfileEditForm());
		assertEquals(PicsActionSupport.REDIRECT, employeeAction.update());
		assertTrue(employeeAction.getUrl().startsWith("/employee-guard/employee/profile/"));
		verify(profileService).update(any(EmployeeProfileEditForm.class), eq(ID), eq(Identifiable.SYSTEM));
	}

	@Test
	public void testUpdate_Photo() throws Exception {
		employeeAction.setId(ID);
		employeeAction.setProfilePhotoForm(new ProfilePhotoForm());
		assertEquals(PicsActionSupport.REDIRECT, employeeAction.update());
		assertTrue(employeeAction.getUrl().startsWith("/employee-guard/employee/profile/"));
		verify(profileService).findById(ID);
		verify(profileDocumentService).update(any(ProfilePhotoForm.class), anyString(), any(Profile.class), eq(Identifiable.SYSTEM));
	}

	@Test
	public void testBadge() throws Exception {
		assertEquals("badge", employeeAction.badge());
		assertNotNull(employeeAction.getPersonalInfo());
		verify(profileService).findByAppUserId(Identifiable.SYSTEM);
	}

	@Test
	public void testSettings() throws Exception {
		assertEquals("settings", employeeAction.settings());
		assertNotNull(employeeAction.getPersonalInfo());
		verify(profileService).findByAppUserId(Identifiable.SYSTEM);
	}
}
