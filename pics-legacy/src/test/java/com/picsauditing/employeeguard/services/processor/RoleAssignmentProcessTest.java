package com.picsauditing.employeeguard.services.processor;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.spun.util.ArrayUtils;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class RoleAssignmentProcessTest {

	public static final int SITE_ID = 812;
	public static final int CONTRACTOR_ID = 89;
	public static final int CORPORATE_ID = 9019;

	@Test
	public void testGetCorporateRoleEmployees() {
		Map<Role, Set<Employee>> result = new RoleAssignmentProcess().getCorporateRoleEmployees(
				getProjectEmployeeRoles(),
				getSiteRoleEmployees()
		);

		verifyResult(result);
	}

	private Map<Role, Set<Employee>> getProjectEmployeeRoles() {
		return new HashMap<Role, Set<Employee>>() {{
			put(new RoleBuilder()
					.accountId(SITE_ID)
					.name("Project Role")
					.build(),

					new HashSet<>(Arrays.asList(new EmployeeBuilder()
							.accountId(CONTRACTOR_ID)
							.email("test@test.com")
							.build())));
		}};
	}

	private Map<Role, Set<Employee>> getSiteRoleEmployees() {
		return new HashMap<Role, Set<Employee>>() {{
			put(new RoleBuilder()
					.accountId(SITE_ID)
					.name("Site Role")
					.build(),

					new HashSet<>(Arrays.asList(
							new EmployeeBuilder()
									.accountId(CONTRACTOR_ID)
									.email("test@test.com")
									.build(),

							new EmployeeBuilder()
									.accountId(CONTRACTOR_ID)
									.email("another_employee@test.com")
									.build())));
		}};
	}

	private Map<Role, Role> getSiteToCorporateRoleMap() {
		return new HashMap<Role, Role>() {{
			put(new RoleBuilder()
					.accountId(SITE_ID)
					.name("Site Role")
					.build(),

					new RoleBuilder()
							.accountId(CORPORATE_ID)
							.name("Corporate Role")
							.build());
		}};
	}

	private void verifyResult(final Map<Role, Set<Employee>> roleEmployeeAssignmentsForSite) {
		assertEquals(2, roleEmployeeAssignmentsForSite.size());

		for (Role role : roleEmployeeAssignmentsForSite.keySet()) {
			switch (role.getName()) {
				case "Corporate Role":
					verifyEmployeeEmails(roleEmployeeAssignmentsForSite.get(role),
							"test@test.com", "another_employee@test.com");
					break;

				case "Project Role":
					verifyEmployeeEmails(roleEmployeeAssignmentsForSite.get(role), "test@test.com");
					break;

				default:
					fail("Invalid role" + role.getName());
			}
		}
 	}

	private void verifyEmployeeEmails(final Collection<Employee> employees, String... employeeEmails) {
		for (Employee employee : employees) {
			assertTrue(ArrayUtils.contains(employeeEmails, employee.getEmail()));
		}
	}
}
