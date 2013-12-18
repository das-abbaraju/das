package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountGroupEmployeeDAO;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.GroupEmployee;
import com.picsauditing.employeeguard.entities.Profile;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


public class AccountGroupEmployeeServiceTest {

	private AccountGroupEmployeeService accountGroupEmployeeService;

	@Mock
	private AccountGroupEmployeeDAO accountGroupEmployeeDAO;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		accountGroupEmployeeService = new AccountGroupEmployeeService();

		Whitebox.setInternalState(accountGroupEmployeeService, "accountGroupEmployeeDAO", accountGroupEmployeeDAO);
	}

	@Test
	public void testGetMapOfAccountGroupEmployeeByAccountId_NullProfile() {
		Map<Integer, List<GroupEmployee>> result = accountGroupEmployeeService.getMapOfAccountGroupEmployeeByAccountId(null);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetMapOfAccountGroupEmployeeByAccountId() {
		when(accountGroupEmployeeDAO.findByProfile(any(Profile.class))).thenReturn(getAccountGroupEmployees());

		Map<Integer, List<GroupEmployee>> result = accountGroupEmployeeService.getMapOfAccountGroupEmployeeByAccountId(new Profile());

		verifyResult(result);
	}

	private List<GroupEmployee> getAccountGroupEmployees() {
		List<GroupEmployee> groupEmployees = new ArrayList<>();
		groupEmployees.add(buildAccountGroupEmployee(1100, 1, 2));
		groupEmployees.add(buildAccountGroupEmployee(1100, 3, 4));
		groupEmployees.add(buildAccountGroupEmployee(1101, 5, 6));
		return groupEmployees;
	}

	private GroupEmployee buildAccountGroupEmployee(int accountId, int groupId, int employeeId) {
		Group group = new Group();
		group.setAccountId(accountId);
		group.setId(groupId);

		Employee employee = new Employee();
		employee.setAccountId(accountId);
		employee.setId(employeeId);

		GroupEmployee groupEmployee = new GroupEmployee();
		groupEmployee.setGroup(group);
		groupEmployee.setEmployee(employee);
		return groupEmployee;
	}

	private void verifyResult(Map<Integer, List<GroupEmployee>> result) {
		assertEquals(2, result.size());
	}
}
