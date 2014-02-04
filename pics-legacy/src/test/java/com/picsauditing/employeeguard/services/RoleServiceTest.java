package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.*;

public class RoleServiceTest {

	public static final int CONTRACTOR_ID = 1234;
	public static final int CORPORATE_ID = 712;
	public static final int CORPORATE_ID_2 = 55653;
	public static final int CORPORATE_ROLE_ID = 23;
	public static final int EMPLOYEE_ID = 3;
	public static final int SITE_ID = 345;
	public static final int SITE_ID_2 = 55654;
	public static final int USER_ID = 6;

	private RoleService roleService;

	@Mock
	private AccountService accountService;
	@Mock
	private AccountSkillDAO accountSkillDAO;
	@Mock
	private AccountSkillEmployeeDAO accountSkillEmployeeDAO;
	@Mock
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Mock
	private EmployeeDAO employeeDAO;
	@Mock
	private ProjectCompanyDAO projectCompanyDAO;
	@Mock
	private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;
	@Mock
	private RoleDAO roleDAO;
	@Mock
	private RoleEmployeeDAO roleEmployeeDAO;
	@Mock
	private SiteSkillDAO siteSkillDAO;
	// Entities
	@Mock
	private AccountSkillEmployee accountSkillEmployee;
	@Mock
	private AccountSkillRole accountSkillRole;
	@Mock
	private Employee employee;
	@Mock
	private Project project;
	@Mock
	private ProjectCompany projectCompany;
	@Mock
	private ProjectRole projectRole;
	@Mock
	private ProjectRoleEmployee projectRoleEmployee;
	@Mock
	private Role role;
	@Mock
	private RoleEmployee roleEmployee;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		roleService = new RoleService();

