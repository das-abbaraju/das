package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.AccountSkillRoleDAO;
import com.picsauditing.employeeguard.daos.ProjectSkillDAO;
import com.picsauditing.employeeguard.daos.SiteSkillDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.util.Strings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.picsauditing.employeeguard.services.entity.EntityAuditInfoConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class SkillEntityServiceTest {

	private SkillEntityService service;

	@Mock
	private AccountSkillDAO accountSkillDAO;
	@Mock
	private AccountSkillRoleDAO accountSkillRoleDAO;
	@Mock
	private ProjectSkillDAO projectSkillDAO;
	@Mock
	private SiteSkillDAO siteSkillDAO;

	@Before
	public void setUp() throws Exception {
		service = new SkillEntityService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(service, "accountSkillDAO", accountSkillDAO);
		Whitebox.setInternalState(service, "accountSkillRoleDAO", accountSkillRoleDAO);
		Whitebox.setInternalState(service, "projectSkillDAO", projectSkillDAO);
		Whitebox.setInternalState(service, "siteSkillDAO", siteSkillDAO);
	}

	@Test(expected = NullPointerException.class)
	public void testFind_NullId() throws Exception {
		service.find(null);
	}

	@Test
	public void testFind() throws Exception {
		when(accountSkillDAO.find(ENTITY_ID)).thenReturn(buildFakeAccountSkill());

		AccountSkill skill = service.find(ENTITY_ID);

		assertNotNull(skill);
		assertEquals(ENTITY_ID, skill.getId());
	}

	@Test
	public void testGetRequiredSkillsForProjects() {
		List<Project> projects = buildFakeProjects();
		List<AccountSkill> skills = buildFakeAccountSkills();
		List<ProjectSkill> projectSkills = buildFakeProjectSkills(projects, skills);

		when(projectSkillDAO.findByProjects(projects)).thenReturn(projectSkills);

		Map<Project, Set<AccountSkill>> result = service.getRequiredSkillsForProjects(projects);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(result.containsKey(projects.get(0)));
		assertTrue(result.containsKey(projects.get(1)));
		assertTrue(result.get(projects.get(0)).contains(projectSkills.get(0).getSkill()));
		assertTrue(result.get(projects.get(1)).contains(projectSkills.get(1).getSkill()));
	}

	@Test
	public void testGetSkillsForRoles() {
		List<Role> roles = buildFakeRoles();
		List<AccountSkill> skills = buildFakeAccountSkills();
		List<AccountSkillRole> roleSkills = buildFakeRoleSkills(roles, skills);

		when(accountSkillRoleDAO.findSkillsByRoles(roles)).thenReturn(roleSkills);

		Map<Role, Set<AccountSkill>> result = service.getSkillsForRoles(roles);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(result.containsKey(roles.get(0)));
		assertTrue(result.containsKey(roles.get(1)));
		assertTrue(result.get(roles.get(0)).contains(roleSkills.get(0).getSkill()));
		assertTrue(result.get(roles.get(1)).contains(roleSkills.get(1).getSkill()));
	}

	@Test
	public void testGetSiteRequiredSkills() {
		List<AccountSkill> skills = buildFakeAccountSkills();

		when(siteSkillDAO.findByAccountIds(anyListOf(Integer.class))).thenReturn(buildFakeSiteSkills(skills));

		Set<AccountSkill> result = service.getSiteRequiredSkills(ACCOUNT_ID, CORPORATE_IDS);

		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.contains(skills.get(0)));
		assertTrue(result.contains(skills.get(1)));
	}

	@Test
	public void testSearch_NullOrEmpty() throws Exception {
		List<AccountSkill> skills = service.search(null, ACCOUNT_ID);

		assertNotNull(skills);
		assertTrue(skills.isEmpty());
		verify(accountSkillDAO, never()).search(anyString(), anyInt());

		skills = service.search(Strings.EMPTY_STRING, ACCOUNT_ID);

		assertNotNull(skills);
		assertTrue(skills.isEmpty());
		verify(accountSkillDAO, never()).search(anyString(), anyInt());
	}

	@Test
	public void testSearch() throws Exception {
		when(accountSkillDAO.search(SEARCH_TERM, ACCOUNT_ID)).thenReturn(Arrays.asList(buildFakeAccountSkill()));

		List<AccountSkill> skills = service.search(SEARCH_TERM, ACCOUNT_ID);

		verify(accountSkillDAO).search(SEARCH_TERM, ACCOUNT_ID);
		assertNotNull(skills);
		assertFalse(skills.isEmpty());
	}

	@Test
	public void testSave() throws Exception {
		AccountSkill fakeAccountSkill = buildFakeAccountSkill();

		when(accountSkillDAO.save(fakeAccountSkill)).thenReturn(fakeAccountSkill);

		AccountSkill result = service.save(fakeAccountSkill, CREATED);

		assertNotNull(result);
		assertEquals(fakeAccountSkill.getId(), result.getId());
		assertEquals(USER_ID, result.getCreatedBy());
		assertEquals(CREATED_DATE, result.getCreatedDate());
	}

	@Test
	public void testUpdate() throws Exception {
		AccountSkill fakeAccountSkill = buildFakeAccountSkill();
		fakeAccountSkill.setName("Original name");

		AccountSkill updatedAccountSkill = buildFakeAccountSkill();
		updatedAccountSkill.setName("Skill");
		updatedAccountSkill.setDescription("Description");

		when(accountSkillDAO.find(fakeAccountSkill.getId())).thenReturn(fakeAccountSkill);
		when(accountSkillDAO.save(fakeAccountSkill)).thenReturn(fakeAccountSkill);

		AccountSkill result = service.update(updatedAccountSkill, UPDATED);

		assertNotNull(result);
		assertEquals(updatedAccountSkill.getId(), result.getId());
		assertEquals(updatedAccountSkill.getName(), result.getName());
		assertEquals(USER_ID, result.getUpdatedBy());
		assertEquals(UPDATED_DATE, result.getUpdatedDate());
	}

	@Test
	public void testDelete() throws Exception {
		AccountSkill fakeAccountSkill = buildFakeAccountSkill();

		service.delete(fakeAccountSkill);

		verify(accountSkillDAO).delete(fakeAccountSkill);
	}

	@Test
	public void testDeleteById() throws Exception {
		AccountSkill fakeAccountSkill = buildFakeAccountSkill();

		when(accountSkillDAO.find(fakeAccountSkill.getId())).thenReturn(fakeAccountSkill);

		service.deleteById(fakeAccountSkill.getId());

		verify(accountSkillDAO).find(fakeAccountSkill.getId());
		verify(accountSkillDAO).delete(fakeAccountSkill);
	}
}
