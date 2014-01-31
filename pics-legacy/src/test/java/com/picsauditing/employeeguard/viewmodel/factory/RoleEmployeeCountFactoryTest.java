package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class RoleEmployeeCountFactoryTest {
	public static final int SITE_ROLE_ID = 12345;
	public static final int CORPORATE_ROLE_ID = 67890;
	public static final String NAME = "Test";

	private RoleEmployeeCountFactory roleEmployeeCountFactory;

	@Mock
	private Employee employee;
	@Mock
	private ProjectRole projectRole;
	@Mock
	private ProjectRoleEmployee projectRoleEmployee;
	@Mock
	private Role corporateRole;
	@Mock
	private Role siteRole;
	@Mock
	private RoleEmployee roleEmployee;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		roleEmployeeCountFactory = new RoleEmployeeCountFactory();
	}

	@Test
	public void testCreate_EmptyData() throws Exception {
		Map<RoleInfo, Integer> roleCount = roleEmployeeCountFactory
				.create(
						Collections.<RoleInfo>emptyList(),
						Collections.<Role, Role>emptyMap(),
						Collections.<Employee>emptyList());

		assertNotNull(roleCount);
		assertTrue(roleCount.isEmpty());
	}

	@Test
	public void testCreate_NullData() throws Exception {
		Map<RoleInfo, Integer> roleCount = roleEmployeeCountFactory
				.create(null, null, null);

		assertNotNull(roleCount);
		assertTrue(roleCount.isEmpty());
	}

	@Test
	public void testCreate_ValidData_SiteRole() throws Exception {
		RoleInfo roleInfo = new RoleInfo.Builder().id(SITE_ROLE_ID).name(NAME).build();

		when(corporateRole.getId()).thenReturn(roleInfo.getId());
		when(employee.getRoles()).thenReturn(Arrays.asList(roleEmployee));
		when(siteRole.getId()).thenReturn(CORPORATE_ROLE_ID);
		when(roleEmployee.getRole()).thenReturn(siteRole);
		Map<Role, Role> corporateToSiteRoles = new HashMap<>();
		corporateToSiteRoles.put(corporateRole, siteRole);

		Map<RoleInfo, Integer> roleCount = roleEmployeeCountFactory
				.create(Arrays.asList(roleInfo), corporateToSiteRoles, Arrays.asList(employee));

		assertNotNull(roleCount);
		assertFalse(roleCount.isEmpty());
		assertTrue(roleCount.containsKey(roleInfo));
		assertEquals(1, (int) roleCount.get(roleInfo));
	}

	@Test
	public void testCreate_ValidData_CorporateRole() throws Exception {
		RoleInfo roleInfo = new RoleInfo.Builder().id(CORPORATE_ROLE_ID).name(NAME).build();

		when(corporateRole.getId()).thenReturn(roleInfo.getId());
		when(employee.getProjectRoles()).thenReturn(Arrays.asList(projectRoleEmployee));
		when(projectRole.getRole()).thenReturn(corporateRole);
		when(projectRoleEmployee.getProjectRole()).thenReturn(projectRole);

		Map<RoleInfo, Integer> roleCount = roleEmployeeCountFactory
				.create(Arrays.asList(roleInfo), null, Arrays.asList(employee));

		assertNotNull(roleCount);
		assertFalse(roleCount.isEmpty());
		assertTrue(roleCount.containsKey(roleInfo));
		assertEquals(1, (int) roleCount.get(roleInfo));
	}
}
