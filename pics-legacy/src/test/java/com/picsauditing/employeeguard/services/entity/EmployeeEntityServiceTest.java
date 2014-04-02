package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleEmployeeDAO;
import com.picsauditing.employeeguard.daos.RoleEmployeeDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.*;
import com.picsauditing.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static com.picsauditing.employeeguard.services.entity.EntityAuditInfoConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class EmployeeEntityServiceTest {

	private EmployeeEntityService employeeEntityService;

	@Mock
	private EmployeeDAO employeeDAO;
	@Mock
	private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;
	@Mock
	private RoleEmployeeDAO roleEmployeeDAO;

	@Before
	public void setUp() throws Exception {
		employeeEntityService = new EmployeeEntityService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(employeeEntityService, "employeeDAO", employeeDAO);
		Whitebox.setInternalState(employeeEntityService, "projectRoleEmployeeDAO", projectRoleEmployeeDAO);
		Whitebox.setInternalState(employeeEntityService, "roleEmployeeDAO", roleEmployeeDAO);
	}

	@Test(expected = NullPointerException.class)
	public void testFind_NullId() throws Exception {
		employeeEntityService.find(null);
	}

	@Test
	public void testFind() throws Exception {
		when(employeeDAO.find(ENTITY_ID)).thenReturn(buildFakeEmployee());

		Employee employee = employeeEntityService.find(ENTITY_ID);

		assertNotNull(employee);
		assertEquals(ENTITY_ID, employee.getId());
	}

	@Test
	public void testGetEmployeesByProject_NullArgument() {
		Map<Project, Set<Employee>> result = employeeEntityService.getEmployeesByProjects(null);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetEmployeesByProject() {
		List<ProjectRoleEmployee> fakeProjectRoleEmployees = buildFakeProjectRoleEmployees();
		when(projectRoleEmployeeDAO.findByProjects(anyCollectionOf(Project.class))).thenReturn(fakeProjectRoleEmployees);

		Map<Project, Set<Employee>> result = employeeEntityService.getEmployeesByProjects(Arrays.asList(new Project()));

		verifyTestGetEmployeesByProject(result);
	}

	private List<ProjectRoleEmployee> buildFakeProjectRoleEmployees() {
		return Arrays.asList(
				new ProjectRoleEmployeeBuilder()
						.projectRole(new ProjectRoleBuilder()
								.project(new ProjectBuilder().accountId(ACCOUNT_ID).name("Test Project").build())
								.role(new RoleBuilder().accountId(ACCOUNT_ID).name("Test Role").build())
								.build())
						.employee(new EmployeeBuilder().accountId(923).email("Tester@Test.com").build())
						.build()
		);
	}

	private void verifyTestGetEmployeesByProject(final Map<Project, Set<Employee>> result) {
		assertEquals(1, result.size());

		for (Project project : result.keySet()) {
			assertEquals("Test Project", project.getName());
			for (Employee employee : result.get(project)) {
				assertEquals("Tester@Test.com", employee.getEmail());
			}
		}
	}

	@Test
	public void testGetEmployeesByProjectRoles_NullOrEmpty() {
		Map<Role, Set<Employee>> result = employeeEntityService
				.getEmployeesByProjectRoles(Collections.<Project>emptyList());

		assertNotNull(result);
		assertTrue(result.isEmpty());

		result = employeeEntityService.getEmployeesByProjectRoles(new ArrayList<Project>());

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetEmployeesByProjectRoles() {
		List<Project> projects = setupForGetEmployeesByProjectRoles();

		Map<Role, Set<Employee>> result = employeeEntityService.getEmployeesByProjectRoles(projects);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(1, result.keySet().size());
		assertNotNull(result.values());
	}

	private List<Project> setupForGetEmployeesByProjectRoles() {
		Project project = buildFakeProject();
		List<Project> projects = Arrays.asList(project);
		ProjectRoleEmployee projectRoleEmployee = buildFakeProjectRoleEmployee();
		List<ProjectRoleEmployee> projectRoleEmployees = Arrays.asList(projectRoleEmployee);

		when(projectRoleEmployeeDAO.findByProjects(projects)).thenReturn(projectRoleEmployees);

		return projects;
	}

	@Test
	public void testGetEmployeesBySiteRoles_NullOrEmpty() {
		Map<Role, Set<Employee>> result = employeeEntityService.getEmployeesBySiteRoles(null);

		assertNotNull(result);
		assertTrue(result.isEmpty());

		result = employeeEntityService.getEmployeesBySiteRoles(new ArrayList<Integer>());

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetEmployeesBySiteRoles_NullOrEmptyEmployees() {
		List<Integer> siteIds = Arrays.asList(ACCOUNT_ID);
		when(employeeDAO.findEmployeesAssignedToSites(siteIds)).thenReturn(null);

		Map<Role, Set<Employee>> result = employeeEntityService.getEmployeesBySiteRoles(siteIds);

		assertNotNull(result);
		assertTrue(result.isEmpty());

		verify(employeeDAO).findEmployeesAssignedToSites(siteIds);
	}

	@Test
	public void testGetEmployeesBySiteRoles() {
		Employee employee = buildFakeEmployee();
		List<Employee> employees = Arrays.asList(employee);
		List<Integer> siteIds = Arrays.asList(ACCOUNT_ID);

		RoleEmployee roleEmployee = buildFakeRoleEmployee();
		List<RoleEmployee> roleEmployees = Arrays.asList(roleEmployee);

		when(employeeDAO.findEmployeesAssignedToSites(siteIds)).thenReturn(employees);
		when(roleEmployeeDAO.findByEmployeesAndSiteIds(employees, siteIds)).thenReturn(roleEmployees);

		Map<Role, Set<Employee>> result = employeeEntityService.getEmployeesBySiteRoles(siteIds);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(1, result.keySet().size());
		assertNotNull(result.values());

		verify(employeeDAO).findEmployeesAssignedToSites(siteIds);
		verify(roleEmployeeDAO).findByEmployeesAndSiteIds(employees, siteIds);
	}

	@Test
	public void testGetEmployeesAssignedToSite_NullOrEmpty() {
		List<Employee> result = employeeEntityService.getEmployeesAssignedToSite(null, ACCOUNT_ID);

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetEmployeesAssignedToSite() {
		Employee employee = buildFakeEmployee();
		employee.setAccountId(ACCOUNT_ID);

		List<Employee> employees = Arrays.asList(employee);
		List<Integer> contractorIds = Arrays.asList(ACCOUNT_ID);

		when(employeeDAO.findEmployeesAssignedToSites(Arrays.asList(ACCOUNT_ID))).thenReturn(employees);

		List<Employee> result = employeeEntityService.getEmployeesAssignedToSite(contractorIds, ACCOUNT_ID);

		assertNotNull(result);
		assertFalse(result.isEmpty());
	}

	@Test
	public void testSearch_NullOrEmpty() throws Exception {
		List<Employee> employees = employeeEntityService.search(null, ACCOUNT_ID);

		assertNotNull(employees);
		assertTrue(employees.isEmpty());
		verify(employeeDAO, never()).search(anyString(), anyInt());

		employees = employeeEntityService.search(Strings.EMPTY_STRING, ACCOUNT_ID);

		assertNotNull(employees);
		assertTrue(employees.isEmpty());
		verify(employeeDAO, never()).search(anyString(), anyInt());
	}

	@Test
	public void testSearch() throws Exception {
		when(employeeDAO.search(SEARCH_TERM, ACCOUNT_ID)).thenReturn(Arrays.asList(buildFakeEmployee()));

		List<Employee> employees = employeeEntityService.search(SEARCH_TERM, ACCOUNT_ID);

		verify(employeeDAO).search(SEARCH_TERM, ACCOUNT_ID);
		assertNotNull(employees);
		assertFalse(employees.isEmpty());
	}

	@Test
	public void testSave() throws Exception {
		Employee fakeEmployee = buildFakeEmployee();

		when(employeeDAO.save(fakeEmployee)).thenReturn(fakeEmployee);

		Employee result = employeeEntityService.save(fakeEmployee, CREATED);

		assertNotNull(result);
		assertEquals(fakeEmployee.getId(), result.getId());
		assertEquals(USER_ID, result.getCreatedBy());
		assertEquals(CREATED_DATE, result.getCreatedDate());
	}

	@Test
	public void testUpdate() throws Exception {
		Employee fakeEmployee = buildFakeEmployee();
		Employee updatedEmployee = buildFakeEmployee();
		updatedEmployee.setFirstName("First");
		updatedEmployee.setPositionName("Title");
		updatedEmployee.setSlug("EID123");

		when(employeeDAO.find(fakeEmployee.getId())).thenReturn(fakeEmployee);
		when(employeeDAO.save(fakeEmployee)).thenReturn(fakeEmployee);

		Employee result = employeeEntityService.update(updatedEmployee, UPDATED);

		assertNotNull(result);
		assertEquals(updatedEmployee.getId(), result.getId());
		assertEquals(updatedEmployee.getFirstName(), result.getFirstName());
		assertEquals(updatedEmployee.getPositionName(), result.getPositionName());
		assertEquals(updatedEmployee.getSlug(), result.getSlug());
		assertEquals(USER_ID, result.getUpdatedBy());
		assertEquals(UPDATED_DATE, result.getUpdatedDate());
	}

	@Test
	public void testDelete() throws Exception {
		Employee fakeEmployee = buildFakeEmployee();

		employeeEntityService.delete(fakeEmployee);

		verify(employeeDAO).delete(fakeEmployee);
	}

	@Test
	public void testDeleteById() throws Exception {
		Employee fakeEmployee = buildFakeEmployee();

		when(employeeDAO.find(fakeEmployee.getId())).thenReturn(fakeEmployee);

		employeeEntityService.deleteById(fakeEmployee.getId());

		verify(employeeDAO).find(fakeEmployee.getId());
		verify(employeeDAO).delete(fakeEmployee);
	}

	private Employee buildFakeEmployee() {
		return new EmployeeBuilder()
				.id(ENTITY_ID)
				.build();
	}

	private Project buildFakeProject() {
		return new ProjectBuilder()
				.build();
	}

	private ProjectRoleEmployee buildFakeProjectRoleEmployee() {
		return new ProjectRoleEmployeeBuilder()
				.projectRole(new ProjectRoleBuilder()
						.role(buildFakeRole())
						.build())
				.build();
	}

	private Role buildFakeRole() {
		return new RoleBuilder()
				.build();
	}

	private RoleEmployee buildFakeRoleEmployee() {
		return new RoleEmployeeBuilder()
				.employee(buildFakeEmployee())
				.role(buildFakeRole())
				.build();
	}

}
