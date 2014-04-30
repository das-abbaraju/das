package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.entities.builders.ProjectBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.services.external.AccountService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class AssignmentServiceTest {
	public static final int SITE_ID = 123;
  private static final int ACCOUNT_ID = 1100;
	public static final List<Integer> CONTRACTOR_IDS = Arrays.asList(12, 34);
	public static final List<Integer> CORPORATE_IDS = Arrays.asList(56, 78);

	private AssignmentService service;

	@Mock
	private AccountService accountService;
	@Mock
	private EmployeeEntityService employeeEntityService;
	@Mock
	private ProjectEntityService projectEntityService;
	@Mock
	private RoleEntityService roleEntityService;
	@Mock
	private SkillEntityService skillEntityService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		service = new AssignmentService();

		Whitebox.setInternalState(service, "accountService", accountService);
		Whitebox.setInternalState(service, "employeeEntityService", employeeEntityService);
		Whitebox.setInternalState(service, "projectEntityService", projectEntityService);
		Whitebox.setInternalState(service, "roleEntityService", roleEntityService);
		Whitebox.setInternalState(service, "skillEntityService", skillEntityService);

		when(accountService.getContractorIds(SITE_ID)).thenReturn(CONTRACTOR_IDS);
		when(accountService.getContractorIds(Arrays.asList(SITE_ID))).thenReturn(CONTRACTOR_IDS);
		when(accountService.getTopmostCorporateAccountIds(SITE_ID)).thenReturn(CORPORATE_IDS);
	}

	@Test
	public void testGetEmployeeSkillsForSite() throws Exception {
		final List<Employee> employees = buildFakeEmployees();
		final List<Project> projects = buildFakeProjects();
		final List<Role> roles = buildFakeRoles();
		List<AccountSkill> skills = buildFakeSkills();

		setupGetEmployeeSkillsForSite(employees, projects, roles, skills);

		Map<Employee, Set<AccountSkill>> result = service.getEmployeeSkillsForSite(SITE_ID);

		verifyGetEmployeeSkillsForSite(employees, skills, result);
	}

	private void setupGetEmployeeSkillsForSite(final List<Employee> employees,
	                                           final List<Project> projects,
	                                           final List<Role> roles,
	                                           final List<AccountSkill> skills) {

		final AccountSkill skill1 = skills.get(0);
		final AccountSkill skill2 = skills.get(1);
		final AccountSkill skill3 = skills.get(2);

		setupGetEmployeesAssignedToSite(employees);
		setupGetAllEmployeeRolesForSite(employees, roles);

		when(projectEntityService.getProjectsForEmployeesBySiteId(anySetOf(Employee.class), eq(SITE_ID)))
				.thenReturn(new HashMap<Employee, Set<Project>>() {{
					Set<Project> projectSet = new HashSet<>(projects);
					put(employees.get(0), projectSet);
					put(employees.get(1), projectSet);
				}});
		when(skillEntityService.getRequiredSkillsForProjects(anySetOf(Project.class)))
				.thenReturn(new HashMap<Project, Set<AccountSkill>>() {{
					put(projects.get(0), new HashSet<>(Arrays.asList(skill1)));
				}});
		when(skillEntityService.getSkillsForRoles(anySetOf(Role.class)))
				.thenReturn(new HashMap<Role, Set<AccountSkill>>() {{
					put(roles.get(0), new HashSet<>(Arrays.asList(skill2)));
				}});
		when(skillEntityService.getSiteRequiredSkills(SITE_ID, CORPORATE_IDS))
				.thenReturn(new HashSet<>(Arrays.asList(skill3)));
	}

	private void verifyGetEmployeeSkillsForSite(List<Employee> employees,
	                                            List<AccountSkill> skills,
	                                            Map<Employee, Set<AccountSkill>> result) {

		final AccountSkill skill1 = skills.get(0);
		final AccountSkill skill2 = skills.get(1);
		final AccountSkill skill3 = skills.get(2);

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(3, result.get(employees.get(0)).size());
		assertEquals(3, result.get(employees.get(1)).size());
		assertTrue(result.get(employees.get(0)).contains(skill1));
		assertTrue(result.get(employees.get(0)).contains(skill2));
		assertTrue(result.get(employees.get(0)).contains(skill3));
		assertTrue(result.get(employees.get(1)).contains(skill2));
		assertTrue(result.get(employees.get(1)).contains(skill3));
	}

	@Test
	public void testGetEmployeesAssignedToSite() throws Exception {
		List<Employee> employees = buildFakeEmployees();
		setupGetEmployeesAssignedToSite(employees);

		Set<Employee> result = service.getEmployeesAssignedToSite(SITE_ID);

		verifyGetEmployeesAssignedToSite(employees, result);
	}

	private void setupGetEmployeesAssignedToSite(final List<Employee> employees) {
		final Role role = buildFakeRole();
		final List<Project> projects = buildFakeProjects();

		List<Integer> siteIds = Arrays.asList(SITE_ID);

		when(employeeEntityService.getEmployeesAssignedToSites(CONTRACTOR_IDS, siteIds))
				.thenReturn(employees);
		when(projectEntityService.getProjectsForEmployeesBySiteIds(employees, siteIds))
				.thenReturn(new HashMap<Employee, Set<Project>>() {{
					put(employees.get(0), new HashSet<>(Arrays.asList(projects.get(0))));
				}});
		when(roleEntityService.getSiteRolesForEmployees(employees, siteIds))
				.thenReturn(new HashMap<Employee, Set<Role>>() {{
					put(employees.get(1), new HashSet<>(Arrays.asList(role)));
				}});
	}

	private void verifyGetEmployeesAssignedToSite(List<Employee> employees, Set<Employee> result) {
		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.contains(employees.get(0)));
		assertTrue(result.contains(employees.get(1)));
	}

	@Test
	public void testGetAllEmployeeRolesForSite() throws Exception {
		final List<Employee> employees = buildFakeEmployees();
		final List<Role> roles = buildFakeRoles();

		setupGetAllEmployeeRolesForSite(employees, roles);

		Map<Employee, Set<Role>> result = service.getAllEmployeeRolesForSite(SITE_ID);

		verifyGetAllEmployeeRolesForSite(employees, roles, result);
	}

	private void setupGetAllEmployeeRolesForSite(List<Employee> employees, List<Role> roles) {
		final Employee employee1 = employees.get(0);
		final Employee employee2 = employees.get(1);
		final Role role1 = roles.get(0);
		final Role role2 = roles.get(1);
		final Role role3 = roles.get(2);

		List<Integer> siteIds = Arrays.asList(SITE_ID);
		when(employeeEntityService.getEmployeesAssignedToSites(CONTRACTOR_IDS, siteIds))
				.thenReturn(employees);
		when(roleEntityService.getProjectRolesForEmployees(employees, SITE_ID))
				.thenReturn(new HashMap<Employee, Set<Role>>() {{
					put(employee1, new HashSet<>(Arrays.asList(role1, role2)));
					put(employee2, new HashSet<>(Arrays.asList(role3)));
				}});
		when(roleEntityService.getSiteRolesForEmployees(employees, SITE_ID))
				.thenReturn(new HashMap<Employee, Set<Role>>() {{
					put(employee2, new HashSet<>(Arrays.asList(role1)));
				}});
	}

	private void verifyGetAllEmployeeRolesForSite(List<Employee> employees,
	                                              List<Role> roles,
	                                              Map<Employee, Set<Role>> result) {

		final Employee employee1 = employees.get(0);
		final Employee employee2 = employees.get(1);
		final Role role1 = roles.get(0);
		final Role role2 = roles.get(1);
		final Role role3 = roles.get(2);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertNotNull(result.get(employee1));
		assertNotNull(result.get(employee2));
		assertEquals(2, result.get(employee1).size());
		assertEquals(2, result.get(employee2).size());
		assertTrue(result.get(employee1).contains(role1));
		assertTrue(result.get(employee1).contains(role2));
		assertTrue(result.get(employee2).contains(role1));
		assertTrue(result.get(employee2).contains(role3));
	}

	private Employee buildFakeEmployee() {
		return new EmployeeBuilder()
				.accountId(CONTRACTOR_IDS.get(0))
				.firstName("Employee")
				.lastName("One")
				.email("1@employee.com")
				.slug("Employee1")
				.build();
	}

	private List<Employee> buildFakeEmployees() {
		return Arrays.asList(
				buildFakeEmployee(),
				new EmployeeBuilder()
						.accountId(CONTRACTOR_IDS.get(1))
						.firstName("Employee")
						.lastName("Two")
						.email("2@employee.com")
						.slug("Employee2")
						.build());
	}

	private Project buildFakeProject() {
		return new ProjectBuilder()
				.id(1)
				.accountId(SITE_ID)
				.name("Project 1")
				.build();
	}

	private List<Project> buildFakeProjects() {
		return Arrays.asList(
				buildFakeProject(),
				new ProjectBuilder()
						.id(2)
						.accountId(SITE_ID)
						.name("Project 2")
						.build()
		);
	}

	private Role buildFakeRole() {
		return new RoleBuilder()
				.accountId(CORPORATE_IDS.get(0))
				.name("Role 1")
				.build();
	}

	private List<Role> buildFakeRoles() {
		return Arrays.asList(
				buildFakeRole(),
				new RoleBuilder()
						.accountId(CORPORATE_IDS.get(0))
						.name("Role 2")
						.build(),
				new RoleBuilder()
						.accountId(CORPORATE_IDS.get(1))
						.name("Role 3")
						.build());
	}

	private List<AccountSkill> buildFakeSkills() {
		return Arrays.asList(
				new AccountSkillBuilder(ACCOUNT_ID)
						.accountId(CORPORATE_IDS.get(0))
						.skillType(SkillType.Certification)
						.name("Skill 1")
						.build(),
				new AccountSkillBuilder(ACCOUNT_ID)
						.accountId(CORPORATE_IDS.get(1))
						.skillType(SkillType.Certification)
						.name("Skill 2")
						.build(),
				new AccountSkillBuilder(ACCOUNT_ID)
						.accountId(CORPORATE_IDS.get(0))
						.skillType(SkillType.Certification)
						.name("Skill 3")
						.build());
	}
}
