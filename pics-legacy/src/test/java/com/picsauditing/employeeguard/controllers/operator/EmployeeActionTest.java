package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.employeeguard.services.LiveIDEmployeeService;
import com.picsauditing.employeeguard.services.OperatorEmployeeService;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@UseReporter(DiffReporter.class)
public class EmployeeActionTest extends PicsActionTest {

	EmployeeAction employeeAction;

	@Mock
	private LiveIDEmployeeService liveIDEmployeeService;
	@Mock
	private OperatorEmployeeService operatorEmployeeModelService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		employeeAction = new EmployeeAction();
		super.setUp(employeeAction);

		Whitebox.setInternalState(employeeAction, "liveIDEmployeeService", liveIDEmployeeService);
	}

	@Test
	public void testShow() throws Exception {
		when(liveIDEmployeeService.buildLiveIDEmployee(anyString(), anyInt())).thenReturn(null);

		String result = employeeAction.show();

		assertEquals(PicsActionSupport.JSON_STRING, result);
	}
}
