package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.PicsActionTest;
import com.picsauditing.access.NoRightsException;
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

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UseReporter(DiffReporter.class)
public class AssignmentSummaryActionTest extends PicsActionTest {

	public static final int CONTRACTOR_ID = 234;
	public static final int SITE_ID = 123;

	AssignmentSummaryAction summaryAction;

	@Mock
	private AssignmentService assignmentService;
	@Mock
	private StatusCalculatorService statusCalculatorService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		summaryAction = new AssignmentSummaryAction();
		super.setUp(summaryAction);

		when(permissions.getAccountId()).thenReturn(SITE_ID);
		when(permissions.getAccountName()).thenReturn("Site Name");

		Whitebox.setInternalState(summaryAction, "assignmentService", assignmentService);
		Whitebox.setInternalState(summaryAction, "statusCalculatorService", statusCalculatorService);
	}

	@Test(expected = NoRightsException.class)
	public void testSummary_Site() throws Exception {
		when(permissions.isOperator()).thenReturn(true);

		summaryAction.summary();
	}

	@Test
	public void testSummary_Corporate() throws Exception {
		setupTestSummary_Corporate();

		String result = summaryAction.summary();

		verifyTestSummary_Corporate(result);
	}

	private void setupTestSummary_Corporate() {
		setupForIndex();

		when(permissions.isCorporate()).thenReturn(true);
		when(permissions.getOperatorChildren()).thenReturn(new HashSet<>(Arrays.asList(SITE_ID)));

		summaryAction.setId(Integer.toString(SITE_ID));
	}

	private void setupForIndex() {
		Map<Employee, Set<AccountSkill>> map = mock(Map.class);

		when(assignmentService.getEmployeeSkillsForSite(SITE_ID)).thenReturn(map);
		when(map.size()).thenReturn(2);
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
	}

	private void verifyTestSummary_Corporate(String result) throws Exception {
		assertEquals(PicsActionSupport.JSON_STRING, result);
		assertNotNull(summaryAction.getJsonString());

		Approvals.verify(summaryAction.getJsonString());
	}
}
