package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.Map;
import java.util.Set;

public class ProfileSkillData {

	private Map<AccountModel, Set<Role>> roles;
	private Map<Role, Set<AccountSkill>> allRoleSkills;
	private Map<AccountModel, Set<Project>> siteProjects;
	private Map<Project, Set<AccountSkill>> projectRequiredSkills;
	private Map<Project, Set<Role>> projectRoles;
	private Map<AccountModel, Set<Group>> contractorGroups;
	private Map<Role, SkillStatus> roleStatuses;
	private Map<AccountModel, Set<Role>> siteAssignmentRoles;

	// All we need for the models
	private Map<AccountModel, Set<AccountSkill>> allRequiredSkills;
	private Map<Project, SkillStatus> projectStatuses;
	private Map<AccountModel, SkillStatus> siteStatuses;
	private Map<Project, Set<AccountSkill>> allProjectSkills;
	private Map<AccountSkill, SkillStatus> skillStatusMap;
	private SkillStatus overallStatus;
	private Map<Integer, AccountModel> contractorAccounts;
	private Map<Integer, AccountModel> siteAndCorporateAccounts;
	private Map<Integer, AccountModel> allAccounts;

	public Map<AccountSkill, SkillStatus> getSkillStatusMap() {
		return skillStatusMap;
	}

	public void setSkillStatusMap(Map<AccountSkill, SkillStatus> skillStatusMap) {
		this.skillStatusMap = skillStatusMap;
	}

	public Map<AccountModel, Set<Role>> getRoles() {
		return roles;
	}

	public void setRoles(Map<AccountModel, Set<Role>> roles) {
		this.roles = roles;
	}

	public Map<Role, Set<AccountSkill>> getAllRoleSkills() {
		return allRoleSkills;
	}

	public void setAllRoleSkills(Map<Role, Set<AccountSkill>> allRoleSkills) {
		this.allRoleSkills = allRoleSkills;
	}

	public Map<AccountModel, Set<Project>> getSiteProjects() {
		return siteProjects;
	}

	public void setSiteProjects(Map<AccountModel, Set<Project>> siteProjects) {
		this.siteProjects = siteProjects;
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

	public Map<AccountModel, Set<Group>> getContractorGroups() {
		return contractorGroups;
	}

	public void setContractorGroups(Map<AccountModel, Set<Group>> contractorGroups) {
		this.contractorGroups = contractorGroups;
	}

	public Map<Role, SkillStatus> getRoleStatuses() {
		return roleStatuses;
	}

	public void setRoleStatuses(Map<Role, SkillStatus> roleStatuses) {
		this.roleStatuses = roleStatuses;
	}

	public Map<AccountModel, Set<Role>> getSiteAssignmentRoles() {
		return siteAssignmentRoles;
	}

	public void setSiteAssignmentRoles(Map<AccountModel, Set<Role>> siteAssignmentRoles) {
		this.siteAssignmentRoles = siteAssignmentRoles;
	}

	public Map<AccountModel, Set<AccountSkill>> getAllRequiredSkills() {
		return allRequiredSkills;
	}

	public void setAllRequiredSkills(Map<AccountModel, Set<AccountSkill>> allRequiredSkills) {
		this.allRequiredSkills = allRequiredSkills;
	}

	public Map<Project, SkillStatus> getProjectStatuses() {
		return projectStatuses;
	}

	public void setProjectStatuses(Map<Project, SkillStatus> projectStatuses) {
		this.projectStatuses = projectStatuses;
	}

	public Map<AccountModel, SkillStatus> getSiteStatuses() {
		return siteStatuses;
	}

	public void setSiteStatuses(Map<AccountModel, SkillStatus> siteStatuses) {
		this.siteStatuses = siteStatuses;
	}

	public Map<Project, Set<AccountSkill>> getAllProjectSkills() {
		return allProjectSkills;
	}

	public void setAllProjectSkills(Map<Project, Set<AccountSkill>> allProjectSkills) {
		this.allProjectSkills = allProjectSkills;
	}

	public SkillStatus getOverallStatus() {
		return overallStatus;
	}

	public void setOverallStatus(SkillStatus overallStatus) {
		this.overallStatus = overallStatus;
	}

	public Map<Integer, AccountModel> getContractorAccounts() {
		return contractorAccounts;
	}

	public void setContractorAccounts(Map<Integer, AccountModel> contractorAccounts) {
		this.contractorAccounts = contractorAccounts;
	}

	public Map<Integer, AccountModel> getSiteAndCorporateAccounts() {
		return siteAndCorporateAccounts;
	}

	public void setSiteAndCorporateAccounts(Map<Integer, AccountModel> siteAndCorporateAccounts) {
		this.siteAndCorporateAccounts = siteAndCorporateAccounts;
	}

	public Map<Integer, AccountModel> getAllAccounts() {
		return allAccounts;
	}

	public void setAllAccounts(Map<Integer, AccountModel> allAccounts) {
		this.allAccounts = allAccounts;
	}
}
