package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;

import java.util.Set;

public class MAssignmentsSkillsRoles {
	@Expose
	private MAssignments assignments;
	@Expose
	private Set<MSkillsManager.MSkill> reqdSkills;
	@Expose
	Set<MRolesManager.MRole> roles;

	public MAssignments getAssignments() {
		return assignments;
	}

	public void setAssignments(MAssignments assignments) {
		this.assignments = assignments;
	}

	public Set<MSkillsManager.MSkill> getReqdSkills() {
		return reqdSkills;
	}

	public void setReqdSkills(Set<MSkillsManager.MSkill> reqdSkills) {
		this.reqdSkills = reqdSkills;
	}

	public Set<MRolesManager.MRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<MRolesManager.MRole> roles) {
		this.roles = roles;
	}
}
