package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.employeeguard.util.ListUtil;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignment;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorEmployeeRoleAssignmentMatrix;
import com.picsauditing.employeeguard.viewmodel.contractor.SiteAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModelFactory;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class SiteAssignmentAction extends PicsRestActionSupport {

    public static final Logger LOG = LoggerFactory.getLogger(SiteAssignmentAction.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountSkillEmployeeService accountSkillEmployeeService;
    @Autowired
    private EmployeeService employeeService;
	@Autowired
	private SkillService skillService;
    @Autowired
    private SkillUsageLocator skillUsageLocator;
    @Autowired
    private RoleService roleService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

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
        return ViewModelFactory.getContractorSiteAssignmentModelFactory().create(site, Arrays.asList(account), skillUsages, roleCounts);
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
        try {
            Employee employee = employeeService.findEmployee(id, permissions.getAccountId());
            roleService.assignEmployeeToRole(siteId, roleId, employee, permissions.getAppUserID());
            json.put("status", "SUCCESS");
        } catch (Exception e) {
            LOG.error("Error assigning employee id = " + id + " to role id = " + roleId, e);
            json.put("status", "FAILURE");
        }

        return JSON;
    }

	public String unassign() {
		try {
			Employee employee = employeeService.findEmployee(id, permissions.getAccountId());
			roleService.unassignEmployeeFromRole(employee, roleId, siteId);
			json.put("status", "SUCCESS");
		} catch (Exception e) {
			LOG.error("Error unassigning employee id = " + id + " from site id = " + siteId, e);
			json.put("status", "FAILURE");
		}

		return JSON;
	}

    public String unassignAll() throws Exception {
        try {
            Employee employee = employeeService.findEmployee(id, permissions.getAccountId());
            roleService.unassignEmployeeFromSite(employee, siteId);
            json.put("status", "SUCCESS");
        } catch (Exception e) {
            LOG.error("Error unassigning employee id = " + id + " from site id = " + siteId, e);
            json.put("status", "FAILURE");
        }

        return JSON;
    }

    public String role() {
        role = roleService.getRole(id);       // corporate role id
        site = accountService.getAccountById(siteId);
        assignmentMatrix = buildRoleAssignmentMatrix(role, site);

        return "role";
    }

    private ContractorEmployeeRoleAssignmentMatrix buildRoleAssignmentMatrix(final Role corporateRole, final AccountModel site) {
        int contractorId = permissions.getAccountId();

        List<Employee> employees = employeeService.getEmployeesForAccount(contractorId);
        List<ContractorEmployeeRoleAssignment> assignments = buildContractorEmployeeRoleAssignments(contractorId,
				employees, corporateRole, site);
        Collections.sort(assignments);

        List<Employee> employeesAssignedToSite = employeeService.getEmployeesAssignedToSite(contractorId, site.getId());
        Map<RoleInfo, Integer> roleCounts = getRoleEmployeeCounts(employees);

        List<AccountSkill> roleSkills = ExtractorUtil.extractList(corporateRole.getSkills(),
				AccountSkillRole.SKILL_EXTRACTOR);
		roleSkills.addAll(skillService.getRequiredSkills(site.getId()));
		roleSkills.addAll(skillService.getRequiredSkills(corporateRole.getAccountId()));
		roleSkills = ListUtil.removeDuplicatesAndSort(roleSkills);

        return ViewModelFactory.getContractorEmployeeRoleAssignmentMatrixFactory()
                .create(employeesAssignedToSite.size(), roleSkills, roleCounts, assignments);
    }

    private List<ContractorEmployeeRoleAssignment> buildContractorEmployeeRoleAssignments(final int contractorId,
																						  final List<Employee> employees,
																						  final Role corporateRole,
																						  final AccountModel site) {
        Map<Role, Set<Employee>> employeesAssignedToRole = roleService.getRoleAssignments(contractorId, site.getId());
		List<AccountSkill> allSkillsForRole = skillService.getSkillsForRole(corporateRole);
		allSkillsForRole.addAll(skillService.getRequiredSkills(site.getId()));
		allSkillsForRole.addAll(skillService.getRequiredSkills(corporateRole.getAccountId()));
		allSkillsForRole = ListUtil.removeDuplicatesAndSort(allSkillsForRole);

		Map<Employee, List<SkillStatus>> employeeSkillStatusMap = statusCalculatorService
				.getEmployeeStatusRollUpForSkills(employees, allSkillsForRole);

        return ViewModelFactory.getContractorEmployeeRoleAssignmentFactory()
                .build(employees, corporateRole, employeesAssignedToRole.get(corporateRole), employeeSkillStatusMap);
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
