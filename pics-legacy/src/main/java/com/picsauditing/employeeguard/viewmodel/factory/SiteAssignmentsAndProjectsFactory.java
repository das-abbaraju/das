package com.picsauditing.employeeguard.viewmodel.factory;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.contractor.ContractorProjectForm;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.employeeguard.viewmodel.contractor.ProjectAssignmentBreakdown;
import com.picsauditing.employeeguard.viewmodel.contractor.ProjectStatisticsModel;
import com.picsauditing.employeeguard.viewmodel.contractor.SiteAssignmentStatisticsModel;
import com.picsauditing.util.generic.GenericPredicate;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class SiteAssignmentsAndProjectsFactory {

	public Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> create(
			final Map<AccountModel, Set<Project>> projects,
			final Map<Employee, Set<Role>> employeeRoles,
			final List<AccountSkillEmployee> employeeSkills) {

		Map<AccountModel, Map<Employee, Set<Role>>> employeeRolesPerSite = getEmployeeRolesPerSite(projects, employeeRoles);
		Table<Employee, Role, Set<AccountSkillEmployee>> employeeSkillsByRole = getEmployeeSkillsByRole(employeeRoles, employeeSkills);

		List<SiteAssignmentStatisticsModel> siteAssignmentStatistics = buildSiteAssignmentStatistics(employeeRolesPerSite, employeeSkillsByRole);
		List<ProjectStatisticsModel> projectStatistics = buildProjectStatistics(projects, employeeSkillsByRole);

		return buildSiteAssignmentsAndProjects(siteAssignmentStatistics, projectStatistics);
	}

	private Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> buildSiteAssignmentsAndProjects(
			List<SiteAssignmentStatisticsModel> siteAssignmentStatistics,
			List<ProjectStatisticsModel> projectStatistics) {
		Map<Integer, List<ProjectStatisticsModel>> siteNameToProjectStatistics = Utilities.convertToMapOfLists(
				projectStatistics,
				new Utilities.MapConvertable<Integer, ProjectStatisticsModel>() {
					@Override
					public Integer getKey(ProjectStatisticsModel entity) {
						return entity.getProject().getSiteId();
					}
				});

		Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> siteAssignmentsAndProjects = new HashMap<>();
		for (SiteAssignmentStatisticsModel siteAssignmentStatistic : siteAssignmentStatistics) {
			siteAssignmentsAndProjects.put(siteAssignmentStatistic, siteNameToProjectStatistics.get(siteAssignmentStatistic.getSite().getId()));
		}

		return Collections.unmodifiableMap(siteAssignmentsAndProjects);
	}

	private Map<AccountModel, Map<Employee, Set<Role>>> getEmployeeRolesPerSite(Map<AccountModel, Set<Project>> projects, Map<Employee, Set<Role>> employeeRoles) {
		Map<AccountModel, Map<Employee, Set<Role>>> employeeRolesToSite = new HashMap<>();

		for (Map.Entry<AccountModel, Set<Project>> projectEntry : projects.entrySet()) {
			for (Map.Entry<Employee, Set<Role>> employeeEntry : employeeRoles.entrySet()) {
				AccountModel site = projectEntry.getKey();
				Employee employee = employeeEntry.getKey();

				if (employeeBelongsToSite(site, employee) || employeeBelongsToProject(projectEntry.getValue(), employee)) {
					if (!employeeRolesToSite.containsKey(site)) {
						employeeRolesToSite.put(site, new HashMap<Employee, Set<Role>>());
					}

					employeeRolesToSite.get(site).put(employee, employeeEntry.getValue());
				}
			}
		}

		return employeeRolesToSite;
	}

	private boolean employeeBelongsToSite(AccountModel site, Employee employee) {
		for (RoleEmployee roleEmployee : employee.getRoles()) {
			if (roleEmployee.getRole().getAccountId() == site.getId()) {
				return true;
			}
		}

		return false;
	}

	private boolean employeeBelongsToProject(Set<Project> projects, Employee employee) {
		for (ProjectRoleEmployee projectRoleEmployee : employee.getProjectRoles()) {
			if (projects.contains(projectRoleEmployee.getProjectRole().getProject())) {
				return true;
			}
		}

		return false;
	}

	private Table<Employee, Role, Set<AccountSkillEmployee>> getEmployeeSkillsByRole(Map<Employee, Set<Role>> employeeRoles, List<AccountSkillEmployee> employeeSkills) {
		Table<Employee, Role, Set<AccountSkillEmployee>> projectRoleSkills = TreeBasedTable.create();

		for (Map.Entry<Employee, Set<Role>> roleEntry : employeeRoles.entrySet()) {
			final Employee employee = roleEntry.getKey();

			for (final Role role : roleEntry.getValue()) {
				Set<AccountSkillEmployee> employeeRoleSkills = filterAccountSkillEmployees(employeeSkills, employee, role);
				projectRoleSkills.put(employee, role, employeeRoleSkills);
			}
		}

		return projectRoleSkills;
	}

	private Set<AccountSkillEmployee> filterAccountSkillEmployees(List<AccountSkillEmployee> employeeSkills, final Employee employee, final Role role) {
		Set<AccountSkillEmployee> employeeRoleSkills = new HashSet<>(employeeSkills);
		CollectionUtils.filter(employeeRoleSkills, new GenericPredicate<AccountSkillEmployee>() {
			@Override
			public boolean evaluateEntity(AccountSkillEmployee accountSkillEmployee) {
				List<AccountSkill> roleSkills = ExtractorUtil.extractList(role.getSkills(), AccountSkillRole.SKILL_EXTRACTOR);

				return accountSkillEmployee.getEmployee().equals(employee) && roleSkills.contains(accountSkillEmployee.getSkill());
			}
		});
		return employeeRoleSkills;
	}

	private List<SiteAssignmentStatisticsModel> buildSiteAssignmentStatistics(
			Map<AccountModel, Map<Employee, Set<Role>>> employeeRolesPerSite,
			Table<Employee, Role, Set<AccountSkillEmployee>> employeeSkillsPerRole) {

		Map<AccountModel, Set<AccountSkillEmployee>> employeeSkillsPerSite = getEmployeeSkillsPerSite(employeeRolesPerSite, employeeSkillsPerRole);
		Table<AccountModel, SkillStatus, Integer> countOfSkillStatusPerSite = getCountOfSkillStatusPerSite(employeeSkillsPerSite);

		return buildSiteAssignmentStatisticsModels(countOfSkillStatusPerSite);
	}

	private Map<AccountModel, Set<AccountSkillEmployee>> getEmployeeSkillsPerSite(Map<AccountModel, Map<Employee, Set<Role>>> employeeRolesPerSite,
	                                                                              Table<Employee, Role, Set<AccountSkillEmployee>> employeeSkillsPerRole) {
		Map<AccountModel, Set<AccountSkillEmployee>> employeeSkillsPerSite = new HashMap<>();

		for (Map.Entry<AccountModel, Map<Employee, Set<Role>>> siteEntry : employeeRolesPerSite.entrySet()) {
			Map<Employee, Set<Role>> employeeRoles = siteEntry.getValue();

			for (Map.Entry<Employee, Set<Role>> roleEntry : employeeRoles.entrySet()) {
				for (Role role : roleEntry.getValue()) {
					Utilities.addAllToMapOfKeyToSet(employeeSkillsPerSite, siteEntry.getKey(), employeeSkillsPerRole.get(roleEntry.getKey(), role));
				}
			}
		}

		return employeeSkillsPerSite;
	}

	private Table<AccountModel, SkillStatus, Integer> getCountOfSkillStatusPerSite(Map<AccountModel, Set<AccountSkillEmployee>> employeeSkillsPerSite) {
		Table<AccountModel, SkillStatus, Integer> countOfSkillStatusPerSite = TreeBasedTable.create();

		for (Map.Entry<AccountModel, Set<AccountSkillEmployee>> entry : employeeSkillsPerSite.entrySet()) {
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

	private List<SiteAssignmentStatisticsModel> buildSiteAssignmentStatisticsModels(Table<AccountModel, SkillStatus, Integer> countOfSkillStatusPerSite) {
		List<SiteAssignmentStatisticsModel> siteAssignmentStatistics = new ArrayList<>();

		for (Map.Entry<AccountModel, Map<SkillStatus, Integer>> entry : countOfSkillStatusPerSite.rowMap().entrySet()) {
			Map<SkillStatus, Integer> statusCount = entry.getValue();

			SiteAssignmentStatisticsModel siteStatistics = new SiteAssignmentStatisticsModel.Builder()
					.site(entry.getKey())
					.completed(statusCount.get(SkillStatus.Complete))
					.expiring(statusCount.get(SkillStatus.Expiring))
					.expired(statusCount.get(SkillStatus.Expired))
					.build();
			siteAssignmentStatistics.add(siteStatistics);
		}

		return Collections.unmodifiableList(siteAssignmentStatistics);
	}

	private <K> void calculateCountOfSkillStatus(Table<K, SkillStatus, Integer> countOfSkillStatus, K key, Collection<AccountSkillEmployee> employeeSkills) {
		initializeCountOfSkillStatus(countOfSkillStatus, key);

		for (AccountSkillEmployee employeeSkill : employeeSkills) {
			incrementSkillStatusCount(countOfSkillStatus, key, employeeSkill);
		}
	}

	private List<ProjectStatisticsModel> buildProjectStatistics(final Map<AccountModel, Set<Project>> accountsToProjects,
	                                                            final Table<Employee, Role, Set<AccountSkillEmployee>> employeeSkillsByRole) {

		Set<Project> projects = Utilities.extractAndFlattenValuesFromMap(accountsToProjects);
		List<ContractorProjectForm> contractorProjects = ViewModeFactory.getContractorProjectFormFactory().build(accountsToProjects.keySet(), projects);

		List<ProjectStatisticsModel> projectStatistics = new ArrayList<>();
		for (Project project : projects) {
			ContractorProjectForm contractorProject = findContractorProject(project.getId(), contractorProjects);

			Set<AccountSkillEmployee> allSkills = new HashSet<>();
			Set<ProjectRoleEmployee> allRoles = new HashSet<>();

			for (ProjectRole projectRole : project.getRoles()) {
				Map<Employee, Set<AccountSkillEmployee>> employeesAndSkills = employeeSkillsByRole.column(projectRole.getRole());

				allSkills.addAll(Utilities.extractAndFlattenValuesFromMap(employeesAndSkills));
				allRoles.addAll(filterProjectRoleEmployees(projectRole, employeesAndSkills));
			}

			ProjectAssignmentBreakdown assignmentBreakdown = ViewModeFactory.getProjectAssignmentBreakdownFactory().create(allRoles, allSkills);
			projectStatistics.add(new ProjectStatisticsModel(contractorProject, assignmentBreakdown));
		}

		return projectStatistics;
	}

	private ContractorProjectForm findContractorProject(int projectId, List<ContractorProjectForm> contractorProjects) {
		for (ContractorProjectForm contractorProject : contractorProjects) {
			if (contractorProject.getProjectId() == projectId) {
				return contractorProject;
			}
		}

		return null;
	}

	private Set<ProjectRoleEmployee> filterProjectRoleEmployees(ProjectRole projectRole, final Map<Employee, Set<AccountSkillEmployee>> employeesAndSkills) {
		Set<ProjectRoleEmployee> projectRoleEmployees = new HashSet<>(projectRole.getEmployees());

		CollectionUtils.filter(projectRoleEmployees, new GenericPredicate<ProjectRoleEmployee>() {
			@Override
			public boolean evaluateEntity(ProjectRoleEmployee projectRoleEmployee) {
				return employeesAndSkills.containsKey(projectRoleEmployee.getEmployee());
			}
		});

		return projectRoleEmployees;
	}
}
