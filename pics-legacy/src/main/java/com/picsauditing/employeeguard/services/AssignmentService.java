package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.services.external.AccountService;
import com.picsauditing.employeeguard.services.processor.ProjectAssignmentDataSet;
import com.picsauditing.employeeguard.services.processor.ProjectAssignmentProcess;
import com.picsauditing.employeeguard.services.processor.RoleAssignmentProcess;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class AssignmentService {

	@Autowired
	private AccountService accountService;
	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private ProjectEntityService projectEntityService;
	@Autowired
	private RoleEntityService roleEntityService;
	@Autowired
	private SkillEntityService skillEntityService;

	public Map<Project, Map<Employee, Set<AccountSkill>>> getEmployeeSkillsForProjectsUnderSite(final int siteId) {

		List<Project> siteProjects = projectEntityService.getAllProjectsForSite(siteId);
		Map<Project, Set<Employee>> projectEmployees = employeeEntityService.getEmployeesByProjects(siteProjects);
		Map<Role, Set<Employee>> roleEmployees = getEmployeesByRole(siteId, siteProjects);
		Map<Project, Set<AccountSkill>> projectRequiredSkill = skillEntityService
				.getRequiredSkillsForProjects(siteProjects);
		Map<Project, Set<Role>> projectRoles = roleEntityService.getRolesForProjects(siteProjects);
		Map<Role, Set<AccountSkill>> projectRoleSkills = skillEntityService
				.getSkillsForRoles(PicsCollectionUtil.mergeCollectionOfCollections(projectRoles.values()));
		Set<AccountSkill> siteAndCorporateRequiredSkills = skillEntityService
				.getSiteRequiredSkills(siteId, accountService.getTopmostCorporateAccountIds(siteId));

		ProjectAssignmentDataSet dataSet = new ProjectAssignmentDataSet.Builder()
				.projectEmployees(projectEmployees)
				.projectRequiredSkills(projectRequiredSkill)
				.projectRoles(projectRoles)
				.projectRoleSkills(projectRoleSkills)
				.projects(siteProjects)
				.roleEmployees(roleEmployees)
				.siteAndCorporateRequiredSkills(siteAndCorporateRequiredSkills)
				.build();

		return new ProjectAssignmentProcess().getProjectSkillsForEmployees(dataSet);
	}

	/**
	 * Retrieves a map of employees to both site and project roles for the specific site
	 *
	 * @param siteId
	 * @param projects
	 * @return
	 */
	public Map<Role, Set<Employee>> getEmployeesByRole(final int siteId, final Collection<Project> projects) {
		return new RoleAssignmentProcess().getCorporateRoleEmployees(
				employeeEntityService.getEmployeesByProjectRoles(projects),
				employeeEntityService.getEmployeesBySiteRoles(Arrays.asList(siteId)));
	}

	public Map<Employee, Set<AccountSkill>> getEmployeeSkillsForSite(final int siteId) {
		Set<Employee> employeesAssignedToSite = getEmployeesAssignedToSite(siteId);

		Map<Employee, Set<AccountSkill>> employeeSkillsForProjects = getEmployeeSkillsForProjects(siteId, employeesAssignedToSite);
		Map<Employee, Set<AccountSkill>> employeeSkillsForRole = getEmployeeSkillsForRoles(siteId);

		Map<Employee, Set<AccountSkill>> allEmployeeRequiredSkills = PicsCollectionUtil.mergeMapOfSets(employeeSkillsForProjects, employeeSkillsForRole);
		List<Integer> accountIdsInHierarchy = accountService.getTopmostCorporateAccountIds(siteId);

		return appendSiteAndCorporateSkills(allEmployeeRequiredSkills,
				skillEntityService.getSiteRequiredSkills(siteId, accountIdsInHierarchy));
	}

	private Map<Employee, Set<AccountSkill>> getEmployeeSkillsForProjects(int siteId, Set<Employee> employeesAssignedToSite) {
		Map<Employee, Set<Project>> employeeProjects = projectEntityService.getProjectsForEmployeesBySiteId(employeesAssignedToSite, siteId);
		Set<Project> allProjects = PicsCollectionUtil.extractAndFlattenValuesFromMap(employeeProjects);
		Map<Project, Set<AccountSkill>> projectSkills = skillEntityService.getRequiredSkillsForProjects(allProjects);
		return getKeyToSetOfValues(employeeProjects, projectSkills);
	}

	private Map<Employee, Set<AccountSkill>> getEmployeeSkillsForRoles(int siteId) {
		Map<Employee, Set<Role>> employeeRoles = getAllEmployeeRolesForSite(siteId);
		Set<Role> allRoles = PicsCollectionUtil.extractAndFlattenValuesFromMap(employeeRoles);
		Map<Role, Set<AccountSkill>> roleSkills = skillEntityService.getSkillsForRoles(allRoles);
		return getKeyToSetOfValues(employeeRoles, roleSkills);
	}

	private <KEY, ENTITY, VALUE> Map<KEY, Set<VALUE>> getKeyToSetOfValues(Map<KEY, Set<ENTITY>> keyToEntities, Map<ENTITY, Set<VALUE>> entityToValues) {
		Map<KEY, Set<VALUE>> keyValues = new HashMap<>();
		for (Map.Entry<KEY, Set<ENTITY>> keyEntityEntry : keyToEntities.entrySet()) {
			KEY key = keyEntityEntry.getKey();

			keyValues.put(key, new HashSet<VALUE>());

			for (ENTITY entity : keyEntityEntry.getValue()) {
				if (entityToValues.containsKey(entity)) {
					keyValues.get(key).addAll(entityToValues.get(entity));
				}
			}
		}

		return keyValues;
	}

	private <E> Map<E, Set<AccountSkill>> appendSiteAndCorporateSkills(final Map<E, Set<AccountSkill>> entitySkillMap,
																	   final Collection<AccountSkill> siteAndCorporateRequiredSkills) {
		if (CollectionUtils.isEmpty(siteAndCorporateRequiredSkills)) {
			return entitySkillMap;
		}

		for (E entity : entitySkillMap.keySet()) {
			if (entitySkillMap.containsKey(entity)) {
				entitySkillMap.get(entity).addAll(siteAndCorporateRequiredSkills);
			}
		}

		return entitySkillMap;
	}

	public Set<Employee> getEmployeesAssignedToSite(final int siteId) {
		return getEmployeesAssignedToSites(Arrays.asList(siteId));
	}

	public Set<Employee> getEmployeesAssignedToSites(final Collection<Integer> siteIds) {
		List<Employee> employeesAssignedToSite = getSiteContractorEmployees(siteIds);

		final Map<Employee, Set<Project>> employeeProjects = projectEntityService.getProjectsForEmployeesBySiteIds(employeesAssignedToSite, siteIds);
		final Map<Employee, Set<Role>> employeeRoles = roleEntityService.getSiteRolesForEmployees(employeesAssignedToSite, siteIds);

		return new HashSet<Employee>() {{
			addAll(employeeProjects.keySet());
			addAll(employeeRoles.keySet());
		}};
	}

	private List<Employee> getSiteContractorEmployees(final Collection<Integer> siteIds) {
		List<Integer> contractorIds = accountService.getContractorIds(siteIds);
		return employeeEntityService.getEmployeesAssignedToSites(contractorIds, siteIds);
	}

	public Map<Employee, Set<Role>> getAllEmployeeRolesForSite(final int siteId) {
		List<Employee> employeesAssignedToSite = getSiteContractorEmployees(Arrays.asList(siteId));

		final Map<Employee, Set<Role>> employeeProjectRoles = roleEntityService.getProjectRolesForEmployees(employeesAssignedToSite, siteId);
		final Map<Employee, Set<Role>> employeeRoles = roleEntityService.getSiteRolesForEmployees(employeesAssignedToSite, siteId);

		return PicsCollectionUtil.mergeMapOfSets(employeeProjectRoles, employeeRoles);
	}

	public Map<Project, Set<Employee>> getEmployeesAssignedToProjects(final int siteId) {
		return getEmployeesAssignedToProjects(Arrays.asList(siteId));
	}

	public Map<Project, Set<Employee>> getEmployeesAssignedToProjects(final Collection<Integer> siteIds) {
		if (CollectionUtils.isEmpty(siteIds)) {
			return Collections.emptyMap();
		}

		Collection<Project> projects = projectEntityService.getProjectsBySiteIds(siteIds);
		return employeeEntityService.getEmployeesByProjects(projects);
	}

	public void assignEmployeeToProject(final Project project, final int roleId, final int employeeId,
										final EntityAuditInfo entityAuditInfo) {
		projectEntityService.assignEmployeeToProjectRole(project, roleId, employeeId, entityAuditInfo);

		assignEmployeeToSiteRole(project.getAccountId(), roleId, employeeId, entityAuditInfo);
	}

	public void unassignEmployeeFromProject(final Project project, final int roleId, final int employeeId) {
		projectEntityService.removeEmployeeFromProjectRole(project, roleId, employeeId);
	}

	public void assignEmployeeToSiteRole(final int siteId, final int roleId, final int employeeId,
										 final EntityAuditInfo entityAuditInfo) {
		roleEntityService.addEmployeeSiteAssignment(siteId, roleId, employeeId, entityAuditInfo);
	}

	public void unassignEmployeeFromSiteRole(final int siteId, final int roleId, final int employeeId) {
		roleEntityService.deleteEmployeeSiteAssignment(siteId, roleId, employeeId);

		projectEntityService.deleteEmployeeFromProjectRole(siteId, roleId, employeeId);
	}

	public void unassignEmployeeFromSite(final int siteId, final int employeeId) {
		roleEntityService.deleteAllEmployeeSiteAssignmentsForSite(siteId, employeeId);

		projectEntityService.unassignEmployeeFromAllProjectsOnSite(siteId, employeeId);
	}

	public Set<Integer> findAllEmployeeSiteAssignments(final Employee employee) {
		return employeeEntityService.getAllSiteIdsForEmployeeAssignments(employee);
	}
}
