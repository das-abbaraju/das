package com.picsauditing.employeeguard.process;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
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

		return employeeSiteStatusResult;
	}

	private EmployeeSiteStatusResult addProjectRoles(final EmployeeSiteStatusResult employeeSiteStatusResult,
													 final Collection<Project> projects, final Employee employee) {
		employeeSiteStatusResult.setProjectRoles(
				roleEntityService.getRolesForProjectsAndEmployees(projects, Arrays.asList(employee)));

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
		employeeSiteStatusResult.setProjectRequiredSkills(skillEntityService.getRequiredSkillsForProjects(projects));

		return employeeSiteStatusResult;
	}

	private EmployeeSiteStatusResult addRoleSkills(final EmployeeSiteStatusResult employeeSiteStatusResult,
												   final Employee employee, final int siteId) {
		Set<Role> roles = PicsCollectionUtil.mergeCollectionOfCollections(employeeSiteStatusResult.getProjectRoles().values());
		roles.addAll(roleEntityService.getSiteRolesForEmployee(employee, siteId));

		employeeSiteStatusResult.setRoleSkills(skillEntityService.getSkillsForRoles(roles));

		return employeeSiteStatusResult;
	}

	private EmployeeSiteStatusResult addSkillStatuses(final EmployeeSiteStatusResult employeeSiteStatusResult,
													  final Employee employee) {
		Set<AccountSkill> allSkills = new HashSet<>();
		allSkills.addAll(employeeSiteStatusResult.getSiteAndCorporateRequiredSkills());
		allSkills.addAll(PicsCollectionUtil.mergeCollectionOfCollections(employeeSiteStatusResult.getProjectRequiredSkills().values()));
		allSkills.addAll(PicsCollectionUtil.mergeCollectionOfCollections(employeeSiteStatusResult.getRoleSkills().values()));

		employeeSiteStatusResult.setSkillStatus(statusCalculatorService.getSkillStatuses(employee, allSkills));

		return employeeSiteStatusResult;
	}

	private EmployeeSiteStatusResult addRoleStatuses(final EmployeeSiteStatusResult employeeSiteStatusResult) {
		Map<Role, Set<AccountSkill>> aggregatedRoleSkills = aggregateRoleSkills(employeeSiteStatusResult);
		Map<Role, List<SkillStatus>> roleStatuses = convertToSkillStatusMap(aggregatedRoleSkills,
				employeeSiteStatusResult.getSkillStatus());

		employeeSiteStatusResult.setRoleStatuses(statusCalculatorService.getOverallStatusPerEntity(roleStatuses));

		return employeeSiteStatusResult;
	}

	private Map<Role, Set<AccountSkill>> aggregateRoleSkills(final EmployeeSiteStatusResult employeeSiteStatusResult) {
		Map<Role, Set<AccountSkill>> roleSkills = new HashMap<>(employeeSiteStatusResult.getRoleSkills());
		roleSkills = appendSiteAndCorporateRequiredSkills(roleSkills, employeeSiteStatusResult.getSiteAndCorporateRequiredSkills());
		return roleSkills;
	}

	private EmployeeSiteStatusResult addProjectStatuses(final EmployeeSiteStatusResult employeeSiteStatusResult) {
		return null;  //To change body of created methods use File | Settings | File Templates.
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

	private <K> Map<K, List<SkillStatus>> convertToSkillStatusMap(final Map<K, Set<AccountSkill>> skillMap,
																  final Map<AccountSkill, SkillStatus> statusMap) {
		if (MapUtils.isEmpty(skillMap) || MapUtils.isEmpty(skillMap)) {
			return Collections.emptyMap();
		}

		Map<K, List<SkillStatus>> skillStatuses = new HashMap<>();
		for (K key : skillMap.keySet()) {

			ArrayList<SkillStatus> statuses = new ArrayList<>();
			for (AccountSkill accountSkill : skillMap.get(key)) {
				statuses.add(statusMap.get(accountSkill));
			}

			skillStatuses.put(key, statuses);
		}

		return skillStatuses;
	}
}
