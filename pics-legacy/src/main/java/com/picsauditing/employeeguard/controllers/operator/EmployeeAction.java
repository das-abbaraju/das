package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.viewmodel.employee.OperatorEmployeeModel;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModelFactory;
import org.springframework.beans.factory.annotation.Autowired;

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

//		json = operatorEmployeeModel.toJSON();

		return JSON;
	}

	private OperatorEmployeeModel buildOperatorEmployeeModel() {
		Employee employeeEntity = employeeService.findEmployee(id);

		Map<Role, Set<AccountSkill>> roleSkillMap = getRoleSkillMap(employeeEntity);
		Map<Project, Set<AccountSkill>> projectSkillMap = getProjectSkillMap(employeeEntity);

		Map<Role, SkillStatus> roleStatusMap = statusCalculatorService.getSkillStatusPerEntity(employeeEntity, roleSkillMap);
		Map<Project, SkillStatus> projectStatusMap = statusCalculatorService.getSkillStatusPerEntity(employeeEntity, projectSkillMap);
		SkillStatus overallStatus = statusCalculatorService.calculateOverallStatus(Utilities.mergeCollections(roleStatusMap.values(), projectStatusMap.values()));

		Set<AccountSkill> merged = Utilities.mergeCollectionOfCollections(roleSkillMap.values(), projectSkillMap.values());
		Map<AccountSkill, SkillStatus> skillStatusMap = statusCalculatorService.getSkillStatuses(employeeEntity, merged);

		return ViewModelFactory.getOperatorEmployeeModelFactory().create(
				employeeEntity, projectStatusMap, roleStatusMap, skillStatusMap, overallStatus);
	}

	private Map<Role, Set<AccountSkill>> getRoleSkillMap(Employee employeeEntity) {
		Set<Role> employeeRoles = roleService.getEmployeeRolesForSite(siteId, employeeEntity);
		return skillService.getSkillsForRoles(siteId, employeeRoles);
	}

	private Map<Project, Set<AccountSkill>> getProjectSkillMap(Employee employeeEntity) {
		Map<Project, Set<Role>> projectRoleMap = projectService.getProjectRolesForEmployee(siteId, employeeEntity);
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
