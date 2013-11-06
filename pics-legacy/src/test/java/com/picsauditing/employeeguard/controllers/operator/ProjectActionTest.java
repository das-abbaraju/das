package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.forms.factory.ProjectInfoFactory;
import com.picsauditing.employeeguard.forms.operator.ProjectForm;
import com.picsauditing.employeeguard.forms.operator.ProjectInfo;
import com.picsauditing.employeeguard.forms.operator.ProjectNameSkillsForm;
import com.picsauditing.employeeguard.forms.operator.ProjectRolesForm;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.GroupService;
import com.picsauditing.employeeguard.services.ProjectService;
import com.picsauditing.employeeguard.services.SkillService;
import com.picsauditing.employeeguard.services.factory.AccountServiceFactory;
import com.picsauditing.employeeguard.services.factory.GroupServiceFactory;
import com.picsauditing.employeeguard.services.factory.ProjectServiceFactory;
import com.picsauditing.employeeguard.services.factory.SkillServiceFactory;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.jpa.entities.Account;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectActionTest extends PicsActionTest {
	public static final String ID = "id";
	public static final String LIST_URL = "/employee-guard/operator/project";
	public static final String CREATE_URL = "/employee-guard/operator/project/create";
	private ProjectAction projectAction;

	private AccountService accountService;
	private GroupService roleService;
	private ProjectService projectService;
	private SkillService skillService;

	@Mock
	private FormBuilderFactory formBuilderFactory;
	@Mock
	private ProjectInfoFactory projectInfoFactory;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		projectAction = new ProjectAction();
		accountService = AccountServiceFactory.getAccountService();
		projectService = ProjectServiceFactory.getProjectService();
		roleService = GroupServiceFactory.getGroupService();
		skillService = SkillServiceFactory.getSkillService();

		super.setUp(projectAction);

		Whitebox.setInternalState(projectAction, "accountService", accountService);
		Whitebox.setInternalState(projectAction, "formBuilderFactory", formBuilderFactory);
		Whitebox.setInternalState(projectAction, "projectService", projectService);
		Whitebox.setInternalState(projectAction, "roleService", roleService);
		Whitebox.setInternalState(projectAction, "skillService", skillService);

		when(permissions.getAccountId()).thenReturn(Account.PicsID);
		when(permissions.getAppUserID()).thenReturn(Identifiable.SYSTEM);
		when(formBuilderFactory.getProjectInfoFactory()).thenReturn(projectInfoFactory);
	}

	@Test
	public void testIndex() throws Exception {
		ProjectInfo projectInfo = new ProjectInfo.Builder().id(1).name("Project").build();
		when(projectInfoFactory.build(anyList())).thenReturn(Arrays.asList(projectInfo));

		assertEquals(PicsRestActionSupport.LIST, projectAction.index());

		verify(projectService).getProjectsForAccount(Account.PicsID);
		assertNotNull(projectAction.getProjects());
		assertFalse(projectAction.getProjects().isEmpty());
	}

	@Test
	public void testIndex_Search() throws Exception {
		ProjectInfo projectInfo = new ProjectInfo.Builder().id(1).name("Project").build();
		when(projectInfoFactory.build(anyList())).thenReturn(Arrays.asList(projectInfo));

		SearchForm searchForm = new SearchForm();
		searchForm.setSearchTerm("search");

		projectAction.setSearchForm(searchForm);

		assertEquals(PicsRestActionSupport.LIST, projectAction.index());

		verify(projectService).search("search", Account.PicsID);
		assertNotNull(projectAction.getProjects());
		assertFalse(projectAction.getProjects().isEmpty());
	}

	@Test
	public void testShow() throws Exception {
		ProjectInfo projectInfo = new ProjectInfo.Builder().id(1).name("Name").build();

		when(accountService.getAccountsByIds(any(List.class))).thenReturn(Collections.<AccountModel>emptyList());
		when(projectInfoFactory.build(any(Project.class))).thenReturn(projectInfo);

		projectAction.setId(ID);

		assertEquals(PicsRestActionSupport.SHOW, projectAction.show());

		verify(projectService).getProject(ID, Account.PicsID);
		assertNotNull(projectAction.getProject());
	}

	@Test
	public void testCreate() throws Exception {
		assertEquals(PicsRestActionSupport.CREATE, projectAction.create());
		assertNotNull(projectAction.getProjectRoles());
		assertNotNull(projectAction.getProjectSkills());
	}

	@Test
	public void testEditProjectNameSkillsSection() throws Exception {
		projectAction.setId(ID);

		assertEquals("name-skills-form", projectAction.editProjectNameSkillsSection());
		verify(projectService).getProject(ID, Account.PicsID);
		assertNotNull(projectAction.getProjectNameSkillsForm());
		assertNotNull(projectAction.getProjectSkills());
	}

	@Test
	public void testEditProjectJobRolesSection() throws Exception {
		projectAction.setId(ID);

		assertEquals("job-roles-form", projectAction.editProjectJobRolesSection());
		verify(projectService).getProject(ID, Account.PicsID);
		assertNotNull(projectAction.getProjectRolesForm());
		assertNotNull(projectAction.getProjectRoles());
	}

	@Test
	public void testInsert() throws Exception {
		projectAction.setProjectForm(new ProjectForm());

		assertEquals(PicsActionSupport.REDIRECT, projectAction.insert());
		assertEquals(LIST_URL, projectAction.getUrl());
		verify(projectService).save(any(Project.class), eq(Account.PicsID), eq(Identifiable.SYSTEM));
	}

	@Test
	public void testInsert_AddAnother() throws Exception {
		ProjectForm projectForm = new ProjectForm();
		projectForm.setAddAnother(true);
		projectAction.setProjectForm(projectForm);

		assertEquals(PicsActionSupport.REDIRECT, projectAction.insert());
		assertEquals(CREATE_URL, projectAction.getUrl());
		verify(projectService).save(any(Project.class), eq(Account.PicsID), eq(Identifiable.SYSTEM));
	}

	@Test
	public void testUpdate_NameSkills() throws Exception {
		projectAction.setProjectNameSkillsForm(new ProjectNameSkillsForm());

		assertEquals(PicsActionSupport.REDIRECT, projectAction.update());
		assertTrue(projectAction.getUrl().startsWith(LIST_URL + "/"));
		verify(projectService).update(any(Project.class), any(ProjectNameSkillsForm.class), eq(Identifiable.SYSTEM));
	}

	@Test
	public void testUpdate_Roles() throws Exception {
		projectAction.setProjectRolesForm(new ProjectRolesForm());

		assertEquals(PicsActionSupport.REDIRECT, projectAction.update());
		assertTrue(projectAction.getUrl().startsWith(LIST_URL + "/"));
		verify(projectService).update(any(Project.class), any(ProjectRolesForm.class), eq(Identifiable.SYSTEM));
	}

	@Test
	public void testDelete() throws Exception {
		projectAction.setId(ID);

		assertEquals(PicsActionSupport.REDIRECT, projectAction.delete());
		assertEquals(LIST_URL, projectAction.getUrl());
	}
}
