package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignmentMatrix;
import com.picsauditing.employeeguard.viewmodel.contractor.SiteAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModelFactory;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class SiteAssignmentAction extends PicsRestActionSupport {

	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;
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
		List<Employee> employees = employeeService.getEmployeesAssignedToSite(permissions.getAccountId(), site.getId());
		List<SkillUsage> skillUsages = skillUsageLocator.getSkillUsagesForEmployees(new TreeSet<>(employees));

		Map<RoleInfo, Integer> roleCounts = getRoleEmployeeCounts(employees);

		AccountModel account = accountService.getAccountById(permissions.getAccountId());
		return ViewModelFactory.getSiteAssignmentModelFactory().create(site, Arrays.asList(account), skillUsages, roleCounts);
	}

	private Map<RoleInfo, Integer> getRoleEmployeeCounts(List<Employee> employees) {
		Map<Role, Role> siteToCorporateRoles = roleService.getSiteToCorporateRoles(site.getId());
		Map<Role, Role> corporateToSiteRoles = Utilities.invertMap(siteToCorporateRoles);

		List<Role> siteRoles = getSiteRoles();
		List<RoleInfo> roleInfos = ViewModelFactory.getRoleInfoFactory().build(siteRoles);

		return ViewModelFactory.getRoleEmployeeCountFactory()
				.create(roleInfos, corporateToSiteRoles, employees);
	}

	private List<Role> getSiteRoles() {
		List<Integer> siteAndCorporateIds = accountService.getTopmostCorporateAccountIds(site.getId());
		siteAndCorporateIds.add(site.getId());
		return roleService.getRolesForAccounts(siteAndCorporateIds);
	}

	public String unassign() {
		return "unassign";
	}

	public String role() {
		role = roleService.getRole(id);
		site = accountService.getAccountById(siteId);
		assignmentMatrix = buildRoleAssignmentMatrix();

		return "role";
	}

	private ContractorEmployeeRoleAssignmentMatrix buildRoleAssignmentMatrix() {
		List<Employee> employees = employeeService.getEmployeesForAccount(permissions.getAccountId());
		List<Employee> employeesAssignedToSite =
				employeeService.getEmployeesAssignedToSite(permissions.getAccountId(), site.getId());
		List<AccountSkillEmployee> employeeSkills =
				accountSkillEmployeeService.getSkillsForAccount(permissions.getAccountId());

		Map<RoleInfo, Integer> roleCounts = getRoleEmployeeCounts(employees);

		return ViewModelFactory.getContractorEmployeeRoleAssignmentMatrixFactory()
				.create(employeesAssignedToSite.size(), roleCounts, employees, employeeSkills);
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
