package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.builders.ProjectBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.models.RoleStatusModel;
import com.picsauditing.employeeguard.models.SkillStatusModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class RoleStatusModelFactoryTest {
	private RoleStatusModelFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new RoleStatusModelFactory();
	}

	@Test
	public void testCreate() throws Exception {
		Role role = getFakeRole();

		List<SkillStatusModel> skills = Arrays.asList(new SkillStatusModel());

		RoleStatusModel roleStatusModel = factory.create(role, skills, SkillStatus.Completed);

		assertEquals(role.getId(), roleStatusModel.getId());
		assertEquals(role.getName(), roleStatusModel.getName());
		assertEquals(SkillStatus.Completed, roleStatusModel.getStatus());
		assertEquals(skills, roleStatusModel.getSkills());
	}

	@Test
	public void testCreate_List_NoData() throws Exception {
		List<RoleStatusModel> roleStatusModels = factory.create((Collection<Role>) null, null, null);

		assertNotNull(roleStatusModels);
		assertTrue(roleStatusModels.isEmpty());
	}

	@Test
	public void testCreate_List_WithData() throws Exception {
		final Role role = getFakeRole();

		List<RoleStatusModel> roleStatusModels = factory.create(
				Arrays.asList(role),
				new HashMap<Integer, List<SkillStatusModel>>() {{
					put(role.getId(), new ArrayList<SkillStatusModel>());
				}},
				new HashMap<Role, SkillStatus>() {{
					put(role, SkillStatus.Completed);
				}}
		);

		assertNotNull(roleStatusModels);
		assertFalse(roleStatusModels.isEmpty());
		assertNotNull(roleStatusModels.get(0));
		assertEquals(role.getId(), roleStatusModels.get(0).getId());
		assertEquals(SkillStatus.Completed, roleStatusModels.get(0).getStatus());
	}

	@Test
	public void testCreateProjectIdToRoleModelMap_NoData() throws Exception {
		Map<Integer, List<RoleStatusModel>> projectIdToRoleModelMap = factory.createProjectIdToRoleModelMap(null, null, null, null);

		assertNotNull(projectIdToRoleModelMap);
		assertTrue(projectIdToRoleModelMap.isEmpty());
	}

	@Test
	public void testCreateProjectIdToRoleModelMap_WithData() throws Exception {
		final Role role = getFakeRole();
		final Project project = getFakeProject();

		Map<Integer, List<RoleStatusModel>> projectIdToRoleModelMap = factory.createProjectIdToRoleModelMap(
				Arrays.asList(project),
				new HashMap<Project, List<Role>>() {{
					put(project, Arrays.asList(role));
				}},
				new HashMap<Integer, List<SkillStatusModel>>() {{
					put(role.getId(), new ArrayList<SkillStatusModel>());
				}},
				new HashMap<Role, SkillStatus>() {{
					put(role, SkillStatus.Expired);
				}}
		);

		assertNotNull(projectIdToRoleModelMap);
		assertFalse(projectIdToRoleModelMap.isEmpty());
		assertNotNull(projectIdToRoleModelMap.get(project.getId()));

		List<RoleStatusModel> roleStatusModels = projectIdToRoleModelMap.get(project.getId());

		assertNotNull(roleStatusModels.get(0));
		assertEquals(role.getId(), roleStatusModels.get(0).getId());
		assertEquals(SkillStatus.Expired, roleStatusModels.get(0).getStatus());
	}

	private Role getFakeRole() {
		return new RoleBuilder()
				.id(123)
				.accountId(456)
				.name("Role")
				.build();
	}

	private Project getFakeProject() {
		return new ProjectBuilder()
				.id(234)
				.build();
	}
}
