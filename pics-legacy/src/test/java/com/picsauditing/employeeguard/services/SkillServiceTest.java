package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.AccountSkillRoleDAO;
import com.picsauditing.employeeguard.daos.ProjectSkillDAO;
import com.picsauditing.employeeguard.daos.SiteSkillDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.builders.*;
import com.picsauditing.util.Strings;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SkillServiceTest {

	public static final String PROJECT_SKILL_NAME = "Project Skill";
	public static final String TEST_SKILL_NAME = "Test Skill";
	public static final String TEST_PROJECT_NAME = "Test Project";
	public static final String SITE_SKILL_NAME = "Site Skill";
	public static final String TEST_ROLE_NAME = "Test Role";
	public static final int SITE_ID = 90;
	public static final int CORPORATE_ID = 91;
	public static final int SITE_SKILL_ID = 67;
	public static final int CORPORATE_SKILL_ID = 68;
	private static final int DELETE_SKILL_ID = 2;
	private static final int ACCOUNT_ID = 4567;
	private static final int APP_USER_ID = 123;
	private static final int ACCOUNT_SKILL_GROUP_ID = 5678;

	private SkillService skillService;

	@Mock
	private AccountService accountService;
	@Mock
	private AccountSkillDAO accountSkillDAO;
	@Mock
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Mock
	private AccountSkillRoleDAO accountSkillRoleDAO;
	@Mock
	private ProjectSkillDAO projectSkillDAO;
	@Mock
	private SiteSkillDAO siteSkillDAO;
	@Mock
	private com.picsauditing.employeeguard.services.entity.SkillService skillEntityService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		skillService = new SkillService();

		Whitebox.setInternalState(skillService, "accountService", accountService);
		Whitebox.setInternalState(skillService, "accountSkillDAO", accountSkillDAO);
		Whitebox.setInternalState(skillService, "accountSkillEmployeeService", accountSkillEmployeeService);
		Whitebox.setInternalState(skillService, "accountSkillRoleDAO", accountSkillRoleDAO);
		Whitebox.setInternalState(skillService, "projectSkillDAO", projectSkillDAO);
		Whitebox.setInternalState(skillService, "siteSkillDAO", siteSkillDAO);
		Whitebox.setInternalState(skillService, "skillEntityService", skillEntityService);
	}

	private AccountSkill buildAccountSkill() {
		AccountSkill accountSkill = new AccountSkill();
		accountSkill.setGroups(Arrays.asList(buildAccountSkillGroup()));
		return accountSkill;
	}

	private AccountSkillGroup buildAccountSkillGroup() {
		AccountSkillGroup accountSkillGroup = new AccountSkillGroup();
		accountSkillGroup.setId(ACCOUNT_SKILL_GROUP_ID);
		return accountSkillGroup;
	}

	@Test
	public void testDelete() {
		skillService.delete(Integer.toString(DELETE_SKILL_ID), ACCOUNT_ID, APP_USER_ID);

		verify(skillEntityService).deleteById(DELETE_SKILL_ID);
	}

	@Test
	public void testSearch() {
		when(skillEntityService.search("My Skill", ACCOUNT_ID)).thenReturn(Arrays.asList(new AccountSkill(), new AccountSkill()));

		List<AccountSkill> results = skillService.search("My Skill", ACCOUNT_ID);

		assertEquals(2, results.size());
	}

	@Test
	public void testSearch_EmptySearchTerm() {
		List<AccountSkill> result = skillService.search(Strings.EMPTY_STRING, ACCOUNT_ID);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetAllProjectSkillsForEmployeeProjectRoles_EmptyProjectRoleMap() {
		Map<Project, Set<AccountSkill>> result = skillService.getAllProjectSkillsForEmployeeProjectRoles(ACCOUNT_ID,
				Collections.<Project, Set<Role>>emptyMap());

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetAllProjectSkillsForEmployeeProjectRoles() {
		List<Role> fakeCorporateRoles = setupTestGetSkillsForRoles();
		List<Project> fakeProjects = buildFakeProjects();
		List<ProjectSkill> fakeProjectSkills = buildFakeProjectSkills(fakeProjects);
		when(projectSkillDAO.findByProjects(anyCollectionOf(Project.class))).thenReturn(fakeProjectSkills);

		Map<Project, Set<Role>> projectRoleMap = new HashMap<>();
		for (Project project : fakeProjects) {
			projectRoleMap.put(project, new HashSet<>(fakeCorporateRoles));
		}

		Map<Project, Set<AccountSkill>> result = skillService.getAllProjectSkillsForEmployeeProjectRoles(ACCOUNT_ID,
				projectRoleMap);

		assertEquals(2, result.size());
		verifySkillNames(result.get(fakeProjects.get(0)), TEST_SKILL_NAME + " 2", TEST_SKILL_NAME + " 1",
				TEST_SKILL_NAME + " 3", SITE_SKILL_NAME + " 1", SITE_SKILL_NAME + " 2", PROJECT_SKILL_NAME + " 1",
				PROJECT_SKILL_NAME + " 2");
		verifySkillNames(result.get(fakeProjects.get(1)), TEST_SKILL_NAME + " 1", TEST_SKILL_NAME + " 2",
				TEST_SKILL_NAME + " 3", SITE_SKILL_NAME + " 1", SITE_SKILL_NAME + " 2", PROJECT_SKILL_NAME + " 3");
	}

	private void verifySkillNames(final Set<AccountSkill> accountSkills, final String... expectedSkillNames) {
		assertEquals(expectedSkillNames.length, accountSkills.size());

		for (AccountSkill accountSkill : accountSkills) {
			assertTrue(ArrayUtils.contains(expectedSkillNames, accountSkill.getName()));
		}
	}

	private List<Project> buildFakeProjects() {
		return new ArrayList<Project>() {{
			add(new ProjectBuilder().accountId(ACCOUNT_ID).name(TEST_PROJECT_NAME + " 1").build());
			add(new ProjectBuilder().accountId(ACCOUNT_ID).name(TEST_PROJECT_NAME + " 2").build());
		}};
	}

	private List<ProjectSkill> buildFakeProjectSkills(final List<Project> fakeProjects) {
		return new ArrayList<ProjectSkill>() {{
			add(new ProjectSkillBuilder()
					.project(fakeProjects.get(0))
					.skill(new AccountSkillBuilder(732, ACCOUNT_ID)
							.name(PROJECT_SKILL_NAME + " 1")
							.skillType(SkillType.Training)
							.build())
					.build());

			add(new ProjectSkillBuilder()
					.project(fakeProjects.get(0))
					.skill(new AccountSkillBuilder(735, ACCOUNT_ID)
							.name(PROJECT_SKILL_NAME + " 2")
							.skillType(SkillType.Training)
							.build())
					.build());

			add(new ProjectSkillBuilder()
					.project(fakeProjects.get(1))
					.skill(new AccountSkillBuilder(935, ACCOUNT_ID)
							.name(PROJECT_SKILL_NAME + " 3")
							.skillType(SkillType.Training)
							.build())
					.build());
		}};
	}

	@Test
	public void testGetSkillsForRoles_EmptyRoleSkillsMap() {
		Map<Role, Set<AccountSkill>> result = skillService.getSkillsForRoles(ACCOUNT_ID,
				Collections.<Role>emptyList());

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testGetSkillsForRoles() {
		List<Role> fakeCorporateRoles = setupTestGetSkillsForRoles();

		Map<Role, Set<AccountSkill>> result = skillService.getSkillsForRoles(ACCOUNT_ID,
				fakeCorporateRoles);

		verifyTestGetSkillsForRoles(result);
	}

	private void verifyTestGetSkillsForRoles(Map<Role, Set<AccountSkill>> result) {
		assertEquals(2, result.size());
		for (Role role : result.keySet()) {
			verifySkillsForRole(result, role);
		}
	}

	private void verifySkillsForRole(Map<Role, Set<AccountSkill>> result, Role role) {
		if ((TEST_ROLE_NAME + " 1").equals(role.getName())) {
			verifySkillNames(result.get(role), TEST_SKILL_NAME + " 1", TEST_SKILL_NAME + " 3", SITE_SKILL_NAME + " 1",
					SITE_SKILL_NAME + " 2");
		}

		if ((TEST_ROLE_NAME + " 2").equals(role.getName())) {
			verifySkillNames(result.get(role), TEST_SKILL_NAME + " 2", SITE_SKILL_NAME + " 1", SITE_SKILL_NAME + " 2");
		}
	}

	private List<Role> setupTestGetSkillsForRoles() {
		List<Role> fakeCorporateRoles = buildFakeCorporateRoles();
		when(accountSkillRoleDAO.findSkillsByRoles(anyCollectionOf(Role.class)))
				.thenReturn(buildFakeAccountSkillRoles(fakeCorporateRoles));
		List<Integer> siteAndCorporateIds = new ArrayList<>(Arrays.asList(567, 890));
		when(accountService.getTopmostCorporateAccountIds(ACCOUNT_ID)).thenReturn(siteAndCorporateIds);
		when(siteSkillDAO.findByAccountIds(siteAndCorporateIds)).thenReturn(buildFakeSiteSkills());
		return fakeCorporateRoles;
	}

	public List<Role> buildFakeCorporateRoles() {
		return new ArrayList<Role>() {{
			add(new RoleBuilder().accountId(ACCOUNT_ID).name(TEST_ROLE_NAME + " 1").build());
			add(new RoleBuilder().accountId(ACCOUNT_ID).name(TEST_ROLE_NAME + " 2").build());
		}};
	}

	public List<AccountSkillRole> buildFakeAccountSkillRoles(final List<Role> fakeCorporateSkills) {
		return new ArrayList<AccountSkillRole>() {{
			add(new AccountSkillRoleBuilder()
					.skill(new AccountSkillBuilder(123, ACCOUNT_ID)
							.name(TEST_SKILL_NAME + " 1")
							.skillType(SkillType.Training)
							.build())
					.role(fakeCorporateSkills.get(0))
					.build());

			add(new AccountSkillRoleBuilder()
					.skill(new AccountSkillBuilder(145, ACCOUNT_ID)
							.name(TEST_SKILL_NAME + " 2")
							.skillType(SkillType.Training)
							.build())
					.role(fakeCorporateSkills.get(1))
					.build());

			add(new AccountSkillRoleBuilder()
					.skill(new AccountSkillBuilder(678, ACCOUNT_ID)
							.name(TEST_SKILL_NAME + " 3")
							.skillType(SkillType.Certification)
							.build())
					.role(fakeCorporateSkills.get(0))
					.build());
		}};
	}

	public List<SiteSkill> buildFakeSiteSkills() {
		return new ArrayList<SiteSkill>() {{
			add(new SiteSkillBuilder()
					.siteId(SITE_ID)
					.skill(new AccountSkillBuilder(SITE_SKILL_ID, SITE_ID)
							.name(SITE_SKILL_NAME + " 1")
							.skillType(SkillType.Certification)
							.build())
					.build());

			add(new SiteSkillBuilder()
					.siteId(CORPORATE_ID)
					.skill(new AccountSkillBuilder(CORPORATE_SKILL_ID, CORPORATE_ID)
							.name(SITE_SKILL_NAME + " 2")
							.skillType(SkillType.Training)
							.build())
					.build());
		}};
	}
}
