package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.Map;
import java.util.Set;

public class ProfileSkillData {

	// All Corporate/Site Required Skills + SiteAssignment Role Skills (for Roles not in Projects)
	// All Contractor Group Skills + Skills Required for all Employees
	private Map<AccountModel, Set<AccountSkill>> allRequiredSkills;

	private Map<AccountModel, Set<Project>> siteProjects;
 	private Map<Project, SkillStatus> projectStatuses;
	private Map<AccountModel, SkillStatus> siteStatuses;
	private Map<Project, Set<AccountSkill>> allProjectSkills;
	private Map<AccountSkill, SkillStatus> skillStatusMap;
	private SkillStatus overallStatus;
	private Map<Integer, AccountModel> contractorAccounts;
	private Map<Integer, AccountModel> siteAccounts;
	private Map<Integer, AccountModel> allAccounts;
	private Map<AccountModel, Set<AccountModel>> parentSites;

	public Map<AccountModel, Set<AccountSkill>> getAllRequiredSkills() {

		return allRequiredSkills;
	}

	public void setAllRequiredSkills(Map<AccountModel, Set<AccountSkill>> allRequiredSkills) {
		this.allRequiredSkills = allRequiredSkills;
	}

	public Map<AccountModel, Set<Project>> getSiteProjects() {
		return siteProjects;
	}

	public void setSiteProjects(Map<AccountModel, Set<Project>> siteProjects) {
		this.siteProjects = siteProjects;
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

	public Map<AccountSkill, SkillStatus> getSkillStatusMap() {
		return skillStatusMap;
	}

	public void setSkillStatusMap(Map<AccountSkill, SkillStatus> skillStatusMap) {
		this.skillStatusMap = skillStatusMap;
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

	public Map<Integer, AccountModel> getSiteAccounts() {
		return siteAccounts;
	}

	public void setSiteAccounts(Map<Integer, AccountModel> siteAccounts) {
		this.siteAccounts = siteAccounts;
	}

	public Map<Integer, AccountModel> getAllAccounts() {
		return allAccounts;
	}

	public void setAllAccounts(Map<Integer, AccountModel> allAccounts) {
		this.allAccounts = allAccounts;
	}

	public Map<AccountModel, Set<AccountModel>> getParentSites() {
		return parentSites;
	}

	public void setParentSites(Map<AccountModel, Set<AccountModel>> parentSites) {
		this.parentSites = parentSites;
	}
}
