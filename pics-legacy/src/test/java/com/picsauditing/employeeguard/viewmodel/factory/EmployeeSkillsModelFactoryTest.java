package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.viewmodel.employee.EmployeeSkillModel;
import com.picsauditing.employeeguard.viewmodel.employee.EmployeeSkillsModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class EmployeeSkillsModelFactoryTest {
	private EmployeeSkillsModelFactory factory;

	@Mock
	private AccountSkill skillForCorporateRequirement;
	@Mock
	private AccountSkill skillForCorporateRole;
	@Mock
	private AccountSkill skillForProjectRole;
	@Mock
	private AccountSkill skillForProjectSkill;
	@Mock
	private AccountSkill skillForSiteRequirement;
	@Mock
	private AccountSkillEmployee employeeSkillForCorporateRequirement;
	@Mock
	private AccountSkillEmployee employeeSkillForCorporateRole;
	@Mock
	private AccountSkillEmployee employeeSkillForProjectRole;
	@Mock
	private AccountSkillEmployee employeeSkillForProjectSkill;
	@Mock
	private AccountSkillEmployee employeeSkillForSiteRequirement;
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
	@Mock
	private ProjectSkill projectSkill;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		factory = new EmployeeSkillsModelFactory();
	}

	@Test
	public void testCreate() throws Exception {
		setupMocks();

		Map<Role, Role> siteToCorporateRoles = new HashMap<>();
		siteToCorporateRoles.put(roleForSite, roleForCorporate);

		EmployeeSkillsModel model = factory.create(employee, Arrays.asList(projectRole), Arrays.asList(roleForSite),
				siteToCorporateRoles, Arrays.asList(skillForCorporateRequirement, skillForSiteRequirement));

		performAssertions(model);
	}

	private void setupMocks() {
		when(employee.getRoles()).thenReturn(Arrays.asList(roleEmployee));
		when(employee.getProjectRoles()).thenReturn(Arrays.asList(projectRoleEmployee));
		when(project.getSkills()).thenReturn(Arrays.asList(projectSkill));
		when(projectRole.getProject()).thenReturn(project);
		when(projectRole.getRole()).thenReturn(roleForProjectRole);
		when(projectRoleEmployee.getProjectRole()).thenReturn(projectRole);
		when(projectSkill.getSkill()).thenReturn(skillForProjectSkill);
		when(roleEmployee.getRole()).thenReturn(roleForSite);
		when(roleForCorporate.getSkills()).thenReturn(Arrays.asList(roleSkillForCorporateRole));
		when(roleSkillForCorporateRole.getSkill()).thenReturn(skillForCorporateRole);

		setupEmployeeSkillMocks();
		setupMockNames();
	}

	private void setupEmployeeSkillMocks() {
		when(employee.getSkills()).thenReturn(Arrays.asList(employeeSkillForCorporateRequirement,
				employeeSkillForCorporateRole, employeeSkillForProjectRole, employeeSkillForProjectSkill,
				employeeSkillForSiteRequirement));

		Date expired = DateBean.today();
		Date completed = DateBean.addYears(expired, 1);
		Date expiring = DateBean.addDays(expired, 15);

		when(employeeSkillForCorporateRequirement.getSkill()).thenReturn(skillForCorporateRequirement);
		when(employeeSkillForCorporateRequirement.getEndDate()).thenReturn(completed);

		when(employeeSkillForCorporateRole.getSkill()).thenReturn(skillForCorporateRole);
		when(employeeSkillForCorporateRequirement.getEndDate()).thenReturn(expiring);

		when(employeeSkillForProjectRole.getSkill()).thenReturn(skillForProjectRole);
		when(employeeSkillForProjectRole.getEndDate()).thenReturn(expired);

		when(employeeSkillForProjectSkill.getSkill()).thenReturn(skillForProjectSkill);
		when(employeeSkillForProjectSkill.getEndDate()).thenReturn(completed);

		when(employeeSkillForSiteRequirement.getSkill()).thenReturn(skillForSiteRequirement);
		when(employeeSkillForSiteRequirement.getEndDate()).thenReturn(expiring);
	}

	private void setupMockNames() {
		when(roleForCorporate.getName()).thenReturn("Corporate Role");
		when(roleForProjectRole.getName()).thenReturn("Project Role");

		when(skillForCorporateRequirement.getName()).thenReturn("Corporate Required Skill");
		when(skillForCorporateRole.getName()).thenReturn("Corporate Role Skill");
		when(skillForProjectRole.getName()).thenReturn("Project Role Skill");
		when(skillForProjectSkill.getName()).thenReturn("Project Skill");
		when(skillForSiteRequirement.getName()).thenReturn("Site Required Skill");
	}

	private void performAssertions(EmployeeSkillsModel model) {
		assertNotNull(model);
		assertNotNull(model.getEmployeeSkills());
		assertFalse(model.getEmployeeSkills().isEmpty());

		List<String> skillSections = new ArrayList<>(model.getEmployeeSkills().keySet());
		assertEquals("Project Required", skillSections.get(0));
		assertEquals("Corporate Role", skillSections.get(1));
		assertEquals("Project Role", skillSections.get(2));

		EmployeeSkillModel employeeSkillModel = model.getEmployeeSkills().get(skillSections.get(0)).get(0);
		assertEquals("Project Skill", employeeSkillModel.getSkillName());
		assertEquals(SkillStatus.Complete, employeeSkillModel.getSkillStatus());
	}
}
