package com.picsauditing.employeeguard.controllers.operator;

import com.google.gson.Gson;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class EmployeeAction extends PicsRestActionSupport {

	@Autowired
	private AccountService accountService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;
	@Autowired
	private SkillService skillService;

	public String show() {
		CompanyEmployeeStatusModel companyEmployeeStatusModel = buildCompanyEmployeeStatusModel();

		jsonString = new Gson().toJson(companyEmployeeStatusModel);

		return JSON_STRING;
	}

	private CompanyEmployeeStatusModel buildCompanyEmployeeStatusModel() {
		Employee employee = employeeService.findEmployee(id);
		int siteId = permissions.getAccountId();

		Map<Project, Set<Role>> projectRoleMap = projectService.getProjectRolesForEmployee(siteId, employee);

		Map<Role, Set<AccountSkill>> roleSkillMap = getRoleSkillMap(siteId, employee, projectRoleMap);
		Map<Project, Set<AccountSkill>> projectSkillMap = getProjectSkillMap(siteId, projectRoleMap);

		Map<Role, SkillStatus> roleStatusMap = statusCalculatorService.getSkillStatusPerEntity(employee, roleSkillMap);
		Map<Project, SkillStatus> projectStatusMap = statusCalculatorService.getSkillStatusPerEntity(employee, projectSkillMap);
		SkillStatus overallStatus = statusCalculatorService.calculateOverallStatus(Utilities.mergeCollections(roleStatusMap.values(),
				projectStatusMap.values()));

		Map<Integer, Employee> employeesAssignedToSite = employeeService
				.getAccountToEmployeeMapForEmployeesAssignedToSiteByEmployeeProfile(accountService.getContractorIds(siteId),
						siteId, employee);
		Map<Integer, AccountModel> accounts = accountService.getContractorsForEmployeesMap(new ArrayList<>(employeesAssignedToSite.values()));

		Set<AccountSkill> skills = Utilities.mergeCollectionOfCollections(roleSkillMap.values(), projectSkillMap.values());
		Map<AccountSkill, SkillStatus> skillStatusMap = statusCalculatorService.getSkillStatuses(employee, skills);

		Map<Integer, List<SkillStatusModel>> roleIdToSkillStatusModelMap = getRoleIdToSkillStatusModelMap(roleSkillMap, skillStatusMap);
		List<RoleStatusModel> roleStatusModels = getRoleStatusModels(roleSkillMap, roleStatusMap, roleIdToSkillStatusModelMap);

		List<ProjectStatusModel> projectStatusModels = getProjectStatusModels(
				projectRoleMap,
				projectSkillMap,
				roleStatusMap,
				projectStatusMap,
				skillStatusMap,
				roleIdToSkillStatusModelMap);

		List<EmploymentInfoModel> companyModels = ModelFactory.getEmploymentInfoModelFactory()
				.create(new HashSet<>(accounts.values()), employeesAssignedToSite);

		return ModelFactory.getCompanyEmployeeStatusModelFactory().create(employee, companyModels, projectStatusModels,
				roleStatusModels, overallStatus);
	}

	private List<ProjectStatusModel> getProjectStatusModels(Map<Project, Set<Role>> projectRoleMap, Map<Project, Set<AccountSkill>> projectSkillMap, Map<Role, SkillStatus> roleStatusMap, Map<Project, SkillStatus> projectStatusMap, Map<AccountSkill, SkillStatus> skillStatusMap, Map<Integer, List<SkillStatusModel>> roleIdToSkillStatusModelMap) {
		Map<Integer, List<SkillStatusModel>> projectIdToSkillStatusModelMap = getProjectIdToSkillStatusModelMap(projectSkillMap, skillStatusMap);
		Map<Integer, List<RoleStatusModel>> projectIdToRoleStatusModelMap = getProjectIdToRoleStatusModelMap(projectRoleMap, roleStatusMap, roleIdToSkillStatusModelMap);
		return getProjectStatusModels(projectRoleMap, projectStatusMap, projectIdToSkillStatusModelMap, projectIdToRoleStatusModelMap);
	}

	private Map<Integer, List<SkillStatusModel>> getRoleIdToSkillStatusModelMap(Map<Role, Set<AccountSkill>> roleSkillMap, Map<AccountSkill, SkillStatus> skillStatusMap) {
		return ModelFactory.getSkillStatusModelFactory().createRoleIdToSkillStatusModelMap(roleSkillMap, skillStatusMap);
	}

	private Map<Integer, List<SkillStatusModel>> getProjectIdToSkillStatusModelMap(Map<Project, Set<AccountSkill>> projectSkillMap, Map<AccountSkill, SkillStatus> skillStatusMap) {
		return ModelFactory.getSkillStatusModelFactory().createProjectIdToSkillStatusModelMap(projectSkillMap, skillStatusMap);
	}

	private Map<Integer, List<RoleStatusModel>> getProjectIdToRoleStatusModelMap(Map<Project, Set<Role>> projectRoleMap, Map<Role, SkillStatus> roleStatusMap, Map<Integer, List<SkillStatusModel>> roleIdToSkillStatusModelMap) {
		return ModelFactory.getRoleStatusModelFactory().createProjectIdToRoleModelMap(
				projectRoleMap.keySet(),
				projectRoleMap,
				roleIdToSkillStatusModelMap,
				roleStatusMap);
	}

	private List<ProjectStatusModel> getProjectStatusModels(Map<Project, Set<Role>> projectRoleMap, Map<Project, SkillStatus> projectStatusMap, Map<Integer, List<SkillStatusModel>> projectIdToSkillStatusModelMap, Map<Integer, List<RoleStatusModel>> projectIdToRoleStatusModelMap) {
		return ModelFactory.getProjectStatusModelFactory().create(
				projectRoleMap.keySet(),
				projectIdToRoleStatusModelMap,
				projectIdToSkillStatusModelMap,
				projectStatusMap);
	}

	private List<RoleStatusModel> getRoleStatusModels(Map<Role, Set<AccountSkill>> roleSkillMap, Map<Role, SkillStatus> roleStatusMap, Map<Integer, List<SkillStatusModel>> roleIdToSkillStatusModelMap) {
		return ModelFactory.getRoleStatusModelFactory().create(
				roleSkillMap.keySet(),
				roleIdToSkillStatusModelMap,
				roleStatusMap);
	}

	private Map<Role, Set<AccountSkill>> getRoleSkillMap(int siteId, Employee employeeEntity, Map<Project, Set<Role>> projectRoleMap) {
		Set<Role> employeeRoles = roleService.getEmployeeRolesForSite(siteId, employeeEntity);
		employeeRoles.addAll(Utilities.flattenCollectionOfCollection(projectRoleMap.values()));
		return skillService.getSkillsForRoles(siteId, employeeRoles);
	}

	private Map<Project, Set<AccountSkill>> getProjectSkillMap(int siteId, Map<Project, Set<Role>> projectRoleMap) {
		return skillService.getAllProjectSkillsForEmployeeProjectRoles(siteId,
				projectRoleMap);
	}
}
