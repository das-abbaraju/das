package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.builders.RoleBuilder;
import com.picsauditing.employeeguard.models.RoleStatusModel;
import com.picsauditing.employeeguard.models.SkillModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RoleStatusModelFactoryTest {
	private RoleStatusModelFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new RoleStatusModelFactory();
	}

	@Test
	public void testCreate() throws Exception {
		Role role = new RoleBuilder()
				.id(123)
				.name("Role")
				.build();

		List<SkillModel> skills = Arrays.asList(new SkillModel());

		RoleStatusModel roleStatusModel = factory.create(role, skills, SkillStatus.Complete);

		assertEquals(role.getId(), roleStatusModel.getId());
		assertEquals(role.getName(), roleStatusModel.getName());
		assertEquals(SkillStatus.Complete, roleStatusModel.getStatus());
		assertEquals(skills, roleStatusModel.getSkills());
	}
}
