package com.picsauditing.employeeguard.controllers.operator;

import com.google.gson.Gson;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.process.EmployeeSiteStatusProcess;
import com.picsauditing.employeeguard.process.EmployeeSiteStatusResult;
import com.picsauditing.employeeguard.services.external.AccountService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class EmployeeAction extends PicsRestActionSupport {

	@Autowired
	private AccountService accountService;
	@Autowired
	private EmployeeSiteStatusProcess employeeSiteStatusProcess;

	public String show() {
		OperatorEmployeeModel operatorEmployeeModel = buildOperatorEmployeeStatusModel();

		jsonString = new Gson().toJson(operatorEmployeeModel);

		return JSON_STRING;
	}

	private OperatorEmployeeModel buildOperatorEmployeeStatusModel() {
		int siteId = permissions.getAccountId();
		List<Integer> parentAccounts = accountService.getTopmostCorporateAccountIds(siteId);

		EmployeeSiteStatusResult employeeSiteStatusResult = employeeSiteStatusProcess
				.getEmployeeSiteStatusResult(getNumericId(), siteId, parentAccounts);

		return ModelFactory.getOperatorEmployeeModelFactory().create(
				getRequiredSkills(employeeSiteStatusResult),
				getProjectStatusModels(employeeSiteStatusResult),
				getRoleStatusModels(employeeSiteStatusResult));
	}

	private RequiredSkills getRequiredSkills(EmployeeSiteStatusResult employeeSiteStatusResult) {
		return ModelFactory.getSkillStatusModelFactory().createRequiredSkills(employeeSiteStatusResult.getSiteAndCorporateRequiredSkills(),
				employeeSiteStatusResult.getSkillStatus());
	}

	private List<RoleStatusModel> getRoleStatusModels(final EmployeeSiteStatusResult employeeSiteStatusResult) {
//		return ModelFactory.getRoleStatusModelFactory().c
		return null;
	}

	private List<ProjectStatusModel> getProjectStatusModels(final EmployeeSiteStatusResult employeeSiteStatusResult) {
//		List<ProjectStatusModel> projectStatusModels = ModelFactory.getProjectStatusModelFactory().create();
//
//		return projectStatusModels;

		return null;
	}

	private Map<Integer, List<SkillStatusModel>> getSkillStatusModelsForProjects(final EmployeeSiteStatusResult employeeSiteStatusResult) {
//		List<SkillStatusModel> skillStatusModels = ModelFactory.getSkillStatusModelFactory()
//				.create();

		return null;
	}

	private Map<Integer, List<SkillStatusModel>> getSkillStatusModelsForRoles(final EmployeeSiteStatusResult employeeSiteStatusResult) {
//		List<SkillStatusModel> skillStatusModels = ModelFactory.getSkillStatusModelFactory()
//				.

		return null;
	}

//	private List<ProjectStatusModel> getProjectStatusModels(Map<Project, Set<Role>> projectRoleMap, Map<Project, Set<AccountSkill>> projectSkillMap, Map<Role, SkillStatus> roleStatusMap, Map<Project, SkillStatus> projectStatusMap, Map<AccountSkill, SkillStatus> skillStatusMap, Map<Integer, List<SkillStatusModel>> roleIdToSkillStatusModelMap) {
//		Map<Integer, List<SkillStatusModel>> projectIdToSkillStatusModelMap = getProjectIdToSkillStatusModelMap(projectSkillMap, skillStatusMap);
//		Map<Integer, List<RoleStatusModel>> projectIdToRoleStatusModelMap = getProjectIdToRoleStatusModelMap(projectRoleMap, roleStatusMap, roleIdToSkillStatusModelMap);
//		return getProjectStatusModels(projectRoleMap, projectStatusMap, projectIdToSkillStatusModelMap, projectIdToRoleStatusModelMap);
//	}
//
//	private Map<Integer, List<SkillStatusModel>> getRoleIdToSkillStatusModelMap(Map<Role, Set<AccountSkill>> roleSkillMap, Map<AccountSkill, SkillStatus> skillStatusMap) {
//		return ModelFactory.getSkillStatusModelFactory().createRoleIdToSkillStatusModelMap(roleSkillMap, skillStatusMap);
//	}
//
//	private Map<Integer, List<SkillStatusModel>> getProjectIdToSkillStatusModelMap(Map<Project, Set<AccountSkill>> projectSkillMap, Map<AccountSkill, SkillStatus> skillStatusMap) {
//		return ModelFactory.getSkillStatusModelFactory().createProjectIdToSkillStatusModelMap(projectSkillMap, skillStatusMap);
//	}
//
//	private Map<Integer, List<RoleStatusModel>> getProjectIdToRoleStatusModelMap(Map<Project, Set<Role>> projectRoleMap, Map<Role, SkillStatus> roleStatusMap, Map<Integer, List<SkillStatusModel>> roleIdToSkillStatusModelMap) {
//		return ModelFactory.getRoleStatusModelFactory().createProjectIdToRoleModelMap(
//				projectRoleMap.keySet(),
//				projectRoleMap,
//				roleIdToSkillStatusModelMap,
//				roleStatusMap);
//	}
//
//	private List<ProjectStatusModel> getProjectStatusModels(Map<Project, Set<Role>> projectRoleMap,
//															Map<Project, SkillStatus> projectStatusMap,
//															Map<Integer, List<SkillStatusModel>> projectIdToSkillStatusModelMap,
//															Map<Integer, List<RoleStatusModel>> projectIdToRoleStatusModelMap) {
////		return ModelFactory.getProjectStatusModelFactory().create(
////				projectRoleMap.keySet(),
////				projectIdToRoleStatusModelMap,
////				projectIdToSkillStatusModelMap,
////				projectStatusMap);
//
//		return null;
//	}
//
//	private List<RoleStatusModel> getRoleStatusModels(Map<Role, Set<AccountSkill>> roleSkillMap, Map<Role, SkillStatus> roleStatusMap, Map<Integer, List<SkillStatusModel>> roleIdToSkillStatusModelMap) {
//		return ModelFactory.getRoleStatusModelFactory().create(
//				roleSkillMap.keySet(),
//				roleIdToSkillStatusModelMap,
//				roleStatusMap);
//	}
//
//	private Map<Role, Set<AccountSkill>> getRoleSkillMap(int siteId, Employee employeeEntity, Map<Project, Set<Role>> projectRoleMap) {
//		Set<Role> employeeRoles = roleService.getEmployeeRolesForSite(siteId, employeeEntity);
//		employeeRoles.addAll(PicsCollectionUtil.flattenCollectionOfCollection(projectRoleMap.values()));
//		return skillService.getSkillsForRoles(siteId, employeeRoles);
//	}
//
//	private Map<Project, Set<AccountSkill>> getProjectSkillMap(int siteId, Map<Project, Set<Role>> projectRoleMap) {
//		return skillService.getAllProjectSkillsForEmployeeProjectRoles(siteId,
//				projectRoleMap);
//	}
}
