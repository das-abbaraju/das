package com.picsauditing.employeeguard.viewmodel.operator.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.viewmodel.operator.SiteAssignmentModel;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OperatorSiteAssignmentModelFactoryTest {

	private OperatorSiteAssignmentModelFactory operatorSiteAssignmentModelFactory;

	@Before
	public void setUp() throws Exception {
		operatorSiteAssignmentModelFactory = new OperatorSiteAssignmentModelFactory();
	}

	@Test
	public void testCreate() throws Exception {
		List<Employee> employees = Arrays.asList(new Employee(), new Employee(), new Employee());
		Map<RoleInfo, Integer> roleCounts = new HashMap<>();
		roleCounts.put(new RoleInfo.Builder().name("Role").build(), 3);

		SiteAssignmentModel siteAssignmentModel = operatorSiteAssignmentModelFactory.create(employees, null, roleCounts);

		assertNotNull(siteAssignmentModel);
		assertEquals(3, siteAssignmentModel.getTotalEmployeesAssignedToSite());
		assertEquals(1, siteAssignmentModel.getRoleEmployee().size());

		for (Map.Entry<RoleInfo, Integer> entry : siteAssignmentModel.getRoleEmployee().entrySet()) {
			assertEquals("Role", entry.getKey().getName());
			assertEquals(3, (int) entry.getValue());
		}
	}
}
