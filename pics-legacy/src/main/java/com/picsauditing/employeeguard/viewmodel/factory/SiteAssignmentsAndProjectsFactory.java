package com.picsauditing.employeeguard.viewmodel.factory;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.AccountSkillRole;
import com.picsauditing.employeeguard.entities.ProjectRoleEmployee;
import com.picsauditing.employeeguard.forms.contractor.ContractorProjectForm;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.viewmodel.contractor.ProjectAssignmentBreakdown;
import com.picsauditing.employeeguard.viewmodel.contractor.ProjectStatisticsModel;
import com.picsauditing.employeeguard.viewmodel.contractor.SiteAssignmentStatisticsModel;

import java.util.*;

public class SiteAssignmentsAndProjectsFactory {

	public Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> create(
			final List<ContractorProjectForm> contractorProjects,
			final List<ProjectRoleEmployee> employeeRoles,
			final List<AccountSkillEmployee> employeeSkills) {

		Map<String, List<ContractorProjectForm>> siteProjects = getSiteProjects(contractorProjects);
		Map<String, List<ProjectRoleEmployee>> employeeProjectRolesPerSite = getEmployeeProjectRolesPerSite(contractorProjects, employeeRoles);
		Map<ProjectRoleEmployee, List<AccountSkillEmployee>> employeeSkillsByProjectRole = getEmployeeSkillsByProjectRole(employeeRoles, employeeSkills);

		List<SiteAssignmentStatisticsModel> siteAssignmentStatistics = buildSiteAssignmentStatistics(siteProjects, employeeProjectRolesPerSite, employeeSkillsByProjectRole);

		Map<ContractorProjectForm, List<ProjectRoleEmployee>> employeeRolesByProject = getEmployeeRolesByProject(contractorProjects, employeeRoles);
		List<ProjectStatisticsModel> projectStatistics = buildProjectStatistics(employeeRolesByProject, employeeSkillsByProjectRole);

		return buildSiteAssignmentsAndProjects(siteAssignmentStatistics, projectStatistics);
	}

