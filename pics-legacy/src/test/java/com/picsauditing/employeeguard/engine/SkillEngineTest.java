package com.picsauditing.employeeguard.engine;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.*;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.entity.*;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.services.models.AccountType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.fail;
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

		Group group = buildFakeGroup();
		final Set<Group> groups = new HashSet<>(Arrays.asList(group));

		AccountSkill skill = buildFakeSkill();
		final Set<AccountSkill> skills = new HashSet<>(Arrays.asList(skill));

		final Role role1 = buildFakeRole();
		role1.setName("Role 1");

		final Role role2 = buildFakeRole();
		role2.setName("Role 2");

		AccountModel contractor = buildFakeContractor();
		List<Integer> siteIds = Arrays.asList(SITE_ID);

		Map<Employee, Set<Group>> employeeGroups = new HashMap<Employee, Set<Group>>() {{
			put(employee, groups);
		}};

		Map<Employee, Set<AccountSkill>> employeeGroupSkills = new HashMap<Employee, Set<AccountSkill>>() {{
			put(employee, skills);
		}};

		Map<Role, Set<Employee>> roleEmployees = new HashMap<Role, Set<Employee>>() {{
			put(role1, new HashSet<>(employees));
		}};

		when(accountService.getOperatorIdsForContractor(CONTRACTOR_ID)).thenReturn(siteIds);
		when(employeeEntityService.getEmployeesBySiteRoles(siteIds)).thenReturn(roleEmployees);
		when(groupEntityService.getEmployeeGroups(employees)).thenReturn(employeeGroups);
		when(skillEntityService.getGroupSkillsForEmployees(employeeGroups)).thenReturn(employeeGroupSkills);

		fail();
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
