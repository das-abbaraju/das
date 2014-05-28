package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.status.StatusCalculatorService;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class EmployeeSiteStatusProcess {

	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private ProjectEntityService projectEntityService;
	@Autowired
	private RoleEntityService roleEntityService;
	@Autowired
	private SkillEntityService skillEntityService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	public EmployeeSiteStatusResult getEmployeeSiteStatusResult(final int employeeId, final int siteId,
																final Collection<Integer> parentSites) {
		EmployeeSiteStatusResult employeeSiteStatusResult = new EmployeeSiteStatusResult();

		Employee employee = employeeEntityService.find(employeeId);
		Set<Project> projects = projectEntityService.getProjectsForEmployeeBySiteId(employee, siteId);

		employeeSiteStatusResult.setProjects(projects);

		employeeSiteStatusResult = addProjectRoles(employeeSiteStatusResult, projects, employee);
		employeeSiteStatusResult = addSiteAndCorporateSkills(employeeSiteStatusResult, siteId, parentSites);
		employeeSiteStatusResult = addProjectRequiredSkills(employeeSiteStatusResult, projects);
		employeeSiteStatusResult = addRoleSkills(employeeSiteStatusResult, employee, siteId);
		employeeSiteStatusResult = addSkillStatuses(employeeSiteStatusResult, employee);
		employeeSiteStatusResult = addRoleStatuses(employeeSiteStatusResult);
		employeeSiteStatusResult = addProjectStatuses(employeeSiteStatusResult);
		employeeSiteStatusResult = addSiteAssignmentRoles(employeeSiteStatusResult, siteId, employee);

		return employeeSiteStatusResult;
	}

	private EmployeeSiteStatusResult addProjectRoles(final EmployeeSiteStatusResult employeeSiteStatusResult,
													 final Collection<Project> projects, final Employee employee) {
		Map<Project, Set<Role>> projectRoles = PicsCollectionUtil.addKeys(roleEntityService
				.getRolesForProjectsAndEmployees(projects, Arrays.asList(employee)), projects);

		employeeSiteStatusResult.setProjectRoles(projectRoles);

		return employeeSiteStatusResult;
	}

	private EmployeeSiteStatusResult addSiteAndCorporateSkills(final EmployeeSiteStatusResult employeeSiteStatusResult,
															   final int siteId, final Collection<Integer> parentSites) {
		employeeSiteStatusResult.setSiteAndCorporateRequiredSkills(
				skillEntityService.getSiteAndCorporateRequiredSkills(siteId, parentSites));

		return employeeSiteStatusResult;
	}

	private EmployeeSiteStatusResult addProjectRequiredSkills(final EmployeeSiteStatusResult employeeSiteStatusResult,
															  final Set<Project> projects) {
		employeeSiteStatusResult.setProjectRequiredSkills(PicsCollectionUtil.addKeys(skillEntityService
				.getRequiredSkillsForProjects(projects), projects));

		return employeeSiteStatusResult;
	}

	private EmployeeSiteStatusResult addRoleSkills(final EmployeeSiteStatusResult employeeSiteStatusResult,
												   final Employee employee, final int siteId) {
		Set<Role> roles = PicsCollectionUtil.mergeCollectionOfCollections(employeeSiteStatusResult.getProjectRoles().values());
		roles.addAll(roleEntityService.getSiteRolesForEmployee(employee, siteId));

		Map<Role, Set<AccountSkill>> roleSkills = PicsCollectionUtil.addKeys(skillEntityService
				.getSkillsForRoles(roles), roles);

		employeeSiteStatusResult.setAllRoleSkills(roleSkills);

		return employeeSiteStatusResult;
	}

	private EmployeeSiteStatusResult addSkillStatuses(final EmployeeSiteStatusResult employeeSiteStatusResult,
													  final Employee employee) {
		Set<AccountSkill> allSkills = new HashSet<>();
		allSkills.addAll(employeeSiteStatusResult.getSiteAndCorporateRequiredSkills());
		allSkills.addAll(PicsCollectionUtil.mergeCollectionOfCollections(employeeSiteStatusResult.getProjectRequiredSkills().values()));
		allSkills.addAll(PicsCollectionUtil.mergeCollectionOfCollections(employeeSiteStatusResult.getAllRoleSkills().values()));

		employeeSiteStatusResult.setSkillStatus(statusCalculatorService.getSkillStatuses(employee, allSkills));

		return employeeSiteStatusResult;
	}

	private EmployeeSiteStatusResult addRoleStatuses(final EmployeeSiteStatusResult employeeSiteStatusResult) {
		Map<Role, Set<AccountSkill>> aggregatedRoleSkills = aggregateRoleSkills(employeeSiteStatusResult);
		Map<Role, List<SkillStatus>> roleStatuses = PicsCollectionUtil.reduceMaps(aggregatedRoleSkills,
				employeeSiteStatusResult.getSkillStatus());

		employeeSiteStatusResult.setRoleStatuses(statusCalculatorService.getOverallStatusPerEntity(roleStatuses));

		return employeeSiteStatusResult;
	}

	private Map<Role, Set<AccountSkill>> aggregateRoleSkills(final EmployeeSiteStatusResult employeeSiteStatusResult) {
		Map<Role, Set<AccountSkill>> aggregatedRoleSkills = new HashMap<>();
		for (Map.Entry<Role, Set<AccountSkill>> roleSkillEntry : employeeSiteStatusResult.getAllRoleSkills().entrySet()) {
			aggregatedRoleSkills.put(roleSkillEntry.getKey(), new HashSet<>(roleSkillEntry.getValue()));
		}

		aggregatedRoleSkills = appendSiteAndCorporateRequiredSkills(aggregatedRoleSkills,
				employeeSiteStatusResult.getSiteAndCorporateRequiredSkills());

		return aggregatedRoleSkills;
	}

	private EmployeeSiteStatusResult addProjectStatuses(final EmployeeSiteStatusResult employeeSiteStatusResult) {
		Map<Project, List<SkillStatus>> projectStatuses = new HashMap<>();

		projectStatuses = addProjectStatuses(projectStatuses, employeeSiteStatusResult.getProjectRoles(),
				employeeSiteStatusResult.getRoleStatuses());
		projectStatuses = addProjectRequiredSkillStatuses(projectStatuses, employeeSiteStatusResult);

		employeeSiteStatusResult.setProjectStatuses(appendProjectsNoSkillsAndNoRoles(
				statusCalculatorService.getOverallStatusPerEntity(projectStatuses), employeeSiteStatusResult));

		return employeeSiteStatusResult;
	}

	// It is possible that a project can exist without roles and without any required skills
	private Map<Project, List<SkillStatus>> filterOutEmptyProjectStatuses(final Map<Project, List<SkillStatus>> projectStatuses) {
		if (MapUtils.isEmpty(projectStatuses)) {
			return projectStatuses;
		}

		Map<Project, List<SkillStatus>> filteredProjectStatusMap = new HashMap<>();
		for (Project project : projectStatuses.keySet()) {
			if (CollectionUtils.isNotEmpty(projectStatuses.get(project))) {
				filteredProjectStatusMap.put(project, projectStatuses.get(project));
			}
		}

		return filteredProjectStatusMap;
	}

	private Map<Project, SkillStatus> appendProjectsNoSkillsAndNoRoles(final Map<Project, SkillStatus> projectSkillStatusMap,
																	   final EmployeeSiteStatusResult employeeSiteStatusResult) {
		Set<Project> projects = employeeSiteStatusResult.getProjects();
		if (CollectionUtils.isEmpty(projects)) {
			return projectSkillStatusMap;
		}

		SkillStatus siteAndCorporateRequiredSkillStatus = statusCalculatorService
				.calculateOverallStatus(getSiteAndCorporateRequiredSkillsList(employeeSiteStatusResult));
		for (Project project : projects) {
			if (!projectSkillStatusMap.containsKey(project)) {
				projectSkillStatusMap.put(project, siteAndCorporateRequiredSkillStatus);
			}
		}

		return projectSkillStatusMap;
	}

	private List<SkillStatus> getSiteAndCorporateRequiredSkillsList(final EmployeeSiteStatusResult employeeSiteStatusResult) {
		Set<AccountSkill> siteAndCorporateRequiredSkills = employeeSiteStatusResult.getSiteAndCorporateRequiredSkills();
		if (CollectionUtils.isEmpty(siteAndCorporateRequiredSkills)) {
			return Collections.emptyList();
		}

		Map<AccountSkill, SkillStatus> skillStatus = employeeSiteStatusResult.getSkillStatus();

		List<SkillStatus> siteAndCorporateSkillStatuses = new ArrayList<>();
		for (AccountSkill accountSkill : siteAndCorporateRequiredSkills) {
			siteAndCorporateSkillStatuses.add(skillStatus.get(accountSkill));
		}

		return siteAndCorporateSkillStatuses;
	}

	private Map<Project, List<SkillStatus>> addProjectRequiredSkillStatuses(final Map<Project, List<SkillStatus>> projectStatuses,
																			final EmployeeSiteStatusResult employeeSiteStatusResult) {
		Map<Project, List<SkillStatus>> projectRequiredSkillStatuses =
				PicsCollectionUtil.reduceMaps(employeeSiteStatusResult.getProjectRequiredSkills(),
						employeeSiteStatusResult.getSkillStatus());

		return PicsCollectionUtil.mergeMapOfLists(projectStatuses, projectRequiredSkillStatuses);
	}

	private Map<Project, List<SkillStatus>> addProjectStatuses(final Map<Project, List<SkillStatus>> projectStatuses,
															   final Map<Project, Set<Role>> projectRoles,
															   final Map<Role, SkillStatus> roleSkillStatuses) {
		return PicsCollectionUtil.mergeMapOfLists(projectStatuses,
				PicsCollectionUtil.reduceMaps(projectRoles, roleSkillStatuses));
	}


	private EmployeeSiteStatusResult addSiteAssignmentRoles(final EmployeeSiteStatusResult employeeSiteStatusResult,
															final int siteId, final Employee employee) {
		employeeSiteStatusResult.setSiteAssignmentRoles(roleEntityService.getSiteRolesForEmployee(employee, siteId));

		return employeeSiteStatusResult;
	}

	private <K> Map<K, Set<AccountSkill>> appendSiteAndCorporateRequiredSkills(final Map<K, Set<AccountSkill>> skillMap,
																			   final Set<AccountSkill> siteAndCorporateRequiredSkills) {
		if (MapUtils.isEmpty(skillMap)) {
			return Collections.emptyMap();
		}

		for (K key : skillMap.keySet()) {
			Set<AccountSkill> skills = skillMap.get(key);
			skills.addAll(siteAndCorporateRequiredSkills);
		}

		return skillMap;
	}
}
