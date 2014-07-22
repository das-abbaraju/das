package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.entities.builders.ProjectBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.services.status.StatusCalculatorService;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.entity.employee.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class EmployeeSiteStatusProcessTest {

	public static final int SITE_ID = 90;
	public static final int EMPLOYEE_ID = 671;
	public static final int CORPORATE_ID = 78090;
	public static final List<Integer> CORPORATE_ACCOUNT_IDS = Arrays.asList(CORPORATE_ID);

	// Project Mock Data
	public static final Project PROJECT_NO_SKILLS_NO_ROLES = new ProjectBuilder().accountId(SITE_ID).name("Test Project No Skills No Roles").build();
	public static final Project PROJECT_WITH_SKILLS = new ProjectBuilder().accountId(SITE_ID).name("Test Project Has Skills").build();
	public static final Project PROJECT_NO_SKILLS = new ProjectBuilder().accountId(SITE_ID).name("Test Project No Skills").build();

	// Skill Mock Data
	public static final AccountSkill SITE_REQUIRE_SKILL = new AccountSkillBuilder(CORPORATE_ID).name("Site Skill 1").build();
	public static final AccountSkill CORPORATE_REQUIRED_SKILL = new AccountSkillBuilder(CORPORATE_ID).name("Corp Skill 1").build();
	public static final AccountSkill SKILL_FOR_ROLE_WITH_SKILLS = new AccountSkillBuilder(CORPORATE_ID).name("Skill for Role With Skills").build();
	public static final AccountSkill SITE_ASSIGNMENT_ROLE_SKILL = new AccountSkillBuilder(CORPORATE_ID).name("Site Assignment Role Skill").build();
	public static final AccountSkill PROJECT_REQUIRED_SKILL_2 = new AccountSkillBuilder(CORPORATE_ID).name("Project Skill 2").build();
	public static final AccountSkill PROJECT_REQUIRED_SKILL_1 = new AccountSkillBuilder(CORPORATE_ID).name("Project Skill 1").build();

	// Role Mock Data
	public static final Role SITE_ASSIGNMENT_ROLE = new RoleBuilder().accountId(CORPORATE_ID).name("Site Assignment Role").build();
	public static final Role ROLE_WITH_SKILLS = new RoleBuilder().accountId(CORPORATE_ID).name("Role With Skills").build();
	public static final Role ROLE_NO_SKILLS = new RoleBuilder().accountId(CORPORATE_ID).name("Role No Skills").build();

	private EmployeeSiteStatusProcess employeeSiteStatusProcess;

	@Mock
	private EmployeeEntityService employeeEntityService;
	@Mock
	private ProjectEntityService projectEntityService;
	@Mock
	private RoleEntityService roleEntityService;
	@Mock
	private SkillEntityService skillEntityService;
	@Mock
	private StatusCalculatorService statusCalculatorService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeSiteStatusProcess = new EmployeeSiteStatusProcess();

		Whitebox.setInternalState(employeeSiteStatusProcess, "employeeEntityService", employeeEntityService);
		Whitebox.setInternalState(employeeSiteStatusProcess, "projectEntityService", projectEntityService);
		Whitebox.setInternalState(employeeSiteStatusProcess, "roleEntityService", roleEntityService);
		Whitebox.setInternalState(employeeSiteStatusProcess, "skillEntityService", skillEntityService);
		Whitebox.setInternalState(employeeSiteStatusProcess, "statusCalculatorService", statusCalculatorService);
	}

	@Test
	public void testGetEmployeeSiteStatusResult() throws Exception {
		setupTestGetEmployeeSiteStatusResult();

		EmployeeSiteStatusResult result = employeeSiteStatusProcess.getEmployeeSiteStatusResult(EMPLOYEE_ID, SITE_ID, CORPORATE_ACCOUNT_IDS);

		verifyTestGetEmployeeSiteStatusResult(result);
	}

	public void setupTestGetEmployeeSiteStatusResult() {
		Employee fakeEmployee = new EmployeeBuilder().build();
		when(employeeEntityService.find(EMPLOYEE_ID)).thenReturn(fakeEmployee);

		Set<Project> fakeProjects = buildFakeProjects();
		Map<Project, Set<Role>> fakeProjectRoles = buildFakeProjectRoles();
		Set<AccountSkill> fakeSiteAndCorporateSkills = buildFakeSiteAndCorporateSkills();
		Set<Role> fakeSiteAssignmentRoles = buildFakeSiteAssignmentRoles();
		Map<Project, Set<AccountSkill>> fakeProjectRequiredSkills = buildFakeProjectRequiredSkills();
		Map<Role, Set<AccountSkill>> fakeRoleSkills = buildFakeRoleSkills();
		Map<AccountSkill, SkillStatus> fakeSkillStatusMap = buildFakeSkillStatusMap();

		when(projectEntityService.getProjectsForEmployeeBySiteId(fakeEmployee, SITE_ID)).thenReturn(fakeProjects);

		when(roleEntityService.getRolesForProjectsAndEmployees(anyCollectionOf(Project.class), anyCollectionOf(Employee.class))).thenReturn(fakeProjectRoles);

		when(skillEntityService.getSiteAndCorporateRequiredSkills(SITE_ID, CORPORATE_ACCOUNT_IDS)).thenReturn(fakeSiteAndCorporateSkills);

		when(skillEntityService.getRequiredSkillsForProjects(fakeProjects)).thenReturn(fakeProjectRequiredSkills);

		when(roleEntityService.getSiteRolesForEmployee(fakeEmployee, SITE_ID)).thenReturn(fakeSiteAssignmentRoles);

		when(skillEntityService.getSkillsForRoles(anyCollectionOf(Role.class))).thenReturn(fakeRoleSkills);

		when(statusCalculatorService.getSkillStatuses(any(Employee.class), anyCollectionOf(AccountSkill.class))).thenReturn(fakeSkillStatusMap);

		when(statusCalculatorService.getOverallStatusPerEntity(anyMap())).thenCallRealMethod();

		when(statusCalculatorService.calculateOverallStatus(anyCollectionOf(SkillStatus.class))).thenCallRealMethod();
	}

	private Set<Project> buildFakeProjects() {
		return new HashSet<Project>() {{
			add(PROJECT_NO_SKILLS);

			add(PROJECT_WITH_SKILLS);

			add(PROJECT_NO_SKILLS_NO_ROLES);
		}};
	}

	private Map<Project, Set<Role>> buildFakeProjectRoles() {
		return new HashMap<Project, Set<Role>>() {{

			put(PROJECT_NO_SKILLS,
					new HashSet<>(Arrays.asList(ROLE_NO_SKILLS)));

			put(PROJECT_WITH_SKILLS,
					new HashSet<>(Arrays.asList(ROLE_NO_SKILLS, ROLE_WITH_SKILLS)));

		}};
	}

	private Set<Role> buildFakeSiteAssignmentRoles() {
		return new HashSet<Role>() {{

			add(SITE_ASSIGNMENT_ROLE);

		}};
	}

	private Set<AccountSkill> buildFakeSiteAndCorporateSkills() {
		return new HashSet<AccountSkill>() {{

			add(CORPORATE_REQUIRED_SKILL);

			add(SITE_REQUIRE_SKILL);

		}};
	}

	private Map<Project, Set<AccountSkill>> buildFakeProjectRequiredSkills() {
		return new HashMap<Project, Set<AccountSkill>>() {{

			put(PROJECT_WITH_SKILLS, new HashSet<>(Arrays.asList(PROJECT_REQUIRED_SKILL_1, PROJECT_REQUIRED_SKILL_2)));

		}};
	}

	private Map<Role, Set<AccountSkill>> buildFakeRoleSkills() {
		return new HashMap<Role, Set<AccountSkill>>() {{

			put(SITE_ASSIGNMENT_ROLE, new HashSet<>(Arrays.asList(SITE_ASSIGNMENT_ROLE_SKILL)));

			put(ROLE_WITH_SKILLS, new HashSet<>(Arrays.asList(SKILL_FOR_ROLE_WITH_SKILLS)));

		}};
	}

	private Map<AccountSkill, SkillStatus> buildFakeSkillStatusMap() {
		return new HashMap<AccountSkill, SkillStatus>() {{

			// Project Required Skill Statuses
			put(PROJECT_REQUIRED_SKILL_1, SkillStatus.Completed);
			put(PROJECT_REQUIRED_SKILL_2, SkillStatus.Expiring);

			put(SKILL_FOR_ROLE_WITH_SKILLS, SkillStatus.Expired);
			put(SITE_ASSIGNMENT_ROLE_SKILL, SkillStatus.Expiring);

			put(CORPORATE_REQUIRED_SKILL, SkillStatus.Completed);
			put(SITE_REQUIRE_SKILL, SkillStatus.Completed);
		}};
	}

	private void verifyTestGetEmployeeSiteStatusResult(final EmployeeSiteStatusResult result) {
		verifyProjectStatus(result);

		verifyRoleStatus(result);
	}

	private void verifyProjectStatus(EmployeeSiteStatusResult result) {
		Map<Project, SkillStatus> projectStatusMap = result.getProjectStatuses();

		assertEquals(3, projectStatusMap.size());

		assertEquals(SkillStatus.Completed, projectStatusMap.get(PROJECT_NO_SKILLS));
		assertEquals(SkillStatus.Completed, projectStatusMap.get(PROJECT_NO_SKILLS_NO_ROLES));
		assertEquals(SkillStatus.Expired, projectStatusMap.get(PROJECT_WITH_SKILLS));
	}

	private void verifyRoleStatus(EmployeeSiteStatusResult result) {
		Map<Role, SkillStatus> roleStatusMap = result.getRoleStatuses();

		assertEquals(3, roleStatusMap.size());

		assertEquals(SkillStatus.Completed, roleStatusMap.get(ROLE_NO_SKILLS));
		assertEquals(SkillStatus.Expired, roleStatusMap.get(ROLE_WITH_SKILLS));
		assertEquals(SkillStatus.Expiring, roleStatusMap.get(SITE_ASSIGNMENT_ROLE));
	}
}
