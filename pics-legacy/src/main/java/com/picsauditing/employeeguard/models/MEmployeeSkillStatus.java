package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;

public class MEmployeeSkillStatus {
	@Expose
	String status;

	@Expose
	MSkillsManager.MSkill skill;

	public MEmployeeSkillStatus(String status, MSkillsManager.MSkill skill) {
		this.status = status;
		this.skill = skill;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public MSkillsManager.MSkill getSkill() {
		return skill;
	}

	public void setSkill(MSkillsManager.MSkill skill) {
		this.skill = skill;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		MEmployeeSkillStatus that = (MEmployeeSkillStatus) o;

		if (!skill.equals(that.skill)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return skill.hashCode();
	}
}
