package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.ProjectDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleEmployeeDAO;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectCompany;
import com.picsauditing.employeeguard.entities.ProjectRoleEmployee;
import com.picsauditing.employeeguard.entities.builders.*;
import com.picsauditing.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.*;

import static com.picsauditing.employeeguard.services.entity.EntityAuditInfoConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectEntityServiceTest {

	private ProjectEntityService projectEntityService;

	@Mock
	private ProjectDAO projectDAO;
	@Mock
	private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;

	@Before
	public void setUp() throws Exception {
		projectEntityService = new ProjectEntityService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(projectEntityService, "projectDAO", projectDAO);
		Whitebox.setInternalState(projectEntityService, "projectRoleEmployeeDAO", projectRoleEmployeeDAO);
	}

	@Test(expected = NullPointerException.class)
	public void testFind_NullId() throws Exception {
		projectEntityService.find(null);
	}

	@Test
	public void testFind() throws Exception {
		Project expected = buildFakeProject(null);

		when(projectDAO.find(expected.getId())).thenReturn(expected);

		Project result = projectEntityService.find(expected.getId());

		assertNotNull(result);
		assertEquals(expected.getId(), result.getId());
	}

	@Test
	public void testGetProjectsForEmployees() {
		Project fakeProject = buildFakeProject(null);
		List<Employee> fakeEmployees = buildFakeEmployees();
		when(projectRoleEmployeeDAO.findByEmployeesAndSiteIds(anyListOf(Employee.class), anyListOf(Integer.class)))
				.thenReturn(buildFakeProjectRoleEmployees(fakeEmployees.get(0), fakeProject));

		Map<Employee, Set<Project>> result = projectEntityService.getProjectsForEmployeesBySiteId(Arrays.asList(new Employee()),
				ACCOUNT_ID);

		verifyTestGetProjectsForEmployees(result);
	}

	private void verifyTestGetProjectsForEmployees(Map<Employee, Set<Project>> result) {
		assertEquals(1, result.size());
		for (Employee employee : result.keySet()) {
			assertEquals("Bob", employee.getFirstName());
			assertEquals(123, employee.getAccountId());
			assertEquals(1, result.get(employee).size());
			for (Project project : result.get(employee)) {
				assertEquals("Test Project", project.getName());
			}
		}
	}

	@Test
	public void testGetProjectsForEmployee() {
		List<Project> projects = Arrays.asList(buildFakeProject(null));
		when(projectDAO.findByEmployee(any(Employee.class))).thenReturn(projects);

		Set<Project> result = projectEntityService.getProjectsForEmployee(new Employee());

		assertEquals(projects, result);
	}

	@Test
	public void testGetProjectByRoleAndAccount() {
		Project fakeProject = buildFakeProject(null);

		when(projectDAO.findProjectByRoleAndAccount(anyInt(), anyInt())).thenReturn(fakeProject);

		assertEquals(fakeProject, fakeProject);
	}

	@Test
	public void testGetContractorIdsForProject() {
		Project fakeProject = buildFakeProject(buildFakeProjectCompanies());

		Set<Integer> result = projectEntityService.getContractorIdsForProject(fakeProject);

		assertTrue(Utilities.collectionsAreEqual(Arrays.asList(456, 789), result));
	}

	private List<ProjectCompany> buildFakeProjectCompanies() {
		return Arrays.asList(
				new ProjectCompanyBuilder().accountId(456).build(),
				new ProjectCompanyBuilder().accountId(789).build()
		);
	}

	private List<Employee> buildFakeEmployees() {
		return Arrays.asList(new EmployeeBuilder()
				.accountId(123)
				.firstName("Bob")
				.lastName("Test")
				.email("bob.test@test.com")
				.build());
	}

	private List<ProjectRoleEmployee> buildFakeProjectRoleEmployees(final Employee employee, final Project project) {
		return Arrays.asList(
				new ProjectRoleEmployeeBuilder()
						.projectRole(new ProjectRoleBuilder().project(project).build())
						.employee(employee)
						.build()
		);
	}

	@Test
	public void testGetAllProjectsForSite() {
		List<Project> fakeProjects = buildFakeProjects();
		when(projectDAO.findByAccount(ACCOUNT_ID)).thenReturn(fakeProjects);

		List<Project> result = projectEntityService.getAllProjectsForSite(ACCOUNT_ID);

		assertTrue(Utilities.collectionsAreEqual(buildFakeProjects(), result));
	}

	@Test
	public void testSearch_NullEmpty() throws Exception {
		List<Project> nullSearch = projectEntityService.search(null, ACCOUNT_ID);
		assertNotNull(nullSearch);
		assertTrue(nullSearch.isEmpty());

		List<Project> emptySearch = projectEntityService.search(Strings.SINGLE_SPACE, ACCOUNT_ID);
		assertNotNull(emptySearch);
		assertTrue(emptySearch.isEmpty());
	}

	@Test
	public void testSearch() throws Exception {
		when(projectDAO.search(SEARCH_TERM, ACCOUNT_ID)).thenReturn(Arrays.asList(buildFakeProject(null)));

		List<Project> result = projectEntityService.search(SEARCH_TERM, ACCOUNT_ID);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(ENTITY_ID, result.get(0).getId());
	}

	@Test
	public void testSave() throws Exception {
		Project fakeProject = buildFakeProject(null);

		when(projectDAO.save(fakeProject)).thenReturn(fakeProject);

		Project result = projectEntityService.save(fakeProject, CREATED);

		verify(projectDAO).save(fakeProject);
		assertEquals(USER_ID, result.getCreatedBy());
		assertEquals(CREATED_DATE, result.getCreatedDate());
		assertNull(result.getUpdatedDate());
	}

	@Test
	public void testUpdate() throws Exception {
		Project fakeProject = buildFakeProject(null);
		fakeProject.setName("Fake Project");
		fakeProject.setLocation("Fake Location");

		Project updatedProject = buildFakeProject(null);

		when(projectDAO.find(updatedProject.getId())).thenReturn(updatedProject);
		when(projectDAO.save(updatedProject)).thenReturn(updatedProject);

		Project result = projectEntityService.update(fakeProject, UPDATED);

		verify(projectDAO).find(updatedProject.getId());
		verify(projectDAO).save(updatedProject);
		assertEquals(updatedProject.getId(), result.getId());
		assertEquals(fakeProject.getName(), result.getName());
		assertEquals(fakeProject.getLocation(), result.getLocation());
		assertEquals(USER_ID, result.getUpdatedBy());
		assertEquals(UPDATED_DATE, result.getUpdatedDate());
	}

	@Test
	public void testDelete() throws Exception {
		Project fakeProject = buildFakeProject(null);

		projectEntityService.delete(fakeProject);

		verify(projectDAO).delete(fakeProject);
	}

	@Test
	public void testDeleteById() throws Exception {
		Project fakeProject = buildFakeProject(null);

		when(projectDAO.find(fakeProject.getId())).thenReturn(fakeProject);

		projectEntityService.deleteById(fakeProject.getId());

		verify(projectDAO).delete(fakeProject);
	}

	private Project buildFakeProject(final List<ProjectCompany> companies) {
		return new ProjectBuilder()
				.id(ENTITY_ID)
				.name("Test Project")
				.companies(companies)
				.build();
	}

}
