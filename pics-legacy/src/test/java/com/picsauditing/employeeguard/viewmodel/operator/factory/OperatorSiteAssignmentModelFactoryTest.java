package com.picsauditing.employeeguard.viewmodel.operator.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.viewmodel.operator.SiteAssignmentModel;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

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

		SiteAssignmentModel siteAssignmentModel = operatorSiteAssignmentModelFactory.create(employees, null);

		assertNotNull(siteAssignmentModel);
		assertEquals(3, siteAssignmentModel.getTotalEmployeesAssignedToSite());
	}
}
