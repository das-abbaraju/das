package com.picsauditing.employeeguard.entities.builders;


import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.GroupEmployee;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EmployeeBuilderTest {

	private static final int ID = 123;
	private static final int ACCOUNT_ID = 1100;
	private static final String FIRST_NAME = "John";
	private static final String LAST_NAME = "Doe";
	private static final String EMAIL = "john.doe@test.com";
	private static final String PHONE_NUMBER = "555-555-5555";
	private static final String SLUG = "JOHN_DOE_TEST_SLUG";
	private static final String POSITION_NAME = "General Maintenance Worker";
	private static final List<String> GROUPS = Collections.unmodifiableList(Arrays.asList("Welders",
			"Maintenance", "All Employees"));

	@Test
	public void testBuild() {
		Employee employee = new EmployeeBuilder().id(ID).accountId(ACCOUNT_ID).firstName(FIRST_NAME).lastName(LAST_NAME)
				.email(EMAIL).phoneNumber(PHONE_NUMBER).slug(SLUG).positionName(POSITION_NAME)
				.groups(GROUPS.toArray(new String[0])).build();

		verifyEmployee(employee);
	}

	private void verifyEmployee(Employee employee) {
		assertEquals(ID, employee.getId());
		assertEquals(ACCOUNT_ID, employee.getAccountId());
		assertEquals(FIRST_NAME, employee.getFirstName());
		assertEquals(LAST_NAME, employee.getLastName());
		assertEquals(EMAIL, employee.getEmail());
		assertEquals(PHONE_NUMBER, employee.getPhone());
		assertEquals(SLUG, employee.getSlug());
		assertEquals(POSITION_NAME, employee.getPositionName());

		verifyGroups(employee);
	}

	private void verifyGroups(Employee employee) {
		int index = 0;
		for (GroupEmployee groupEmployee : employee.getGroups()) {
			assertEquals(ACCOUNT_ID, groupEmployee.getGroup().getAccountId());
			assertEquals(GROUPS.get(index), groupEmployee.getGroup().getName());
			assertEquals(employee, groupEmployee.getEmployee());
			index++;
		}
	}
}
