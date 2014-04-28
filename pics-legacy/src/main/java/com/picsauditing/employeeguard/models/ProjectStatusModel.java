package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.List;

public class ProjectStatusModel implements SkillStatusInfo {

	private int id;
	private String name;
	private SkillStatus status;
	private List<RoleStatusModel> roles;
	private List<SkillStatusModel> skills;

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

	public List<SkillStatusModel> getSkills() {
		return skills;
	}

	public void setSkills(List<SkillStatusModel> skills) {
		this.skills = skills;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ProjectStatusModel that = (ProjectStatusModel) o;

		if (id != that.id) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (status != that.status) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (status != null ? status.hashCode() : 0);
		return result;
	}
}
