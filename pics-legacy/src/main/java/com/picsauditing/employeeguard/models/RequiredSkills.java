package com.picsauditing.employeeguard.models;

import java.util.List;

public class RequiredSkills {

  private List<SkillStatusModel> skills;

  public RequiredSkills(List<SkillStatusModel> skills) {
    this.skills = skills;
  }

  public List<SkillStatusModel> getSkills() {
		return skills;
	}

	public void setSkills(List<SkillStatusModel> skills) {
		this.skills = skills;
	}
}
