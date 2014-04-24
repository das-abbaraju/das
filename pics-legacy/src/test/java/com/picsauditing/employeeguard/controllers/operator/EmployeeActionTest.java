package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.models.factories.LiveIDEmployeeModelFactory;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.models.AccountModel;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@UseReporter(DiffReporter.class)
public class EmployeeActionTest extends PicsActionTest {

	EmployeeAction employeeAction;

	@Mock
	private AccountService accountService;
	@Mock
	private EmployeeService employeeService;
	@Mock
	private ProjectService projectService;
	@Mock
	private RoleService roleService;
	@Mock
	private StatusCalculatorService statusCalculatorService;
	@Mock
	private SkillService skillService;
	@Mock
	private LiveIDEmployeeService liveIDEmployeeService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeAction = new EmployeeAction();
		super.setUp(employeeAction);

		Whitebox.setInternalState(employeeAction, "accountService", accountService);
		Whitebox.setInternalState(employeeAction, "employeeService", employeeService);
		Whitebox.setInternalState(employeeAction, "projectService", projectService);
		Whitebox.setInternalState(employeeAction, "roleService", roleService);
		Whitebox.setInternalState(employeeAction, "statusCalculatorService", statusCalculatorService);
		Whitebox.setInternalState(employeeAction, "skillService", skillService);
		Whitebox.setInternalState(employeeAction, "liveIDEmployeeService", liveIDEmployeeService);
	}

	@Test
	public void testShow() throws Exception {
		when(liveIDEmployeeService.buildLiveIDEmployee(anyString(), anyInt())).thenReturn(null);

		String result = employeeAction.show();

		assertEquals(PicsActionSupport.JSON_STRING, result);
	}
}
