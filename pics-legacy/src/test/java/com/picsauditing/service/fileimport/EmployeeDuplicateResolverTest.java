package com.picsauditing.service.fileimport;

import com.picsauditing.dao.LegacyEmployeeDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class EmployeeDuplicateResolverTest {
	private EmployeeDuplicateResolver duplicateResolver;

	@Mock
	private Account account;
	@Mock
	private LegacyEmployeeDAO legacyEmployeeDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testResolveDuplicates() throws Exception {
		Set<Employee> imported = setUpEmployees();

		Employee employee = new Employee();
		employee.setFirstName("First");
		employee.setLastName("Last");
		employee.setTitle("Title");
		employee.setAccount(account);

		List<Employee> existing = new ArrayList<>();
		existing.add(employee);

		when(legacyEmployeeDAO.findByFirstNameLastNameAndAccount(imported)).thenReturn(existing);

		duplicateResolver = new EmployeeDuplicateResolver(imported, legacyEmployeeDAO);
		duplicateResolver.resolveDuplicates();

		assertFalse(duplicateResolver.getDuplicateEmployees().isEmpty());
		assertFalse(duplicateResolver.getUniqueEmployees().isEmpty());
	}

	private Set<Employee> setUpEmployees() {
		Employee employee1 = new Employee();
		employee1.setFirstName("First");
		employee1.setLastName("Last");
		employee1.setTitle("Title");
		employee1.setAccount(account);

		Employee employee2 = new Employee();
		employee2.setFirstName("First2");
		employee2.setLastName("Last2");
		employee2.setTitle("Title2");
		employee2.setAccount(account);

		Set<Employee> employees = new HashSet<>();
		employees.add(employee1);
		employees.add(employee2);

		return employees;
	}
}
