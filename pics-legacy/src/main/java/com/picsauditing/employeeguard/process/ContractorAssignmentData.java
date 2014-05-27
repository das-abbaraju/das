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

	private Map<AccountModel, Set<Employee>> employeeAssignmentMap;
	private Map<AccountSkill, SkillStatus> skillStatusMap;
	private Map<Employee, Set<Project>> employeeProjects;
	private Map<AccountModel, Set<AccountSkill>> siteAndCorporateRequiredSkills;
	private Map<Employee, Set<Role>> employeeRoles;
	private Map<Role, Set<AccountSkill>> roleSkills;
	private Map<Project, Set<AccountSkill>> projectRequiredSkills;
	private Map<Project, Set<Role>> projectRoles;
	private Map<AccountModel, Set<Project>> accountProjects;
	private Set<AccountModel> contractorSiteAssignments;

	public Map<AccountModel, Set<Employee>> getEmployeeAssignmentMap() {
		return employeeAssignmentMap;
	}

	public void setEmployeeAssignmentMap(Map<AccountModel, Set<Employee>> employeeAssignmentMap) {
		this.employeeAssignmentMap = employeeAssignmentMap;
	}

	public Map<AccountSkill, SkillStatus> getSkillStatusMap() {
		return skillStatusMap;
	}

	public void setSkillStatusMap(Map<AccountSkill, SkillStatus> skillStatusMap) {
		this.skillStatusMap = skillStatusMap;
	}

	public Map<Employee, Set<Project>> getEmployeeProjects() {
		return employeeProjects;
	}

	public void setEmployeeProjects(Map<Employee, Set<Project>> employeeProjects) {
		this.employeeProjects = employeeProjects;
	}

	public Map<AccountModel, Set<AccountSkill>> getSiteAndCorporateRequiredSkills() {
		return siteAndCorporateRequiredSkills;
	}

	public void setSiteAndCorporateRequiredSkills(Map<AccountModel, Set<AccountSkill>> siteAndCorporateRequiredSkills) {
		this.siteAndCorporateRequiredSkills = siteAndCorporateRequiredSkills;
	}

	public Map<Employee, Set<Role>> getEmployeeRoles() {
		return employeeRoles;
	}

	public void setEmployeeRoles(Map<Employee, Set<Role>> employeeRoles) {
		this.employeeRoles = employeeRoles;
	}

	public Map<Role, Set<AccountSkill>> getRoleSkills() {
		return roleSkills;
	}

	public void setRoleSkills(Map<Role, Set<AccountSkill>> roleSkills) {
		this.roleSkills = roleSkills;
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

	public Map<AccountModel, Set<Project>> getAccountProjects() {
		return accountProjects;
	}

	public void setAccountProjects(Map<AccountModel, Set<Project>> accountProjects) {
		this.accountProjects = accountProjects;
	}

	public Set<AccountModel> getContractorSiteAssignments() {
		return contractorSiteAssignments;
	}

	public void setContractorSiteAssignments(Set<AccountModel> contractorSiteAssignments) {
		this.contractorSiteAssignments = contractorSiteAssignments;
	}
}
