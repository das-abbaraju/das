package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.EmployeeDAO;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.List;

import static com.picsauditing.employeeguard.services.entity.EntityAuditInfoConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class EmployeeServiceTest {

	private EmployeeService employeeService;

	@Mock
	private EmployeeDAO employeeDAO;

	@Before
	public void setUp() throws Exception {
		employeeService = new EmployeeService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(employeeService, "employeeDAO", employeeDAO);
	}

	@Test(expected = NullPointerException.class)
	public void testFind_NullId() throws Exception {
		employeeService.find(null);
	}

	@Test
	public void testFind() throws Exception {
		when(employeeDAO.find(ENTITY_ID)).thenReturn(buildFakeEmployee());

		Employee employee = employeeService.find(ENTITY_ID);

		assertNotNull(employee);
		assertEquals(ENTITY_ID, employee.getId());
	}

	@Test
	public void testSearch_NullOrEmpty() throws Exception {
		List<Employee> employees = employeeService.search(null, ACCOUNT_ID);

		assertNotNull(employees);
		assertTrue(employees.isEmpty());
		verify(employeeDAO, never()).search(anyString(), anyInt());

		employees = employeeService.search(Strings.EMPTY_STRING, ACCOUNT_ID);

		assertNotNull(employees);
		assertTrue(employees.isEmpty());
		verify(employeeDAO, never()).search(anyString(), anyInt());
	}

	@Test
	public void testSearch() throws Exception {
		when(employeeDAO.search(SEARCH_TERM, ACCOUNT_ID)).thenReturn(Arrays.asList(buildFakeEmployee()));

		List<Employee> employees = employeeService.search(SEARCH_TERM, ACCOUNT_ID);

		verify(employeeDAO).search(SEARCH_TERM, ACCOUNT_ID);
		assertNotNull(employees);
		assertFalse(employees.isEmpty());
	}

	@Test
	public void testSave() throws Exception {
		Employee fakeEmployee = buildFakeEmployee();

		when(employeeDAO.save(fakeEmployee)).thenReturn(fakeEmployee);

		Employee result = employeeService.save(fakeEmployee, CREATED);

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

		Employee result = employeeService.update(updatedEmployee, UPDATED);

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

		employeeService.delete(fakeEmployee);

		verify(employeeDAO).delete(fakeEmployee);
	}

	@Test
	public void testDeleteById() throws Exception {
		Employee fakeEmployee = buildFakeEmployee();

		when(employeeDAO.find(fakeEmployee.getId())).thenReturn(fakeEmployee);

		employeeService.deleteById(fakeEmployee.getId());

		verify(employeeDAO).find(fakeEmployee.getId());
		verify(employeeDAO).delete(fakeEmployee);
	}

	private Employee buildFakeEmployee() {
		return new EmployeeBuilder()
				.id(ENTITY_ID)
				.build();
	}
}
