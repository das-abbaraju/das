package com.picsauditing.employeeguard.models;

import java.util.Date;
import java.util.List;

public class ProjectModel implements Identifiable, Nameable, Comparable<ProjectModel> {

	private int id;
	private String name;
	private List<? extends RoleModel> roles;
	private List<? extends SkillModel> skills;
	private String location;
	private Long startDate;
	private Long endDate;

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public List<? extends RoleModel> getRoles() {
		return roles;
	}

	public void setRoles(List<? extends RoleModel> roles) {
		this.roles = roles;
	}

	public List<? extends SkillModel> getSkills() {
		return skills;
	}

	public void setSkills(List<? extends SkillModel> skills) {
		this.skills = skills;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate != null ? startDate.getTime() : null;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate != null ? endDate.getTime() : null;
	}

	@Override
	public int compareTo(ProjectModel that) {
		return this.name.compareToIgnoreCase(that.name);
	}
}
