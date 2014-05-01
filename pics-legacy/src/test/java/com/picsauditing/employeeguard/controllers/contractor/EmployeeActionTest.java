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
import com.picsauditing.employeeguard.process.EmployeeSkillData;
import com.picsauditing.employeeguard.process.EmployeeSkillDataProcess;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.email.EmailService;
import com.picsauditing.employeeguard.services.factory.EmailHashServiceFactory;
import com.picsauditing.employeeguard.services.factory.GroupServiceFactory;
import com.picsauditing.employeeguard.services.factory.ProfileDocumentServiceFactory;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.util.PhotoUtil;
import com.picsauditing.employeeguard.util.PhotoUtilFactory;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.util.web.UrlBuilder;
import org.apache.commons.lang.math.NumberUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmployeeActionTest extends PicsActionTest {

	public static final int ID = 6780;

	private EmployeeAction employeeAction;

	private EmailHashService emailHashService;
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
	@Mock
	private EmployeeEntityService employeeEntityService;
	@Mock
	private EmployeeService employeeService;
	@Mock
	private EmployeeSkillDataProcess employeeSkillDataProcess;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeAction = new EmployeeAction();

		emailHashService = EmailHashServiceFactory.getEmailHashService();
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
		when(employeeSkillDataProcess.buildEmployeeSkillData(any(Employee.class), anyCollectionOf(Integer.class))).thenReturn(new EmployeeSkillData());

		Whitebox.setInternalState(employeeAction, "accountService", accountService);
		Whitebox.setInternalState(employeeAction, "emailService", emailService);
		Whitebox.setInternalState(employeeAction, "emailHashService", emailHashService);
		Whitebox.setInternalState(employeeAction, "employeeEntityService", employeeEntityService);
		Whitebox.setInternalState(employeeAction, "employeeService", employeeService);
		Whitebox.setInternalState(employeeAction, "formBuilderFactory", formBuilderFactory);
		Whitebox.setInternalState(employeeAction, "groupService", groupService);
		Whitebox.setInternalState(employeeAction, "photoUtil", photoUtil);
		Whitebox.setInternalState(employeeAction, "profileDocumentService", profileDocumentService);
		Whitebox.setInternalState(employeeAction, "projectRoleService", projectRoleService);
		Whitebox.setInternalState(employeeAction, "urlBuilder", urlBuilder);
		Whitebox.setInternalState(employeeAction, "employeeSkillDataProcess", employeeSkillDataProcess);
	}

	@Test
	public void testIndex() throws Exception {
		when(employeeEntityService.getEmployeesForAccount(anyInt())).thenReturn(Arrays.asList(new Employee()));

		assertEquals(PicsRestActionSupport.LIST, employeeAction.index());
		assertFalse(employeeAction.getEmployees().isEmpty());
		assertNotNull(employeeAction.getEmployeeSkillStatuses());

		verify(employeeEntityService).getEmployeesForAccount(Account.PicsID);
	}

	@Test
	public void testIndex_Search() throws Exception {
		SearchForm searchForm = new SearchForm();
		searchForm.setSearchTerm("Test");
		employeeAction.setSearchForm(searchForm);
		when(employeeEntityService.search(anyString(), anyInt())).thenReturn(Arrays.asList(new Employee()));

		assertEquals(PicsRestActionSupport.LIST, employeeAction.index());
		assertFalse(employeeAction.getEmployees().isEmpty());
		assertNotNull(employeeAction.getEmployeeSkillStatuses());

		verify(employeeEntityService).search("Test", Account.PicsID);
	}

	@Test
	public void testShow() throws Exception {
		when(employeeEntityService.find(anyInt(), anyInt())).thenReturn(new Employee());
		employeeAction.setId(String.valueOf(ID));

		assertEquals(PicsRestActionSupport.SHOW, employeeAction.show());
		assertNotNull(employeeAction.getEmployee());
		assertNotNull(employeeAction.getSkillInfoList());

		verify(employeeEntityService).find(ID, Account.PicsID);
	}

	@Test
	public void testCreate() throws Exception {
		assertEquals(PicsRestActionSupport.CREATE, employeeAction.create());
		assertFalse(employeeAction.getEmployeeGroups().isEmpty());

		verify(groupService).getGroupsForAccount(Account.PicsID);
	}

	@Test
	public void testEditPersonalSection() throws Exception {
		when(employeeEntityService.find(anyInt(), anyInt())).thenReturn(new Employee());
		when(groupService.getGroupsForAccount(anyInt())).thenReturn(Arrays.asList(new Group()));
		employeeAction.setId(String.valueOf(ID));
		assertEquals("personal-form", employeeAction.editPersonalSection());
		assertNotNull(employeeAction.getEmployee());
		verify(employeeEntityService).find(ID, Account.PicsID);
	}

	@Test
	public void testEditEmploymentSection() throws Exception {
		when(employeeEntityService.find(anyInt(), anyInt())).thenReturn(new Employee());
		employeeAction.setId(String.valueOf(ID));
		assertEquals("employment-form", employeeAction.editEmploymentSection());
		assertNotNull(employeeAction.getEmployee());
		assertFalse(employeeAction.getEmployeeGroups().isEmpty());
		verify(employeeEntityService).find(ID, Account.PicsID);
		verify(groupService).getGroupsForAccount(Account.PicsID);
	}

	@Test
	public void testEditAssignmentSection() throws Exception {
		when(employeeEntityService.find(anyInt(), anyInt())).thenReturn(new Employee());
		employeeAction.setId(String.valueOf(ID));
		assertEquals("assignment-form", employeeAction.editAssignmentSection());
		assertNotNull(employeeAction.getEmployee());
		verify(employeeEntityService).find(ID, Account.PicsID);
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
		when(employeeService.save(any(EmployeeForm.class), anyString(), anyInt(), anyInt())).thenReturn(new Employee());
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
		employeeAction.setId(String.valueOf(ID));
		assertEquals(PicsActionSupport.REDIRECT, employeeAction.update());
		assertEquals("/employee-guard/contractor/employee/" + ID, employeeAction.getUrl());
	}

	@Test
	public void testUpdate_Personal() throws Exception {
		when(employeeService.updatePersonal(any(EmployeePersonalForm.class), anyInt(), anyInt(), anyInt())).thenReturn(new Employee());
		EmployeePersonalForm employeePersonalForm = new EmployeePersonalForm();
		employeeAction.setId(String.valueOf(ID));
		employeeAction.setEmployeePersonalForm(employeePersonalForm);

		assertEquals(PicsActionSupport.REDIRECT, employeeAction.update());
		assertTrue(employeeAction.getUrl().startsWith("/employee-guard/contractor/employee/"));

		verify(employeeService).updatePersonal(employeePersonalForm, ID, Account.PicsID, Identifiable.SYSTEM);
	}

	@Test
	public void testUpdate_Employment() throws Exception {
		when(employeeService.updateEmployment(any(EmployeeEmploymentForm.class), anyInt(), anyInt(), anyInt())).thenReturn(new Employee());
		EmployeeEmploymentForm employeeEmploymentForm = new EmployeeEmploymentForm();
		employeeAction.setId(String.valueOf(ID));
		employeeAction.setEmployeeEmploymentForm(employeeEmploymentForm);

		assertEquals(PicsActionSupport.REDIRECT, employeeAction.update());
		assertTrue(employeeAction.getUrl().startsWith("/employee-guard/contractor/employee/"));

		verify(employeeService).updateEmployment(employeeEmploymentForm, ID, Account.PicsID, Identifiable.SYSTEM);
	}

	@Test
	public void testUpdate_Photo() throws Exception {
		when(employeeEntityService.updatePhoto(any(EmployeePhotoForm.class), anyString(), anyInt(), anyInt())).thenReturn(new Employee());
		EmployeePhotoForm employeePhotoForm = new EmployeePhotoForm();
		employeeAction.setId(String.valueOf(ID));
		employeeAction.setEmployeePhotoForm(employeePhotoForm);

		assertEquals(PicsActionSupport.REDIRECT, employeeAction.update());
		assertTrue(employeeAction.getUrl().startsWith("/employee-guard/contractor/employee/"));

		verify(employeeEntityService).updatePhoto(eq(employeePhotoForm), anyString(), eq(ID), eq(Account.PicsID));
	}

	@Test
	public void testDelete() throws Exception {
		String url = "/employee-guard/contractor/employee";
		when(urlBuilder.action(anyString())).thenReturn(urlBuilder);
		when(urlBuilder.build()).thenReturn(url);

		assertEquals(PicsActionSupport.REDIRECT, employeeAction.delete());
		verify(permissions).getAccountId();
		verify(employeeEntityService).delete(anyInt(), anyInt());
		assertEquals(url, employeeAction.getUrl());
	}

	@Test
	public void testPhoto_EmployeePhotoExists() throws Exception {
		String id = "1";
		employeeAction.setId(id);

		assertEquals("photo", employeeAction.photo());
		assertNotNull(employeeAction.getInputStream());
		verify(employeeEntityService).find(NumberUtils.toInt(id), Account.PicsID);
		verify(photoUtil).getPhotoStreamForEmployee(any(Employee.class), eq(Account.PicsID), anyString());
	}

	@Test
	public void testPhoto_OnlyProfilePhotoExists() throws Exception {
		String id = "1";
		employeeAction.setId(id);

		Employee employee = new Employee();
		employee.setProfile(new Profile());

		when(employeeEntityService.find(NumberUtils.toInt(id), Account.PicsID)).thenReturn(employee);
		when(photoUtil.getPhotoStreamForEmployee(eq(employee), eq(Account.PicsID), anyString())).thenReturn(null);

		assertEquals("photo", employeeAction.photo());
		assertNotNull(employeeAction.getInputStream());
		verify(employeeEntityService).find(NumberUtils.toInt(id), Account.PicsID);
		verify(profileDocumentService).getPhotoDocumentFromProfile(any(Profile.class));
		verify(photoUtil).getPhotoStreamForProfile(any(ProfileDocument.class), anyString());
	}
}
