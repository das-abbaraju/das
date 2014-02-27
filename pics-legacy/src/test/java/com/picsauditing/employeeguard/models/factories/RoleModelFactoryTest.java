package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.builders.ProjectBuilder;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.models.RoleModel;
import com.picsauditing.employeeguard.models.SkillModel;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class RoleModelFactoryTest {
	public static final int ROLE_ID = 123;
	public static final int PROJECT_ID = 234;
	private RoleModelFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new RoleModelFactory();
	}

	@Test
	public void testCreate() throws Exception {
		final Role role = getFakeRole();

		List<SkillModel> skillModels = Arrays.asList(new SkillModel());

		RoleModel roleModel = factory.create(role, skillModels);

		assertEquals(role.getId(), roleModel.getId());
		assertEquals(role.getName(), roleModel.getName());
		assertEquals(skillModels, roleModel.getSkills());
	}

	@Test
	public void testCreate_List_NoData() {
		List<RoleModel> roleModels = factory.create((List<Role>) null, (Map<Integer, List<SkillModel>>) null);

		assertNotNull(roleModels);
		assertTrue(roleModels.isEmpty());
	}

	@Test
	public void testCreate_List_WithData() {
		final Role role = getFakeRole();

		List<RoleModel> roleModels = factory.create(Arrays.asList(role), new HashMap<Integer, List<SkillModel>>() {{
			put(role.getId(), new ArrayList<SkillModel>());
		}});

		assertNotNull(roleModels);
		assertFalse(roleModels.isEmpty());
		assertNotNull(roleModels.get(0));
		assertEquals(ROLE_ID, roleModels.get(0).getId());
	}

	@Test
	public void testCreateProjectIdToRoleModelMap_NoData() {
		Map<Integer, List<RoleModel>> projectIdToRoleModelMap = factory.createProjectIdToRoleModelMap(null, null, null);

		assertNotNull(projectIdToRoleModelMap);
		assertTrue(projectIdToRoleModelMap.isEmpty());
	}

	@Test
	public void testCreateProjectIdToRoleModelMap_WithData() {
		final Project project = getFakeProject();
		final Role role = getFakeRole();

		Map<Integer, List<RoleModel>> projectIdToRoleModelMap = factory.createProjectIdToRoleModelMap(
				Arrays.asList(project),
				new HashMap<Project, List<Role>>() {{
					put(project, Arrays.asList(role));
				}},
				new HashMap<Integer, List<SkillModel>>() {{
					put(ROLE_ID, new ArrayList<SkillModel>());
				}}
		);

		assertNotNull(projectIdToRoleModelMap);
		assertFalse(projectIdToRoleModelMap.isEmpty());
		assertNotNull(projectIdToRoleModelMap.get(PROJECT_ID));

		List<RoleModel> roleModels = projectIdToRoleModelMap.get(PROJECT_ID);

		assertEquals(ROLE_ID, roleModels.get(0).getId());
		assertNotNull(roleModels.get(0).getSkills());
	}

	private Role getFakeRole() {
		return new RoleBuilder()
				.id(ROLE_ID)
				.name("Role")
				.build();
	}

	private Project getFakeProject() {
		return new ProjectBuilder()
				.id(PROJECT_ID)
				.build();
	}
}
