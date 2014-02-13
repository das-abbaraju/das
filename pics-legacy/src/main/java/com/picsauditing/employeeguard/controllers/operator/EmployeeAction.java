package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.RoleService;
import com.picsauditing.employeeguard.services.SkillService;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.viewmodel.employee.EmployeeProjectAndRoleStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class EmployeeAction extends PicsRestActionSupport {

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;
	@Autowired
	private SkillService skillService;

	private int contractorId;

	private EmployeeProjectAndRoleStatus employeeProjectAndRoleStatus;

	public String show() {
		Employee employee = employeeService.findEmployee(id, contractorId);
		Map<Role, Role> siteToCorporateRoles = roleService.getSiteToCorporateRoles(permissions.getAccountId());
		List<AccountSkill> siteRequiredSkills = skillService.getRequiredSkillsForSiteAndCorporates(permissions.getAccountId());

		employeeProjectAndRoleStatus = statusCalculatorService.getEmployeeStatusesForProjectsAndJobRoles(
				employee, siteToCorporateRoles, siteRequiredSkills);

		return SHOW;
	}

	public int getContractorId() {
		return contractorId;
	}

	public void setContractorId(int contractorId) {
		this.contractorId = contractorId;
	}

	public EmployeeProjectAndRoleStatus getEmployeeProjectAndRoleStatus() {
		return employeeProjectAndRoleStatus;
	}
}
