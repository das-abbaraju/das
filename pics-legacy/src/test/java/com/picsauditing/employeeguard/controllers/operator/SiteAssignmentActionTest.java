package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.PicsActionTest;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.RoleEmployee;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.RoleService;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.operator.SiteAssignmentModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class SiteAssignmentActionTest extends PicsActionTest {

	public static final int CONTRACTOR_ID_1 = 1;
	public static final int CONTRACTOR_ID_2 = 2;
	public static final int SITE_ID = 1234;
	public static final int CORPORATE_ID = 10000;

	private SiteAssignmentAction siteAssignmentAction;

	@Mock
	private AccountService accountService;
	@Mock
	private EmployeeService employeeService;
	@Mock
	private RoleService roleService;
	@Mock
	private StatusCalculatorService statusCalculatorService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		super.setupMocks();

		siteAssignmentAction = new SiteAssignmentAction();

		Whitebox.setInternalState(siteAssignmentAction, "accountService", accountService);
		Whitebox.setInternalState(siteAssignmentAction, "employeeService", employeeService);
		Whitebox.setInternalState(siteAssignmentAction, "permissions", permissions);
		Whitebox.setInternalState(siteAssignmentAction, "roleService", roleService);
		Whitebox.setInternalState(siteAssignmentAction, "statusCalculatorService", statusCalculatorService);
	}

	@Test
	public void testStatus_ClientSite_TotalCountOfEmployees() throws Exception {
		setupMocksForTotalCount();

		assertEquals("status", siteAssignmentAction.status());

		SiteAssignmentModel siteAssignmentModel = siteAssignmentAction.getSiteAssignmentModel();
		performAssertionsOnTotalCount(siteAssignmentModel);
	}

	private void setupMocksForTotalCount() {
		List<AccountModel> contractors = Arrays.asList(
				new AccountModel.Builder().id(CONTRACTOR_ID_1).name("Contractor 1").build(),
				new AccountModel.Builder().id(CONTRACTOR_ID_2).name("Contractor 2").build()
		);

		List<Employee> employees = Arrays.asList(
				new EmployeeBuilder().firstName("1").lastName("One").email("1@one").accountId(CONTRACTOR_ID_1).build(),
				new EmployeeBuilder().firstName("2").lastName("Two").email("2@two").accountId(CONTRACTOR_ID_1).build(),
				new EmployeeBuilder().firstName("3").lastName("Three").email("3@three").accountId(CONTRACTOR_ID_1).build());

		setupPermissions();
		when(accountService.getContractors(SITE_ID)).thenReturn(contractors);
		when(employeeService.getEmployeesAssignedToSite(anySetOf(Integer.class), eq(SITE_ID))).thenReturn(employees);
		when(roleService.getRolesForAccounts(anyListOf(Integer.class))).thenReturn(Collections.<Role>emptyList());
		when(roleService.getSiteToCorporateRoles(SITE_ID)).thenReturn(Collections.<Role, Role>emptyMap());
	}

	private void performAssertionsOnTotalCount(SiteAssignmentModel siteAssignmentModel) {
		assertNotNull(siteAssignmentModel);
		assertEquals(3, siteAssignmentModel.getTotalEmployeesAssignedToSite());

		verify(accountService).getAccountById(SITE_ID);
		verify(accountService).getContractors(SITE_ID);
		verify(employeeService).getEmployeesAssignedToSite(anySetOf(Integer.class), eq(SITE_ID));
	}

	private void setupPermissions() {
		when(permissions.getAccountId()).thenReturn(SITE_ID);
		when(permissions.isOperator()).thenReturn(true);
	}

	@Test
	public void testStatus_ClientSite_RoleInfo() throws Exception {
		List<Integer> corporateIds = setupMocksForClientSiteRoleInfoAndReturnCorporateIds();

		assertEquals("status", siteAssignmentAction.status());

		SiteAssignmentModel siteAssignmentModel = siteAssignmentAction.getSiteAssignmentModel();

		performAssertionsOnRoleInfo(corporateIds, siteAssignmentModel);
	}

	private void performAssertionsOnRoleInfo(List<Integer> corporateIds, SiteAssignmentModel siteAssignmentModel) {
		assertNotNull(siteAssignmentModel);
		assertEquals(2, siteAssignmentModel.getTotalEmployeesAssignedToSite());
		assertEquals(2, siteAssignmentModel.getRoleEmployee().size());
		assertTrue(siteAssignmentModel.getRoleEmployee().values().contains(1));

		verify(roleService).getRolesForAccounts(corporateIds);
		verify(roleService).getSiteToCorporateRoles(SITE_ID);
	}

	private List<Integer> setupMocksForClientSiteRoleInfoAndReturnCorporateIds() {
		Role siteRole1 = mock(Role.class);
		Role siteRole2 = mock(Role.class);
		Role corporateRole1 = mock(Role.class);
		Role corporateRole2 = mock(Role.class);

		RoleEmployee roleEmployee = mock(RoleEmployee.class);
		Employee employee = mock(Employee.class);

		List<Integer> corporateIds = Arrays.asList(CORPORATE_ID);
		List<Role> roles = Arrays.asList(corporateRole1, corporateRole2);

		Map<Role, Role> siteToCorporateRoles = new HashMap<>();
		siteToCorporateRoles.put(siteRole1, corporateRole1);
		siteToCorporateRoles.put(siteRole2, corporateRole2);

		setupPermissions();
		when(accountService.getContractors(SITE_ID)).thenReturn(Collections.<AccountModel>emptyList());
		when(accountService.getTopmostCorporateAccountIds(SITE_ID)).thenReturn(corporateIds);
		when(corporateRole1.getName()).thenReturn("Corporate Role 1");
		when(corporateRole2.getName()).thenReturn("Corporate Role 2");
		when(employeeService.getEmployeesAssignedToSite(anySetOf(Integer.class), eq(SITE_ID)))
				.thenReturn(Arrays.asList(employee, mock(Employee.class)));
		when(employee.getRoles()).thenReturn(Arrays.asList(roleEmployee));
		when(roleEmployee.getRole()).thenReturn(siteRole1);
		when(roleService.getRolesForAccounts(anyListOf(Integer.class))).thenReturn(roles);
		when(roleService.getSiteToCorporateRoles(SITE_ID)).thenReturn(siteToCorporateRoles);
		when(siteRole1.getName()).thenReturn("Site Role 1");
		when(siteRole2.getName()).thenReturn("Site Role 2");
		return corporateIds;
	}

	@Test
	public void testRole() throws Exception {
		assertEquals("role", siteAssignmentAction.role());
	}
}
