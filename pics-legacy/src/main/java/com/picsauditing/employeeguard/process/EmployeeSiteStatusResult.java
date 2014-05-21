package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.status.SkillStatus;

import java.util.Map;
import java.util.Set;

public class EmployeeSiteStatusResult {

	private Set<Project> projects;
	private Map<AccountSkill, SkillStatus> skillStatus;
	private Map<Role, Set<AccountSkill>> allRoleSkills;
	private Map<Project, Set<AccountSkill>> projectRequiredSkills;
	private Map<Project, Set<Role>> projectRoles;
	private Set<AccountSkill> siteAndCorporateRequiredSkills;
	private Map<Project, SkillStatus> projectStatuses;
	private Map<Role, SkillStatus> roleStatuses;
	private Set<Role> siteAssignmentRoles;

	public Set<Project> getProjects() {
		return projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}

	public Map<AccountSkill, SkillStatus> getSkillStatus() {
		return skillStatus;
	}

	public void setSkillStatus(Map<AccountSkill, SkillStatus> skillStatus) {
		this.skillStatus = skillStatus;
	}

	public Map<Role, Set<AccountSkill>> getAllRoleSkills() {
		return allRoleSkills;
	}

	public void setAllRoleSkills(Map<Role, Set<AccountSkill>> allRoleSkills) {
		this.allRoleSkills = allRoleSkills;
	}

	public Map<Project, Set<AccountSkill>> getProjectRequiredSkills() {
		return projectRequiredSkills;
	}

	public void setProjectRequiredSkills(Map<Project, Set<AccountSkill>> projectRequiredSkills) {
		this.projectRequiredSkills = projectRequiredSkills;
	}

	public Map<Project, Set<Role>> getProjectRoles() {
		return projectRoles;
	}

	public void setProjectRoles(Map<Project, Set<Role>> projectRoles) {
		this.projectRoles = projectRoles;
	}

	public Set<AccountSkill> getSiteAndCorporateRequiredSkills() {
		return siteAndCorporateRequiredSkills;
	}

	public void setSiteAndCorporateRequiredSkills(Set<AccountSkill> siteAndCorporateRequiredSkills) {
		this.siteAndCorporateRequiredSkills = siteAndCorporateRequiredSkills;
	}

	public Map<Project, SkillStatus> getProjectStatuses() {
		return projectStatuses;
	}

	public void setProjectStatuses(Map<Project, SkillStatus> projectStatuses) {
		this.projectStatuses = projectStatuses;
	}

	public Map<Role, SkillStatus> getRoleStatuses() {
		return roleStatuses;
	}

	public void setRoleStatuses(Map<Role, SkillStatus> roleStatuses) {
		this.roleStatuses = roleStatuses;
	}

	public Set<Role> getSiteAssignmentRoles() {
		return siteAssignmentRoles;
	}

	public void setSiteAssignmentRoles(Set<Role> siteAssignmentRoles) {
		this.siteAssignmentRoles = siteAssignmentRoles;
	}
}
