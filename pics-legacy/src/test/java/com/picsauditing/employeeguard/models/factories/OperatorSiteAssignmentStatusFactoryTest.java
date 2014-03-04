package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.models.OperatorSiteAssignmentStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class OperatorSiteAssignmentStatusFactoryTest {

	public static final int ACCOUNT_ID = 234;
	private OperatorSiteAssignmentStatusFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new OperatorSiteAssignmentStatusFactory();
	}

	@Test
	public void testCreate() throws Exception {
		List<OperatorSiteAssignmentStatus> sites = factory.create(
				123,
				"Site Name",
				new HashMap<Employee, SkillStatus>() {{
					put(new EmployeeBuilder()
							.firstName("1")
							.lastName("Test")
							.accountId(ACCOUNT_ID)
							.email("1@test.com")
							.slug("1")
							.build(),
							SkillStatus.Complete);
					put(new EmployeeBuilder()
							.firstName("2")
							.lastName("Test")
							.accountId(ACCOUNT_ID)
							.email("2@test.com")
							.slug("2")
							.build(),
							SkillStatus.Expiring);
					put(new EmployeeBuilder()
							.firstName("3")
							.lastName("Test")
							.accountId(ACCOUNT_ID)
							.email("3@test.com")
							.slug("3")
							.build(),
							SkillStatus.Expired);
				}});

		assertNotNull(sites);
		assertFalse(sites.isEmpty());

		OperatorSiteAssignmentStatus operatorSiteAssignmentStatus = sites.get(0);

		assertEquals(1, operatorSiteAssignmentStatus.getCompleted());
		assertEquals(1, operatorSiteAssignmentStatus.getExpiring());
		assertEquals(1, operatorSiteAssignmentStatus.getExpired());
		assertEquals(3, operatorSiteAssignmentStatus.getEmployees());
	}
}
