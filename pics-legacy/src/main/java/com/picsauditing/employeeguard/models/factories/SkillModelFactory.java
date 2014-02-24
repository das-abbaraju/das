package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.models.SkillModel;

public class SkillModelFactory {

	public SkillModel create(final AccountSkill skill) {
		SkillModel skillModel = new SkillModel();
		skillModel.setId(skill.getId());
		skillModel.setName(skill.getName());
		return skillModel;
	}
}
