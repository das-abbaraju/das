package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.List;

public class ProjectStatusModel implements IdNameComposite, SkillStatusInfo {

	private int id;
	private String name;
	private SkillStatus status;
	private List<RoleStatusModel> roles;
	private RequiredSkills skills;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SkillStatus getStatus() {
		return status;
	}

	public void setStatus(SkillStatus status) {
		this.status = status;
	}

	public List<RoleStatusModel> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleStatusModel> roles) {
		this.roles = roles;
	}

	public RequiredSkills getSkills() {
		return skills;
	}

	public void setSkills(RequiredSkills skills) {
		this.skills = skills;
	}
}
