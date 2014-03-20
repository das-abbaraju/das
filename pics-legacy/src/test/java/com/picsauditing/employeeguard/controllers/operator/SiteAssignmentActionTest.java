package com.picsauditing.employeeguard.controllers.operator;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.PicsActionTest;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.entities.builders.AccountSkillEmployeeBuilder;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

public class SiteAssignmentActionTest extends PicsActionTest {

	public static final int CONTRACTOR_ID_1 = 1;
	public static final int CONTRACTOR_ID_2 = 2;
	public static final int SITE_ID = 1234;
	public static final int CORPORATE_ID = 10000;
	public static final int ROLE_ID = 7890;

	private SiteAssignmentAction siteAssignmentAction;

	@Mock
	private AccountService accountService;
	@Mock
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Mock
	private EmployeeEntityService employeeEntityService;
	@Mock
	private EmployeeService employeeService;
	@Mock
	private RoleService roleService;
	@Mock
	private SkillService skillService;
	@Mock
	private StatusCalculatorService statusCalculatorService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		super.setupMocks();

		siteAssignmentAction = new SiteAssignmentAction();

		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.isOperator()).thenReturn(true);

		Whitebox.setInternalState(siteAssignmentAction, "accountService", accountService);
		Whitebox.setInternalState(siteAssignmentAction, "accountSkillEmployeeService", accountSkillEmployeeService);
		Whitebox.setInternalState(siteAssignmentAction, "employeeEntityService", employeeEntityService);
		Whitebox.setInternalState(siteAssignmentAction, "employeeService", employeeService);
		Whitebox.setInternalState(siteAssignmentAction, "permissions", permissions);
		Whitebox.setInternalState(siteAssignmentAction, "roleService", roleService);
		Whitebox.setInternalState(siteAssignmentAction, "skillService", skillService);
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
	}

	private List<Integer> setupMocksForClientSiteRoleInfoAndReturnCorporateIds() {
		Role corporateRole1 = mock(Role.class);
		Role corporateRole2 = mock(Role.class);

		RoleEmployee roleEmployee = mock(RoleEmployee.class);
		Employee employee = mock(Employee.class);

		List<Integer> corporateIds = Arrays.asList(CORPORATE_ID);
		List<Role> roles = Arrays.asList(corporateRole1, corporateRole2);

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
		when(siteRole1.getName()).thenReturn("Site Role 1");
		when(siteRole2.getName()).thenReturn("Site Role 2");
		return corporateIds;
	}

	@Test
	public void testRole() throws Exception {
		Role corporateRole = new RoleBuilder().accountId(CORPORATE_ID).name("Corporate Role").build();
		AccountSkill skill = new AccountSkillBuilder().name("Corporate Skill").build();
		Employee employee = new EmployeeBuilder()
				.firstName("First")
				.lastName("Last")
				.email("Email")
				.accountId(CONTRACTOR_ID_1)
				.build();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 15);

		Table<Employee, AccountSkill, AccountSkillEmployee> table = TreeBasedTable.create();
		AccountSkillEmployee accountSkillEmployee = new AccountSkillEmployeeBuilder().endDate(calendar.getTime()).build();
		table.put(employee, skill, accountSkillEmployee);

		AccountModel accountModel = new AccountModel.Builder().id(CONTRACTOR_ID_1).name("Contractor").build();
		Map<Integer, AccountModel> accountMap = new HashMap<>();
		accountMap.put(CONTRACTOR_ID_1, accountModel);

		when(accountService.getContractorMapForSite(SITE_ID)).thenReturn(accountMap);
		when(accountSkillEmployeeService.buildTable(anyListOf(Employee.class), anyListOf(AccountSkill.class))).thenReturn(table);
		when(employeeService.getEmployeesAssignedToSiteRole(anyListOf(Integer.class), eq(SITE_ID), any(Role.class)))
				.thenReturn(Arrays.asList(employee));
		when(employeeService.getEmployeesAssignedToSite(anyListOf(Integer.class), eq(SITE_ID))).thenReturn(Arrays.asList(employee));
		when(roleService.getRole(Integer.toString(ROLE_ID))).thenReturn(corporateRole);
		when(skillService.getSkillsForRole(corporateRole)).thenReturn(new ArrayList<AccountSkill>(Arrays.asList(skill)));

		siteAssignmentAction.setSiteId(SITE_ID);
		siteAssignmentAction.setId(Integer.toString(ROLE_ID));
		assertEquals("role", siteAssignmentAction.role());

		SiteAssignmentModel siteAssignmentModel = siteAssignmentAction.getSiteAssignmentModel();
		assertNotNull(siteAssignmentModel);
		assertEquals(1, siteAssignmentModel.getTotalEmployeesAssignedToSite());
		assertNotNull(siteAssignmentModel.getSkills());
		assertFalse(siteAssignmentModel.getSkills().isEmpty());

		verify(accountService).getAccountById(SITE_ID);
		verify(accountService).getContractorMapForSite(SITE_ID);
		verify(skillService).getSkillsForRole(corporateRole);
	}
}
