package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ProjectStatusModelFactoryTest extends ProjectModelFactoryTest {

	ProjectStatusModelFactory projectStatusModelFactory;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		projectStatusModelFactory = new ProjectStatusModelFactory();
	}

	@Override
	@Test
	public void testCreate() {
		Project fakeProject = super.buildFakeProject();
		List<RoleStatusModel> fakeRoleModels = new ArrayList<>();
		List<SkillStatusModel> fakeSkillModels = new ArrayList<>();
		RequiredSkills requiredSkills = new RequiredSkills(fakeSkillModels);

		ProjectStatusModel projectStatusModel = projectStatusModelFactory.create(fakeProject, fakeRoleModels,
				requiredSkills, SkillStatus.Expiring);

		verifyTestCreate(fakeRoleModels, fakeSkillModels, projectStatusModel);
		assertEquals(SkillStatus.Expiring, projectStatusModel.getStatus());
	}

	private void verifyTestCreate(List<RoleStatusModel> fakeRoleModels,
									List<SkillStatusModel> fakeSkillModels,
									ProjectStatusModel projectStatusModel) {

		assertEquals(PROJECT_ID, projectStatusModel.getId());
		assertEquals(PROJECT_NAME, projectStatusModel.getName());
		assertEquals(fakeRoleModels, projectStatusModel.getRoles());
		assertEquals(fakeSkillModels, projectStatusModel.getRequired().getSkills());
	}

	@Override
	@Test
	public void testCreate_List_NoData() {
		List<ProjectStatusModel> projectStatusModels = projectStatusModelFactory.create((List<Project>) null, null, null, null);

		assertNotNull(projectStatusModels);
		assertTrue(projectStatusModels.isEmpty());
	}

	@Test
	public void testCreate_List_WithData() {
		final Project project = buildFakeProject();

		List<ProjectStatusModel> projectStatusModels = projectStatusModelFactory.create(
				Arrays.asList(project),
				new HashMap<Integer, List<RoleStatusModel>>() {{
					put(project.getId(), new ArrayList<RoleStatusModel>());
				}},
				new HashMap<Integer, RequiredSkills>() {{
					put(project.getId(), new RequiredSkills(Arrays.asList(new SkillStatusModel())));
				}},
				new HashMap<Project, SkillStatus>() {{
					put(project, SkillStatus.Completed);
				}}
		);

		verifyTestCreateList(project, projectStatusModels);

		assertEquals(SkillStatus.Completed, projectStatusModels.get(0).getStatus());
	}

	protected void verifyTestCreateList(Project project, List<ProjectStatusModel> projectModels) {
		assertNotNull(projectModels);
		assertFalse(projectModels.isEmpty());
		assertNotNull(projectModels.get(0));
		assertEquals(project.getId(), projectModels.get(0).getId());
	}
}
