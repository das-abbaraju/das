package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

public class SkillStatusModel extends SkillModel {

	private SkillStatus skillStatus;

	public SkillStatus getSkillStatus() {
		return skillStatus;
	}

	public void setSkillStatus(SkillStatus skillStatus) {
		this.skillStatus = skillStatus;
	}
}
