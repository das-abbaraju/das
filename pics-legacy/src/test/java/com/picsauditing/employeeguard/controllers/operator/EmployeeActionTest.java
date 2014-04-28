package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.process.EmployeeSiteStatusProcess;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.external.AccountService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@UseReporter(DiffReporter.class)
public class EmployeeActionTest extends PicsActionTest {

	EmployeeAction employeeAction;

	@Mock
	private AccountService accountService;
	@Mock
	private EmployeeSiteStatusProcess employeeSiteStatusProcess;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeAction = new EmployeeAction();
		super.setUp(employeeAction);

		Whitebox.setInternalState(employeeAction, "accountService", accountService);
		Whitebox.setInternalState(employeeAction, "employeeSiteStatusProcess", employeeSiteStatusProcess);
	}

	@Ignore
	@Test
	public void testShow() throws Exception {
		fail("Needs to be fixed");
	}

	private Employee buildFakeEmployee() {
		return new EmployeeBuilder()
				.id(7)
				.accountId(456)
				.firstName("Bob")
				.lastName("Smith")
				.positionName("Master Welder")
				.build();
	}
}
