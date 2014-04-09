package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.ProjectRole;
import com.picsauditing.employeeguard.entities.ProjectRoleEmployee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class RoleEmployeeCountFactoryTest {

	public static final int ROLE_ID = 67890;
	public static final String NAME = "Test";

	private RoleEmployeeCountFactory roleEmployeeCountFactory;

	@Mock
	private Employee employee;
	@Mock
	private ProjectRole projectRole;
	@Mock
	private ProjectRoleEmployee projectRoleEmployee;
	@Mock
	private Role role;

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
						Collections.<Employee>emptyList(),
						Collections.<Role, Set<Employee>>emptyMap());

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
	public void testCreate_ValidData_Role() throws Exception {
		RoleInfo roleInfo = new RoleInfo.Builder().id(ROLE_ID).name(NAME).build();

		when(role.getId()).thenReturn(roleInfo.getId());
		when(employee.getProjectRoles()).thenReturn(Arrays.asList(projectRoleEmployee));
		when(projectRole.getRole()).thenReturn(role);
		when(projectRoleEmployee.getProjectRole()).thenReturn(projectRole);

		Map<RoleInfo, Integer> roleCount = roleEmployeeCountFactory.create(
				Arrays.asList(roleInfo),
				Arrays.asList(employee),
				new HashMap<Role, Set<Employee>>() {{
					put(role, new HashSet<Employee>());
					get(role).add(employee);
				}});

		assertNotNull(roleCount);
		assertFalse(roleCount.isEmpty());
		assertTrue(roleCount.containsKey(roleInfo));
		assertEquals(1, (int) roleCount.get(roleInfo));
	}
}
