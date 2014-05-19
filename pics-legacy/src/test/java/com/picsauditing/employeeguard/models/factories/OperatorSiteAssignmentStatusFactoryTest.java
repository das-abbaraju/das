package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.OperatorSiteAssignmentStatus;
import com.picsauditing.employeeguard.models.ProjectAssignmentModel;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OperatorSiteAssignmentStatusFactoryTest {

	public static final int ACCOUNT_ID = 234;
	public static final int SITE_ID = 123;
	public static final String SITE_NAME = "Site Name";

	private OperatorSiteAssignmentStatusFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new OperatorSiteAssignmentStatusFactory();
	}

	@Test
	public void testCreate() throws Exception {
		OperatorSiteAssignmentStatus site = factory.create(123, "Site Name", 4, new ArrayList<ProjectAssignmentModel>(),
				new HashMap<Employee, SkillStatus>());

		assertNotNull(site);
		assertEquals(SITE_ID, site.getId());
		assertEquals(SITE_NAME, site.getName());
		assertEquals(4, site.getEmployees());
	}
}
