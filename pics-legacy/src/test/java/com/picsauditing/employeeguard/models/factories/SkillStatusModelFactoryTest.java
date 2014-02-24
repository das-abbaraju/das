package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.models.SkillStatusModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SkillStatusModelFactoryTest {
	private SkillStatusModelFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new SkillStatusModelFactory();
	}

	@Test
	public void testCreate_Single() throws Exception {
		AccountSkill accountSkill = new AccountSkillBuilder()
				.id(123)
				.name("Account Skill")
				.build();

		SkillStatusModel skillStatusModel = factory.create(accountSkill, SkillStatus.Expiring);

		assertEquals(accountSkill.getName(), skillStatusModel.getName());
		assertEquals(SkillStatus.Expiring, skillStatusModel.getStatus());
		assertEquals(123, skillStatusModel.getId());
	}

	@Test
	public void testCreate_List() throws Exception {
		AccountSkill accountSkill = new AccountSkillBuilder()
				.id(123)
				.name("Account Skill")
				.build();
		AccountSkill secondSkill = new AccountSkillBuilder()
				.id(124)
				.name("Second Skill")
				.build();

		Map<AccountSkill, SkillStatus> skillStatusMap = new HashMap<>();
		skillStatusMap.put(accountSkill, SkillStatus.Complete);
		skillStatusMap.put(secondSkill, SkillStatus.Expired);

		List<SkillStatusModel> skillStatusModels = factory.create(Arrays.asList(accountSkill, secondSkill), skillStatusMap);

		assertEquals(2, skillStatusModels.size());
		assertEquals(accountSkill.getId(), skillStatusModels.get(0).getId());
		assertEquals(accountSkill.getName(), skillStatusModels.get(0).getName());
		assertEquals(SkillStatus.Complete, skillStatusModels.get(0).getStatus());
		assertEquals(secondSkill.getId(), skillStatusModels.get(1).getId());
		assertEquals(secondSkill.getName(), skillStatusModels.get(1).getName());
		assertEquals(SkillStatus.Expired, skillStatusModels.get(1).getStatus());
	}

	@Test
	public void testCreate_List_Empty() throws Exception {
		List<SkillStatusModel> skillStatusModels = factory.create(null, new HashMap<AccountSkill, SkillStatus>());

		assertNotNull(skillStatusModels);
		assertEquals(0, skillStatusModels.size());
	}
}
