package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

public class RoleStatusModel extends RoleModel {

	private SkillStatus skillStatus;

	public SkillStatus getSkillStatus() {
		return skillStatus;
	}

	public void setSkillStatus(SkillStatus skillStatus) {
		this.skillStatus = skillStatus;
	}
}
