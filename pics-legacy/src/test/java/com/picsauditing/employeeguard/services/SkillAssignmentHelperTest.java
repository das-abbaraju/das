package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.RoleEmployeeDAO;
import com.picsauditing.employeeguard.daos.SiteSkillDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

public class SkillAssignmentHelperTest {

	public static final int CORPORATE_ID = 32;
	public static final int SITE_ID = 423;
	public static final int OTHER_SITE_ID = 123;

	private SkillAssignmentHelper skillAssignmentHelper;

	@Mock
	private AccountService accountService;
	@Mock
	private RoleEmployeeDAO roleEmployeeDAO;
	@Mock
	private SiteSkillDAO siteSkillDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		skillAssignmentHelper = new SkillAssignmentHelper();

		Whitebox.setInternalState(skillAssignmentHelper, "accountService", accountService);
		Whitebox.setInternalState(skillAssignmentHelper, "roleEmployeeDAO", roleEmployeeDAO);
		Whitebox.setInternalState(skillAssignmentHelper, "siteSkillDAO", siteSkillDAO);
	}

	@Test
	public void testGetRequiredSkillsFromProjectsAndSiteRoles_NoProjectCompanies() throws Exception {
		Set<AccountSkill> result = skillAssignmentHelper.getRequiredSkillsFromProjectsAndSiteRoles(null, null, null);
		assertTrue(result.isEmpty());
	}

	private List<ProjectCompany> getFakeProjectCompanies() {
		return Arrays.asList(
				new ProjectCompanyBuilder()
						.project(
								new ProjectBuilder()
										.skills(Arrays.asList(
												new ProjectSkillBuilder()
														.skill(
																new AccountSkillBuilder()
																		.name("Project Skill")
																		.build())
														.build()))
										.roles(Arrays.asList(
												new ProjectRoleBuilder()
														.role(
																new RoleBuilder()
																		.skills(Arrays.asList(
																				new AccountSkillBuilder()
																						.name("Project Role Skill")
																						.build()))
																		.build())
														.build()
										))
										.accountId(SITE_ID)
										.build())
						.build(),
				new ProjectCompanyBuilder()
						.project(
								new ProjectBuilder()
										.roles(Arrays.asList(
												new ProjectRoleBuilder()
														.role(
																new RoleBuilder()
																		.skills(Arrays.asList(
																				new AccountSkillBuilder()
																						.name("Project Role Skill to keep")
																						.build()))
																		.build())
														.build()))
										.accountId(OTHER_SITE_ID)
										.build()
						)
						.build());
	}

	@Test
	public void testGetRequiredSkillsFromProjectsAndSiteRoles_() throws Exception {
		when(accountService.getTopmostCorporateAccountIds(SITE_ID)).thenReturn(Arrays.asList(CORPORATE_ID, 45));

		// TODO: Provide correct data here (site skill builder with correct skill
		when(siteSkillDAO.findByAccountIds(anyListOf(Integer.class))).thenReturn(Arrays.asList(new SiteSkill()));

		Role siteRole = new RoleBuilder()
				.accountId(OTHER_SITE_ID)
				.name("Other Site Role")
				.build();

		// TODO: Provide correct data here (site skill builder with correct skill
		RoleEmployee roleEmployee = new RoleEmployee();
		roleEmployee.setRole(siteRole);

		when(roleEmployeeDAO.findByEmployeeAndSiteIds(anyInt(), anyListOf(Integer.class))).thenReturn(Arrays.asList(roleEmployee));

		Map<Role, Role> siteToCorporateRoles = initializeSiteToCorporateRoles(siteRole);
		Set<AccountSkill> result = skillAssignmentHelper.getRequiredSkillsFromProjectsAndSiteRoles(
				getFakeProjectCompanies(),
				new EmployeeBuilder().build(),
				siteToCorporateRoles);

		assertTrue(result.isEmpty());
	}

	private Map<Role, Role> initializeSiteToCorporateRoles(Role siteRole) {
		Role corporateRole = new RoleBuilder()
				.accountId(CORPORATE_ID)
				.name("Corporate Role")
				.skills(Arrays.asList(
						new AccountSkillBuilder()
								.name("Corporate Role Skill")
								.build()))
				.build();

		Map<Role, Role> siteToCorporateRoles = new HashMap<>();
		siteToCorporateRoles.put(siteRole, corporateRole);

		return siteToCorporateRoles;
	}

	@Test
	public void testFilterNoLongerNeededEmployeeSkills() throws Exception {
		fail("Not implemented yet.");
	}
}