	private Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> buildSiteAssignmentsAndProjects(
			List<SiteAssignmentStatisticsModel> siteAssignmentStatistics,
			List<ProjectStatisticsModel> projectStatistics) {
		Map<String, List<ProjectStatisticsModel>> siteNameToProjectStatistics = Utilities.convertToMapOfLists(
				projectStatistics,
				new Utilities.MapConvertable<String, ProjectStatisticsModel>() {
			@Override
			public String getKey(ProjectStatisticsModel entity) {
				return entity.getProject().getSiteName();
			}
		});

		Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> siteAssignmentsAndProjects = new HashMap<>();
		for (SiteAssignmentStatisticsModel siteAssignmentStatistic : siteAssignmentStatistics) {
			siteAssignmentsAndProjects.put(siteAssignmentStatistic, siteNameToProjectStatistics.get(siteAssignmentStatistic.getSiteName()));
		}

		return Collections.unmodifiableMap(siteAssignmentsAndProjects);
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

	private List<SiteAssignmentStatisticsModel> buildSiteAssignmentStatistics(
			Map<String, List<ContractorProjectForm>> siteProjects,
			Map<String, List<ProjectRoleEmployee>> employeeProjectRolesPerSite,
			Map<ProjectRoleEmployee, List<AccountSkillEmployee>> employeeSkillsPerProjectRole) {

		Map<String, Set<AccountSkillEmployee>> employeeSkillsPerSite = getEmployeeSkillsPerSite(employeeProjectRolesPerSite, employeeSkillsPerProjectRole);
		Table<String, SkillStatus, Integer> countOfSkillStatusPerSite = getCountOfSkillStatusPerSite(employeeSkillsPerSite);

		return buildSiteAssignmentStatisticsModels(countOfSkillStatusPerSite);
	}

	private Map<String, Set<AccountSkillEmployee>> getEmployeeSkillsPerSite(Map<String, List<ProjectRoleEmployee>> employeeProjectRolesPerSite,
	                                                                        Map<ProjectRoleEmployee, List<AccountSkillEmployee>> employeeSkillsPerProjectRole) {
		Map<String, Set<AccountSkillEmployee>> employeeSkillsPerSite = new HashMap<>();

		for (Map.Entry<String, List<ProjectRoleEmployee>> entry : employeeProjectRolesPerSite.entrySet()) {
			for (ProjectRoleEmployee employeeRole : entry.getValue()) {
				Utilities.addAllToMapOfKeyToSet(employeeSkillsPerSite, entry.getKey(), employeeSkillsPerProjectRole.get(employeeRole));
			}
		}

		return employeeSkillsPerSite;
	}

	private Table<String, SkillStatus, Integer> getCountOfSkillStatusPerSite(Map<String, Set<AccountSkillEmployee>> employeeSkillsPerSite) {
		Table<String, SkillStatus, Integer> countOfSkillStatusPerSite = TreeBasedTable.create();

		for (Map.Entry<String, Set<AccountSkillEmployee>> entry : employeeSkillsPerSite.entrySet()) {
			calculateCountOfSkillStatus(countOfSkillStatusPerSite, entry.getKey(), entry.getValue());
		}

		return countOfSkillStatusPerSite;
	}

	private <K> void initializeCountOfSkillStatus(Table<K, SkillStatus, Integer> countOfSkillStatus, K key) {
		countOfSkillStatus.put(key, SkillStatus.Complete, 0);
		countOfSkillStatus.put(key, SkillStatus.Expiring, 0);
		countOfSkillStatus.put(key, SkillStatus.Expired, 0);
	}

	private <K> void incrementSkillStatusCount(Table<K, SkillStatus, Integer> countOfSkillStatus, K key, AccountSkillEmployee employeeSkill) {
		switch (SkillStatusCalculator.calculateStatusFromSkill(employeeSkill)) {
			case Complete:
			case Pending:
				countOfSkillStatus.put(key, SkillStatus.Complete, countOfSkillStatus.get(key, SkillStatus.Complete) + 1);
				break;
			case Expiring:
				countOfSkillStatus.put(key, SkillStatus.Expiring, countOfSkillStatus.get(key, SkillStatus.Expiring) + 1);
				break;
			default:
				countOfSkillStatus.put(key, SkillStatus.Expired, countOfSkillStatus.get(key, SkillStatus.Expired) + 1);
		}
	}

	private List<SiteAssignmentStatisticsModel> buildSiteAssignmentStatisticsModels(Table<String, SkillStatus, Integer> countOfSkillStatusPerSite) {
		List<SiteAssignmentStatisticsModel> siteAssignmentStatistics = new ArrayList<>();

		for (Map.Entry<String, Map<SkillStatus, Integer>> entry : countOfSkillStatusPerSite.rowMap().entrySet()) {
			Map<SkillStatus, Integer> statusCount = entry.getValue();

			SiteAssignmentStatisticsModel siteStatistics = new SiteAssignmentStatisticsModel.Builder()
					.siteName(entry.getKey())
					.completed(statusCount.get(SkillStatus.Complete))
					.expiring(statusCount.get(SkillStatus.Expiring))
					.expired(statusCount.get(SkillStatus.Expired))
					.build();
			siteAssignmentStatistics.add(siteStatistics);
		}

		return Collections.unmodifiableList(siteAssignmentStatistics);
	}

	private Map<ContractorProjectForm, List<ProjectRoleEmployee>> getEmployeeRolesByProject(List<ContractorProjectForm> contractorProjects, List<ProjectRoleEmployee> employeeRoles) {
		Map<Integer, List<ProjectRoleEmployee>> employeeRolesToProjectId = Utilities.convertToMapOfLists(employeeRoles, new Utilities.MapConvertable<Integer, ProjectRoleEmployee>() {
			@Override
			public Integer getKey(ProjectRoleEmployee entity) {
				return entity.getProjectRole().getProject().getId();
			}
		});

		Map<ContractorProjectForm, List<ProjectRoleEmployee>> employeeRolesByProject = new HashMap<>();
		for (ContractorProjectForm contractorProject : contractorProjects) {
			employeeRolesByProject.put(contractorProject, employeeRolesToProjectId.get(contractorProject.getProjectId()));
		}

		return employeeRolesByProject;
	}

	private List<ProjectStatisticsModel> buildProjectStatistics(Map<ContractorProjectForm, List<ProjectRoleEmployee>> employeeRolesByProject,
	                                                            Map<ProjectRoleEmployee, List<AccountSkillEmployee>> employeeSkillsByProjectRole) {
		Map<ContractorProjectForm, Set<AccountSkillEmployee>> employeeSkillsPerProject = getEmployeeSkillsPerProject(employeeRolesByProject, employeeSkillsByProjectRole);
		Table<ContractorProjectForm, SkillStatus, Integer> countOfSkillStatusPerProject = getCountOfSkillStatusPerProject(employeeSkillsPerProject);

		return buildProjectStatisticsModels(countOfSkillStatusPerProject);
	}

	private Map<ContractorProjectForm, Set<AccountSkillEmployee>> getEmployeeSkillsPerProject(Map<ContractorProjectForm, List<ProjectRoleEmployee>> employeeRolesByProject, Map<ProjectRoleEmployee, List<AccountSkillEmployee>> employeeSkillsByProjectRole) {
		Map<ContractorProjectForm, Set<AccountSkillEmployee>> employeeSkillsPerProject = new HashMap<>();
		for (Map.Entry<ContractorProjectForm, List<ProjectRoleEmployee>> entry : employeeRolesByProject.entrySet()) {
			for (ProjectRoleEmployee projectRoleEmployee : entry.getValue()) {
				Utilities.addAllToMapOfKeyToSet(employeeSkillsPerProject, entry.getKey(), employeeSkillsByProjectRole.get(projectRoleEmployee));
			}
		}
		return employeeSkillsPerProject;
	}

	private Table<ContractorProjectForm, SkillStatus, Integer> getCountOfSkillStatusPerProject(Map<ContractorProjectForm, Set<AccountSkillEmployee>> employeeSkillsPerProject) {
		Table<ContractorProjectForm, SkillStatus, Integer> countOfSkillStatusPerProject = TreeBasedTable.create();

		for (Map.Entry<ContractorProjectForm, Set<AccountSkillEmployee>> entry : employeeSkillsPerProject.entrySet()) {
			calculateCountOfSkillStatus(countOfSkillStatusPerProject, entry.getKey(), entry.getValue());
		}

		return countOfSkillStatusPerProject;
	}

	private <K> void calculateCountOfSkillStatus(Table<K, SkillStatus, Integer> countOfSkillStatus, K key, Collection<AccountSkillEmployee> employeeSkills) {
		initializeCountOfSkillStatus(countOfSkillStatus, key);

		for (AccountSkillEmployee employeeSkill : employeeSkills) {
			incrementSkillStatusCount(countOfSkillStatus, key, employeeSkill);
		}
	}

	private List<ProjectStatisticsModel> buildProjectStatisticsModels(Table<ContractorProjectForm, SkillStatus, Integer> countOfSkillStatusPerProject) {
		List<ProjectStatisticsModel> projectStatistics = new ArrayList<>();

		for (Map.Entry<ContractorProjectForm, Map<SkillStatus, Integer>> entry : countOfSkillStatusPerProject.rowMap().entrySet()) {
			projectStatistics.add(new ProjectStatisticsModel(entry.getKey(), new ProjectAssignmentBreakdown(entry.getValue())));
		}

		return Collections.unmodifiableList(projectStatistics);
	}
}
