package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.models.RoleModel;
import com.picsauditing.employeeguard.models.SkillModel;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RoleModelFactoryTest {
	private RoleModelFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new RoleModelFactory();
	}

	@Test
	public void testCreate() throws Exception {
		Role role = new RoleBuilder()
				.id(123)
				.name("Role")
				.build();

		List<SkillModel> skillModels = Arrays.asList(new SkillModel());

		RoleModel roleModel = factory.create(role, skillModels);

		assertEquals(role.getId(), roleModel.getId());
		assertEquals(role.getName(), roleModel.getName());
		assertEquals(skillModels, roleModel.getSkills());
	}
}
