package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.entities.builders.ProjectBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.models.SkillModel;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SkillModelFactoryTest {
	private SkillModelFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new SkillModelFactory();
	}

	@Test
	public void testCreate() throws Exception {
		AccountSkill skill = getFakeSkill();

		SkillModel skillModel = factory.create(skill);

		assertEquals(skill.getId(), skillModel.getId());
		assertEquals(skill.getName(), skillModel.getName());
	}

	@Test
	public void testCreate_List_NoData() throws Exception {
		List<SkillModel> skillModels = factory.create((List<AccountSkill>) null);

		assertNotNull(skillModels);
		assertTrue(skillModels.isEmpty());
	}

	@Test
	public void testCreate_List_WithData() throws Exception {
		AccountSkill skill = getFakeSkill();

		List<SkillModel> skillModels = factory.create(Arrays.asList(skill));

		assertNotNull(skillModels);
		assertFalse(skillModels.isEmpty());
		assertNotNull(skillModels.get(0));
		assertEquals(skill.getId(), skillModels.get(0).getId());
	}

	@Test
	public void testCreateProjectIdToSkillModelMap_NoData() {
		Map<Integer, List<SkillModel>> projectIdToSkillModelMap = factory.createProjectIdToSkillModelMap(null);

		assertNotNull(projectIdToSkillModelMap);
		assertTrue(projectIdToSkillModelMap.isEmpty());
	}

	@Test
	public void testCreateProjectIdToSkillModelMap_WithData() {
		final AccountSkill skill = getFakeSkill();
		final Project project = getFakeProject();

		Map<Integer, List<SkillModel>> projectIdToSkillModelMap = factory.createProjectIdToSkillModelMap(
				new HashMap<Project, List<AccountSkill>>() {{
					put(project, Arrays.asList(skill));
				}});

		assertNotNull(projectIdToSkillModelMap);
		assertFalse(projectIdToSkillModelMap.isEmpty());
		assertNotNull(projectIdToSkillModelMap.get(project.getId()));
		assertEquals(skill.getId(), projectIdToSkillModelMap.get(project.getId()).get(0).getId());
	}

	@Test
	public void testCreateRoleIdToSkillModelMap_NoData() {
		Map<Integer, List<SkillModel>> roleIdToSkillModelMap = factory.createRoleIdToSkillModelMap(null);

		assertNotNull(roleIdToSkillModelMap);
		assertTrue(roleIdToSkillModelMap.isEmpty());
	}

	@Test
	public void testCreateRoleIdToSkillModelMap_WithData() {
		final AccountSkill skill = getFakeSkill();
		final Role role = getFakeRole();

		Map<Integer, List<SkillModel>> roleIdToSkillModelMap = factory.createRoleIdToSkillModelMap(
				new HashMap<Role, List<AccountSkill>>() {{
					put(role, Arrays.asList(skill));
				}});

		assertNotNull(roleIdToSkillModelMap);
		assertFalse(roleIdToSkillModelMap.isEmpty());
		assertNotNull(roleIdToSkillModelMap.get(role.getId()));
		assertEquals(skill.getId(), roleIdToSkillModelMap.get(role.getId()).get(0).getId());
	}

	private AccountSkill getFakeSkill() {
		return new AccountSkillBuilder()
				.id(123)
				.name("Skill")
				.build();
	}

	private Project getFakeProject() {
		return new ProjectBuilder()
				.id(234)
				.build();
	}

	private Role getFakeRole() {
		return new RoleBuilder()
				.id(345)
				.build();
	}
}
