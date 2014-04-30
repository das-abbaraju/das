package com.picsauditing.employeeguard.models;

import java.util.List;

public class RequiredSkills {

	private List<SkillStatusModel> skills;

	public RequiredSkills() {
	}

	public RequiredSkills(List<SkillStatusModel> requiredSkills) {
		this.skills = requiredSkills;
	}

	public List<SkillStatusModel> getSkills() {
		return skills;
	}

	public void setSkills(List<SkillStatusModel> skills) {
		this.skills = skills;
	}
}
