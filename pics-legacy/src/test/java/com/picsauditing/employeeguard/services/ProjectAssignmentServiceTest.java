package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectAssignmentServiceTest {

	public static final int SITE_ID = 567;

	ProjectAssignmentService projectAssignmentService;

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

		projectAssignmentService = new ProjectAssignmentService();

		Whitebox.setInternalState(projectAssignmentService, "accountService", accountService);
		Whitebox.setInternalState(projectAssignmentService, "employeeEntityService", employeeEntityService);
		Whitebox.setInternalState(projectAssignmentService, "projectEntityService", projectEntityService);
		Whitebox.setInternalState(projectAssignmentService, "roleEntityService", roleEntityService);
		Whitebox.setInternalState(projectAssignmentService, "skillEntityService", skillEntityService);
	}

	@Test
	public void testGetEmployeeSkillsForProjectsUnderSite() throws Exception {
		List<Project> projects = new ArrayList<>();
		when(projectEntityService.getAllProjectsForSite(SITE_ID)).thenReturn(projects);

		Map<Project, Map<Employee, Set<AccountSkill>>> result = projectAssignmentService
				.getEmployeeSkillsForProjectsUnderSite(SITE_ID);

		verifyTestGetEmployeeSkillsForProjectsUnderSite(projects, result);
	}

	private void verifyTestGetEmployeeSkillsForProjectsUnderSite(final List<Project> projects,
																 final Map<Project, Map<Employee, Set<AccountSkill>>> result) {
		assertNotNull(result);

		verify(projectEntityService).getAllProjectsForSite(SITE_ID);
		verify(employeeEntityService).getEmployeesByProjects(projects);
		verify(skillEntityService).getRequiredSkillsForProjects(projects);
		verify(roleEntityService).getRolesForProjects(projects);
		verify(skillEntityService).getSkillsForRoles(anyCollectionOf(Role.class));
		verify(skillEntityService).getSiteRequiredSkills(anyInt(), anyListOf(Integer.class));

		verifyCommonServiceCalls(projects);
	}

	@Test
	public void testGetEmployeesByRole() throws Exception {
		List<Project> projects = new ArrayList<>();

		Map<Role, Set<Employee>> result = projectAssignmentService.getEmployeesByRole(SITE_ID, projects);

		verifyTestGetEmployeesByRole(projects, result);
	}

	private void verifyTestGetEmployeesByRole(final List<Project> projects, final Map<Role, Set<Employee>> result) {
		assertNotNull(result);

		verifyCommonServiceCalls(projects);
	}

	private void verifyCommonServiceCalls(final List<Project> projects) {
		verify(employeeEntityService).getEmployeesByProjectRoles(projects);
		verify(employeeEntityService).getEmployeesBySiteRoles(Arrays.asList(SITE_ID));
	}
}
