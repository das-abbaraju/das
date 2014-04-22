package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeeEmploymentForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeeForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeePersonalForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeePhotoForm;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.external.AccountService;
import com.picsauditing.employeeguard.services.external.EmailService;
import com.picsauditing.employeeguard.services.factory.EmailHashServiceFactory;
import com.picsauditing.employeeguard.services.factory.EmployeeServiceFactory;
import com.picsauditing.employeeguard.services.factory.GroupServiceFactory;
import com.picsauditing.employeeguard.services.factory.ProfileDocumentServiceFactory;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.PhotoUtil;
import com.picsauditing.employeeguard.util.PhotoUtilFactory;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.util.web.UrlBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmployeeActionTest extends PicsActionTest {

	public static final String ID = "ID";

	private EmployeeAction employeeAction;

	private EmailHashService emailHashService;
	private EmployeeService employeeService;
	private GroupService groupService;
	private FormBuilderFactory formBuilderFactory;
	private PhotoUtil photoUtil;
	private ProfileDocumentService profileDocumentService;

	@Mock
	private EmailService emailService;
	@Mock
	private UrlBuilder urlBuilder;
    @Mock
    private ProjectRoleService projectRoleService;
    @Mock
    private AccountService accountService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeAction = new EmployeeAction();

		emailHashService = EmailHashServiceFactory.getEmailHashService();
		employeeService = EmployeeServiceFactory.getEmployeeService();
		groupService = GroupServiceFactory.getGroupService();
		formBuilderFactory = new FormBuilderFactory();
		photoUtil = PhotoUtilFactory.getPhotoUtil();
		profileDocumentService = ProfileDocumentServiceFactory.getProfileDocumentService();

		super.setUp(employeeAction);

		when(permissions.getAccountId()).thenReturn(Account.PicsID);
		when(permissions.getAppUserID()).thenReturn(Identifiable.SYSTEM);
        when(projectRoleService.getRolesForProfile(any(Profile.class))).thenReturn(new ArrayList<ProjectRole>());
        when(accountService.getIdToAccountModelMap(anyCollectionOf(Integer.class))).thenReturn(new HashMap<Integer, AccountModel>());
		when(accountService.getAccountById(anyInt())).thenReturn(new AccountModel.Builder().name("Test Account").build());

        Whitebox.setInternalState(employeeAction, "accountService", accountService);
		Whitebox.setInternalState(employeeAction, "emailService", emailService);
		Whitebox.setInternalState(employeeAction, "emailHashService", emailHashService);
		Whitebox.setInternalState(employeeAction, "employeeService", employeeService);
		Whitebox.setInternalState(employeeAction, "formBuilderFactory", formBuilderFactory);
		Whitebox.setInternalState(employeeAction, "groupService", groupService);
		Whitebox.setInternalState(employeeAction, "photoUtil", photoUtil);
		Whitebox.setInternalState(employeeAction, "profileDocumentService", profileDocumentService);
        Whitebox.setInternalState(employeeAction, "projectRoleService", projectRoleService);
		Whitebox.setInternalState(employeeAction, "urlBuilder", urlBuilder);
	}

	@Test
	public void testIndex() throws Exception {
		assertEquals(PicsRestActionSupport.LIST, employeeAction.index());
		assertFalse(employeeAction.getEmployees().isEmpty());
		assertNotNull(employeeAction.getEmployeeSkillStatuses());

		verify(employeeService).getEmployeesForAccount(Account.PicsID);
	}

	@Test
	public void testIndex_Search() throws Exception {
		SearchForm searchForm = new SearchForm();
		searchForm.setSearchTerm("Test");
		employeeAction.setSearchForm(searchForm);

		assertEquals(PicsRestActionSupport.LIST, employeeAction.index());
		assertFalse(employeeAction.getEmployees().isEmpty());
		assertNotNull(employeeAction.getEmployeeSkillStatuses());

		verify(employeeService).search("Test", Account.PicsID);
	}

	@Test
	public void testShow() throws Exception {
		employeeAction.setId(ID);

		assertEquals(PicsRestActionSupport.SHOW, employeeAction.show());
		assertNotNull(employeeAction.getEmployee());
		assertNotNull(employeeAction.getSkillInfoList());

		verify(employeeService).findEmployee(ID, Account.PicsID);
	}

	@Test
	public void testCreate() throws Exception {
		assertEquals(PicsRestActionSupport.CREATE, employeeAction.create());
		assertFalse(employeeAction.getEmployeeGroups().isEmpty());

		verify(groupService).getGroupsForAccount(Account.PicsID);
	}

	@Test
	public void testEditPersonalSection() throws Exception {
		employeeAction.setId(ID);
		assertEquals("personal-form", employeeAction.editPersonalSection());
		assertNotNull(employeeAction.getEmployee());
		verify(employeeService).findEmployee(ID, Account.PicsID);
	}

	@Test
	public void testEditEmploymentSection() throws Exception {
		employeeAction.setId(ID);
		assertEquals("employment-form", employeeAction.editEmploymentSection());
		assertNotNull(employeeAction.getEmployee());
		assertFalse(employeeAction.getEmployeeGroups().isEmpty());
		verify(employeeService).findEmployee(ID, Account.PicsID);
		verify(groupService).getGroupsForAccount(Account.PicsID);
	}

	@Test
	public void testEditAssignmentSection() throws Exception {
		employeeAction.setId(ID);
		assertEquals("assignment-form", employeeAction.editAssignmentSection());
		assertNotNull(employeeAction.getEmployee());
		verify(employeeService).findEmployee(ID, Account.PicsID);
	}

	@Test
	public void testInsert() throws Exception {
		String url = "/employee-guard/contractor/employee";
		when(urlBuilder.action(anyString())).thenReturn(urlBuilder);
		when(urlBuilder.build()).thenReturn(url);

		assertEquals(PicsActionSupport.REDIRECT, employeeAction.insert());
		assertEquals(url, employeeAction.getUrl());

		verify(employeeService).save(any(EmployeeForm.class), anyString(), eq(Account.PicsID), eq(Identifiable.SYSTEM));
		verify(emailHashService).createNewHash(any(Employee.class));
		verify(emailService).sendEGWelcomeEmail(any(EmailHash.class), anyString());
	}

	@Test
	public void testInsert_AddAnother() throws Exception {
		EmployeeForm employeeForm = new EmployeeForm();
		employeeForm.setAddAnother(true);
		employeeAction.setEmployeeForm(employeeForm);

		assertEquals(PicsActionSupport.REDIRECT, employeeAction.insert());
		assertEquals("/employee-guard/contractor/employee/create", employeeAction.getUrl());

		verify(employeeService).save(any(EmployeeForm.class), anyString(), eq(Account.PicsID), eq(Identifiable.SYSTEM));
		verify(emailHashService).createNewHash(any(Employee.class));
		verify(emailService).sendEGWelcomeEmail(any(EmailHash.class), anyString());
	}

	@Test
	public void testUpdate() throws Exception {
		employeeAction.setId(ID);
		assertEquals(PicsActionSupport.REDIRECT, employeeAction.update());
		assertEquals("/employee-guard/contractor/employee/ID", employeeAction.getUrl());
	}


	@Test
	public void testUpdate_Personal() throws Exception {
		EmployeePersonalForm employeePersonalForm = new EmployeePersonalForm();
		employeeAction.setId(ID);
		employeeAction.setEmployeePersonalForm(employeePersonalForm);

		assertEquals(PicsActionSupport.REDIRECT, employeeAction.update());
		assertTrue(employeeAction.getUrl().startsWith("/employee-guard/contractor/employee/"));

		verify(employeeService).updatePersonal(employeePersonalForm, ID, Account.PicsID, Identifiable.SYSTEM);
	}

	@Test
	public void testUpdate_Employment() throws Exception {
		EmployeeEmploymentForm employeeEmploymentForm = new EmployeeEmploymentForm();
		employeeAction.setId(ID);
		employeeAction.setEmployeeEmploymentForm(employeeEmploymentForm);

		assertEquals(PicsActionSupport.REDIRECT, employeeAction.update());
		assertTrue(employeeAction.getUrl().startsWith("/employee-guard/contractor/employee/"));

		verify(employeeService).updateEmployment(employeeEmploymentForm, ID, Account.PicsID, Identifiable.SYSTEM);
	}

	@Test
	public void testUpdate_Photo() throws Exception {
		EmployeePhotoForm employeePhotoForm = new EmployeePhotoForm();
		employeeAction.setId(ID);
		employeeAction.setEmployeePhotoForm(employeePhotoForm);

		assertEquals(PicsActionSupport.REDIRECT, employeeAction.update());
		assertTrue(employeeAction.getUrl().startsWith("/employee-guard/contractor/employee/"));

		verify(employeeService).updatePhoto(eq(employeePhotoForm), anyString(), eq(ID), eq(Account.PicsID));
	}

	@Test
	public void testDelete() throws Exception {
		String url = "/employee-guard/contractor/employee";
		when(urlBuilder.action(anyString())).thenReturn(urlBuilder);
		when(urlBuilder.build()).thenReturn(url);

		assertEquals(PicsActionSupport.REDIRECT, employeeAction.delete());
		verify(permissions).getAccountId();
		verify(permissions).getAppUserID();
		verify(employeeService).delete(anyString(), anyInt(), anyInt());
		assertEquals(url, employeeAction.getUrl());
	}

	@Test
	public void testPhoto() throws Exception {
		/*
		if (NumberUtils.toInt(id) > 0) {
			employee = employeeService.findEmployee(id, permissions.getAccountId());
			File employeePhoto = photoUtil.getPhotoForEmployee(employee, permissions.getAccountId(), getFtpDir());

			if (employeePhoto != null && employeePhoto.exists()) {
				inputStream = new FileInputStream(employeePhoto);
			} else if (employee.getProfile() != null) {
				File profilePhoto = photoUtil.getPhotoForProfile(profileDocumentService.getPhotoDocumentFromProfile(employee.getProfile()), getFtpDir());
				if (profilePhoto != null && profilePhoto.exists()) {
					inputStream = new FileInputStream(profilePhoto);
				}
			}
		}

		if (inputStream == null) {
			inputStream = new FileInputStream(photoUtil.getDefaultPhoto(getFtpDir()));
		}

		return "photo";
		 */
		assertEquals("photo", employeeAction.photo());
		assertNotNull(employeeAction.getInputStream());
		verify(photoUtil).getDefaultPhotoStream(anyString());
	}

	@Test
	public void testPhoto_EmployeePhotoExists() throws Exception {
		String id = "1";
		employeeAction.setId(id);

		assertEquals("photo", employeeAction.photo());
		assertNotNull(employeeAction.getInputStream());
		verify(employeeService).findEmployee(id, Account.PicsID);
		verify(photoUtil).getPhotoStreamForEmployee(any(Employee.class), eq(Account.PicsID), anyString());
	}

	@Test
	public void testPhoto_OnlyProfilePhotoExists() throws Exception {
		String id = "1";
		employeeAction.setId(id);

		Employee employee = new Employee();
		employee.setProfile(new Profile());

		when(employeeService.findEmployee(id, Account.PicsID)).thenReturn(employee);
		when(photoUtil.getPhotoStreamForEmployee(eq(employee), eq(Account.PicsID), anyString())).thenReturn(null);

		assertEquals("photo", employeeAction.photo());
		assertNotNull(employeeAction.getInputStream());
		verify(employeeService).findEmployee(id, Account.PicsID);
		verify(profileDocumentService).getPhotoDocumentFromProfile(any(Profile.class));
		verify(photoUtil).getPhotoStreamForProfile(any(ProfileDocument.class), anyString());
	}
}
