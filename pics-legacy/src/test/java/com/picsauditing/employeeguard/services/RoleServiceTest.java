package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.AccountSkillEmployeeDAO;
import com.picsauditing.employeeguard.daos.RoleDAO;
import com.picsauditing.employeeguard.daos.SiteAssignmentDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.services.engine.SkillEngine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RoleServiceTest {

	public static final int CONTRACTOR_ID = 1234;
	public static final int CORPORATE_ID = 712;
	public static final int CORPORATE_ROLE_ID = 23;
	public static final int EMPLOYEE_ID = 3;
	public static final int SITE_ID = 345;
	public static final int USER_ID = 6;

	private RoleService roleService;

	private EntityAuditInfo auditInfo = new EntityAuditInfo.Builder()
			.appUserId(USER_ID)
			.timestamp(new Date())
			.build();

	@Mock
	private AccountService accountService;
	@Mock
	private AccountSkillDAO accountSkillDAO;
	@Mock
	private AccountSkillEmployeeDAO accountSkillEmployeeDAO;
	@Mock
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Mock
	private RoleDAO roleDAO;
	@Mock
	private RoleAssignmentHelper roleAssignmentHelper;
	@Mock
	private SiteAssignmentDAO siteAssignmentDAO;
	@Mock
	private SkillEngine skillEngine;
	@Mock
	private SkillUsageLocator skillUsageLocator;

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
	private SkillUsage skillUsage;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		roleService = new RoleService();

		Whitebox.setInternalState(roleService, "accountService", accountService);
		Whitebox.setInternalState(roleService, "accountSkillDAO", accountSkillDAO);
		Whitebox.setInternalState(roleService, "accountSkillEmployeeDAO", accountSkillEmployeeDAO);
		Whitebox.setInternalState(roleService, "accountSkillEmployeeService", accountSkillEmployeeService);
		Whitebox.setInternalState(roleService, "roleDAO", roleDAO);
		Whitebox.setInternalState(roleService, "roleAssignmentHelper", roleAssignmentHelper);
		Whitebox.setInternalState(roleService, "siteAssignmentDAO", siteAssignmentDAO);
		Whitebox.setInternalState(roleService, "skillEngine", skillEngine);
		Whitebox.setInternalState(roleService, "skillUsageLocator", skillUsageLocator);
	}

	@Test
	public void testAssignEmployeeToSite() {
		setupTestForAssigningEmployeeToSite();

		roleService.assignEmployeeToRole(SITE_ID, CORPORATE_ROLE_ID, buildFakeEmployee(), auditInfo);

		verifyTest();
	}

	private void setupTestForAssigningEmployeeToSite() {
		List<Integer> corporateIds = Arrays.asList(CORPORATE_ID);
		when(accountService.getTopmostCorporateAccountIds(SITE_ID)).thenReturn(corporateIds);
	}

	@Test
	public void testUnassignEmployeeFromSite_NoCorporateOrOtherSites() {
		List<AccountSkillEmployee> accountSkillEmployees = Arrays.asList(accountSkillEmployee);

		when(accountSkillEmployeeDAO.findByEmployeeAndCorporateIds(anyInt(), anyList())).thenReturn(accountSkillEmployees);
		when(employee.getId()).thenReturn(EMPLOYEE_ID);

		roleService.unassignEmployeeFromSite(employee, SITE_ID);

		verifyUnassign();
	}

	@Test
	public void testUnassignEmployeeFromSite_WithCorporateAndOtherSites() {
		setUpUnassignEmployeeFromSite();

		roleService.unassignEmployeeFromSite(employee, SITE_ID);

		verifyUnassign();
	}

	@Test
	public void testUnassignEmployeeFromRole() {
		List<AccountSkillEmployee> accountSkillEmployees = Arrays.asList(new AccountSkillEmployee());
		Set<AccountSkill> accountSkills = new HashSet<>(Arrays.asList(new AccountSkill()));

		Role role = new RoleBuilder().name("Corporate Role").build();
		Role siteRole = new RoleBuilder().name("Site Role").build();

		List<Integer> corporateIds = Arrays.asList(CORPORATE_ID);

		HashMap<Role, Role> siteToCorporateRoles = new HashMap<>();
		siteToCorporateRoles.put(siteRole, role);

		when(accountService.getTopmostCorporateAccountIds(SITE_ID)).thenReturn(corporateIds);
		when(accountSkillEmployeeDAO.findByAccountAndEmployee(employee)).thenReturn(Collections.<AccountSkillEmployee>emptyList());
		when(employee.getSkills()).thenReturn(accountSkillEmployees);
		when(roleDAO.find(CORPORATE_ROLE_ID)).thenReturn(role);
		when(skillUsage.allSkills()).thenReturn(accountSkills);
		when(skillUsageLocator.getSkillUsagesForEmployee(employee)).thenReturn(skillUsage);

		roleService.unassignEmployeeFromRole(employee, CORPORATE_ROLE_ID, SITE_ID);

		verify(accountSkillEmployeeDAO).deleteByIds(anyListOf(Integer.class));
		verify(roleAssignmentHelper).deleteProjectRolesFromEmployee(employee, role);
		verify(roleAssignmentHelper).deleteSiteRoleFromEmployee(employee, role, SITE_ID);
	}

	private void setUpUnassignEmployeeFromSite() {
		when(employee.getAccountId()).thenReturn(CONTRACTOR_ID);
		when(employee.getId()).thenReturn(EMPLOYEE_ID);
	}

	private void verifyUnassign() {
		verify(roleAssignmentHelper).deleteProjectRolesFromEmployee(EMPLOYEE_ID, SITE_ID);
		verify(roleAssignmentHelper).deleteSiteRolesFromEmployee(EMPLOYEE_ID, SITE_ID);
	}

	private void verifyTest() {
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
