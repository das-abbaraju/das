package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.forms.operator.ProjectRoleAssignment;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

public class SiteAssignmentAction extends PicsRestActionSupport {

	private static final long serialVersionUID = 1288428610452669599L;

	@Autowired
	private AccountService accountService;
	@Autowired
	private EmployeeService employeeService;

	int roleId;

	private List<ProjectRoleAssignment> projectRoleAssignments;

	public String status() {
		List<AccountModel> contractors = accountService.getContractors(permissions.getAccountId());
		Set<Integer> contractorIds = Utilities.getIdsFromCollection(contractors, new Utilities.Identitifable<AccountModel, Integer>() {
			@Override
			public Integer getId(AccountModel element) {
				return element.getId();
			}
		});

		List<Employee> employees = employeeService.getEmployeesAssignedToSite(contractorIds, permissions.getAccountId());

		return "status";
	}

	public String role() {
		return "role";
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public List<ProjectRoleAssignment> getProjectRoleAssignments() {
		return projectRoleAssignments;
	}
}
