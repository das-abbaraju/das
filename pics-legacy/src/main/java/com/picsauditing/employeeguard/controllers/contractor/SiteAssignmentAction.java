package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillRole;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.process.ContractorAssignmentData;
import com.picsauditing.employeeguard.process.ContractorAssignmentProcess;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.RoleService;
import com.picsauditing.employeeguard.services.SkillUsageLocator;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.status.StatusCalculatorService;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.employeeguard.util.ListUtil;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignment;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignmentMatrix;
import com.picsauditing.employeeguard.viewmodel.contractor.SiteAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class SiteAssignmentAction extends PicsRestActionSupport {

	public static final Logger LOG = LoggerFactory.getLogger(SiteAssignmentAction.class);

	@Autowired
	private AccountService accountService;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private ContractorAssignmentProcess contractorAssignmentProcess;
	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;
	@Autowired
	private SkillEntityService skillEntityService;
	@Autowired
	private SkillUsageLocator skillUsageLocator;

	private int siteId;
	private int roleId;

	private AccountModel site;
	private Role role;

	private SiteAssignmentModel siteAssignmentModel;
	private ContractorEmployeeRoleAssignmentMatrix assignmentMatrix;

	public String status() {
		site = accountService.getAccountById(getIdAsInt());
		siteAssignmentModel = buildSiteAssignmentModel(site);

		return "status";
	}

	private SiteAssignmentModel buildSiteAssignmentModel(final AccountModel site) {
		List<Employee> employees = employeeEntityService.getEmployeesAssignedToSite(permissions.getAccountId(), site.getId());
		Map<RoleInfo, Integer> roleCounts = getRoleEmployeeCounts(employees);

		AccountModel account = accountService.getAccountById(permissions.getAccountId());

		int contractorId = permissions.getAccountId();
		Map<AccountModel, Set<AccountModel>> siteHierarchy = accountService.getSiteParentAccounts(
				accountService.getOperatorIdsForContractor(contractorId));

		ContractorAssignmentData contractorAssignmentData = contractorAssignmentProcess
				.buildContractorAssignmentData(contractorId,
						new HashSet<>(accountService.getOperatorsForContractors(Arrays.asList(contractorId))),
						siteHierarchy);

		Map<AccountModel, Map<Employee, SkillStatus>> employeeAccountAssignment = contractorAssignmentProcess
				.buildEmployeeSiteAssignmentStatistics(siteHierarchy, contractorAssignmentData);

		return ViewModelFactory.getContractorSiteAssignmentModelFactory().create(site, Arrays.asList(account),
				new HashSet<>(employees), employeeAccountAssignment.get(site), roleCounts);
	}

	private Map<RoleInfo, Integer> getRoleEmployeeCounts(final List<Employee> employees) {
		List<Role> roles = getRoles();
		List<RoleInfo> roleInfos = ViewModelFactory.getRoleInfoFactory().build(roles);

		Map<Role, Set<Employee>> employeeRoles = employeeEntityService
				.getEmployeesBySiteRoles(Arrays.asList(site.getId()));

		return ViewModelFactory.getRoleEmployeeCountFactory().create(roleInfos, employees, employeeRoles);
	}

	private List<Role> getRoles() {
		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(site.getId());

		return roleService.getRolesForAccounts(corporateIds);
	}

	public String assign() {
		try {
			assignmentService.assignEmployeeToSiteRole(siteId, roleId, getIdAsInt(),
					new EntityAuditInfo.Builder().appUserId(permissions.getAppUserID()).timestamp(DateBean.today()).build());

			json.put("status", "SUCCESS");
		} catch (Exception e) {
			LOG.error("Error assigning employee id = " + id + " to role id = " + roleId, e);
			json.put("status", "FAILURE");
		}

		return JSON;
	}

	public String unassign() {
		try {
			assignmentService.unassignEmployeeFromSiteRole(siteId, roleId, getIdAsInt());

			json.put("status", "SUCCESS");
		} catch (Exception e) {
			LOG.error("Error unassigning employee id = " + id + " from site id = " + siteId, e);
			json.put("status", "FAILURE");
		}

		return JSON;
	}

	public String unassignAll() throws Exception {
		try {
			assignmentService.unassignEmployeeFromSite(siteId, getIdAsInt());

			json.put("status", "SUCCESS");
		} catch (Exception e) {
			LOG.error("Error unassigning employee id = " + id + " from site id = " + siteId, e);
			json.put("status", "FAILURE");
		}

		return JSON;
	}

	public String role() {
		role = roleService.getRole(id);
		site = accountService.getAccountById(siteId);
		assignmentMatrix = buildRoleAssignmentMatrix(role, site);

		return "role";
	}

	private ContractorEmployeeRoleAssignmentMatrix buildRoleAssignmentMatrix(final Role corporateRole, final AccountModel site) {
		int contractorId = permissions.getAccountId();

		List<Employee> employees = employeeEntityService.getEmployeesForAccount(contractorId);
		List<ContractorEmployeeRoleAssignment> assignments = buildContractorEmployeeRoleAssignments(contractorId,
				employees, corporateRole, site);
		Collections.sort(assignments);

		List<Employee> employeesAssignedToSite = employeeEntityService.getEmployeesAssignedToSite(contractorId, site.getId());
		Map<RoleInfo, Integer> roleCounts = getRoleEmployeeCounts(employees);

		List<AccountSkill> roleSkills = ExtractorUtil.extractList(corporateRole.getSkills(),
				AccountSkillRole.SKILL_EXTRACTOR);

		List<Integer> corporateAccountIds = accountService.getTopmostCorporateAccountIds(site.getId());
		roleSkills.addAll(skillEntityService.getSiteAndCorporateRequiredSkills(site.getId(), corporateAccountIds));
		roleSkills = ListUtil.removeDuplicatesAndSort(roleSkills);

		return ViewModelFactory.getContractorEmployeeRoleAssignmentMatrixFactory()
				.create(employeesAssignedToSite.size(), roleSkills, roleCounts, assignments);
	}

	private List<ContractorEmployeeRoleAssignment> buildContractorEmployeeRoleAssignments(final int contractorId,
																						  final List<Employee> employees,
																						  final Role corporateRole,
																						  final AccountModel site) {
		Map<Role, Set<Employee>> employeesAssignedToRole = roleService.getRoleAssignments(contractorId, site.getId());
		Set<AccountSkill> allSkillsForRole = skillEntityService.getSkillsForRole(corporateRole);

		List<Integer> corporateAccountIds = accountService.getTopmostCorporateAccountIds(site.getId());
		allSkillsForRole.addAll(skillEntityService.getSiteAndCorporateRequiredSkills(site.getId(), corporateAccountIds));

		List<AccountSkill> orderedSkills = new ArrayList<>(allSkillsForRole);
		Collections.sort(orderedSkills);

		Map<Employee, List<SkillStatus>> employeeSkillStatusMap = statusCalculatorService
				.getEmployeeStatusRollUpForSkills(employees, orderedSkills);

		return ViewModelFactory.getContractorEmployeeRoleAssignmentFactory()
				.build(employees, employeesAssignedToRole.get(corporateRole), employeeSkillStatusMap);
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
