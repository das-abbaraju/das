package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignment;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignmentMatrix;
import com.picsauditing.employeeguard.viewmodel.contractor.SiteAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModelFactory;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

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
	private int roleId;

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

		List<Role> corporateRoles = getCorporateRoles();
		List<RoleInfo> corporateRoleInfo = ViewModelFactory.getRoleInfoFactory().build(corporateRoles);

		return ViewModelFactory.getRoleEmployeeCountFactory()
				.create(corporateRoleInfo, corporateToSiteRoles, employees);
	}

	private List<Role> getCorporateRoles() {
		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(site.getId());
		return roleService.getRolesForAccounts(corporateIds);
	}

	public String assign() {
        Employee employee = null;
        roleService.assignEmployeeToSite(siteId, NumberUtils.toInt(id), employee, permissions.getUserId());
		return "assign";
	}

	public String unassign() {
		// project/site-assignment/{siteId}/employee/{id}/unassign
		roleService.removeSiteRolesFromEmployee(NumberUtils.toInt(id), siteId);

		return "unassign";
	}

	public String role() {
		role = roleService.getRole(id);
		site = accountService.getAccountById(siteId);
		assignmentMatrix = buildRoleAssignmentMatrix();

		return "role";
	}

	private ContractorEmployeeRoleAssignmentMatrix buildRoleAssignmentMatrix() {
		int contractorId = permissions.getAccountId();

		List<Employee> employees = employeeService.getEmployeesForAccount(contractorId);
		List<ContractorEmployeeRoleAssignment> assignments = buildContractorEmployeeRoleAssignments(contractorId, employees);
		Collections.sort(assignments);

		List<Employee> employeesAssignedToSite = employeeService.getEmployeesAssignedToSite(contractorId, site.getId());
		Map<RoleInfo, Integer> roleCounts = getRoleEmployeeCounts(employees);

		List<AccountSkill> roleSkills = ExtractorUtil.extractList(role.getSkills(), AccountSkillRole.SKILL_EXTRACTOR);
		Collections.sort(roleSkills);

		return ViewModelFactory.getContractorEmployeeRoleAssignmentMatrixFactory()
				.create(employeesAssignedToSite.size(), roleSkills, roleCounts, assignments);
	}

	private List<ContractorEmployeeRoleAssignment> buildContractorEmployeeRoleAssignments(int contractorId,
	                                                                                      List<Employee> employees) {
		Map<Role, Set<Employee>> employeesAssignedToRole = roleService.getRoleAssignments(contractorId, site.getId());
		Map<Employee, Set<AccountSkillEmployee>> employeeSkills =
				accountSkillEmployeeService.getSkillMapForAccountAndRole(contractorId, role.getId());

		return ViewModelFactory.getContractorEmployeeRoleAssignmentFactory()
				.build(employees, role, employeesAssignedToRole.get(role), employeeSkills);
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
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
