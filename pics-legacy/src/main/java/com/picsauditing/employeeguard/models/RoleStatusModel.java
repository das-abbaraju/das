package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

public class RoleStatusModel extends RoleModel implements SkillStatusInfo {

	private SkillStatus status;

	public RoleStatusModel(final RoleModel roleModel) {
		this.setId(roleModel.getId());
		this.setName(roleModel.getName());
		this.setSkills(roleModel.getSkills());
	}

	@Override
	public SkillStatus getStatus() {
		return status;
	}

	@Override
	public void setStatus(SkillStatus skillStatus) {
		this.status = skillStatus;
	}
}
