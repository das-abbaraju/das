package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.employee.EmployeeModel;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModelFactory;
import com.picsauditing.employeeguard.viewmodel.operator.EmployeeNav;
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

	private int siteId;

	private EmployeeModel employee;
	private EmployeeNav employeeNav;


	public String show() {
		Employee employeeEntity = employeeService.findEmployee(id);

		Map<Integer, AccountModel> contractors = accountService.getContractorsForEmployee(employeeEntity);
		employee = ViewModelFactory.getEmployeeModelFactory().create(employeeEntity, contractors);

		employeeNav = getEmployeeNav(employeeEntity);

		return SHOW;
	}

	private EmployeeNav getEmployeeNav(final Employee employeeEntity) {
		Map<Project, Set<Role>> projectRoleMap = projectService.getProjectRolesForEmployee(siteId, employeeEntity);
		Map<Role, SkillStatus> roleOverallStatusMap = getRoleSkillStatusMap(employeeEntity, projectRoleMap);
		Map<Project, SkillStatus> projectOverallStatusMap = getProjectSkillStatusMap(employeeEntity, projectRoleMap);
		SkillStatus overallStatus = getOverallSkillStatus(roleOverallStatusMap, projectOverallStatusMap);

		return ViewModelFactory.getEmployeeNavFactory().create(overallStatus,
				ViewModelFactory.getNavItemFactory().createForRoles(roleOverallStatusMap),
				ViewModelFactory.getNavItemFactory().createForProjects(projectOverallStatusMap));
	}

	private Map<Role, SkillStatus> getRoleSkillStatusMap(final Employee employeeEntity,
														 final Map<Project, Set<Role>> projectRoleMap) {
		Set<Role> employeeRoles = roleService.getEmployeeRolesForSite(siteId, employeeEntity);
		employeeRoles.addAll(Utilities.extractAndFlattenValuesFromMap(projectRoleMap));
		Map<Role, List<AccountSkill>> roleSkillMap = skillService.getSkillsForRoles(siteId, employeeRoles);
		Map<Role, List<SkillStatus>> roleSkillStatuses = statusCalculatorService
				.getSkillStatusListPerEntity(employeeEntity, roleSkillMap);
		return statusCalculatorService
				.getOverallStatusPerEntity(roleSkillStatuses);
	}

	private Map<Project, SkillStatus> getProjectSkillStatusMap(final Employee employeeEntity,
															   final Map<Project, Set<Role>> projectRoleMap) {
		Map<Project, List<AccountSkill>> projectSkillMap = skillService.getAllProjectSkillsForEmployee(siteId,
				employeeEntity, projectRoleMap);
		Map<Project, List<SkillStatus>> projectSkillStatuses = statusCalculatorService
				.getSkillStatusListPerEntity(employeeEntity, projectSkillMap);
		return statusCalculatorService
				.getOverallStatusPerEntity(projectSkillStatuses);
	}

	private SkillStatus getOverallSkillStatus(final Map<Role, SkillStatus> roleOverallStatusMap,
											  final Map<Project, SkillStatus> projectOverallStatusMap) {
		Collection<SkillStatus> skillStatuses = new ArrayList<>();
		skillStatuses.addAll(roleOverallStatusMap.values());
		skillStatuses.addAll(projectOverallStatusMap.values());
		return statusCalculatorService.calculateOverallStatus(skillStatuses);
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public EmployeeModel getEmployee() {
		return employee;
	}

	public EmployeeNav getEmployeeNav() {
		return employeeNav;
	}
}
