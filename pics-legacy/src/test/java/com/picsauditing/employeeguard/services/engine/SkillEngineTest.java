package com.picsauditing.employeeguard.services.engine;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.*;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.entity.*;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.AccountType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class SkillEngineTest {

	public static final int CONTRACTOR_ID = 123;
	public static final int SITE_ID = 234;

	private SkillEngine skillEngine;

	@Mock
	private AccountService accountService;
	@Mock
	private EmployeeEntityService employeeEntityService;
	@Mock
	private GroupEntityService groupEntityService;
	@Mock
	private ProjectEntityService projectEntityService;
	@Mock
	private RoleEntityService roleEntityService;
	@Mock
	private SkillEntityService skillEntityService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		skillEngine = new SkillEngine();

		Whitebox.setInternalState(skillEngine, "accountService", accountService);
		Whitebox.setInternalState(skillEngine, "employeeEntityService", employeeEntityService);
		Whitebox.setInternalState(skillEngine, "groupEntityService", groupEntityService);
		Whitebox.setInternalState(skillEngine, "projectEntityService", projectEntityService);
		Whitebox.setInternalState(skillEngine, "roleEntityService", roleEntityService);
		Whitebox.setInternalState(skillEngine, "skillEntityService", skillEntityService);
	}

	@Test
	public void testGetEmployeeSkillsMapForAccount_Contractor() throws Exception {
		final Employee employee = buildFakeEmployee();
		final List<Employee> employees = Arrays.asList(employee);

		AccountModel contractor = setupForGetEmployeeSkillsMapForAccountContractor(employee, employees);

		Map<Employee, Set<AccountSkill>> result = skillEngine.getEmployeeSkillsMapForAccount(employees, contractor);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(1, result.keySet().size());
		assertEquals(2, result.get(employee).size());
	}

	private AccountModel setupForGetEmployeeSkillsMapForAccountContractor(final Employee employee,
																		  final List<Employee> employees) {
		Group group = buildFakeGroup();
		final Set<Group> groups = new HashSet<>(Arrays.asList(group));

		AccountSkill skill1 = buildFakeSkill();
		skill1.setName("Skill 1");
		final Set<AccountSkill> skills = new HashSet<>(Arrays.asList(skill1));

		AccountSkill requiredSkill = buildFakeSkill();
		requiredSkill.setName("Required Skill by Contractor");

		AccountModel contractor = buildFakeContractor();

		prepareMocksForGetEmployeeSkillsMapForAccountContractor(employee, employees, groups, skills, requiredSkill);

		return contractor;
	}

	private void prepareMocksForGetEmployeeSkillsMapForAccountContractor(final Employee employee,
																		 final List<Employee> employees,
																		 final Set<Group> groups,
																		 final Set<AccountSkill> skills,
																		 final AccountSkill requiredSkill) {

		Map<Employee, Set<Group>> employeeGroups = new HashMap<Employee, Set<Group>>() {{
			put(employee, groups);
		}};

		Map<Employee, Set<AccountSkill>> employeeGroupSkills = new HashMap<Employee, Set<AccountSkill>>() {{
			put(employee, skills);
		}};

		Map<Employee, Set<AccountSkill>> employeeContractorRequiredSkills = new HashMap<Employee, Set<AccountSkill>>() {{
			put(employee, new HashSet<AccountSkill>());
			get(employee).add(requiredSkill);
		}};

		when(groupEntityService.getEmployeeGroups(employees)).thenReturn(employeeGroups);
		when(skillEntityService.getGroupSkillsForEmployees(employeeGroups)).thenReturn(employeeGroupSkills);
		when(skillEntityService.getRequiredSkillsForContractor(CONTRACTOR_ID, employees))
				.thenReturn(employeeContractorRequiredSkills);
	}

	private Employee buildFakeEmployee() {
		return new EmployeeBuilder()
				.firstName("First")
				.lastName("Last")
				.email("Email")
				.slug("Slug")
				.build();
	}

	private Group buildFakeGroup() {
		return new GroupBuilder()
				.build();
	}

	private AccountSkill buildFakeSkill() {
		return new AccountSkillBuilder()
				.build();
	}

	private Role buildFakeRole() {
		return new RoleBuilder()
				.build();
	}

	private Project buildFakeProject() {
		return new ProjectBuilder()
				.build();
	}

	private AccountModel buildFakeContractor() {
		return new AccountModel.Builder()
				.id(CONTRACTOR_ID)
				.accountType(AccountType.CONTRACTOR)
				.build();
	}
}
