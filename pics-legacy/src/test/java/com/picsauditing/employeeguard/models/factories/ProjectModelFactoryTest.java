package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.builders.ProjectBuilder;
import com.picsauditing.employeeguard.models.ProjectModel;
import com.picsauditing.employeeguard.models.RoleModel;
import com.picsauditing.employeeguard.models.SkillModel;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class ProjectModelFactoryTest {

	public static final int PROJECT_ID = 82;
	public static final String PROJECT_NAME = "Test Project";

	ProjectModelFactory projectModelFactory;

	@Before
	public void setUp() throws Exception {
		projectModelFactory = new ProjectModelFactory();
	}

	@Test
	public void testCreate() {
		Project fakeProject = buildFakeProject();
		List<RoleModel> fakeRoleModels = new ArrayList<>();
		List<SkillModel> fakeSkillModels = new ArrayList<>();

		ProjectModel projectModel = projectModelFactory.create(fakeProject, fakeRoleModels, fakeSkillModels);

		verifyTestCreate(fakeRoleModels, fakeSkillModels, projectModel);
	}

	@Test
	public void testCreate_List_NoData() {
		List<ProjectModel> projectModels = projectModelFactory.create((List<Project>) null, null, null);

		assertNotNull(projectModels);
		assertTrue(projectModels.isEmpty());
	}

	@Test
	public void testCreate_List_WithData() {
		final Project project = buildFakeProject();

		List<ProjectModel> projectModels = projectModelFactory.create(
				Arrays.asList(project),
				new HashMap<Integer, List<RoleModel>>() {{
					put(project.getId(), new ArrayList<RoleModel>());
				}},
				new HashMap<Integer, List<SkillModel>>() {{
					put(project.getId(), new ArrayList<SkillModel>());
				}}
		);

		verifyTestCreateList(project, projectModels);
	}

	protected void verifyTestCreate(List<? extends RoleModel> fakeRoleModels,
	                                List<? extends SkillModel> fakeSkillModels,
	                                ProjectModel projectModel) {

		assertEquals(PROJECT_ID, projectModel.getId());
		assertEquals(PROJECT_NAME, projectModel.getName());
		assertEquals(fakeRoleModels, projectModel.getRoles());
		assertEquals(fakeSkillModels, projectModel.getSkills());
	}

	protected void verifyTestCreateList(Project project, List<? extends ProjectModel> projectModels) {
		assertNotNull(projectModels);
		assertFalse(projectModels.isEmpty());
		assertNotNull(projectModels.get(0));
		assertEquals(project.getId(), projectModels.get(0).getId());
	}

	protected Project buildFakeProject() {
		return new ProjectBuilder()
				.id(PROJECT_ID)
				.name(PROJECT_NAME)
				.build();
	}
}
