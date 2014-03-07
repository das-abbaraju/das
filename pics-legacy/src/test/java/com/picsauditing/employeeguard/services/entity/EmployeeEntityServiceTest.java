package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleEmployeeDAO;
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
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class EmployeeEntityServiceTest {

	private EmployeeEntityService employeeEntityService;

	@Mock
	private EmployeeDAO employeeDAO;
	@Mock
	private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;

	@Before
	public void setUp() throws Exception {
		employeeEntityService = new EmployeeEntityService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(employeeEntityService, "employeeDAO", employeeDAO);
		Whitebox.setInternalState(employeeEntityService, "projectRoleEmployeeDAO", projectRoleEmployeeDAO);
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
		Map<Project, Set<Employee>> result = employeeEntityService.getEmployeesByProject(null);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetEmployeesByProject() {
		List<ProjectRoleEmployee> fakeProjectRoleEmployees = buildFakeProjectRoleEmployees();
		when(projectRoleEmployeeDAO.findByProjects(anyCollectionOf(Project.class))).thenReturn(fakeProjectRoleEmployees);

		Map<Project, Set<Employee>> result = employeeEntityService.getEmployeesByProject(Arrays.asList(new Project()));

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
}
