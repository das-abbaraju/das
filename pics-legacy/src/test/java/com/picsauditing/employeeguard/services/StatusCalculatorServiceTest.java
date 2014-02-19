package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.AccountSkillEmployeeDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.entities.builders.AccountSkillEmployeeBuilder;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.when;

public class StatusCalculatorServiceTest {
	public static final int CONTRACTOR_ID = 1234;
	public static final int CORPORATE_ID = 45;

	private StatusCalculatorService service;

	@Mock
	private AccountSkill skill;
	@Mock
	private AccountSkillEmployee accountSkillEmployee;
	@Mock
	private AccountSkillEmployeeDAO accountSkillEmployeeDAO;
	@Mock
	private AccountSkillRole accountSkillRole;
	@Mock
	private Employee employee;
	@Mock
	private Project project;
	@Mock
	private ProjectRole projectRole;
	@Mock
	private ProjectRoleEmployee projectRoleEmployee;
	@Mock
	private Role role;
	@Mock
	private RoleEmployee roleEmployee;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		service = new StatusCalculatorService();

		Whitebox.setInternalState(service, "accountSkillEmployeeDAO", accountSkillEmployeeDAO);
	}

	@Test
	public void testGetEmployeeStatusRollUpForSkills() throws Exception {
		setupMocksForGetEmployeeStatusRollUpForSkills();

		Set<Employee> accountEmployees = new HashSet<>();
		accountEmployees.add(employee);

		Map<Employee, Set<AccountSkill>> employeeSkills = new HashMap<>();
		employeeSkills.put(employee, new HashSet<>(Arrays.asList(skill)));

		Map<Employee, SkillStatus> map = service.getEmployeeStatusRollUpForSkills(accountEmployees, employeeSkills);

		performAssertionsOnGetEmployeeStatusRollUpForSkills(map);
	}

	private void setupMocksForGetEmployeeStatusRollUpForSkills() {
		when(accountSkillEmployee.getEmployee()).thenReturn(employee);
		when(accountSkillEmployee.getEndDate()).thenReturn(DateBean.addDays(DateBean.today(), 15));
		when(accountSkillEmployee.getSkill()).thenReturn(skill);
		when(employee.getAccountId()).thenReturn(CONTRACTOR_ID);
		when(employee.getName()).thenReturn("Employee Name");
		when(employee.getSkills()).thenReturn(Arrays.asList(accountSkillEmployee));
	}

	private void performAssertionsOnGetEmployeeStatusRollUpForSkills(Map<Employee, SkillStatus> map) {
		assertNotNull(map);
		assertFalse(map.isEmpty());
		assertEquals(1, map.size());
		assertEquals(SkillStatus.Expiring, map.get(employee));
	}

	@Test
	public void testGetEmployeeStatusRollUpForSkills_CollectionOfEmployeesAndSkills() {
		List<AccountSkill> fakeSkills = buildFakeAccountSkills();
		List<Employee> fakeEmployees = buildFakeEmployees();
		setupFakeAccountSkillEmployee(fakeSkills, fakeEmployees);

		Map<Employee, List<SkillStatus>> result = service.getEmployeeStatusRollUpForSkills(fakeEmployees, fakeSkills);

		verifyResults(fakeEmployees, result);

	}

	private void setupFakeAccountSkillEmployee(List<AccountSkill> fakeSkills, List<Employee> fakeEmployees) {
		List<AccountSkillEmployee> fakeAccountSkillEmployees = buildFakeAccountSkillEmployees(fakeEmployees,
				fakeSkills);
		when(accountSkillEmployeeDAO.findByEmployeesAndSkills(anyCollectionOf(Employee.class),
				anyCollectionOf(AccountSkill.class))).thenReturn(fakeAccountSkillEmployees);
	}

	private List<AccountSkill> buildFakeAccountSkills() {
		return Arrays.asList(
				new AccountSkillBuilder(1, CORPORATE_ID).name("Test Skill 1").skillType(SkillType.Training).build(),
				new AccountSkillBuilder(2, CORPORATE_ID).name("Test Skill 2").skillType(SkillType.Training).build(),
				new AccountSkillBuilder(3, CORPORATE_ID).name("Test Skill 3").skillType(SkillType.Training).build()
		);
	}

	private List<Employee> buildFakeEmployees() {
		return Arrays.asList(
				new EmployeeBuilder().accountId(CONTRACTOR_ID).email("bob@test.com").build(),
				new EmployeeBuilder().accountId(CONTRACTOR_ID).email("joe@test.com").build(),
				new EmployeeBuilder().accountId(CONTRACTOR_ID).email("jill@test.com").build()
		);
	}

	private void verifyResults(List<Employee> fakeEmployees, Map<Employee, List<SkillStatus>> result) {
		assertEquals(3, result.size());

		// first skill is complete, second and third skill is expired
		assertTrue(Utilities.collectionsAreEqual(Arrays.asList(SkillStatus.Complete, SkillStatus.Expired, SkillStatus.Expired),
				result.get(fakeEmployees.get(0))));

		// first skill is expired, second skill is expiring, third skill is complete
		assertTrue(Utilities.collectionsAreEqual(Arrays.asList(SkillStatus.Expired, SkillStatus.Expiring, SkillStatus.Complete),
				result.get(fakeEmployees.get(1))));

		// all skills are expired
		assertTrue(Utilities.collectionsAreEqual(Arrays.asList(SkillStatus.Expired, SkillStatus.Expired, SkillStatus.Expired),
				result.get(fakeEmployees.get(2))));
	}

	private List<AccountSkillEmployee> buildFakeAccountSkillEmployees(final List<Employee> employees,
																	  final List<AccountSkill> skills) {
		return Arrays.asList(
				new AccountSkillEmployeeBuilder()
						.accountSkill(skills.get(0))
						.employee(employees.get(0))
						.startDate(DateBean.today())
						.endDate(DateBean.addDays(DateBean.today(), 45))
						.build(),
				new AccountSkillEmployeeBuilder()
						.accountSkill(skills.get(1))
						.employee(employees.get(1))
						.startDate(DateBean.today())
						.endDate(DateBean.addDays(DateBean.today(), 5))
						.build(),
				new AccountSkillEmployeeBuilder()
						.accountSkill(skills.get(2))
						.employee(employees.get(1))
						.startDate(DateBean.today())
						.endDate(DateBean.addDays(DateBean.today(), 40))
						.build()
		);
	}

