package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

public class SkillStatusModel extends SkillModel implements SkillStatusInfo {

	private SkillStatus status;

	public SkillStatusModel() {
	}

	public SkillStatusModel(final SkillModel skillModel) {
		this.setId(skillModel.getId());
		this.setName(skillModel.getName());
	}

	@Override
	public SkillStatus getStatus() {
		return status;
	}

	@Override
	public void setStatus(SkillStatus status) {
		this.status = status;
	}
}
