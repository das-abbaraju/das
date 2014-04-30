package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.Map;
import java.util.Set;

public class EmployeeSkillData {

	private Set<AccountSkill> accountSkills;
	private Map<AccountSkill, SkillStatus> skillStatuses;

	public Set<AccountSkill> getAccountSkills() {
		return accountSkills;
	}

	public void setAccountSkills(Set<AccountSkill> accountSkills) {
		this.accountSkills = accountSkills;
	}

	public Map<AccountSkill, SkillStatus> getSkillStatuses() {
		return skillStatuses;
	}

	public void setSkillStatuses(Map<AccountSkill, SkillStatus> skillStatuses) {
		this.skillStatuses = skillStatuses;
	}
}
