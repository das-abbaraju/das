package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class StatusCalculatorServiceTest {
	public static final int CONTRACTOR_ID = 1234;

	private StatusCalculatorService service;

	@Mock
	private AccountSkill skill;
	@Mock
	private AccountSkillEmployee accountSkillEmployee;
	@Mock
	private Employee employee;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		service = new StatusCalculatorService();
	}

	@Test
	public void testGetEmployeeStatusRollUpForSkills() throws Exception {
		Set<Employee> accountEmployees = new HashSet<>();

		when(accountSkillEmployee.getEmployee()).thenReturn(employee);
		when(accountSkillEmployee.getEndDate()).thenReturn(DateBean.addDays(DateBean.today(), 15));
		when(accountSkillEmployee.getSkill()).thenReturn(skill);
		when(employee.getAccountId()).thenReturn(CONTRACTOR_ID);
		when(employee.getName()).thenReturn("Employee Name");
		when(employee.getSkills()).thenReturn(Arrays.asList(accountSkillEmployee));

		accountEmployees.add(employee);

		Map<Employee, Set<AccountSkill>> employeeSkills = new HashMap<>();
		employeeSkills.put(employee, new HashSet<>(Arrays.asList(skill)));

		Map<Employee, SkillStatus> map = service.getEmployeeStatusRollUpForSkills(accountEmployees, employeeSkills);

		assertNotNull(map);
		assertFalse(map.isEmpty());
		assertEquals(1, map.size());
		assertEquals(SkillStatus.Expiring, map.get(employee));
	}
}
