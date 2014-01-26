package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.AccountSkillRole;
import com.picsauditing.employeeguard.entities.ProjectRoleEmployee;
import com.picsauditing.employeeguard.forms.contractor.ContractorProjectForm;
import com.picsauditing.employeeguard.viewmodel.contractor.ProjectStatisticsModel;
import com.picsauditing.employeeguard.viewmodel.contractor.SiteAssignmentStatisticsModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SiteAssignmentsAndProjectsFactory {

	public Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> create(
			final List<ContractorProjectForm> contractorProjects,
			final List<ProjectRoleEmployee> employeeRoles,
			final List<AccountSkillEmployee> employeeSkills) {

		// Project, Employee, Skill, Status

		// Filter projects based on site ID
		Map<String, List<ContractorProjectForm>> siteProjects = getSiteProjects(contractorProjects);
		Map<String, List<ProjectRoleEmployee>> employeeProjectRolesPerSite = getEmployeeProjectRolesPerSite(contractorProjects, employeeRoles);
		Map<ProjectRoleEmployee, List<AccountSkillEmployee>> employeeSkillsByProjectRole = getEmployeeSkillsByProjectRole(employeeRoles, employeeSkills);

		List<SiteAssignmentStatisticsModel> siteAssignmentStatistics = buildSiteAssignmentStatistics(siteProjects, employeeProjectRolesPerSite);

		// Filter projects to project roles
		Map<ContractorProjectForm, List<ProjectRoleEmployee>> employeeRolesByProject = getEmployeeRolesByProject(employeeRoles);
		// Match employee skills to roles


		List<ProjectStatisticsModel> projectStatistics = buildProjectStatistic();

		return buildSiteAssignmentsAndProjectsMap(siteAssignmentStatistics, projectStatistics);
	}

	private Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> buildSiteAssignmentsAndProjectsMap(List<SiteAssignmentStatisticsModel> siteAssignmentStatistics, List<ProjectStatisticsModel> projectStatistics) {
		return null;
	}

	private Map<String, List<ContractorProjectForm>> getSiteProjects(List<ContractorProjectForm> contractorProjects) {
		return Utilities.convertToMapOfLists(contractorProjects, new Utilities.MapConvertable<String, ContractorProjectForm>() {
			@Override
			public String getKey(ContractorProjectForm entity) {
				return entity.getSiteName();
			}
		});
	}

	private Map<String, List<ProjectRoleEmployee>> getEmployeeProjectRolesPerSite(List<ContractorProjectForm> contractorProjects, List<ProjectRoleEmployee> employeeRoles) {
		Map<String, List<ProjectRoleEmployee>> employeeProjectRolesToSite = new HashMap<>();

		for (ContractorProjectForm project : contractorProjects) {
			for (ProjectRoleEmployee projectRoleEmployee : employeeRoles) {
				int projectRoleProjectId = projectRoleEmployee.getProjectRole().getProject().getId();

				if (projectRoleProjectId == project.getProjectId()) {
					Utilities.addToMapOfKeyToList(employeeProjectRolesToSite, project.getSiteName(), projectRoleEmployee);
				}
			}
		}

		return employeeProjectRolesToSite;
	}

	private Map<ProjectRoleEmployee, List<AccountSkillEmployee>> getEmployeeSkillsByProjectRole(List<ProjectRoleEmployee> employeeRoles, List<AccountSkillEmployee> employeeSkills) {
		Map<ProjectRoleEmployee, List<AccountSkillEmployee>> projectRoleSkills = new HashMap<>();

		for (ProjectRoleEmployee employeeRole : employeeRoles) {
			for (AccountSkillEmployee employeeSkill : employeeSkills) {
				boolean employeeMatches = employeeSkill.getEmployee().equals(employeeRole.getEmployee());
				boolean roleUsesSkill = roleSkillsContain(employeeRole, employeeSkill.getSkill());

				if (employeeMatches && roleUsesSkill) {
					Utilities.addToMapOfKeyToList(projectRoleSkills, employeeRole, employeeSkill);
				}
			}
		}

		return projectRoleSkills;
	}

	private boolean roleSkillsContain(ProjectRoleEmployee employeeRole, AccountSkill skill) {
		for (AccountSkillRole accountSkillRole : employeeRole.getProjectRole().getRole().getSkills()) {
			if (accountSkillRole.getSkill().equals(skill)) {
				return true;
			}
		}

		return false;
	}

	private List<SiteAssignmentStatisticsModel> buildSiteAssignmentStatistics(Map<String, List<ContractorProjectForm>> siteProjects, Map<String, List<ProjectRoleEmployee>> employeeProjectRolesPerSite) {
		return null;
	}

	private Map<ContractorProjectForm, List<ProjectRoleEmployee>> getEmployeeRolesByProject(List<ProjectRoleEmployee> employeeRoles) {
		return null;
	}

	private List<ProjectStatisticsModel> buildProjectStatistic() {
		return null;
	}
}
