package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.status.SkillStatus;

import java.util.Map;
import java.util.Set;

public class ContractorAssignmentData {

	private Set<Project> allProjects;
	private Set<AccountModel> contractorSiteAssignments;
	private Map<AccountModel, Set<Employee>> employeeAssignmentMap;
	private Map<Role, Set<AccountSkill>> roleSkills;
	private Map<Project, Set<Role>> projectRoles;
	private Map<Project, Set<AccountSkill>> projectRequiredSkills;
	private Map<Project, Set<Employee>> projectEmployeeAssignments;
	private Map<AccountModel, Set<AccountSkill>> siteAndCorporateRequiredSkills;
	private Map<AccountModel, Set<Project>> accountProjects;

	public Set<Project> getAllProjects() {
		return allProjects;
	}

	public void setAllProjects(Set<Project> allProjects) {
		this.allProjects = allProjects;
	}

	public Set<AccountModel> getContractorSiteAssignments() {
		return contractorSiteAssignments;
	}

	public void setContractorSiteAssignments(Set<AccountModel> contractorSiteAssignments) {
		this.contractorSiteAssignments = contractorSiteAssignments;
	}

	public Map<AccountModel, Set<Employee>> getEmployeeAssignmentMap() {
		return employeeAssignmentMap;
	}

	public void setEmployeeAssignmentMap(Map<AccountModel, Set<Employee>> employeeAssignmentMap) {
		this.employeeAssignmentMap = employeeAssignmentMap;
	}

	public Map<Role, Set<AccountSkill>> getRoleSkills() {
		return roleSkills;
	}

	public void setRoleSkills(Map<Role, Set<AccountSkill>> roleSkills) {
		this.roleSkills = roleSkills;
	}

	public Map<Project, Set<Role>> getProjectRoles() {
		return projectRoles;
	}

	public void setProjectRoles(Map<Project, Set<Role>> projectRoles) {
		this.projectRoles = projectRoles;
	}

	public Map<Project, Set<AccountSkill>> getProjectRequiredSkills() {
		return projectRequiredSkills;
	}

	public void setProjectRequiredSkills(Map<Project, Set<AccountSkill>> projectRequiredSkills) {
		this.projectRequiredSkills = projectRequiredSkills;
	}

	public Map<Project, Set<Employee>> getProjectEmployeeAssignments() {
		return projectEmployeeAssignments;
	}

	public void setProjectEmployeeAssignments(Map<Project, Set<Employee>> projectEmployeeAssignments) {
		this.projectEmployeeAssignments = projectEmployeeAssignments;
	}

	public Map<AccountModel, Set<AccountSkill>> getSiteAndCorporateRequiredSkills() {
		return siteAndCorporateRequiredSkills;
	}

	public void setSiteAndCorporateRequiredSkills(Map<AccountModel, Set<AccountSkill>> siteAndCorporateRequiredSkills) {
		this.siteAndCorporateRequiredSkills = siteAndCorporateRequiredSkills;
	}

	public Map<AccountModel, Set<Project>> getAccountProjects() {
		return accountProjects;
	}

	public void setAccountProjects(Map<AccountModel, Set<Project>> accountProjects) {
		this.accountProjects = accountProjects;
	}
}
