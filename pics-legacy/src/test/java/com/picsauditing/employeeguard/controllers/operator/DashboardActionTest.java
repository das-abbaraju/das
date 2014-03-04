package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.when;

@UseReporter(DiffReporter.class)
public class DashboardActionTest extends PicsActionTest {
	public static final int CONTRACTOR_ID = 234;
	public static final int SITE_ID = 123;

	private DashboardAction dashboard;

	@Mock
	private AssignmentService assignmentService;
	@Mock
	private StatusCalculatorService statusCalculatorService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		dashboard = new DashboardAction();
		super.setUp(dashboard);

		when(permissions.getAccountId()).thenReturn(SITE_ID);
		when(permissions.getAccountName()).thenReturn("Site Name");

		Whitebox.setInternalState(dashboard, "assignmentService", assignmentService);
		Whitebox.setInternalState(dashboard, "statusCalculatorService", statusCalculatorService);
	}

	@Test
	public void testIndex() throws Exception {
		when(assignmentService.getEmployeeSkillsForSite(SITE_ID)).thenReturn(new HashMap<Employee, Set<AccountSkill>>());
		when(statusCalculatorService.getEmployeeStatusRollUpForSkills(anySetOf(Employee.class), anyMap())).thenReturn(
				new HashMap<Employee, SkillStatus>() {{
					put(new EmployeeBuilder()
							.accountId(CONTRACTOR_ID)
							.firstName("Employee")
							.lastName("Test")
							.email("employee@test.com")
							.slug("EmployeeTest")
							.build(),
							SkillStatus.Complete);
					put(new EmployeeBuilder()
							.accountId(CONTRACTOR_ID)
							.firstName("Employee 2")
							.lastName("Test")
							.email("employee2@test.com")
							.slug("Employee2Test")
							.build(),
							SkillStatus.Expired);
				}});

		assertEquals(PicsActionSupport.JSON_STRING, dashboard.index());
		assertNotNull(dashboard.getJsonString());
		Approvals.verify(dashboard.getJsonString());
	}
}
