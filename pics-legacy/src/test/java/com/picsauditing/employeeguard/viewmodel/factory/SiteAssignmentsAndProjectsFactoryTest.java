package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.models.ProjectStatisticsModel;
import com.picsauditing.employeeguard.models.SiteAssignmentStatisticsModel;
import com.picsauditing.employeeguard.models.factories.SiteAssignmentsAndProjectsFactory;
import com.picsauditing.employeeguard.services.models.AccountModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SiteAssignmentsAndProjectsFactoryTest {

	private SiteAssignmentsAndProjectsFactory siteAssignmentsAndProjectsFactory;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		siteAssignmentsAndProjectsFactory = new SiteAssignmentsAndProjectsFactory();
	}

	@Test
	public void testCreate_NoData() throws Exception {
		Map<AccountModel, Set<Project>> siteProjects = Collections.emptyMap();
		Map<AccountModel, Set<AccountSkill>> siteRequiredSkills = Collections.emptyMap();
		Map<Employee, Set<Role>> employeeRoles = Collections.emptyMap();
		List<AccountSkillEmployee> accountSkillEmployees = Collections.emptyList();

		Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> siteAssignments =
				siteAssignmentsAndProjectsFactory.create(siteProjects, siteRequiredSkills, employeeRoles, accountSkillEmployees);

		assertNotNull(siteAssignments);
		assertTrue(siteAssignments.isEmpty());
	}
}
