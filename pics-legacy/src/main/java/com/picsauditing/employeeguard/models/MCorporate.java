package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;

import java.util.Set;

public class MCorporate {

	@Expose
	Set<MSkillsManager.MSkill> corpReqdSkills;
	@Expose
	Set<MSitesManager.MSite> sites;
	@Expose
	Set<MSkillsManager.MSkill> skills;
	@Expose
	Set<MRolesManager.MRole> roles;

	public Set<MSkillsManager.MSkill> getCorpReqdSkills() {
		return corpReqdSkills;
	}

	public void setCorpReqdSkills(Set<MSkillsManager.MSkill> corpReqdSkills) {
		this.corpReqdSkills = corpReqdSkills;
	}

	public Set<MSitesManager.MSite> getSites() {
		return sites;
	}

	public void setSites(Set<MSitesManager.MSite> sites) {
		this.sites = sites;
	}

	public Set<MSkillsManager.MSkill> getSkills() {
		return skills;
	}

	public void setSkills(Set<MSkillsManager.MSkill> skills) {
		this.skills = skills;
	}

	public Set<MRolesManager.MRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<MRolesManager.MRole> roles) {
		this.roles = roles;
	}
}
