package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.employeeguard.engine.SkillEngine;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.models.AccountModel;
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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UseReporter(DiffReporter.class)
public class SummaryActionTest extends PicsActionTest {

	public static final int SITE_ID = 123;
	private SummaryAction summaryAction;

	@Mock
	private AccountService accountService;
	@Mock
	private EmployeeEntityService employeeEntityService;
	@Mock
	private SkillEngine skillEngine;
	@Mock
	private StatusCalculatorService statusCalculatorService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		summaryAction = new SummaryAction();
		super.setUp(summaryAction);

		when(permissions.getAccountId()).thenReturn(SITE_ID);

		Whitebox.setInternalState(summaryAction, "accountService", accountService);
		Whitebox.setInternalState(summaryAction, "employeeEntityService", employeeEntityService);
		Whitebox.setInternalState(summaryAction, "skillEngine", skillEngine);
		Whitebox.setInternalState(summaryAction, "statusCalculatorService", statusCalculatorService);
	}

	@Test
	public void testIndex() throws Exception {
		setupForIndex();

		assertEquals(PicsActionSupport.JSON_STRING, summaryAction.index());
		performVerificationsForIndex();

		Approvals.verify(summaryAction.getJsonString());
	}

	private void setupForIndex() {
		AccountModel accountModel = new AccountModel.Builder().id(SITE_ID).build();
		final Employee employee = new EmployeeBuilder().build();
		final List<Employee> employees = Arrays.asList(employee);
		final AccountSkill accountSkill = new AccountSkillBuilder().build();
		HashMap<Employee, Set<AccountSkill>> employeeSkills = new HashMap<Employee, Set<AccountSkill>>() {{
			put(employee, new HashSet<AccountSkill>());
			get(employee).add(accountSkill);
		}};
		HashMap<Employee, SkillStatus> employeeStatus = new HashMap<Employee, SkillStatus>() {{
			put(employee, SkillStatus.Expiring);
		}};

		when(accountService.getAccountById(SITE_ID)).thenReturn(accountModel);
		when(employeeEntityService.getEmployeesForAccount(SITE_ID)).thenReturn(employees);
		when(employeeEntityService.getRequestedEmployeeCount(SITE_ID)).thenReturn(4);
		when(skillEngine.getEmployeeSkillsMapForAccount(employees, accountModel)).thenReturn(employeeSkills);
		when(statusCalculatorService.getEmployeeStatusRollUpForSkills(employees, employeeSkills))
				.thenReturn(employeeStatus);
	}

	private void performVerificationsForIndex() {
		assertNotNull(summaryAction.getJsonString());

		verify(accountService).getAccountById(SITE_ID);
		verify(employeeEntityService).getEmployeesForAccount(SITE_ID);
		verify(employeeEntityService).getRequestedEmployeeCount(SITE_ID);
		verify(skillEngine).getEmployeeSkillsMapForAccount(anyListOf(Employee.class), any(AccountModel.class));
		verify(statusCalculatorService).getEmployeeStatusRollUpForSkills(anyCollectionOf(Employee.class), anyMap());
	}
}
