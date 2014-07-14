package com.picsauditing.employeeguard.services;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.daos.ProjectDAO;
import com.picsauditing.employeeguard.daos.RoleDAO;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.forms.operator.ProjectNameSkillsForm;
import com.picsauditing.employeeguard.forms.operator.ProjectRolesForm;
import com.picsauditing.employeeguard.services.factory.AccountServiceFactory;
import com.picsauditing.jpa.entities.Account;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectServiceTest extends PicsTranslationTest {

	private ProjectService projectService;

	@Mock
	private AccountSkillDAO accountSkillDAO;
	@Mock
	private EmployeeDAO employeeDAO;
	@Mock
	private ProjectDAO projectDAO;
	@Mock
	private RoleDAO roleDAO;

	private AccountService accountService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		projectService = new ProjectService();
		accountService = AccountServiceFactory.getAccountService();

		Whitebox.setInternalState(projectService, "accountService", accountService);
		Whitebox.setInternalState(projectService, "accountSkillDAO", accountSkillDAO);
		Whitebox.setInternalState(projectService, "employeeDAO", employeeDAO);
		Whitebox.setInternalState(projectService, "projectDAO", projectDAO);
		Whitebox.setInternalState(projectService, "roleDAO", roleDAO);
	}

	@Test
	public void testGetProject() throws Exception {
		projectService.getProject(1, Account.PICS_ID);
		verify(projectDAO).findProjectByAccount(1, Account.PICS_ID);
	}

	@Test
	public void testGetProjectsForAccount() throws Exception {
		projectService.getProjectsForAccount(Account.PICS_ID);
		verify(projectDAO).findByAccount(Account.PICS_ID);
	}

	@Test
	public void testSave() throws Exception {
		Project project = new Project();
		projectService.save(project, Account.PICS_ID, Identifiable.SYSTEM);

		verify(projectDAO).save(project);
		assertNotNull(project.getCreatedDate());
		assertEquals(Identifiable.SYSTEM, project.getCreatedBy());
	}

	@Test
	public void testUpdate_NameSkills() throws Exception {
		when(employeeDAO.findByProject(any(Project.class))).thenReturn(Collections.<Employee>emptyList());

		int skillId = 1;

		List<AccountSkill> skills = new ArrayList<>();
		skills.add(new AccountSkill(skillId, Account.PICS_ID));
		when(accountSkillDAO.findSkillsByAccountsAndIds(Arrays.asList(Account.PICS_CORPORATE_ID), Arrays.asList(skillId))).thenReturn(skills);

		Project project = new Project();
		project.setAccountId(Account.PICS_ID);
		ProjectNameSkillsForm projectNameSkillsForm = new ProjectNameSkillsForm();
		projectNameSkillsForm.setName("Project");
		projectNameSkillsForm.setSkills(new int[]{skillId});

		projectService.update(project, projectNameSkillsForm, Identifiable.SYSTEM);

		verify(projectDAO).save(project);
		assertEquals("Project", project.getName());
		assertNotNull(project.getUpdatedDate());
		assertEquals(Identifiable.SYSTEM, project.getUpdatedBy());
		assertFalse(project.getSkills().isEmpty());
	}

	@Test
	public void testUpdate_Roles() throws Exception {
		String roleName = "The Role";

		List<Role> roles = new ArrayList<>();
		Role role = new Role();
		role.setName(roleName);
		roles.add(role);

		when(roleDAO.findRoleByAccountIdsAndNames(Arrays.asList(Account.PICS_CORPORATE_ID), Arrays.asList(roleName))).thenReturn(roles);

		Project project = new Project();
		project.setAccountId(Account.PICS_ID);
		ProjectRolesForm projectRolesForm = new ProjectRolesForm();
		projectRolesForm.setRoles(new String[]{roleName});

		projectService.update(project, projectRolesForm, Identifiable.SYSTEM);

		verify(projectDAO).save(project);
		assertNotNull(project.getUpdatedDate());
		assertEquals(Identifiable.SYSTEM, project.getUpdatedBy());
		assertFalse(project.getRoles().isEmpty());
	}

	@Test
	public void testDelete() throws Exception {
		Project project = new Project();
		when(projectDAO.findProjectByAccount(1, Account.PICS_ID)).thenReturn(project);

		projectService.delete(1, Account.PICS_ID);

		verify(projectDAO).delete(project);
	}

	@Test
	public void testSearch() throws Exception {
		projectService.search("search", Account.PICS_ID);
		verify(projectDAO).search("search", Account.PICS_ID);
	}

	@Test
	public void testSearch_MissingSearchTerm() throws Exception {
		List<Project> results = projectService.search(null, Account.PICS_ID);
		assertNotNull(results);
		assertTrue(results.isEmpty());
	}
}
