package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.models.SkillModel;
import com.picsauditing.employeeguard.models.SkillStatusModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SkillStatusModelFactory extends SkillModelFactory {

	public List<SkillStatusModel> create(final List<AccountSkill> accountSkills,
										 final Map<AccountSkill, SkillStatus> skillStatusMap) {
		if (CollectionUtils.isEmpty(accountSkills)) {
			return Collections.emptyList();
		}

		List<SkillStatusModel> skillStatusModels = new ArrayList<>();
		for (AccountSkill accountSkill : accountSkills) {
			skillStatusModels.add(create(accountSkill, skillStatusMap.get(accountSkill)));
		}

		return skillStatusModels;
	}

	public SkillStatusModel create(final AccountSkill accountSkill, final SkillStatus skillStatus) {
		SkillModel skillModel = super.create(accountSkill);
		SkillStatusModel skillStatusModel = new SkillStatusModel(skillModel);
		skillStatusModel.setStatus(skillStatus);
		return skillStatusModel;
	}
}