//	public void testGetEmployeeStatusesForProjectsAndJobRoles() {
//		Role corporateRole = mock(Role.class);
//		Role roleOnProject = mock(Role.class);
//
//		Map<Role, Role> siteToCorporateRoles = new HashMap<>();
//		siteToCorporateRoles.put(role, corporateRole);
//		List<AccountSkillRole> accountSkillRoles = Arrays.asList(accountSkillRole);
//
//		when(accountSkillEmployee.getEndDate())
//				.thenReturn(DateBean.addDays(DateBean.today(), 15))
//				.thenReturn(DateBean.addYears(DateBean.today(), 3));
//		when(accountSkillEmployee.getSkill()).thenReturn(skill);
//		when(accountSkillRole.getSkill()).thenReturn(skill);
//		when(corporateRole.getSkills()).thenReturn(accountSkillRoles);
//		when(employee.getProjectRoles()).thenReturn(Arrays.asList(projectRoleEmployee));
//		when(employee.getRoles()).thenReturn(Arrays.asList(roleEmployee));
//		when(employee.getSkills()).thenReturn(Arrays.asList(accountSkillEmployee));
//		when(projectRole.getRole()).thenReturn(roleOnProject);
//		when(projectRole.getProject()).thenReturn(project);
//		when(projectRoleEmployee.getProjectRole()).thenReturn(projectRole);
//		when(roleEmployee.getRole()).thenReturn(role);
//		when(roleOnProject.getSkills()).thenReturn(accountSkillRoles);
//
//		EmployeeProjectAndRoleStatus employeeProjectAndRoleStatus = service.getEmployeeStatusesForProjectsAndJobRoles(
//				employee, siteToCorporateRoles, Collections.<AccountSkill>emptyList());
//
//		assertNotNull(employeeProjectAndRoleStatus);
//		assertNotNull(employeeProjectAndRoleStatus.getProjectStatuses());
//		assertNotNull(employeeProjectAndRoleStatus.getRoleStatuses());
//		assertEquals(SkillStatus.Expiring, employeeProjectAndRoleStatus.getProjectStatuses().get(project));
//		assertEquals(SkillStatus.Complete, employeeProjectAndRoleStatus.getRoleStatuses().get(corporateRole));
//	}
}
