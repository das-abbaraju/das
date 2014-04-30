package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.SiteSkillDAO;
import com.picsauditing.employeeguard.entities.*;
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
import static org.mockito.Matchers.anyCollectionOf;
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
										.accountId(SITE_ID)
										.build())
						.build(),
				new ProjectCompanyBuilder()
						.project(
								new ProjectBuilder()
										.accountId(OTHER_SITE_ID)
										.build()
						)
						.build());
	}

	@Test
	public void testGetRequiredSkillsFromProjectsAndSiteRoles_WithSiteRolesAndSkillsToRemove() throws Exception {
		setupTestGetRequiredSkillsFromProjectsAndSiteRoles_WithSiteRolesAndSkillsToRemove();

		Set<AccountSkill> result = skillAssignmentHelper.getRequiredSkillsFromProjectsAndSiteRoles(
				getFakeProjectCompanies(),
				new EmployeeBuilder().build());

		assertFalse(result.isEmpty());
		assertEquals(3, result.size());
	}

	private void setupTestGetRequiredSkillsFromProjectsAndSiteRoles_WithSiteRolesAndSkillsToRemove() {
		when(accountService.getTopmostCorporateAccountIds(SITE_ID)).thenReturn(Arrays.asList(CORPORATE_ID, 45));
		when(accountSkillDAO.findSiteAndCorporateRequiredSkills(anyCollectionOf(Integer.class)))
				.thenReturn(Arrays.asList(new AccountSkillBuilder(CORPORATE_ID).accountId(SITE_ID).name("Site Skill").build(),
						new AccountSkillBuilder(CORPORATE_ID).accountId(CORPORATE_ID).name("Corporate Skill").build()));
		when(accountSkillDAO.findProjectRequiredSkills(anyCollectionOf(Project.class)))
				.thenReturn(Arrays.asList(new AccountSkillBuilder(CORPORATE_ID).accountId(SITE_ID).name("Project Skill").build()));
	}

	@Test
	public void testFilterNoLongerNeededEmployeeSkills() throws Exception {
		AccountSkill keep = new AccountSkillBuilder(CORPORATE_ID)
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
										new AccountSkillBuilder(CORPORATE_ID)
												.name("Skill to remove")
												.build())
								.build()
				))
				.build();

		Set<AccountSkillEmployee> result = skillAssignmentHelper.filterNoLongerNeededEmployeeSkills(employee, 12345, requiredSkills);

		verifyTestFilterNoLongerNeededEmployeeSkills(result);
	}

	private void verifyTestFilterNoLongerNeededEmployeeSkills(Set<AccountSkillEmployee> result) {
		assertFalse(result.isEmpty());
		assertEquals(1, result.size());

		for (AccountSkillEmployee accountSkillEmployee : result) {
			assertEquals("Skill to remove", accountSkillEmployee.getSkill().getName());
		}
	}
}
