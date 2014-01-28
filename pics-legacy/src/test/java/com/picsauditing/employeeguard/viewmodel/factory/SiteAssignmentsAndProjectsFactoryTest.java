package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.contractor.ProjectStatisticsModel;
import com.picsauditing.employeeguard.viewmodel.contractor.SiteAssignmentStatisticsModel;
import com.picsauditing.jpa.entities.Account;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class SiteAssignmentsAndProjectsFactoryTest {
	public static final String NAME = "Client Site";

	private SiteAssignmentsAndProjectsFactory siteAssignmentsAndProjectsFactory;

	@Mock
	private AccountSkill siteRequired;
	@Mock
	private AccountSkill corpRequired;
	@Mock
	private AccountSkill projectRequired;
	@Mock
	private AccountSkillEmployee siteSkill;
	@Mock
	private AccountSkillEmployee projectSkill;
	@Mock
	private AccountSkillRole projectRoleSkill;
	@Mock
	private Employee employee;
	@Mock
	private Project project;
	@Mock
	private ProjectRole projectRole;
	@Mock
	private ProjectRoleEmployee projectRoleEmployee;
	@Mock
	private Role role;
	@Mock
	private RoleEmployee roleEmployee;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		siteAssignmentsAndProjectsFactory = new SiteAssignmentsAndProjectsFactory();
	}

	@Test
	public void testCreate_NoData() throws Exception {
		Map<AccountModel, Set<Project>> siteProjects = Collections.emptyMap();
		Map<AccountModel, Set<AccountSkill>> siteRequiredSkills = Collections.emptyMap();
		Map<Employee, Set<Role>> employeeRoles = Collections.emptyMap();
		List<AccountSkillEmployee> accountSkillEmployees = Collections.emptyList();

		Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> siteAssignments =
				siteAssignmentsAndProjectsFactory.create(siteProjects, siteRequiredSkills, employeeRoles, accountSkillEmployees);

		assertNotNull(siteAssignments);
		assertTrue(siteAssignments.isEmpty());
	}

	@Test
	public void testCreate_WithData() throws Exception {
		AccountModel accountModel = new AccountModel.Builder().id(Account.PicsID).name(NAME).build();

		Map<AccountModel, Set<Project>> siteProjects = getSiteProjects(accountModel);
		Map<AccountModel, Set<AccountSkill>> siteAndCorporateRequiredSkills = getSiteAndCorporateRequiredSkills(accountModel);
		Map<Employee, Set<Role>> employeeRoles = getEmployeeRoles();
		List<AccountSkillEmployee> accountSkillEmployees = getEmployeeSkills();

		linkMocks(accountModel);

		Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> siteAssignments =
				siteAssignmentsAndProjectsFactory.create(siteProjects, siteAndCorporateRequiredSkills, employeeRoles, accountSkillEmployees);

		performAssertions(accountModel, siteAssignments);
	}

	private Map<AccountModel, Set<Project>> getSiteProjects(AccountModel accountModel) {
		Map<AccountModel, Set<Project>> siteProjects = new HashMap<>();
		siteProjects.put(accountModel, new HashSet<>(Arrays.asList(project))); return siteProjects;
	}

	private Map<AccountModel, Set<AccountSkill>> getSiteAndCorporateRequiredSkills(AccountModel accountModel) {
		Map<AccountModel, Set<AccountSkill>> siteRequiredSkills = new HashMap<>();
		siteRequiredSkills.put(accountModel, new HashSet<>(Arrays.asList(siteRequired, corpRequired)));
		return siteRequiredSkills;
	}

	private Map<Employee, Set<Role>> getEmployeeRoles() {
		Map<Employee, Set<Role>> employeeRoles = new HashMap<>();
		employeeRoles.put(employee, new HashSet<>(Arrays.asList(role))); return employeeRoles;
	}

	private List<AccountSkillEmployee> getEmployeeSkills() {
		List<AccountSkillEmployee> accountSkillEmployees = new ArrayList<>();
		accountSkillEmployees.add(siteSkill);
		accountSkillEmployees.add(projectSkill);
		return accountSkillEmployees;
	}

	private void linkMocks(AccountModel accountModel) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 15);

		when(employee.getRoles()).thenReturn(Arrays.asList(roleEmployee));
		when(siteSkill.getEmployee()).thenReturn(employee);
		when(siteSkill.getEndDate()).thenReturn(calendar.getTime());
		when(siteSkill.getSkill()).thenReturn(siteRequired);

		calendar.add(Calendar.MONTH, 3);

		when(project.getRoles()).thenReturn(Arrays.asList(projectRole));
		when(projectSkill.getEmployee()).thenReturn(employee);
		when(projectSkill.getSkill()).thenReturn(projectRequired);
		when(projectSkill.getEndDate()).thenReturn(calendar.getTime());
		when(projectRole.getRole()).thenReturn(role);
		when(projectRoleEmployee.getEmployee()).thenReturn(employee);
		when(projectRoleEmployee.getProjectRole()).thenReturn(projectRole);
		when(projectRoleSkill.getSkill()).thenReturn(projectRequired);
		when(role.getAccountId()).thenReturn(accountModel.getId());
		when(role.getSkills()).thenReturn(Arrays.asList(projectRoleSkill));
		when(roleEmployee.getEmployee()).thenReturn(employee);
		when(roleEmployee.getRole()).thenReturn(role);
	}

	private void performAssertions(AccountModel accountModel, Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> siteAssignments) {
		assertNotNull(siteAssignments);
		assertFalse(siteAssignments.isEmpty());

		for (Map.Entry<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> siteAssignmentSet : siteAssignments.entrySet()) {
			SiteAssignmentStatisticsModel siteModel = siteAssignmentSet.getKey();

			assertEquals(1, siteModel.getCompleted());
			assertEquals(1, siteModel.getExpiring());
			assertEquals(0, siteModel.getExpired());
			assertEquals(accountModel, siteModel.getSite());

			assertNull(siteAssignmentSet.getValue());

			for (ProjectStatisticsModel projectModel : siteAssignmentSet.getValue()) {
				assertNotNull(projectModel.getProject());
				assertEquals(NAME, projectModel.getProject().getSiteName());
				assertNotNull(projectModel.getAssignments());
				assertEquals(1, projectModel.getAssignments().getComplete());
				assertEquals(0, projectModel.getAssignments().getExpiring());
				assertEquals(0, projectModel.getAssignments().getExpired());
			}
		}
	}
}
