package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ContractorEmployeeRoleAssignmentFactoryTest {
	private ContractorEmployeeRoleAssignmentFactory factory;

	@Mock
	private AccountSkill accountSkill;
	@Mock
	private AccountSkillEmployee accountSkillEmployee;
	@Mock
	private AccountSkillRole accountSkillRole;
	@Mock
	private Employee employee1;
	@Mock
	private Employee employee2;
	@Mock
	private ProjectRole projectRole;
	@Mock
	private ProjectRoleEmployee projectRoleEmployee;
	@Mock
	private Role role;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		factory = new ContractorEmployeeRoleAssignmentFactory();
	}

	@Test
	public void testBuild_Null() throws Exception {
		List<ContractorEmployeeRoleAssignment> assignments = factory.build(null, null, null, null);

		assertNotNull(assignments);
		assertTrue(assignments.isEmpty());
	}

	@Test
	public void testBuild_Empty() throws Exception {
		List<ContractorEmployeeRoleAssignment> assignments = factory.build(
				Collections.<Employee>emptyList(),
				new Role(),
				Collections.<Employee>emptySet(),
				Collections.<Employee, Set<AccountSkillEmployee>>emptyMap());

		assertNotNull(assignments);
		assertTrue(assignments.isEmpty());
	}

	@Test
	public void testBuild_Valid() throws Exception {
		initializeMocks();

		Map<Employee, Set<AccountSkillEmployee>> employeeSkills = new HashMap<>();
		Utilities.addToMapOfKeyToSet(employeeSkills, employee1, accountSkillEmployee);

		List<ContractorEmployeeRoleAssignment> assignments = factory.build(
				Arrays.asList(employee1, employee2),
				role,
				new HashSet<>(Arrays.asList(employee1)),
				employeeSkills);

		performAssertions(assignments);
	}

	private void initializeMocks() {
		Date threeMonthsFromNow = DateBean.addMonths(DateBean.today(), 3);
		System.out.println(threeMonthsFromNow);

		when(accountSkillEmployee.getEndDate()).thenReturn(threeMonthsFromNow);
		when(accountSkillEmployee.getSkill()).thenReturn(accountSkill);
		when(accountSkillRole.getSkill()).thenReturn(accountSkill);
		when(employee1.getSkills()).thenReturn(Arrays.asList(accountSkillEmployee));
		when(employee1.getProjectRoles()).thenReturn(Arrays.asList(projectRoleEmployee));
		when(employee1.getName()).thenReturn("First");
		when(employee2.getName()).thenReturn("Second");
		when(projectRole.getRole()).thenReturn(role);
		when(projectRoleEmployee.getProjectRole()).thenReturn(projectRole);
		when(role.getSkills()).thenReturn(Arrays.asList(accountSkillRole));
	}

	private void performAssertions(List<ContractorEmployeeRoleAssignment> assignments) {
		assertNotNull(assignments);
		assertFalse(assignments.isEmpty());

		for (ContractorEmployeeRoleAssignment assignment : assignments) {
			assertNotNull(assignment.getSkillStatuses());
			assertEquals(1, assignment.getSkillStatuses().size());
		}

		assertEquals(SkillStatus.Complete, assignments.get(0).getSkillStatuses().get(0));
		assertEquals(SkillStatus.Expired, assignments.get(1).getSkillStatuses().get(0));
	}
}
