package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.ProjectRoleEmployee;
import com.picsauditing.employeeguard.forms.contractor.ContractorProjectForm;
import com.picsauditing.employeeguard.viewmodel.contractor.ProjectStatisticsModel;
import com.picsauditing.employeeguard.viewmodel.contractor.SiteAssignmentStatisticsModel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SiteAssignmentsAndProjectsFactory {

	public Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> create(
			final List<ContractorProjectForm> projectCompanies,
			final List<ProjectRoleEmployee> employeeRoles,
			final List<AccountSkillEmployee> employeeSkills) {

		// Project, Employee, Skill, Status

		// Filter projects based on site ID
		//Map<String, List<Project>> siteProjects = getSiteProjects(projectCompanies);
		// Filter projects to project roles
		//Map<Project, List<ProjectRoleEmployee>> employeeRolesByProject = getEmployeeRolesByProject(employeeRoles);
		// Match employee skills to roles
		//Map<ProjectRoleEmployee, List<AccountSkillEmployee>> employeeSkillsByProjectRole = getEmployeeSkillsByProjectRole(employeeRoles, employeeSkills);


		return Collections.emptyMap();
	}
}
