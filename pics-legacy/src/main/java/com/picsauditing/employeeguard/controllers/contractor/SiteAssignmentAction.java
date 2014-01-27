package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.SkillUsage;
import com.picsauditing.employeeguard.services.SkillUsageLocator;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.viewmodel.contractor.SiteAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.factory.EmployeeSiteAssignmentModelFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class SiteAssignmentAction extends PicsRestActionSupport {

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private SkillUsageLocator skillUsageLocator;

	private int roleId;
	private int employeeId;

	private SiteAssignmentModel siteAssignmentModel;

	public String index() {

		// Find employees
		List<Employee> employees = employeeService.getEmployeesForAccount(permissions.getAccountId());
		// Look up overall employee skill status excluding project skills

		Map<Employee, SkillStatus> rollupStatuses = new TreeMap<>();
		List<SkillUsage> skillUsages = skillUsageLocator.getSkillUsagesForEmployees(new TreeSet<>(employees));

		for (SkillUsage skillUsage : skillUsages) {
			// Calculate for account group skills, project role skills, site skills, corporate skills

		}

		EmployeeSiteAssignmentModelFactory employeeSiteAssignmentModelFactory = new EmployeeSiteAssignmentModelFactory();
//		List<EmployeeSiteAssignmentModel> employeeSiteAssignmentModels = employeeSiteAssignmentModelFactory.create();

		return "list";
	}

	public String unassign() {
		return "unassign";
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

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public void setSiteAssignmentModel(SiteAssignmentModel siteAssignmentModel) {
		this.siteAssignmentModel = siteAssignmentModel;
	}
}
