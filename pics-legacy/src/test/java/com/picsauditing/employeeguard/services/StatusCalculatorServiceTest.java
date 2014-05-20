package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.daos.AccountSkillProfileDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.entities.builders.AccountSkillProfileBuilder;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.entities.builders.ProfileBuilder;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.status.StatusCalculatorService;
import org.joda.time.DateTime;
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

	private EGTestDataUtil egTestDataUtil = new EGTestDataUtil();

	public static final int CONTRACTOR_ID = 1234;
	public static final int CORPORATE_ID = 45;
	public static final int SITE_ID = 17;

	private StatusCalculatorService service;

	@Mock
	private AccountSkill skill;

	@Mock
	private AccountSkillProfileDAO accountSkillProfileDAO;
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

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		service = new StatusCalculatorService();

		Whitebox.setInternalState(service, "accountSkillProfileDAO", accountSkillProfileDAO);
	}

	@Test
	public void testGetEmployeeStatusRollUpForSkills_WhenEmployeeHasNoDocumentation() throws Exception {
		List<AccountSkill> fakeSkills = buildFakeAccountSkills();
		List<Employee> fakeEmployees = buildFakeEmployees();

		//-- Employee has no documentation.
		when(accountSkillProfileDAO.findByEmployeesAndSkills(anyCollectionOf(Employee.class),
				anyCollectionOf(AccountSkill.class))).thenReturn(Collections.EMPTY_LIST);

		Map<Employee, List<SkillStatus>> result = service.getEmployeeStatusRollUpForSkills(fakeEmployees, fakeSkills);

		verifyResults_WhenEmployeeHasNoDocumentation(fakeEmployees, result);
	}

	@Test
	public void testGetEmployeeStatusRollUpForSkills() throws Exception {
		AccountSkillProfile accountSkillProfile = egTestDataUtil.prepareExpiringAccountSkillEmployee();
		AccountSkill skill = accountSkillProfile.getSkill();
		accountSkillProfile.setProfile(employee.getProfile());
		accountSkillProfile.setEndDate(null);
		when(employee.getAccountId()).thenReturn(CONTRACTOR_ID);
		when(employee.getName()).thenReturn("Employee Name");
		when(employee.getProfile()).thenReturn(new ProfileBuilder().skills(Arrays.asList(accountSkillProfile)).build());

		Set<Employee> accountEmployees = new HashSet<>();
		accountEmployees.add(employee);

		Map<Employee, Set<AccountSkill>> employeeSkills = new HashMap<>();
		employeeSkills.put(employee, new HashSet<>(Arrays.asList(skill)));

		Map<Employee, SkillStatus> map = service.getEmployeeStatusRollUpForSkills(accountEmployees, employeeSkills);

		performAssertionsOnGetEmployeeStatusRollUpForSkills(map);
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

	private void verifyResults_WhenEmployeeHasNoDocumentation(List<Employee> fakeEmployees, Map<Employee, List<SkillStatus>> result) {
		assertEquals(3, result.size());

		// All expired
		assertTrue(Utilities.collectionsAreEqual(Arrays.asList(SkillStatus.Expired, SkillStatus.Expired, SkillStatus.Expired),
				result.get(fakeEmployees.get(0))));

		// All expired
		assertTrue(Utilities.collectionsAreEqual(Arrays.asList(SkillStatus.Expired, SkillStatus.Expired, SkillStatus.Expired),
				result.get(fakeEmployees.get(1))));

		// All expired
		assertTrue(Utilities.collectionsAreEqual(Arrays.asList(SkillStatus.Expired, SkillStatus.Expired, SkillStatus.Expired),
				result.get(fakeEmployees.get(2))));
	}

	private void verifyResults(List<Employee> fakeEmployees, Map<Employee, List<SkillStatus>> result) {
		assertEquals(3, result.size());

		// first skill is complete, second and third skill is expired
		assertTrue(Utilities.collectionsAreEqual(Arrays.asList(SkillStatus.Expired, SkillStatus.Expired, SkillStatus.Completed),
				result.get(fakeEmployees.get(0))));

		// first skill is expired, second skill is expiring, third skill is complete
		assertTrue(Utilities.collectionsAreEqual(Arrays.asList(SkillStatus.Completed, SkillStatus.Expired, SkillStatus.Expired),
				result.get(fakeEmployees.get(0))));

		// all skills are expired
		assertTrue(Utilities.collectionsAreEqual(Arrays.asList(SkillStatus.Expired, SkillStatus.Expiring, SkillStatus.Expired),
				result.get(fakeEmployees.get(1))));
	}

	@Test
	public void testGetAllSkillStatusesForEntity_NoEmployeeSkill() {
		List<AccountSkill> skills = buildFakeAccountSkills();
		List<Employee> employees = buildFakeEmployees();
		List<AccountSkillProfile> accountSkillProfiles = buildFakeAccountSkillEmployees(employees, skills);

		when(accountSkillProfileDAO.findByEmployeesAndSkills(anyCollectionOf(Employee.class), anyCollectionOf(AccountSkill.class)))
				.thenReturn(accountSkillProfiles.subList(1, accountSkillProfiles.size()));

		HashMap<Project, Map<Employee, Set<AccountSkill>>> entityEmployeeMap = new HashMap<>();
		entityEmployeeMap.put(project, new HashMap<Employee, Set<AccountSkill>>());

		for (Employee employee : employees) {
			entityEmployeeMap.get(project).put(employee, new HashSet<>(skills));
		}

		Map<Project, List<SkillStatus>> result = service.getAllSkillStatusesForEntity(entityEmployeeMap);

		assertNotNull(result);
		assertFalse(result.isEmpty());
	}

	@Test
	public void testGetAllSkillStatusesForEntity_EmployeeHasNoDocumentation() {
		List<AccountSkill> skills = buildFakeAccountSkills();
		List<Employee> employees = buildFakeEmployees();

		when(accountSkillProfileDAO.findByEmployeesAndSkills(anyCollectionOf(Employee.class), anyCollectionOf(AccountSkill.class)))
				.thenReturn(Collections.EMPTY_LIST);

		HashMap<Project, Map<Employee, Set<AccountSkill>>> entityEmployeeMap = new HashMap<>();
		entityEmployeeMap.put(project, new HashMap<Employee, Set<AccountSkill>>());

		for (Employee employee : employees) {
			entityEmployeeMap.get(project).put(employee, new HashSet<>(skills));
		}

		Map<Project, List<SkillStatus>> result = service.getAllSkillStatusesForEntity(entityEmployeeMap);

		assertNotNull(result);
		assertFalse(result.isEmpty());
	}


	private void setupFakeAccountSkillEmployee(List<AccountSkill> fakeSkills, List<Employee> fakeEmployees) {
		List<AccountSkillProfile> fakeAccountSkillProfiles = buildFakeAccountSkillEmployees(fakeEmployees,
				fakeSkills);

		when(accountSkillProfileDAO.findByEmployeesAndSkills(anyCollectionOf(Employee.class),
				anyCollectionOf(AccountSkill.class))).thenReturn(fakeAccountSkillProfiles);
	}

	private List<AccountSkill> buildFakeAccountSkills() {
		return Arrays.asList(
				new AccountSkillBuilder(1, CORPORATE_ID)
						.name("Test Skill 1")
						.intervalPeriod(1)
						.skillType(SkillType.Training)
						.intervalType(IntervalType.YEAR)
						.build(),

				new AccountSkillBuilder(2, CORPORATE_ID)
						.name("Test Skill 2")
						.skillType(SkillType.Training)
						.intervalPeriod(1)
						.intervalType(IntervalType.WEEK)
						.build(),

				new AccountSkillBuilder(3, CORPORATE_ID)
						.name("Test Skill 3")
						.skillType(SkillType.Training)
						.intervalPeriod(1)
						.intervalType(IntervalType.YEAR)
						.build()
		);
	}

	private List<Employee> buildFakeEmployees() {
		return Arrays.asList(
				new EmployeeBuilder()
						.accountId(CONTRACTOR_ID)
						.profile(new ProfileBuilder()
								.id(12)
								.email("bob@test.com")
								.build())
						.email("bob@test.com")
						.build(),

				new EmployeeBuilder()
						.accountId(CONTRACTOR_ID)
						.profile(new ProfileBuilder()
								.id(13)
								.email("joe@test.com")
								.build())
						.email("joe@test.com")
						.build(),

				new EmployeeBuilder()
						.accountId(CONTRACTOR_ID)
						.profile(new ProfileBuilder()
								.id(14)
								.email("jill@test.com")
								.build())
						.email("jill@test.com")
						.build()
		);
	}

	private List<AccountSkillProfile> buildFakeAccountSkillEmployees(final List<Employee> employees,
																	 final List<AccountSkill> skills) {
		return Arrays.asList(
				new AccountSkillProfileBuilder()
						.accountSkill(skills.get(0))
						.profile(employees.get(0).getProfile())
						.startDate((new DateTime().minusDays(2)).toDate())
						.endDate(null)
						.build(),

				new AccountSkillProfileBuilder()
						.accountSkill(skills.get(1))
						.profile(employees.get(1).getProfile())
						.startDate((new DateTime().minusDays(2)).toDate())
						.endDate(null)
						.build(),

				new AccountSkillProfileBuilder()
						.accountSkill(skills.get(2))
						.profile(employees.get(2).getProfile())
						.startDate((new DateTime().minusDays(2)).toDate())
						.endDate(null)
						.build()
		);
	}
}
