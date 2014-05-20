package com.picsauditing.employeeguard.models.factories;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.contractor.ContractorProjectForm;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.status.SkillStatusCalculator;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModelFactory;
import com.picsauditing.util.generic.GenericPredicate;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class SiteAssignmentsAndProjectsFactory {

	public Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> create(
			final Map<AccountModel, Set<Project>> projects,
			final Map<AccountModel, Set<AccountSkill>> siteAndCorporateRequiredSkills,
			final Map<Employee, Set<Role>> employeeRoles,
			final List<AccountSkillProfile> employeeSkills) {

		Map<AccountModel, Map<Employee, Set<Role>>> employeeRolesPerSite = getEmployeeRolesPerSite(projects, employeeRoles);
		Table<Employee, Role, Set<AccountSkillProfile>> employeeSkillsByRole = getEmployeeSkillsByRole(employeeRoles, employeeSkills);
		Table<AccountModel, AccountSkill, Set<AccountSkillProfile>> employeeSkillsBySiteSkills = getEmployeeSkillsBySiteSkills(siteAndCorporateRequiredSkills, employeeSkills);

		List<SiteAssignmentStatisticsModel> siteAssignmentStatistics = buildSiteAssignmentStatistics(employeeRolesPerSite, employeeSkillsByRole, employeeSkillsBySiteSkills);
		List<ProjectStatisticsModel> projectStatistics = buildProjectStatistics(projects, employeeSkillsByRole);

		return buildSiteAssignmentsAndProjects(siteAssignmentStatistics, projectStatistics);
	}

	private Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> buildSiteAssignmentsAndProjects(
			List<SiteAssignmentStatisticsModel> siteAssignmentStatistics,
			List<ProjectStatisticsModel> projectStatistics) {

		Map<Integer, List<ProjectStatisticsModel>> siteNameToProjectStatistics = PicsCollectionUtil.convertToMapOfLists(
				projectStatistics,
				new PicsCollectionUtil.MapConvertable<Integer, ProjectStatisticsModel>() {
					@Override
					public Integer getKey(ProjectStatisticsModel entity) {
						return entity.getProject().getSiteId();
					}
				});

		Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> siteAssignmentsAndProjects = new TreeMap<>();
		for (SiteAssignmentStatisticsModel siteAssignmentStatistic : siteAssignmentStatistics) {
			siteAssignmentsAndProjects.put(siteAssignmentStatistic, siteNameToProjectStatistics.get(siteAssignmentStatistic.getSite().getId()));
		}

		return siteAssignmentsAndProjects;
	}

	private Map<AccountModel, Map<Employee, Set<Role>>> getEmployeeRolesPerSite(Map<AccountModel, Set<Project>> projects, Map<Employee, Set<Role>> employeeRoles) {
		Map<AccountModel, Map<Employee, Set<Role>>> employeeRolesToSite = new HashMap<>();

		for (Map.Entry<AccountModel, Set<Project>> projectEntry : projects.entrySet()) {
			AccountModel site = projectEntry.getKey();

			for (Map.Entry<Employee, Set<Role>> employeeEntry : employeeRoles.entrySet()) {
				Employee employee = employeeEntry.getKey();
				final Set<Project> projectSet = projectEntry.getValue();

				if (employeeBelongsToSite(site, employee) || employeeBelongsToProject(projectSet, employee)) {
					if (!employeeRolesToSite.containsKey(site)) {
						employeeRolesToSite.put(site, new HashMap<Employee, Set<Role>>());
					}

					Set<Role> filteredByProject = new HashSet<>(employeeEntry.getValue());
					CollectionUtils.filter(filteredByProject, new GenericPredicate<Role>() {
						@Override
						public boolean evaluateEntity(Role role) {
							for (ProjectRole projectRole : role.getProjects()) {
								if (projectSet.contains(projectRole.getProject())) {
									return true;
								}
							}

							return false;
						}
					});

					employeeRolesToSite.get(site).put(employee, filteredByProject);
				}
			}

			if (!employeeRolesToSite.containsKey(site)) {
				// No employee has been added to this project yet
				employeeRolesToSite.put(site, Collections.<Employee, Set<Role>>emptyMap());
			}
		}

		return employeeRolesToSite;
	}

	private boolean employeeBelongsToSite(AccountModel site, Employee employee) {
		for (SiteAssignment siteAssignment : employee.getSiteAssignments()) {
			if (siteAssignment.getSiteId() == site.getId()) {
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

	private Table<Employee, Role, Set<AccountSkillProfile>> getEmployeeSkillsByRole(final Map<Employee, Set<Role>> employeeRoles,
																					final List<AccountSkillProfile> employeeSkills) {
		Table<Employee, Role, Set<AccountSkillProfile>> projectRoleSkills = TreeBasedTable.create();

		for (Map.Entry<Employee, Set<Role>> roleEntry : employeeRoles.entrySet()) {
			final Employee employee = roleEntry.getKey();

			for (final Role role : roleEntry.getValue()) {
				Set<AccountSkillProfile> employeeRoleSkills = filterAccountSkillProfilesByEmployeeAndRole(employeeSkills,
								employee, role);

				projectRoleSkills.put(employee, role, employeeRoleSkills);
			}
		}

		return projectRoleSkills;
	}

	private Set<AccountSkillProfile> filterAccountSkillProfilesByEmployeeAndRole(final List<AccountSkillProfile> employeeSkills,
																																							 final Employee employee,
																																							 final Role role) {
		Set<AccountSkillProfile> employeeRoleSkills = new HashSet<>(employeeSkills);
		CollectionUtils.filter(employeeRoleSkills, new GenericPredicate<AccountSkillProfile>() {

			@Override
			public boolean evaluateEntity(AccountSkillProfile accountSkillProfile) {
				List<AccountSkill> roleSkills = ExtractorUtil.extractList(role.getSkills(), AccountSkillRole.SKILL_EXTRACTOR);

				return accountSkillProfile.getProfile().getEmployees().contains(employee)
						&& roleSkills.contains(accountSkillProfile.getSkill());
			}
		});

		return employeeRoleSkills;
	}

	private Table<AccountModel, AccountSkill, Set<AccountSkillProfile>> getEmployeeSkillsBySiteSkills(
			Map<AccountModel, Set<AccountSkill>> siteAndCorporateRequiredSkills,
			List<AccountSkillProfile> employeeSkills) {
		Table<AccountModel, AccountSkill, Set<AccountSkillProfile>> employeeSkillsBySiteSkills = TreeBasedTable.create();

		for (Map.Entry<AccountModel, Set<AccountSkill>> siteRequiredSkills : siteAndCorporateRequiredSkills.entrySet()) {
			for (AccountSkill skill : siteRequiredSkills.getValue()) {
				employeeSkillsBySiteSkills.put(siteRequiredSkills.getKey(), skill, filterAccountSkillProfilesBySkill(employeeSkills, skill));
			}
		}

		return employeeSkillsBySiteSkills;
	}

	private Set<AccountSkillProfile> filterAccountSkillProfilesBySkill(List<AccountSkillProfile> employeeSkills, final AccountSkill skill) {
		Set<AccountSkillProfile> filtered = new HashSet<>(employeeSkills);

		CollectionUtils.filter(filtered, new GenericPredicate<AccountSkillProfile>() {
			@Override
			public boolean evaluateEntity(AccountSkillProfile accountSkillProfile) {
				return accountSkillProfile.getSkill().equals(skill);
			}
		});

		return filtered;
	}

	private List<SiteAssignmentStatisticsModel> buildSiteAssignmentStatistics(
			Map<AccountModel, Map<Employee, Set<Role>>> employeeRolesPerSite,
			Table<Employee, Role, Set<AccountSkillProfile>> employeeSkillsPerRole,
			Table<AccountModel, AccountSkill, Set<AccountSkillProfile>> siteAndCorporateRequiredSkills) {

		Map<AccountModel, Set<AccountSkillProfile>> employeeSkillsPerSite = getEmployeeSkillsPerSite(employeeRolesPerSite,
				employeeSkillsPerRole, siteAndCorporateRequiredSkills);
		Table<AccountModel, SkillStatus, Integer> countOfSkillStatusPerSite = getCountOfSkillStatusPerSite(employeeSkillsPerSite);

		return buildSiteAssignmentStatisticsModels(countOfSkillStatusPerSite);
	}

	private Map<AccountModel, Set<AccountSkillProfile>> getEmployeeSkillsPerSite(
			Map<AccountModel, Map<Employee, Set<Role>>> employeeRolesPerSite,
			Table<Employee, Role, Set<AccountSkillProfile>> employeeSkillsPerRole,
			Table<AccountModel, AccountSkill, Set<AccountSkillProfile>> siteAndCorporateRequiredSkills) {

		Map<AccountModel, Set<AccountSkillProfile>> employeeSkillsPerSite = new HashMap<>();

		for (Map.Entry<AccountModel, Map<Employee, Set<Role>>> siteEntry : employeeRolesPerSite.entrySet()) {
			AccountModel site = siteEntry.getKey();
			Map<Employee, Set<Role>> employeeRoles = siteEntry.getValue();

			for (Map.Entry<Employee, Set<Role>> roleEntry : employeeRoles.entrySet()) {
				for (Role role : roleEntry.getValue()) {
					PicsCollectionUtil.addAllToMapOfKeyToSet(employeeSkillsPerSite, site, employeeSkillsPerRole.get(roleEntry.getKey(), role));
				}
			}

			Map<AccountSkill, Set<AccountSkillProfile>> requiredSkills = siteAndCorporateRequiredSkills.row(site);
			Set<AccountSkillProfile> flattenedRequiredSkills = PicsCollectionUtil.extractAndFlattenValuesFromMap(requiredSkills);
			PicsCollectionUtil.addAllToMapOfKeyToSet(employeeSkillsPerSite, site, flattenedRequiredSkills);

			if (!employeeSkillsPerSite.containsKey(site)) {
				employeeSkillsPerSite.put(site, Collections.<AccountSkillProfile>emptySet());
			}
		}

		return employeeSkillsPerSite;
	}

	private Table<AccountModel, SkillStatus, Integer> getCountOfSkillStatusPerSite(final Map<AccountModel, Set<AccountSkillProfile>> employeeSkillsPerSite) {
		Table<AccountModel, SkillStatus, Integer> countOfSkillStatusPerSite = TreeBasedTable.create();

		for (Map.Entry<AccountModel, Set<AccountSkillProfile>> entry : employeeSkillsPerSite.entrySet()) {
			calculateCountOfSkillStatus(countOfSkillStatusPerSite, entry.getKey(), entry.getValue());
		}

		return countOfSkillStatusPerSite;
	}

	private <K> void initializeCountOfSkillStatus(Table<K, SkillStatus, Integer> countOfSkillStatus, K key) {
		countOfSkillStatus.put(key, SkillStatus.Completed, 0);
		countOfSkillStatus.put(key, SkillStatus.Expiring, 0);
		countOfSkillStatus.put(key, SkillStatus.Expired, 0);
	}

	private <K> void incrementSkillStatusCount(Table<K, SkillStatus, Integer> countOfSkillStatus, K key, AccountSkillProfile employeeSkill) {
		switch (SkillStatusCalculator.calculateStatusFromSkill(employeeSkill)) {
			case Completed:
			case Pending:
				countOfSkillStatus.put(key, SkillStatus.Completed, countOfSkillStatus.get(key, SkillStatus.Completed) + 1);
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
					.completed(statusCount.get(SkillStatus.Completed))
					.expiring(statusCount.get(SkillStatus.Expiring))
					.expired(statusCount.get(SkillStatus.Expired))
					.build();
			siteAssignmentStatistics.add(siteStatistics);
		}

		return Collections.unmodifiableList(siteAssignmentStatistics);
	}

	public List<SiteAssignmentStatisticsModel> buildSiteAssignStatsForClientSitesUnattachedToProjs(List<AccountModel> contractorClientSitesNotAttachedToProjects) {
		List<SiteAssignmentStatisticsModel> siteAssignmentStatistics = new ArrayList<>();

		for (AccountModel accountModel : contractorClientSitesNotAttachedToProjects) {
			SiteAssignmentStatisticsModel siteStatistics = new SiteAssignmentStatisticsModel.Builder()
					.site(accountModel)
					.completed(0)
					.expiring(0)
					.expired(0)
					.build();
			siteAssignmentStatistics.add(siteStatistics);
		}

		return Collections.unmodifiableList(siteAssignmentStatistics);
	}

	private <K> void calculateCountOfSkillStatus(final Table<K, SkillStatus, Integer> countOfSkillStatus,
												 final K key,
												 final Collection<AccountSkillProfile> employeeSkills) {
		initializeCountOfSkillStatus(countOfSkillStatus, key);

		for (AccountSkillProfile employeeSkill : employeeSkills) {
			incrementSkillStatusCount(countOfSkillStatus, key, employeeSkill);
		}
	}

	private List<ProjectStatisticsModel> buildProjectStatistics(final Map<AccountModel, Set<Project>> accountsToProjects,
																final Table<Employee, Role, Set<AccountSkillProfile>> employeeSkillsByRole) {

		Set<Project> projects = PicsCollectionUtil.extractAndFlattenValuesFromMap(accountsToProjects);
		List<ContractorProjectForm> contractorProjects = ViewModelFactory.getContractorProjectFormFactory().build(accountsToProjects.keySet(), projects);

		List<ProjectStatisticsModel> projectStatistics = new ArrayList<>();
		for (Project project : projects) {
			ContractorProjectForm contractorProject = findContractorProject(project.getId(), contractorProjects);

			Set<AccountSkillProfile> allSkills = new HashSet<>();
			Set<ProjectRoleEmployee> allRoles = new HashSet<>();

			for (ProjectRole projectRole : project.getRoles()) {
				Map<Employee, Set<AccountSkillProfile>> employeesAndSkills = employeeSkillsByRole.column(projectRole.getRole());

				allSkills.addAll(PicsCollectionUtil.extractAndFlattenValuesFromMap(employeesAndSkills));
				allRoles.addAll(filterProjectRoleEmployees(projectRole, employeesAndSkills));
			}

			ProjectAssignmentBreakdown assignmentBreakdown = ModelFactory
					.getProjectAssignmentBreakdownFactory().create(allRoles, allSkills, employeeSkillsByRole.rowKeySet());
			projectStatistics.add(new ProjectStatisticsModel(contractorProject, assignmentBreakdown));
		}

		Collections.sort(projectStatistics);

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

	private Set<ProjectRoleEmployee> filterProjectRoleEmployees(final ProjectRole projectRole,
																final Map<Employee, Set<AccountSkillProfile>> employeesAndSkills) {
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
