package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.SiteSkillDAO;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.ProjectCompany;
import com.picsauditing.employeeguard.entities.builders.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class SkillAssignmentHelperTest {

	public static final int CORPORATE_ID = 32;
	public static final int SITE_ID = 423;
	public static final int OTHER_SITE_ID = 123;

	private SkillAssignmentHelper skillAssignmentHelper;

	@Mock
	private AccountService accountService;
	@Mock
	private AccountSkillDAO accountSkillDAO;
	@Mock
	private SiteSkillDAO siteSkillDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		skillAssignmentHelper = new SkillAssignmentHelper();

		Whitebox.setInternalState(skillAssignmentHelper, "accountService", accountService);
		Whitebox.setInternalState(skillAssignmentHelper, "accountSkillDAO", accountSkillDAO);
		Whitebox.setInternalState(skillAssignmentHelper, "siteSkillDAO", siteSkillDAO);
	}

	@Test
	public void testGetRequiredSkillsFromProjectsAndSiteRoles_NoProjectCompanies() throws Exception {
		Set<AccountSkill> result = skillAssignmentHelper.getRequiredSkillsFromProjectsAndSiteRoles(null, null);
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
														.role(new RoleBuilder()
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
														.role(new RoleBuilder()
																.skills(Arrays.asList(
																		new AccountSkillBuilder()
																				.name("Project Role Skill")
																				.build()))
																.build())
														.build()))
										.accountId(OTHER_SITE_ID)
										.build()
						)
						.build());
	}

	@Test
	public void testGetRequiredSkillsFromProjectsAndSiteRoles_WithSiteRolesAndSkillsToRemove() throws Exception {
		when(accountService.getTopmostCorporateAccountIds(SITE_ID)).thenReturn(Arrays.asList(CORPORATE_ID, 45));

		AccountSkill skill = new AccountSkillBuilder()
				.name("Site Skill")
				.build();

		Set<AccountSkill> result = skillAssignmentHelper.getRequiredSkillsFromProjectsAndSiteRoles(
				getFakeProjectCompanies(),
				new EmployeeBuilder().build());

		assertFalse(result.isEmpty());
		assertEquals(3, result.size());
	}

	@Test
	public void testFilterNoLongerNeededEmployeeSkills() throws Exception {
		AccountSkill keep = new AccountSkillBuilder()
				.name("Skill to keep")
				.build();
		Set<AccountSkill> requiredSkills = new HashSet<>();
		requiredSkills.add(keep);

		Employee employee = new EmployeeBuilder()
				.skills(Arrays.asList(
						new AccountSkillEmployeeBuilder()
								.accountSkill(keep)
								.build(),
						new AccountSkillEmployeeBuilder()
								.accountSkill(
										new AccountSkillBuilder()
												.name("Skill to remove")
												.build())
								.build()
				))
				.build();

		Set<AccountSkillEmployee> result = skillAssignmentHelper.filterNoLongerNeededEmployeeSkills(employee, 12345, requiredSkills);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(1, result.size());

		for (AccountSkillEmployee accountSkillEmployee : result) {
			assertEquals("Skill to remove", accountSkillEmployee.getSkill().getName());
		}
	}
}
