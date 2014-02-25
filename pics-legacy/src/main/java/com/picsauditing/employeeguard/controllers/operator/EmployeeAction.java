package com.picsauditing.employeeguard.controllers.operator;

import com.google.gson.Gson;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.models.CompanyEmployeeStatusModel;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.employee.OperatorEmployeeModel;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModelFactory;
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

	private OperatorEmployeeModel operatorEmployeeModel;

	public String show() {
		operatorEmployeeModel = buildOperatorEmployeeModel();

		// Screw you simple json!
		jsonString = new Gson().toJson(operatorEmployeeModel);
		return JSON_STRING;
	}

	private CompanyEmployeeStatusModel buildModel() {
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

		List<AccountSkill> siteSkills = skillService.getRequiredSkillsForSiteAndCorporates(permissions.getAccountId());

//		ModelFactory.getCompanyEmployeeStatusModelFactory().create(employee,
//				ModelFactory.getCompanyModelFactory().create(),
//				ModelFactory.getProjectStatusModelFactory().create(),
//				ModelFactory.getRoleStatusModelFactory().create(),
//				overallStatus
//		);

		return null;
	}

	private OperatorEmployeeModel buildOperatorEmployeeModel() {
		Employee employee = employeeService.findEmployee(id);

		Map<Project, Set<Role>> projectRoleMap = projectService.getProjectRolesForEmployee(siteId, employee);

		Map<Role, Set<AccountSkill>> roleSkillMap = getRoleSkillMap(employee, projectRoleMap);
		Map<Project, Set<AccountSkill>> projectSkillMap = getProjectSkillMap(projectRoleMap);

		Map<Role, SkillStatus> roleStatusMap = statusCalculatorService.getSkillStatusPerEntity(employee, roleSkillMap);
		Map<Project, SkillStatus> projectStatusMap = statusCalculatorService.getSkillStatusPerEntity(employee, projectSkillMap);
		SkillStatus overallStatus = statusCalculatorService.calculateOverallStatus(Utilities.mergeCollections(roleStatusMap.values(), projectStatusMap.values()));

		Set<AccountSkill> skills = Utilities.mergeCollectionOfCollections(roleSkillMap.values(), projectSkillMap.values());
		Map<AccountSkill, SkillStatus> skillStatusMap = statusCalculatorService.getSkillStatuses(employee, skills);

		Map<Integer, AccountModel> accounts = accountService.getContractorsForEmployee(employee);

		List<AccountSkill> siteSkills = skillService.getRequiredSkillsForSiteAndCorporates(permissions.getAccountId());



		return ViewModelFactory.getOperatorEmployeeModelFactory().create(employee,
				ViewModelFactory.getIdNameTitleModelFactory().create(employee, accounts),
				ViewModelFactory.getProjectDetailModelFactory().create(projectStatusMap),
				ViewModelFactory.getRoleModelFactory().create(roleStatusMap),
				ViewModelFactory.getOperatorEmployeeSkillModelFactory().create(skillStatusMap, siteSkills, roleSkillMap.keySet()),
				overallStatus);
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
