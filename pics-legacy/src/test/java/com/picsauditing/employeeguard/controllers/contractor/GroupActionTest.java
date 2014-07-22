package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.contractor.GroupEmployeesForm;
import com.picsauditing.employeeguard.forms.contractor.GroupForm;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.GroupService;
import com.picsauditing.employeeguard.services.SkillService;
import com.picsauditing.employeeguard.services.entity.employee.EmployeeEntityService;
import com.picsauditing.employeeguard.services.factory.GroupServiceFactory;
import com.picsauditing.employeeguard.services.factory.SkillServiceFactory;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.util.web.UrlBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GroupActionTest extends PicsActionTest {

	public static final String ID = "ID";

	private GroupAction groupAction;

	@Mock
	private EmployeeEntityService employeeEntityService;

	private GroupService groupService;
	private SkillService skillService;

	@Mock
	private UrlBuilder urlBuilder;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		groupAction = new GroupAction();
		groupService = GroupServiceFactory.getGroupService();
		skillService = SkillServiceFactory.getSkillService();

		super.setUp(groupAction);

		Whitebox.setInternalState(groupAction, "employeeEntityService", employeeEntityService);
		Whitebox.setInternalState(groupAction, "groupService", groupService);
		Whitebox.setInternalState(groupAction, "skillService", skillService);
		Whitebox.setInternalState(groupAction, "urlBuilder", urlBuilder);

		when(permissions.getAccountId()).thenReturn(Account.PICS_ID);
		when(permissions.getAppUserID()).thenReturn(Identifiable.SYSTEM);
	}

	@Test
	public void testIndex() throws Exception {
		assertEquals(PicsRestActionSupport.LIST, groupAction.index());
		assertFalse(groupAction.getGroups().isEmpty());
		verify(groupService).getGroupsForAccount(Account.PICS_ID);
	}

	@Test
	public void testIndex_Search() throws Exception {
		SearchForm searchForm = new SearchForm();
		searchForm.setSearchTerm("Test");
		groupAction.setSearchForm(searchForm);

		assertEquals(PicsRestActionSupport.LIST, groupAction.index());
		assertFalse(groupAction.getGroups().isEmpty());
		verify(groupService).search("Test", Account.PICS_ID);
	}

	@Test
	public void testShow() throws Exception {
		groupAction.setId(ID);

		assertEquals(PicsRestActionSupport.SHOW, groupAction.show());
		assertNotNull(groupAction.getGroup());
		verify(groupService).getGroup(ID, Account.PICS_ID);
	}

	@Test
	public void testCreate() throws Exception {
		when(employeeEntityService.getEmployeesForAccount(anyInt())).thenReturn(Arrays.asList(new Employee()));

		assertEquals(PicsRestActionSupport.CREATE, groupAction.create());
		assertFalse(groupAction.getGroupEmployees().isEmpty());
		assertFalse(groupAction.getGroupSkills().isEmpty());
		verify(employeeEntityService).getEmployeesForAccount(Account.PICS_ID);
		verify(skillService).getOptionalSkillsForAccount(Account.PICS_ID);
	}

	@Test
	public void testEditNameSkillsSection() throws Exception {
		groupAction.setId(ID);

		assertEquals("name-skills-form", groupAction.editNameSkillsSection());
		assertNotNull(groupAction.getGroup());
		assertFalse(groupAction.getGroupSkills().isEmpty());
		verify(groupService).getGroup(ID, Account.PICS_ID);
		verify(skillService).getOptionalSkillsForAccount(Account.PICS_ID);
	}

	@Test
	public void testEditEmployeesSection() throws Exception {
		when(employeeEntityService.getEmployeesForAccount(anyInt())).thenReturn(Arrays.asList(new Employee()));
		groupAction.setId(ID);

		assertEquals("employees-form", groupAction.editEmployeesSection());
		assertNotNull(groupAction.getGroup());
		assertFalse(groupAction.getGroupEmployees().isEmpty());
		verify(groupService).getGroup(ID, Account.PICS_ID);
		verify(employeeEntityService).getEmployeesForAccount(Account.PICS_ID);
	}

	@Test
	public void testInsert() throws Exception {
		String url = "/employee-guard/contractor/employee-group";

		when(urlBuilder.action(anyString())).thenReturn(urlBuilder);
		when(urlBuilder.build()).thenReturn(url);

		groupAction.setGroupForm(new GroupForm());

		assertEquals(PicsActionSupport.REDIRECT, groupAction.insert());
		assertEquals(url, groupAction.getUrl());

		verify(groupService).save(any(Group.class), eq(Account.PICS_ID), anyInt());
	}

	@Test
	public void testInsert_AddAnother() throws Exception {
		GroupForm groupForm = new GroupForm();
		groupForm.setAddAnother(true);
		groupAction.setGroupForm(groupForm);

		assertEquals(PicsActionSupport.REDIRECT, groupAction.insert());
		assertEquals("/employee-guard/contractor/employee-group/create", groupAction.getUrl());

		verify(groupService).save(any(Group.class), eq(Account.PICS_ID), anyInt());
	}

	@Test
	public void testUpdate_NameSkills() throws Exception {
		GroupNameSkillsForm groupNameSkillsForm = new GroupNameSkillsForm();
		groupAction.setGroupNameSkillsForm(groupNameSkillsForm);
		groupAction.setId(ID);

		assertEquals(PicsActionSupport.REDIRECT, groupAction.update());
		assertNotNull(groupAction.getGroup());
		assertTrue(groupAction.getUrl().startsWith("/employee-guard/contractor/employee-group/"));

		verify(groupService).update(groupNameSkillsForm, ID, Account.PICS_ID, Identifiable.SYSTEM);
	}

	@Test
	public void testUpdate_Employees() throws Exception {
		GroupEmployeesForm groupEmployeesForm = new GroupEmployeesForm();
		groupAction.setGroupEmployeesForm(groupEmployeesForm);
		groupAction.setId(ID);

		assertEquals(PicsActionSupport.REDIRECT, groupAction.update());
		assertNotNull(groupAction.getGroup());
		assertTrue(groupAction.getUrl().startsWith("/employee-guard/contractor/employee-group/"));

		verify(groupService).update(groupEmployeesForm, ID, Account.PICS_ID, Identifiable.SYSTEM);
	}

	@Test
	public void testDelete() throws Exception {
		String url = "/employee-guard/contractor/employee-group";
		when(urlBuilder.action(anyString())).thenReturn(urlBuilder);
		when(urlBuilder.build()).thenReturn(url);

		assertEquals(PicsActionSupport.REDIRECT, groupAction.delete());
		verify(permissions).getAccountId();
		verify(permissions).getAppUserID();
		verify(groupService).delete(anyString(), anyInt(), anyInt());
		assertEquals(url, groupAction.getUrl());
	}
}
