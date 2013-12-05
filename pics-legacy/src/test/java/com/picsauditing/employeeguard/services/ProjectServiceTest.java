package com.picsauditing.employeeguard.services;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.daos.AccountGroupDAO;
import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.daos.ProjectDAO;
import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.builders.AccountGroupBuilder;
import com.picsauditing.employeeguard.forms.operator.ProjectNameSkillsForm;
import com.picsauditing.employeeguard.forms.operator.ProjectRolesForm;
import com.picsauditing.employeeguard.services.factory.AccountServiceFactory;
import com.picsauditing.employeeguard.services.factory.AccountSkillEmployeeServiceFactory;
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
    private AccountGroupDAO accountGroupDAO;
    @Mock
    private AccountSkillDAO accountSkillDAO;
    @Mock
    private EmployeeDAO employeeDAO;
    @Mock
    private ProjectDAO projectDAO;

    private AccountService accountService;
    private AccountSkillEmployeeService accountSkillEmployeeService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        projectService = new ProjectService();
        accountService = AccountServiceFactory.getAccountService();
        accountSkillEmployeeService = AccountSkillEmployeeServiceFactory.getAccountSkillEmployeeService();

        Whitebox.setInternalState(projectService, "accountGroupDAO", accountGroupDAO);
        Whitebox.setInternalState(projectService, "accountService", accountService);
        Whitebox.setInternalState(projectService, "accountSkillDAO", accountSkillDAO);
        Whitebox.setInternalState(projectService, "accountSkillEmployeeService", accountSkillEmployeeService);
        Whitebox.setInternalState(projectService, "employeeDAO", employeeDAO);
        Whitebox.setInternalState(projectService, "projectDAO", projectDAO);
    }

    @Test
    public void testGetProject() throws Exception {
        projectService.getProject("1", Account.PicsID);
        verify(projectDAO).findProjectByAccount(1, Account.PicsID);
    }

    @Test
    public void testGetProjectsForAccount() throws Exception {
        projectService.getProjectsForAccount(Account.PicsID);
        verify(projectDAO).findByAccount(Account.PicsID);
    }

    @Test
    public void testSave() throws Exception {
        Project project = new Project();
        projectService.save(project, Account.PicsID, Identifiable.SYSTEM);

        verify(projectDAO).save(project);
        assertNotNull(project.getCreatedDate());
        assertEquals(Identifiable.SYSTEM, project.getCreatedBy());
    }

    @Test
    public void testUpdate_NameSkills() throws Exception {
        when(employeeDAO.findByProject(any(Project.class))).thenReturn(Collections.<Employee>emptyList());

        int skillId = 1;

        List<AccountSkill> skills = new ArrayList<>();
        skills.add(new AccountSkill(skillId, Account.PicsID));
        when(accountSkillDAO.findSkillsByAccountsAndIds(Arrays.asList(Account.PICS_CORPORATE_ID), Arrays.asList(skillId))).thenReturn(skills);

        Project project = new Project();
        project.setAccountId(Account.PicsID);
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
        String groupName = "Group";

        List<AccountGroup> groups = new ArrayList<>();
        groups.add(new AccountGroupBuilder().name(groupName).build());
        when(accountGroupDAO.findGroupByAccountIdsAndNames(Arrays.asList(Account.PICS_CORPORATE_ID), Arrays.asList(groupName))).thenReturn(groups);

        Project project = new Project();
        project.setAccountId(Account.PicsID);
        ProjectRolesForm projectRolesForm = new ProjectRolesForm();
        projectRolesForm.setRoles(new String[]{groupName});

        projectService.update(project, projectRolesForm, Identifiable.SYSTEM);

        verify(projectDAO).save(project);
        assertNotNull(project.getUpdatedDate());
        assertEquals(Identifiable.SYSTEM, project.getUpdatedBy());
        assertFalse(project.getRoles().isEmpty());
    }

    @Test
    public void testDelete() throws Exception {
        Project project = new Project();
        when(projectDAO.findProjectByAccount(1, Account.PicsID)).thenReturn(project);

        projectService.delete("1", Account.PicsID, Identifiable.SYSTEM);

        verify(projectDAO).delete(project);
    }

    @Test
    public void testSearch() throws Exception {
        projectService.search("search", Account.PicsID);
        verify(projectDAO).search("search", Account.PicsID);
    }

    @Test
    public void testSearch_MissingSearchTerm() throws Exception {
        List<Project> results = projectService.search(null, Account.PicsID);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}
