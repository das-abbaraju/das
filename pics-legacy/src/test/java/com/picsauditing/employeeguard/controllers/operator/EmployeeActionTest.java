package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
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
	}

	@Test
	public void testShow() throws Exception {
		when(permissions.getAccountId()).thenReturn(454);
		Employee fakeEmployee = buildFakeEmployee();
		when(employeeService.findEmployee(anyString())).thenReturn(fakeEmployee);
		HashMap<Project, Set<Role>> projectRoleMap = new HashMap<>();
		when(projectService.getProjectRolesForEmployee(454, fakeEmployee)).thenReturn(projectRoleMap);

		Set<Role> fakeRoles = new HashSet<>();
		when(roleService.getEmployeeRolesForSite(454, fakeEmployee)).thenReturn(fakeRoles);
		Map<Role, Set<AccountSkill>> roleSkillMap = new HashMap<>();
		when(skillService.getSkillsForRoles(454, fakeRoles)).thenReturn(roleSkillMap);

		Map<Project, Set<AccountSkill>> projectSkillMap = new HashMap<>();
		when(skillService.getAllProjectSkillsForEmployeeProjectRoles(454, projectRoleMap)).thenReturn(projectSkillMap);

		Map<Role, SkillStatus> roleStatusMap = new HashMap<>();
		when(statusCalculatorService.getSkillStatusPerEntity(fakeEmployee, roleSkillMap)).thenReturn(roleStatusMap);

		Map<Project, SkillStatus> projectStatusMap = new HashMap<>();
		when(statusCalculatorService.getSkillStatusPerEntity(fakeEmployee, projectSkillMap)).thenReturn(projectStatusMap);

		when(statusCalculatorService.calculateOverallStatus(anyCollectionOf(SkillStatus.class)))
				.thenReturn(SkillStatus.Expiring);

		when(statusCalculatorService.getSkillStatuses(any(Employee.class), anyCollectionOf(AccountSkill.class)))
				.thenReturn(new HashMap<AccountSkill, SkillStatus>());

		Map<Integer, AccountModel> fakeAccounts = new HashMap<>();
		when(accountService.getContractorsForEmployee(fakeEmployee)).thenReturn(fakeAccounts);

		List<AccountSkill> siteSkills = new ArrayList<>();
		when(skillService.getRequiredSkillsForSiteAndCorporates(454)).thenReturn(siteSkills);

		String result = employeeAction.show();

		assertEquals(PicsActionSupport.JSON_STRING, result);
		Approvals.verify(employeeAction.getJsonString());
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
