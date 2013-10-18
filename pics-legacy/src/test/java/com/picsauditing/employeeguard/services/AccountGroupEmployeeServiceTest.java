package com.picsauditing.employeeguard.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.employeeguard.daos.AccountGroupEmployeeDAO;
import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.entities.AccountGroupEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Profile;


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
		Map<Integer, List<AccountGroupEmployee>> result = accountGroupEmployeeService.getMapOfAccountGroupEmployeeByAccountId(null);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void testGetMapOfAccountGroupEmployeeByAccountId() {
		when(accountGroupEmployeeDAO.findByProfile(any(Profile.class))).thenReturn(getAccountGroupEmployees());
		
		Map<Integer, List<AccountGroupEmployee>> result = accountGroupEmployeeService.getMapOfAccountGroupEmployeeByAccountId(new Profile());
		
		verifyResult(result);
	}
	
	private List<AccountGroupEmployee> getAccountGroupEmployees() {
		List<AccountGroupEmployee> accountGroupEmployees = new ArrayList<>();
		accountGroupEmployees.add(buildAccountGroupEmployee(1100, 1, 2));
		accountGroupEmployees.add(buildAccountGroupEmployee(1100, 3, 4));
		accountGroupEmployees.add(buildAccountGroupEmployee(1101, 5, 6));
		return accountGroupEmployees;
	}
	
	private AccountGroupEmployee buildAccountGroupEmployee(int accountId, int groupId, int employeeId) {
		AccountGroup group = new AccountGroup();
		group.setAccountId(accountId);
		group.setId(groupId);
		
		Employee employee = new Employee();
		employee.setAccountId(accountId);
		employee.setId(employeeId);
		
		AccountGroupEmployee accountGroupEmployee = new AccountGroupEmployee();
		accountGroupEmployee.setGroup(group);
		accountGroupEmployee.setEmployee(employee);
		return accountGroupEmployee;
	}
	
	private void verifyResult(Map<Integer, List<AccountGroupEmployee>> result) {
		assertEquals(2, result.size());
	}
}
