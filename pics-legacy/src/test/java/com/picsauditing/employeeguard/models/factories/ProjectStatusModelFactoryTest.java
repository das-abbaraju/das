package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.ProjectStatusModel;
import com.picsauditing.employeeguard.models.RoleModel;
import com.picsauditing.employeeguard.models.SkillModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProjectStatusModelFactoryTest extends ProjectModelFactoryTest {

	ProjectStatusModelFactory projectStatusModelFactory;

	@Override
	@Before
	public void setUp() throws Exception {
		projectStatusModelFactory = new ProjectStatusModelFactory();
	}

	@Override
	@Test
	public void testCreate() {
		Project fakeProject = super.buildFakeProject();
		List<RoleModel> fakeRoleModels = new ArrayList<>();
		List<SkillModel> fakeSkillModels = new ArrayList<>();

		ProjectStatusModel projectStatusModel = projectStatusModelFactory.create(fakeProject, fakeRoleModels,
				fakeSkillModels, SkillStatus.Expiring);

		super.verifyTestCreate(fakeRoleModels, fakeSkillModels, projectStatusModel);
		assertEquals(SkillStatus.Expiring, projectStatusModel.getStatus());
	}
}
