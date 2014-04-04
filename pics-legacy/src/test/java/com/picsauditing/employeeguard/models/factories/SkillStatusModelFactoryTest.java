package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.entities.builders.ProjectBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.models.SkillStatusModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class SkillStatusModelFactoryTest {
	public static final int PROJECT_ID = 345;
	public static final int SKILL_ID = 123;
	public static final int ROLE_ID = 234;
	private SkillStatusModelFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new SkillStatusModelFactory();
	}

	@Test
	public void testCreate_Single() throws Exception {
		AccountSkill accountSkill = new AccountSkillBuilder()
				.id(SKILL_ID)
				.name("Account Skill")
				.build();

		SkillStatusModel skillStatusModel = factory.create(accountSkill, SkillStatus.Expiring);

		assertEquals(accountSkill.getName(), skillStatusModel.getName());
		assertEquals(SkillStatus.Expiring, skillStatusModel.getStatus());
		assertEquals(SKILL_ID, skillStatusModel.getId());
	}

	@Test
	public void testCreate_List() throws Exception {
		AccountSkill accountSkill = new AccountSkillBuilder()
				.id(SKILL_ID)
				.name("Account Skill")
				.build();
		AccountSkill secondSkill = new AccountSkillBuilder()
				.id(124)
				.name("Second Skill")
				.build();

		Map<AccountSkill, SkillStatus> skillStatusMap = new HashMap<>();
		skillStatusMap.put(accountSkill, SkillStatus.Completed);
		skillStatusMap.put(secondSkill, SkillStatus.Expired);

		List<SkillStatusModel> skillStatusModels = factory.create(Arrays.asList(accountSkill, secondSkill), skillStatusMap);

		assertEquals(2, skillStatusModels.size());
		assertEquals(accountSkill.getId(), skillStatusModels.get(0).getId());
		assertEquals(accountSkill.getName(), skillStatusModels.get(0).getName());
		assertEquals(SkillStatus.Completed, skillStatusModels.get(0).getStatus());
		assertEquals(secondSkill.getId(), skillStatusModels.get(1).getId());
		assertEquals(secondSkill.getName(), skillStatusModels.get(1).getName());
		assertEquals(SkillStatus.Expired, skillStatusModels.get(1).getStatus());
	}

	@Test
	public void testCreate_List_Empty() throws Exception {
		List<SkillStatusModel> skillStatusModels = factory.create(null, new HashMap<AccountSkill, SkillStatus>());

		assertNotNull(skillStatusModels);
		assertEquals(0, skillStatusModels.size());
	}

	@Test
	public void testCreateProjectIdToSkillStatusModelMap_EmptyMap() {
		Map<Integer, List<SkillStatusModel>> projectIdToSkillStatusModelMap = factory.createRoleIdToSkillStatusModelMap(null, null);

		assertNotNull(projectIdToSkillStatusModelMap);
		assertTrue(projectIdToSkillStatusModelMap.isEmpty());
	}

	@Test
	public void testCreateProjectIdToSkillStatusModelMap_WithData() {
		final AccountSkill accountSkill = getFakeAccountSkill();
		Map<AccountSkill, SkillStatus> skillStatusMap = new HashMap<AccountSkill, SkillStatus>() {{
			put(accountSkill, SkillStatus.Completed);
		}};

		final Project project = getFakeProject();
		Map<Project, Collection<AccountSkill>> projectSkillsMap = new HashMap<Project, Collection<AccountSkill>>() {{
			put(project, Arrays.asList(accountSkill));
		}};

		Map<Integer, List<SkillStatusModel>> projectIdToSkillStatusModelMap =
				factory.createProjectIdToSkillStatusModelMap(projectSkillsMap, skillStatusMap);

		assertSkillStatusModelMapData(projectIdToSkillStatusModelMap, PROJECT_ID, SkillStatus.Completed);
	}

	@Test
	public void testCreateRoleIdToSkillStatusModelMap_EmptyMap() {
		Map<Integer, List<SkillStatusModel>> roleIdToSkillStatusModelMap = factory.createRoleIdToSkillStatusModelMap(null, null);

		assertNotNull(roleIdToSkillStatusModelMap);
		assertTrue(roleIdToSkillStatusModelMap.isEmpty());
	}

	@Test
	public void testCreateRoleIdToSkillStatusModelMap_WithData() {
		final AccountSkill accountSkill = getFakeAccountSkill();
		Map<AccountSkill, SkillStatus> skillStatusMap = new HashMap<AccountSkill, SkillStatus>() {{
			put(accountSkill, SkillStatus.Expiring);
		}};

		final Role role = getFakeRole();
		Map<Role, Collection<AccountSkill>> roleSkillsMap = new HashMap<Role, Collection<AccountSkill>>() {{
			put(role, Arrays.asList(accountSkill));
		}};

		Map<Integer, List<SkillStatusModel>> roleIdToSkillStatusModelMap =
				factory.createRoleIdToSkillStatusModelMap(roleSkillsMap, skillStatusMap);

		assertSkillStatusModelMapData(roleIdToSkillStatusModelMap, ROLE_ID, SkillStatus.Expiring);
	}

	private void assertSkillStatusModelMapData(Map<Integer, List<SkillStatusModel>> map, int id, SkillStatus status) {
		assertNotNull(map);
		assertFalse(map.isEmpty());
		assertNotNull(map.get(id));

		List<SkillStatusModel> skillStatusModels = map.get(id);

		assertEquals(1, skillStatusModels.size());
		assertEquals(status, skillStatusModels.get(0).getStatus());
	}

	private AccountSkill getFakeAccountSkill() {
		return new AccountSkillBuilder()
				.id(SKILL_ID)
				.build();
	}

	private Project getFakeProject() {
		return new ProjectBuilder()
				.id(PROJECT_ID)
				.build();
	}

	private Role getFakeRole() {
		return new RoleBuilder()
				.id(ROLE_ID)
				.build();
	}
}
