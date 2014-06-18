package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;

import java.util.Set;

public class MContractor {
	@Expose
	Set<MSkillsManager.MSkill> skills;
	@Expose
	Set<MGroupsManager.MGroup> groups;
	@Expose
	int totalEmployees;


	//-- Getters/Setters

	public int getTotalEmployees() {
		return totalEmployees;
	}

	public void setTotalEmployees(int totalEmployees) {
		this.totalEmployees = totalEmployees;
	}

	public Set<MSkillsManager.MSkill> getSkills() {
		return skills;
	}

	public void setSkills(Set<MSkillsManager.MSkill> skills) {
		this.skills = skills;
	}

	public Set<MGroupsManager.MGroup> getGroups() {
		return groups;
	}

	public void setGroups(Set<MGroupsManager.MGroup> groups) {
		this.groups = groups;
	}
}
