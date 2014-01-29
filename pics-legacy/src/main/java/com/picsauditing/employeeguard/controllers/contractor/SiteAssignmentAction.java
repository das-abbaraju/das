package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignmentMatrix;
import com.picsauditing.employeeguard.viewmodel.contractor.SiteAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModeFactory;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class SiteAssignmentAction extends PicsRestActionSupport {

	@Autowired
	private AccountService accountService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private SkillUsageLocator skillUsageLocator;
	@Autowired
	private RoleService roleService;

	private int siteId;
	private int employeeId;

	private AccountModel site;
	private Role role;

	private SiteAssignmentModel siteAssignmentModel;
	private ContractorEmployeeRoleAssignmentMatrix assignmentMatrix;

	public String status() {
		site = accountService.getAccountById(NumberUtils.toInt(id));
		siteAssignmentModel = buildSiteAssignmentModel();

		return "status";
	}

	private SiteAssignmentModel buildSiteAssignmentModel() {
		AccountModel account = accountService.getAccountById(permissions.getAccountId());
		List<Employee> employees = employeeService.getEmployeesAssignedToSite(permissions.getAccountId(), site.getId());
		List<SkillUsage> skillUsages = skillUsageLocator.getSkillUsagesForEmployees(new TreeSet<>(employees));
		List<Role> roles = getSiteRoles();

		return ViewModeFactory.getSiteAssignmentModelFactory().create(site, Arrays.asList(account), skillUsages, roles);
	}

	private List<Role> getSiteRoles() {
		if (role == null) {
			List<Integer> siteAndCorporateIds = accountService.getTopmostCorporateAccountIds(site.getId());
			siteAndCorporateIds.add(site.getId());
			return roleService.getRolesForAccounts(siteAndCorporateIds);
		}

		return Arrays.asList(role);
	}

	public String unassign() {
		return "unassign";
	}

	public String role() {
		role = roleService.getRole(id);
		site = accountService.getAccountById(siteId);

		return "role";
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public AccountModel getSite() {
		return site;
	}

	public Role getRole() {
		return role;
	}

	public SiteAssignmentModel getSiteAssignmentModel() {
		return siteAssignmentModel;
	}

	public ContractorEmployeeRoleAssignmentMatrix getAssignmentMatrix() {
		return assignmentMatrix;
	}
}
