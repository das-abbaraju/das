package com.picsauditing.employeeguard.engine;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.*;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.entity.*;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.services.models.AccountType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.when;

public class SkillEngineTest {

	public static final int CONTRACTOR_ID = 123;
	public static final int SITE_ID = 234;

	private SkillEngine skillEngine;

	@Mock
	private AccountService accountService;
	@Mock
	private EmployeeEntityService employeeEntityService;
	@Mock
	private GroupEntityService groupEntityService;
	@Mock
	private ProjectEntityService projectEntityService;
	@Mock
	private RoleEntityService roleEntityService;
	@Mock
	private SkillEntityService skillEntityService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		skillEngine = new SkillEngine();

		Whitebox.setInternalState(skillEngine, "accountService", accountService);
		Whitebox.setInternalState(skillEngine, "employeeEntityService", employeeEntityService);
		Whitebox.setInternalState(skillEngine, "groupEntityService", groupEntityService);
		Whitebox.setInternalState(skillEngine, "projectEntityService", projectEntityService);
		Whitebox.setInternalState(skillEngine, "roleEntityService", roleEntityService);
		Whitebox.setInternalState(skillEngine, "skillEntityService", skillEntityService);
	}

	@Test
	public void testGetEmployeeSkillsMapForAccount_Contractor() throws Exception {
		final Employee employee = buildFakeEmployee();
		final List<Employee> employees = Arrays.asList(employee);

		AccountModel contractor = setupForGetEmployeeSkillsMapForAccountContractor(employee, employees);

		Map<Employee, Set<AccountSkill>> result = skillEngine.getEmployeeSkillsMapForAccount(employees, contractor);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(1, result.keySet().size());
		assertEquals(3, result.get(employee).size());
	}

	private AccountModel setupForGetEmployeeSkillsMapForAccountContractor(final Employee employee,
	                                                                      final List<Employee> employees) {
		Group group = buildFakeGroup();
		final Set<Group> groups = new HashSet<>(Arrays.asList(group));

		AccountSkill skill1 = buildFakeSkill();
		skill1.setName("Skill 1");
		final Set<AccountSkill> skills = new HashSet<>(Arrays.asList(skill1));

		final AccountSkill skill2 = buildFakeSkill();
		skill2.setName("Skill 2");

		final AccountSkill skill3 = buildFakeSkill();
		skill3.setName("Skill 3");

		final Role role1 = buildFakeRole();
		role1.setName("Role 1");

		final Role role2 = buildFakeRole();
		role2.setName("Role 2");

		final Project project = buildFakeProject();
		List<Project> projects = Arrays.asList(project);

		AccountModel contractor = buildFakeContractor();
		List<Integer> siteIds = Arrays.asList(SITE_ID);

		prepareMocksForGetEmployeeSkillsMapForAccountContractor(
				employee, employees, groups, skills, skill2, skill3, role1, role2, project, projects, siteIds);

		return contractor;
	}

	private void prepareMocksForGetEmployeeSkillsMapForAccountContractor(final Employee employee, final List<Employee> employees, final Set<Group> groups, final Set<AccountSkill> skills, final AccountSkill skill2, final AccountSkill skill3, final Role role1, final Role role2, final Project project, List<Project> projects, List<Integer> siteIds) {
		Map<Employee, Set<Group>> employeeGroups = new HashMap<Employee, Set<Group>>() {{
			put(employee, groups);
		}};

		Map<Employee, Set<AccountSkill>> employeeGroupSkills = new HashMap<Employee, Set<AccountSkill>>() {{
			put(employee, skills);
		}};

		Map<Role, Set<Employee>> roleEmployees2 = new HashMap<Role, Set<Employee>>() {{
			put(role2, new HashSet<>(employees));
		}};

		Map<Role, Set<AccountSkill>> roleSkills = new HashMap<Role, Set<AccountSkill>>() {{
			put(role2, new HashSet<>(Arrays.asList(skill2)));
		}};

		Map<Employee, Set<Project>> employeeProjects = new HashMap<Employee, Set<Project>>() {{
			put(employee, new HashSet<>(Arrays.asList(project)));
		}};

		Map<Project, Set<AccountSkill>> projectSkills = new HashMap<Project, Set<AccountSkill>>() {{
			put(project, new HashSet<>(Arrays.asList(skill3)));
		}};

		Map<Role, Set<Employee>> roleEmployees1 = new HashMap<Role, Set<Employee>>() {{
			put(role1, new HashSet<>(employees));
		}};

		when(accountService.getOperatorIdsForContractor(CONTRACTOR_ID)).thenReturn(siteIds);
		when(employeeEntityService.getEmployeesByProjectRoles(anyCollectionOf(Project.class))).thenReturn(roleEmployees2);
		when(employeeEntityService.getEmployeesBySiteRoles(siteIds)).thenReturn(roleEmployees1);
		when(groupEntityService.getEmployeeGroups(employees)).thenReturn(employeeGroups);
		when(projectEntityService.getProjectsBySiteIds(siteIds)).thenReturn(new HashSet<>(projects));
		when(projectEntityService.getProjectsForEmployees(employees)).thenReturn(employeeProjects);
		when(skillEntityService.getGroupSkillsForEmployees(employeeGroups)).thenReturn(employeeGroupSkills);
		when(skillEntityService.getRequiredSkillsForProjects(anySetOf(Project.class))).thenReturn(projectSkills);
		when(skillEntityService.getSkillsForRoles(anySetOf(Role.class))).thenReturn(roleSkills);
	}

	private Employee buildFakeEmployee() {
		return new EmployeeBuilder()
				.firstName("First")
				.lastName("Last")
				.email("Email")
				.slug("Slug")
				.build();
	}

	private Group buildFakeGroup() {
		return new GroupBuilder()
				.build();
	}

	private AccountSkill buildFakeSkill() {
		return new AccountSkillBuilder()
				.build();
	}

	private Role buildFakeRole() {
		return new RoleBuilder()
				.build();
	}

	private Project buildFakeProject() {
		return new ProjectBuilder()
				.build();
	}

	private AccountModel buildFakeContractor() {
		return new AccountModel.Builder()
				.id(CONTRACTOR_ID)
				.accountType(AccountType.CONTRACTOR)
				.build();
	}
}
