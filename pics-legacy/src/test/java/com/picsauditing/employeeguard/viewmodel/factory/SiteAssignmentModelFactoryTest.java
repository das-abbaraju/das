package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.services.SkillUsage;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.contractor.EmployeeSiteAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.contractor.SiteAssignmentModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class SiteAssignmentModelFactoryTest {
	public static final int SITE_ID = 1234;
	public static final int CONTRACTOR_ID = 2345;
	private SiteAssignmentModelFactory factory;

	@Mock
	private AccountSkill accountSkill;
	@Mock
	private Employee employee;
	@Mock
	private Role role;
//	@Mock
//	private RoleEmployee roleEmployee;
	@Mock
	private SkillUsage skillUsage;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		factory = new SiteAssignmentModelFactory();
	}

	@Test
	public void testCreate_EmployeeSiteAssignmentModels() throws Exception {
		SiteAssignmentModel siteAssignmentModel = factory.create(
				Collections.<EmployeeSiteAssignmentModel>emptyList(),
				Collections.<RoleInfo, Integer>emptyMap());

		assertNotNull(siteAssignmentModel);
		assertTrue(siteAssignmentModel.getEmployeeSiteAssignmentModels().isEmpty());
		assertTrue(siteAssignmentModel.getRoleEmployee().isEmpty());
	}

	@Test
	public void testCreate_EmployeeSiteAssignmentModels_WithData() throws Exception {
		EmployeeSiteAssignmentModel employeeModel = new EmployeeSiteAssignmentModel.Builder()
				.employeeName("Employee Name")
				.build();
		RoleInfo roleInfo = new RoleInfo.Builder().name("Role").build();
		Map<RoleInfo, Integer> roleCount = new HashMap<>();
		roleCount.put(roleInfo, 3);

		SiteAssignmentModel siteAssignmentModel = factory.create(Arrays.asList(employeeModel), roleCount);

		performAssertions(siteAssignmentModel, roleInfo);
	}

	@Test
	public void testCreate_SkillUsages() throws Exception {
		AccountModel site = new AccountModel.Builder().id(SITE_ID).name("Site").build();
		AccountModel contractor = new AccountModel.Builder().id(CONTRACTOR_ID).name("Contractor").build();
		Map<AccountSkill, Set<Integer>> siteRequiredSkills = new HashMap<>();
		siteRequiredSkills.put(accountSkill, new HashSet<>(Arrays.asList(1, 2)));

		RoleInfo roleInfo = new RoleInfo.Builder().name("Role").build();
		Map<RoleInfo, Integer> roleCount = new HashMap<>();
		roleCount.put(roleInfo, 3);

		setupMocks(siteRequiredSkills);

		SiteAssignmentModel siteAssignmentModel = factory.create(
				site,
				Arrays.asList(contractor),
				Arrays.asList(skillUsage),
				roleCount);

		performAssertions(siteAssignmentModel, roleInfo);
	}

	private void setupMocks(Map<AccountSkill, Set<Integer>> siteRequiredSkills) {
		when(employee.getAccountId()).thenReturn(CONTRACTOR_ID);
//		when(employee.getRoles()).thenReturn(Arrays.asList(roleEmployee));
		when(employee.getProjectRoles()).thenReturn(Collections.<ProjectRoleEmployee>emptyList());
		when(employee.getName()).thenReturn("Employee Name");
		when(role.getAccountId()).thenReturn(SITE_ID);
//		when(roleEmployee.getRole()).thenReturn(role);
		when(skillUsage.getEmployee()).thenReturn(employee);
		when(skillUsage.getSiteAssignmentSkills()).thenReturn(Collections.<AccountSkill, Set<Integer>>emptyMap());
		when(skillUsage.getProjectJobRoleSkills()).thenReturn(Collections.<AccountSkill, Set<Role>>emptyMap());
		when(skillUsage.getSiteRequiredSkills()).thenReturn(siteRequiredSkills);
		when(skillUsage.getCorporateRequiredSkills()).thenReturn(Collections.<AccountSkill, Set<Integer>>emptyMap());
	}

	private void performAssertions(SiteAssignmentModel siteAssignmentModel, RoleInfo roleInfo) {
		assertNotNull(siteAssignmentModel);
		assertFalse(siteAssignmentModel.getEmployeeSiteAssignmentModels().isEmpty());
		assertEquals("Employee Name", siteAssignmentModel.getEmployeeSiteAssignmentModels().get(0).getEmployeeName());
		assertFalse(siteAssignmentModel.getRoleEmployee().isEmpty());
		assertEquals(3, (int) siteAssignmentModel.getRoleEmployee().get(roleInfo));
	}
}
