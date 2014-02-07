package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.PicsActionTest;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.operator.SiteAssignmentModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SiteAssignmentActionTest extends PicsActionTest {

	public static final int CONTRACTOR_ID_1 = 1;
	public static final int CONTRACTOR_ID_2 = 2;
	public static final int SITE_ID = 1234;

	private SiteAssignmentAction siteAssignmentAction;

	@Mock
	private AccountService accountService;
	@Mock
	private EmployeeService employeeService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		super.setupMocks();

		siteAssignmentAction = new SiteAssignmentAction();

		Whitebox.setInternalState(siteAssignmentAction, "accountService", accountService);
		Whitebox.setInternalState(siteAssignmentAction, "employeeService", employeeService);
		Whitebox.setInternalState(siteAssignmentAction, "permissions", permissions);
	}

	@Test
	public void testStatus_ClientSite() throws Exception {
		List<AccountModel> contractors = Arrays.asList(
				new AccountModel.Builder().id(CONTRACTOR_ID_1).name("Contractor 1").build(),
				new AccountModel.Builder().id(CONTRACTOR_ID_2).name("Contractor 2").build()
		);

		List<Employee> employees = Arrays.asList(new Employee(), new Employee(), new Employee());

		when(accountService.getContractors(SITE_ID)).thenReturn(contractors);
		when(employeeService.getEmployeesAssignedToSite(anySetOf(Integer.class), eq(SITE_ID))).thenReturn(employees);
		when(permissions.getAccountId()).thenReturn(SITE_ID);
		when(permissions.isOperator()).thenReturn(true);

		assertEquals("status", siteAssignmentAction.status());

		SiteAssignmentModel siteAssignmentModel = siteAssignmentAction.getSiteAssignmentModel();
		assertNotNull(siteAssignmentModel);
		assertEquals(3, siteAssignmentModel.getTotalEmployeesAssignedToSite());

		verify(accountService).getAccountById(SITE_ID);
		verify(accountService).getContractors(SITE_ID);
		verify(employeeService).getEmployeesAssignedToSite(anySetOf(Integer.class), eq(SITE_ID));
	}

	@Test
	public void testRole() throws Exception {
		assertEquals("role", siteAssignmentAction.role());
	}
}
