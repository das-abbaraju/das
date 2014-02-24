package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.models.SkillModel;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SkillModelFactoryTest {
	private SkillModelFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new SkillModelFactory();
	}

	@Test
	public void testCreate() throws Exception {
		AccountSkill skill = new AccountSkillBuilder()
				.id(123)
				.name("Skill")
				.build();

		SkillModel skillModel = factory.create(skill);

		assertEquals(skill.getId(), skillModel.getId());
		assertEquals(skill.getName(), skillModel.getName());
	}
}