		Whitebox.setInternalState(roleService, "accountService", accountService);
		Whitebox.setInternalState(roleService, "accountSkillDAO", accountSkillDAO);
		Whitebox.setInternalState(roleService, "accountSkillEmployeeDAO", accountSkillEmployeeDAO);
		Whitebox.setInternalState(roleService, "accountSkillEmployeeService", accountSkillEmployeeService);
		Whitebox.setInternalState(roleService, "employeeDAO", employeeDAO);
		Whitebox.setInternalState(roleService, "roleDAO", roleDAO);
		Whitebox.setInternalState(roleService, "roleEmployeeDAO", roleEmployeeDAO);
		Whitebox.setInternalState(roleService, "projectCompanyDAO", projectCompanyDAO);
		Whitebox.setInternalState(roleService, "projectRoleEmployeeDAO", projectRoleEmployeeDAO);
		Whitebox.setInternalState(roleService, "siteSkillDAO", siteSkillDAO);
	}

	@Test
	public void testAssignEmployeeToSite() {
		when(accountService.getTopmostCorporateAccountIds(SITE_ID)).thenReturn(Arrays.asList(CORPORATE_ID));

		roleService.assignEmployeeToSite(SITE_ID, CORPORATE_ROLE_ID, buildFakeEmployee(), USER_ID);

		verifyTest();
	}

	@Test
	public void testRemoveSiteSpecificRolesFromEmployee_NoCorporateOrOtherSites() {
		List<AccountSkillEmployee> accountSkillEmployees = Arrays.asList(accountSkillEmployee);
		List<ProjectRoleEmployee> projectRoleEmployees = Arrays.asList(projectRoleEmployee);
		List<RoleEmployee> roleEmployees = Arrays.asList(roleEmployee);

		when(accountSkillEmployeeDAO.findByEmployeeAndCorporateIds(anyInt(), anyList())).thenReturn(accountSkillEmployees);
        when(employee.getId()).thenReturn(EMPLOYEE_ID);
		when(projectRoleEmployeeDAO.findByEmployeeAndSiteId(anyInt(), anyInt())).thenReturn(projectRoleEmployees);
		when(roleEmployeeDAO.findByEmployeeAndSiteId(anyInt(), anyInt())).thenReturn(roleEmployees);

		roleService.unassignEmployeeFromSite(employee, SITE_ID);

		verify(accountSkillEmployeeDAO).delete(accountSkillEmployees);
		verify(projectRoleEmployeeDAO).delete(projectRoleEmployees);
		verify(roleEmployeeDAO).delete(roleEmployees);
	}

	@Test
	public void testUnassignEmployeeFromSite_WithCorporateAndOtherSites() {
		AccountSkill deleteSkill = mock(AccountSkill.class);
		AccountSkill keepSkill = mock(AccountSkill.class);
		AccountSkillEmployee accountSkillEmployee2 = mock(AccountSkillEmployee.class);

		List<AccountSkillEmployee> accountSkillEmployees = Arrays.asList(accountSkillEmployee, accountSkillEmployee2);

		List<Integer> childIds = new ArrayList<>();
		childIds.add(SITE_ID);
		childIds.add(SITE_ID_2);

		List<Integer> corporateIds = Arrays.asList(CORPORATE_ID, CORPORATE_ID_2);
		List<ProjectRoleEmployee> projectRoleEmployees = Arrays.asList(projectRoleEmployee);
		List<RoleEmployee> roleEmployees = Arrays.asList(roleEmployee);

		when(accountSkillEmployee.getSkill()).thenReturn(deleteSkill);
		when(accountSkillEmployee2.getSkill()).thenReturn(keepSkill);
		when(accountSkillEmployeeDAO.findByEmployeeAndCorporateIds(EMPLOYEE_ID, corporateIds)).thenReturn(accountSkillEmployees);
		when(accountSkillRole.getSkill()).thenReturn(keepSkill);
		when(accountService.getChildOperatorIds(corporateIds)).thenReturn(childIds);
		when(accountService.getTopmostCorporateAccountIds(SITE_ID)).thenReturn(corporateIds);
		when(deleteSkill.getAccountId()).thenReturn(SITE_ID);
		when(keepSkill.getAccountId()).thenReturn(SITE_ID_2);
		when(employee.getAccountId()).thenReturn(CONTRACTOR_ID);
        when(employee.getId()).thenReturn(EMPLOYEE_ID);
		when(employeeDAO.find(EMPLOYEE_ID)).thenReturn(employee);
		when(project.getAccountId()).thenReturn(SITE_ID_2);
		when(project.getRoles()).thenReturn(Arrays.asList(projectRole));
		when(projectCompany.getProject()).thenReturn(project);
		when(projectCompanyDAO.findByContractorExcludingSite(CONTRACTOR_ID, SITE_ID)).thenReturn(Arrays.asList(projectCompany));
		when(projectRole.getRole()).thenReturn(role);
		when(projectRoleEmployeeDAO.findByEmployeeAndSiteId(EMPLOYEE_ID, SITE_ID)).thenReturn(projectRoleEmployees);
		when(role.getSkills()).thenReturn(Arrays.asList(accountSkillRole));
		when(roleEmployeeDAO.findByEmployeeAndSiteId(EMPLOYEE_ID, SITE_ID)).thenReturn(roleEmployees);

		roleService.unassignEmployeeFromSite(employee, SITE_ID);

		verify(accountSkillEmployeeDAO).delete(Arrays.asList(accountSkillEmployee));
		verify(projectRoleEmployeeDAO).delete(projectRoleEmployees);
		verify(roleEmployeeDAO).delete(roleEmployees);
	}

	private void verifyTest() {
		verify(roleEmployeeDAO).save(any(RoleEmployee.class));
		verify(accountSkillEmployeeService).save(anyListOf(AccountSkillEmployee.class));
	}

	private Employee buildFakeEmployee() {
		return new EmployeeBuilder()
				.accountId(561)
				.email("test@test.com")
				.slug("ABE456A2")
				.build();
	}
}
