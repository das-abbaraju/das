package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.*;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RoleEntityServiceTest {

	public static final String TEST_ROLE_NAME = "Test Role";

	public static final int ROLE_ID_1 = 1;
	public static final int ROLE_ID_2 = 2;
	public static final int ACCOUNT_ID = 465;
	public static final int USER_ID = 78;
	public static final int ROLE_ID_78 = 78;

	RoleEntityService roleEntityService;

	@Mock
	private ProjectRoleDAO projectRoleDAO;
	@Mock
	private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;
	@Mock
	private RoleDAO roleDAO;
	@Mock
	private SiteAssignmentDAO siteAssignmentDAO;

	@Before
	public void setUp() throws Exception {
		roleEntityService = new RoleEntityService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(roleEntityService, "projectRoleDAO", projectRoleDAO);
		Whitebox.setInternalState(roleEntityService, "projectRoleEmployeeDAO", projectRoleEmployeeDAO);
		Whitebox.setInternalState(roleEntityService, "roleDAO", roleDAO);
		Whitebox.setInternalState(roleEntityService, "siteAssignmentDAO", siteAssignmentDAO);
	}

  @Test
  public void testFindRolesForCorporateAccounts_NoAccountsProvided(){
    List<Role> roles=roleEntityService.findRolesForCorporateAccounts(CollectionUtils.EMPTY_COLLECTION);
    assertTrue(roles.isEmpty());
  }

  @Test
  public void testFindRolesForCorporateAccounts_AccountProvided(){
    roleEntityService.findRolesForCorporateAccounts(Arrays.asList(ACCOUNT_ID));
    verify(roleDAO).findByAccounts(anyCollection());
  }

	@Test
	public void testFind() throws Exception {
		when(roleDAO.find(ROLE_ID_1)).thenReturn(buildFakeRole(ROLE_ID_1, ACCOUNT_ID, TEST_ROLE_NAME));

		Role result = roleEntityService.find(ROLE_ID_1);

		assertEquals(ROLE_ID_1, result.getId());
		assertEquals("Test Role", result.getName());
	}

	@Test(expected = NullPointerException.class)
	public void testFind_NullId() throws Exception {
		roleEntityService.find(null);
	}

	@Test
	public void testGetRolesForProjects() {
		List<ProjectRole> fakeProjectRoles = buildFakeProjectRoles();
		when(projectRoleDAO.findByProjects(anyListOf(Project.class))).thenReturn(fakeProjectRoles);

		Map<Project, Set<Role>> results = roleEntityService.getRolesForProjects(Arrays.asList(new Project()));

		verifyTestGetRolesForProjects(results);
	}

	private List<ProjectRole> buildFakeProjectRoles() {
		return Arrays.asList(
				new ProjectRoleBuilder()
						.project(new ProjectBuilder().accountId(ACCOUNT_ID).name("Test Project").build())
						.role(new RoleBuilder().accountId(ACCOUNT_ID).name("Test Role").build())
						.build()
		);
	}

	private void verifyTestGetRolesForProjects(final Map<Project, Set<Role>> projectRoleMap) {
		assertEquals(1, projectRoleMap.size());

		for (Project project : projectRoleMap.keySet()) {
			assertEquals("Test Project", project.getName());
			for (Role role : projectRoleMap.get(project)) {
				assertEquals("Test Role", role.getName());
			}
		}
	}

	@Test
	public void testGetProjectRolesForEmployees() {
		when(projectRoleEmployeeDAO.findByEmployeesAndSiteIds(anyCollectionOf(Employee.class), anyCollectionOf(Integer.class)))
				.thenReturn(buildFakeProjectRoleEmployees());

		Map<Employee, Set<Role>> results = roleEntityService.getProjectRolesForEmployees(Arrays.asList(new Employee()), 12);

		verifyTestGetProjectRolesForEmployees(results);
	}

	private List<ProjectRoleEmployee> buildFakeProjectRoleEmployees() {
		return Arrays.asList(
			new ProjectRoleEmployeeBuilder()
					.projectRole(new ProjectRoleBuilder()
							.project(new ProjectBuilder().accountId(ACCOUNT_ID).name("Building Renovation").build())
							.role(new RoleBuilder().accountId(ACCOUNT_ID).name("Lead Foreman").build())
							.build())
					.employee(new EmployeeBuilder().accountId(781).email("tester@something.com").build())
					.build()
		);
	}

	private void verifyTestGetProjectRolesForEmployees(final Map<Employee, Set<Role>> employeeRoleMap) {
		assertEquals(1, employeeRoleMap.size());

		for (Employee employee : employeeRoleMap.keySet()) {
			assertEquals("tester@something.com", employee.getEmail());
			for (Role role : employeeRoleMap.get(employee)) {
				assertEquals("Lead Foreman", role.getName());
			}
		}
	}

	@Test
	public void testSearch_NoSearchTermProvided() throws Exception {
		List<Role> result = roleEntityService.search(null, ACCOUNT_ID);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testSearch() throws Exception {
		List<Role> result = roleEntityService.search(null, ACCOUNT_ID);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testSave() throws Exception {
		Role fakeRole = buildFakeRole(ROLE_ID_1, ACCOUNT_ID, TEST_ROLE_NAME);
		when(roleDAO.save(fakeRole)).thenReturn(fakeRole);

		Role group = roleEntityService.save(fakeRole, new EntityAuditInfo.Builder()
				.appUserId(USER_ID)
				.timestamp(new Date())
				.build());

		assertEquals(1, group.getId());
	}

	@Test
	public void testUpdate() throws Exception {
		Role fakeRole = setupTestUpdate();

		Role group = roleEntityService.update(fakeRole, new EntityAuditInfo.Builder()
				.appUserId(USER_ID)
				.timestamp(new Date())
				.build());

		verifyTestUpdate(group);
	}

	private Role setupTestUpdate() {
		Role fakeRole = buildFakeRole(ROLE_ID_2, ACCOUNT_ID, "Updated Role");
		Role groupToUpdate = buildFakeRole(ROLE_ID_2, ACCOUNT_ID, "Original Role");

		when(roleDAO.find(ROLE_ID_2)).thenReturn(groupToUpdate);
		when(roleDAO.save(groupToUpdate)).thenReturn(new Role());

		return fakeRole;
	}

	private void verifyTestUpdate(Role group) {
		assertNotNull(group);

		ArgumentCaptor<Role> argumentCaptor = ArgumentCaptor.forClass(Role.class);
		verify(roleDAO).save(argumentCaptor.capture());
		assertEquals("Updated Role", argumentCaptor.getValue().getName());
	}

	@Test
	public void testDelete() throws Exception {
		Role fakeRole = buildFakeRole(ROLE_ID_78, ACCOUNT_ID, TEST_ROLE_NAME);

		roleEntityService.delete(fakeRole);

		verify(roleDAO).delete(fakeRole);
	}

	@Test(expected = NullPointerException.class)
	public void testDelete_NullEntity() throws Exception {
		roleEntityService.delete(null);
	}

	@Test(expected = NullPointerException.class)
	public void testDeleteById_NullId() throws Exception {
		roleEntityService.deleteById(null);
	}

	@Test
	public void testDeleteById() throws Exception {
		Role fakeRole = buildFakeRole(ROLE_ID_78, ACCOUNT_ID, TEST_ROLE_NAME);
		when(roleDAO.find(ROLE_ID_78)).thenReturn(fakeRole);

		roleEntityService.deleteById(ROLE_ID_78);

		verify(roleDAO).delete(fakeRole);
	}

	private Role buildFakeRole(final int id, final int accountId, final String name) {
		return new RoleBuilder()
				.id(id)
				.accountId(accountId)
				.name(name)
				.build();
	}
}
