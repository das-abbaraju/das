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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

	private int siteId;

	public String show() {
		CompanyEmployeeStatusModel companyEmployeeStatusModel = buildCompanyEmployeeStatusModel();

		// Screw you simple json!
		jsonString = new Gson().toJson(companyEmployeeStatusModel);
		return JSON_STRING;
	}

	private CompanyEmployeeStatusModel buildCompanyEmployeeStatusModel() {
		Employee employee = employeeService.findEmployee(id);

		Map<Project, Set<Role>> projectRoleMap = projectService.getProjectRolesForEmployee(siteId, employee);

		Map<Role, Set<AccountSkill>> roleSkillMap = getRoleSkillMap(employee, projectRoleMap);
		Map<Project, Set<AccountSkill>> projectSkillMap = getProjectSkillMap(projectRoleMap);

		Map<Role, SkillStatus> roleStatusMap = statusCalculatorService.getSkillStatusPerEntity(employee, roleSkillMap);
		Map<Project, SkillStatus> projectStatusMap = statusCalculatorService.getSkillStatusPerEntity(employee, projectSkillMap);
		SkillStatus overallStatus = statusCalculatorService.calculateOverallStatus(Utilities.mergeCollections(roleStatusMap.values(), projectStatusMap.values()));

		List<Employee> employeesAssignedToSite = employeeService
				.getEmployeesAssignedToSiteByEmployeeProfile(accountService.getContractorIds(siteId), siteId, employee);
		Map<Integer, AccountModel> accounts = accountService.getContractorsForEmployeesMap(employeesAssignedToSite);

		Set<AccountSkill> skills = Utilities.mergeCollectionOfCollections(roleSkillMap.values(), projectSkillMap.values());
		Map<AccountSkill, SkillStatus> skillStatusMap = statusCalculatorService.getSkillStatuses(employee, skills);

		Map<Integer, List<SkillStatusModel>> roleIdToSkillStatusModelMap =
				ModelFactory.getSkillStatusModelFactory().createRoleIdToSkillStatusModelMap(roleSkillMap, skillStatusMap);
		Map<Integer, List<SkillStatusModel>> projectIdToSkillStatusModelMap =
				ModelFactory.getSkillStatusModelFactory().createProjectIdToSkillStatusModelMap(projectSkillMap, skillStatusMap);
		Map<Integer, List<RoleStatusModel>> projectIdToRoleStatusModelMap =
				ModelFactory.getRoleStatusModelFactory().createProjectIdToRoleModelMap(
						projectRoleMap.keySet(),
						projectRoleMap,
						roleIdToSkillStatusModelMap,
						roleStatusMap);
		List<ProjectStatusModel> projectStatusModels = ModelFactory.getProjectStatusModelFactory().create(
				projectRoleMap.keySet(),
				projectIdToRoleStatusModelMap,
				projectIdToSkillStatusModelMap,
				projectStatusMap);
		List<RoleStatusModel> roleStatusModels = ModelFactory.getRoleStatusModelFactory().create(
				roleSkillMap.keySet(),
				roleIdToSkillStatusModelMap, roleStatusMap);

		List<CompanyModel> companyModels = ModelFactory.getCompanyModelFactory().create(accounts);
		return ModelFactory.getCompanyEmployeeStatusModelFactory().create(employee, companyModels, projectStatusModels, roleStatusModels, overallStatus);
	}

	private Map<Role, Set<AccountSkill>> getRoleSkillMap(Employee employeeEntity, Map<Project, Set<Role>> projectRoleMap) {
		Set<Role> employeeRoles = roleService.getEmployeeRolesForSite(siteId, employeeEntity);
		employeeRoles.addAll(Utilities.flattenCollectionOfCollection(projectRoleMap.values()));
		return skillService.getSkillsForRoles(siteId, employeeRoles);
	}

	private Map<Project, Set<AccountSkill>> getProjectSkillMap(Map<Project, Set<Role>> projectRoleMap) {
		return skillService.getAllProjectSkillsForEmployeeProjectRoles(siteId,
				projectRoleMap);
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}
}
