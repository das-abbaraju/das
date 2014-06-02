package com.picsauditing.employeeguard.controllers.employee;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.ProjectRole;
import com.picsauditing.employeeguard.forms.contractor.EmployeePhotoForm;
import com.picsauditing.employeeguard.forms.employee.EmployeeProfileEditForm;
import com.picsauditing.employeeguard.forms.employee.EmployeeProfileForm;
import com.picsauditing.employeeguard.forms.factory.EmployeeProfileEditFormBuilder;
import com.picsauditing.employeeguard.forms.factory.EmployeeProfileFormBuilder;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import com.picsauditing.employeeguard.services.ProjectRoleService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.services.factory.ProfileDocumentServiceFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmployeeActionTest extends PicsActionTest {

	public static final int ID = 45670;

	// Class under test
	private EmployeeAction employeeAction;

	private FormBuilderFactory formBuilderFactory;
	private ProfileDocumentService profileDocumentService;

	@Mock
	private AccountService accountService;
	@Mock
	private EmployeeProfileForm employeeProfileForm;
	@Mock
	private EmployeeProfileFormBuilder employeeProfileFormBuilder;
	@Mock
	private ProfileEntityService profileEntityService;
	@Mock
	private ProjectRoleService projectRoleService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeAction = new EmployeeAction();
		formBuilderFactory = new FormBuilderFactory();
		profileDocumentService = ProfileDocumentServiceFactory.getProfileDocumentService();

		super.setUp(employeeAction);

		Whitebox.setInternalState(employeeAction, "accountService", accountService);
		Whitebox.setInternalState(employeeAction, "formBuilderFactory", formBuilderFactory);
		Whitebox.setInternalState(employeeAction, "profileEntityService", profileEntityService);
		Whitebox.setInternalState(employeeAction, "profileDocumentService", profileDocumentService);
		Whitebox.setInternalState(employeeAction, "projectRoleService", projectRoleService);

		Whitebox.setInternalState(formBuilderFactory, "employeeProfileFormBuilder", employeeProfileFormBuilder);
		Whitebox.setInternalState(formBuilderFactory, "employeeProfileEditFormBuilder", new EmployeeProfileEditFormBuilder());

		when(employeeProfileFormBuilder.build(any(Profile.class))).thenReturn(employeeProfileForm);
		when(permissions.getAppUserID()).thenReturn(Identifiable.SYSTEM);
		when(projectRoleService.getRolesForProfile(any(Profile.class))).thenReturn(new ArrayList<ProjectRole>());
		when(accountService.getIdToAccountModelMap(anyCollectionOf(Integer.class))).thenReturn(new HashMap<Integer, AccountModel>());
		when(profileEntityService.find(anyInt())).thenReturn(new Profile());
		when(profileEntityService.findByAppUserId(anyInt())).thenReturn(new Profile());
	}

	@Test
	public void testShow() throws Exception {
		employeeAction.setId(Integer.toString(ID));
		assertEquals(PicsRestActionSupport.SHOW, employeeAction.show());
		assertNotNull(employeeAction.getProfile());
		assertNotNull(employeeAction.getEmployeeProfileForm());
		verify(profileEntityService).find(ID);
	}

	@Test
	public void testEditPersonalSection() throws Exception {
		employeeAction.setId(Integer.toString(ID));
		assertEquals("personal-form", employeeAction.editPersonalSection());
		assertNotNull(employeeAction.getProfile());
		assertNotNull(employeeAction.getPersonalInfo());
		verify(profileEntityService).find(ID);
	}

	@Test
	public void testUpdate() throws Exception {
		employeeAction.setId(Integer.toString(ID));
		assertEquals(PicsActionSupport.REDIRECT, employeeAction.update());
		assertNull(employeeAction.getProfile());
		assertTrue(employeeAction.getUrl().startsWith("/employee-guard/employee/profile/"));
		verify(profileEntityService).find(ID);
	}

	@Test
	public void testUpdate_Personal() throws Exception {
		employeeAction.setId(Integer.toString(ID));
		employeeAction.setPersonalInfo(new EmployeeProfileEditForm());
		when(profileEntityService.update(any(EmployeeProfileEditForm.class), anyString(), anyInt()))
				.thenReturn(new Profile());

		assertEquals(PicsActionSupport.REDIRECT, employeeAction.update());
		assertTrue(employeeAction.getUrl().startsWith("/employee-guard/employee/profile/"));
		verify(profileEntityService).update(any(EmployeeProfileEditForm.class), eq(Integer.toString(ID)),
				eq(Identifiable.SYSTEM));
	}

	@Test
	public void testUpdate_Photo() throws Exception {
		employeeAction.setId(Integer.toString(ID));
		employeeAction.setEmployeePhotoForm(new EmployeePhotoForm());
		assertEquals(PicsActionSupport.REDIRECT, employeeAction.update());
		assertTrue(employeeAction.getUrl().startsWith("/employee-guard/employee/profile/"));
		verify(profileEntityService).find(ID);
		verify(profileDocumentService).update(any(EmployeePhotoForm.class), anyString(), any(Profile.class), eq(Identifiable.SYSTEM));
	}

	@Test
	public void testBadge() throws Exception {
		assertEquals("badge", employeeAction.badge());
		assertNotNull(employeeAction.getPersonalInfo());
		verify(profileEntityService).findByAppUserId(Identifiable.SYSTEM);
	}

	@Test
	public void testSettings() throws Exception {
		assertEquals("settings", employeeAction.settings());
		assertNotNull(employeeAction.getPersonalInfo());
		verify(profileEntityService).findByAppUserId(Identifiable.SYSTEM);
	}
}
