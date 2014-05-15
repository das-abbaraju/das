package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.viewmodel.model.SkillInfo;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class SkillInfoBuilder {

	public List<SkillInfo> build(final Map<AccountSkill, SkillStatus> allEmployeeSkills) {
		if (MapUtils.isEmpty(allEmployeeSkills)) {
			return Collections.emptyList();
		}

		List<SkillInfo> skillInfos = new ArrayList<>();
		for (AccountSkill accountSkill : allEmployeeSkills.keySet()) {
			skillInfos.add(build(accountSkill, allEmployeeSkills.get(accountSkill)));
		}

		return skillInfos;
	}

	public SkillInfo build(final AccountSkill accountSkill, final SkillStatus skillStatus) {
		SkillInfo skillInfo = new SkillInfo();

		skillInfo.setId(accountSkill.getId());
		skillInfo.setAccountId(accountSkill.getAccountId());
		skillInfo.setName(accountSkill.getName());
		skillInfo.setDescription(accountSkill.getDescription());
		skillInfo.setSkillType(accountSkill.getSkillType());
		skillInfo.setSkillStatus(skillStatus);

		return skillInfo;
	}

	public SkillInfo build(AccountSkill accountSkill, SkillStatus skillStatus, Date endDate) {
		SkillInfo skillInfo = new SkillInfo();
		skillInfo.setId(accountSkill.getId());
		skillInfo.setAccountId(accountSkill.getAccountId());
		skillInfo.setEndDate(endDate);

		Date endOfTime = DateBean.addDays(DateBean.getEndOfTime(), -1);

		skillInfo.setDoesNotExpire(endDate != null && endOfTime.before(endDate));
		skillInfo.setName(accountSkill.getName());
		skillInfo.setDescription(accountSkill.getDescription());
		skillInfo.setSkillType(accountSkill.getSkillType());
		skillInfo.setSkillStatus(skillStatus);
		return skillInfo;
	}

	public SkillInfo build(AccountSkillProfile accountSkillProfile) {
		AccountSkill accountSkill = accountSkillProfile.getSkill();
		SkillStatus skillStatus = SkillStatusCalculator.calculateStatusFromSkill(accountSkillProfile);
		return build(accountSkill, skillStatus, accountSkillProfile.getEndDate());
	}

	public List<SkillInfo> build(List<AccountSkillProfile> accountSkillProfiles) {
		List<SkillInfo> skillInfoList = new ArrayList<>();
		for (AccountSkillProfile accountSkillProfile : accountSkillProfiles) {
			skillInfoList.add(build(accountSkillProfile));
		}

		Collections.sort(skillInfoList);
		return skillInfoList;
	}
}
