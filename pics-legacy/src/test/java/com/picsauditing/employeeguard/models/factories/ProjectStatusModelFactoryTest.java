package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.ProjectStatusModel;
import com.picsauditing.employeeguard.models.RoleStatusModel;
import com.picsauditing.employeeguard.models.SkillStatusModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class ProjectStatusModelFactoryTest extends ProjectModelFactoryTest {

	ProjectStatusModelFactory projectStatusModelFactory;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		projectStatusModelFactory = new ProjectStatusModelFactory();
	}

//	@Override
//	@Test
//	public void testCreate() {
//		Project fakeProject = super.buildFakeProject();
//		List<RoleStatusModel> fakeRoleModels = new ArrayList<>();
//		List<SkillStatusModel> fakeSkillModels = new ArrayList<>();
//
//		ProjectStatusModel projectStatusModel = projectStatusModelFactory.create(fakeProject, fakeRoleModels,
//				fakeSkillModels, SkillStatus.Expiring);
//
//		super.verifyTestCreate(fakeRoleModels, fakeSkillModels, projectStatusModel);
//		assertEquals(SkillStatus.Expiring, projectStatusModel.getStatus());
//	}

//	@Override
//	@Test
//	public void testCreate_List_NoData() {
//		List<ProjectStatusModel> projectStatusModels = projectStatusModelFactory.create((List<Project>) null, null, null, null);
//
//		assertNotNull(projectStatusModels);
//		assertTrue(projectStatusModels.isEmpty());
//	}

//	@Test
//	public void testCreate_List_WithData() {
//		final Project project = buildFakeProject();
//
//		List<ProjectStatusModel> projectStatusModels = projectStatusModelFactory.create(
//				Arrays.asList(project),
//				new HashMap<Integer, List<RoleStatusModel>>() {{
//					put(project.getId(), new ArrayList<RoleStatusModel>());
//				}},
//				new HashMap<Integer, List<SkillStatusModel>>() {{
//					put(project.getId(), new ArrayList<SkillStatusModel>());
//				}},
//				new HashMap<Project, SkillStatus>() {{
//					put(project, SkillStatus.Complete);
//				}}
//		);
//
//		verifyTestCreateList(project, projectStatusModels);
//
//		assertEquals(SkillStatus.Complete, projectStatusModels.get(0).getStatus());
//	}
}
