package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.services.SkillUsage;
import com.picsauditing.employeeguard.viewmodel.contractor.EmployeeSiteAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.contractor.SiteAssignmentModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class SiteAssignmentModelFactoryTest {
	public static final int SITE_ID = 1234;

	private SiteAssignmentModelFactory factory;

	public static final int CONTRACTOR_ID = 2345;
	@Mock
	private AccountSkill accountSkill;
	@Mock
	private Employee employee;
	@Mock
	private Role role;
	@Mock
	private SkillUsage skillUsage;

	@Mock
	private SiteAssignment siteAssignment;
	@Mock
	private ProjectRoleEmployee projectRoleEmployee;
	@Mock
	private Project project;
	@Mock
	private ProjectRole projectRole;


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

	private void performAssertions(SiteAssignmentModel siteAssignmentModel, RoleInfo roleInfo) {
		assertNotNull(siteAssignmentModel);
		assertFalse(siteAssignmentModel.getEmployeeSiteAssignmentModels().isEmpty());
		assertEquals("Employee Name", siteAssignmentModel.getEmployeeSiteAssignmentModels().get(0).getEmployeeName());
		assertFalse(siteAssignmentModel.getRoleEmployee().isEmpty());
		assertEquals(3, (int) siteAssignmentModel.getRoleEmployee().get(roleInfo));
	}
}
