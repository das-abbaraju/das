package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.builders.EmployeeBuilder;
import com.picsauditing.employeeguard.entities.builders.ProjectBuilder;
import com.picsauditing.employeeguard.models.ProjectModel;
import com.picsauditing.employeeguard.models.RoleModel;
import com.picsauditing.employeeguard.models.SkillModel;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

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

	protected void verifyTestCreate(List<RoleModel> fakeRoleModels, List<SkillModel> fakeSkillModels, ProjectModel projectModel) {
		assertEquals(PROJECT_ID, projectModel.getId());
		assertEquals(PROJECT_NAME, projectModel.getName());
		assertEquals(fakeRoleModels, projectModel.getRoles());
		assertEquals(fakeSkillModels, projectModel.getSkills());
	}

	protected Project buildFakeProject() {
		return new ProjectBuilder()
				.id(PROJECT_ID)
				.name(PROJECT_NAME)
				.build();
	}
}
