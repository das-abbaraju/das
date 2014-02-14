package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.employee.EmployeeModel;
import com.picsauditing.employeeguard.viewmodel.employee.EmployeeProjectAndRoleStatus;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModelFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class EmployeeAction extends PicsRestActionSupport {

	@Autowired
	private AccountService accountService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;
	@Autowired
	private SkillService skillService;

	private int contractorId;

	private EmployeeModel employee;
	private EmployeeProjectAndRoleStatus employeeProjectAndRoleStatus;

	public String show() {
		Employee employeeEntity = employeeService.findEmployee(id, contractorId);
		Map<Role, Role> siteToCorporateRoles = roleService.getSiteToCorporateRoles(permissions.getAccountId());
		List<AccountSkill> siteRequiredSkills = skillService.getRequiredSkillsForSiteAndCorporates(permissions.getAccountId());

		employeeProjectAndRoleStatus = statusCalculatorService.getEmployeeStatusesForProjectsAndJobRoles(
				employeeEntity, siteToCorporateRoles, siteRequiredSkills);

		Map<Integer, AccountModel> contractors = accountService.getContractorsForEmployee(employeeEntity);
		employee = ViewModelFactory.getEmployeeModelFactory().create(employeeEntity, contractors);

		return SHOW;
	}

	public int getContractorId() {
		return contractorId;
	}

	public void setContractorId(int contractorId) {
		this.contractorId = contractorId;
	}

	public EmployeeModel getEmployee() {
		return employee;
	}

	public EmployeeProjectAndRoleStatus getEmployeeProjectAndRoleStatus() {
		return employeeProjectAndRoleStatus;
	}
}
