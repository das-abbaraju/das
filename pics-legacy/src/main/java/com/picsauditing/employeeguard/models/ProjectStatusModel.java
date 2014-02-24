package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

public class ProjectStatusModel extends ProjectModel implements SkillStatusInfo {

	private SkillStatus status;

	public ProjectStatusModel(final ProjectModel projectModel) {
		this.setId(projectModel.getId());
		this.setName(projectModel.getName());
		this.setRoles(projectModel.getRoles());
		this.setSkills(projectModel.getSkills());
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
