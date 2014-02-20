package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class EmployeeSkillsModelFactoryTest {
	private EmployeeSkillsModelFactory factory;

	@Mock
	private AccountSkill skillForCorporateRole;
	@Mock
	private AccountSkill skillForProjectRole;
	@Mock
	private AccountSkillRole roleSkillForCorporateRole;
	@Mock
	private AccountSkillRole roleSkillForProjectRole;
	@Mock
	private Employee employee;
	@Mock
	private Role roleForCorporate;
	@Mock
	private Role roleForProjectRole;
	@Mock
	private Role roleForSite;
	@Mock
	private RoleEmployee roleEmployee;
	@Mock
	private Project project;
	@Mock
	private ProjectRole projectRole;
	@Mock
	private ProjectRoleEmployee projectRoleEmployee;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		factory = new EmployeeSkillsModelFactory();
	}

	@Test
	public void testCreate() throws Exception {
		when(roleForCorporate.getSkills()).thenReturn(Arrays.asList(roleSkillForCorporateRole));
		when(employee.getRoles()).thenReturn(Arrays.asList(roleEmployee));
		when(employee.getProjectRoles()).thenReturn(Arrays.asList(projectRoleEmployee));
		when(projectRole.getProject()).thenReturn(project);
		when(projectRole.getRole()).thenReturn(roleForProjectRole);
		when(projectRoleEmployee.getProjectRole()).thenReturn(projectRole);
		when(roleEmployee.getRole()).thenReturn(roleForSite);
		when(roleSkillForCorporateRole.getSkill()).thenReturn(skillForCorporateRole);

		fail("Not implemented");
	}
}
