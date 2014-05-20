package com.picsauditing.employeeguard.controllers.operator;

import com.google.common.collect.Table;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.EntityInfo;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.status.StatusCalculatorService;
import com.picsauditing.employeeguard.util.ListUtil;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.employeeguard.viewmodel.contractor.EmployeeSiteAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModelFactory;
import com.picsauditing.employeeguard.viewmodel.operator.SiteAssignmentModel;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class SiteAssignmentAction extends PicsRestActionSupport {

	private static final long serialVersionUID = 1288428610452669599L;

	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountSkillProfileService accountSkillProfileService;
	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private SkillService skillService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	private int siteId;
	private AccountModel site;

	private SiteAssignmentModel siteAssignmentModel;

	public String status() {
		if (!permissions.isOperatorCorporate()) {
			return BLANK;
		}

		int siteId = siteId();
		site = accountService.getAccountById(siteId);

		List<AccountModel> contractors = accountService.getContractors(siteId);
		Set<Integer> contractorIds = PicsCollectionUtil.getIdsFromCollection(
				contractors,
				new PicsCollectionUtil.Identitifable<AccountModel, Integer>() {
					@Override
					public Integer getId(AccountModel element) {
						return element.getId();
					}
				});

		List<Employee> employeesAtSite = employeeService.getEmployeesAssignedToSite(contractorIds, siteId);
		Map<Employee, Set<AccountSkill>> employeeRequiredSkills = roleService.getEmployeeSkillsForSite(siteId, contractorIds);

		List<EmployeeSiteAssignmentModel> employeeSiteAssignments = ViewModelFactory
				.getEmployeeSiteAssignmentModelFactory()
				.create(statusCalculatorService.getEmployeeStatusRollUpForSkills(employeesAtSite, employeeRequiredSkills),
						Collections.<Employee, Set<Role>>emptyMap(),
						accountService.getContractorMapForSite(siteId));

		Map<RoleInfo, Integer> roleCounts = buildRoleCounts(siteId, employeesAtSite);

		siteAssignmentModel = ViewModelFactory.getOperatorSiteAssignmentModelFactory()
				.create(employeesAtSite.size(), employeeSiteAssignments, roleCounts, Collections.<EntityInfo>emptyList());

		return "status";
	}

	private int siteId() {
		if (permissions.isCorporate()) {
			return getIdAsInt();
		}

		return permissions.getAccountId();
	}

	private Map<RoleInfo, Integer> buildRoleCounts(final int siteId, final List<Employee> employeesAtSite) {
		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(siteId);
		List<Role> roles = roleService.getRolesForAccounts(corporateIds);

		Map<Role, Set<Employee>> roleEmployees = employeeEntityService.getEmployeesBySiteRoles(Arrays.asList(siteId));

		List<RoleInfo> roleInfos = ViewModelFactory.getRoleInfoFactory().build(roles);
		return ViewModelFactory.getRoleEmployeeCountFactory()
				.create(roleInfos, employeesAtSite, roleEmployees);
	}

	public String role() {
		if (!permissions.isOperatorCorporate()) {
			return BLANK;
		}

		site = accountService.getAccountById(siteId);
		Role role = roleService.getRole(id);

		Map<Integer, AccountModel> contractors = accountService.getContractorMapForSite(siteId);

		List<Employee> employeesAssignedToRole = getEmployeesAssignedToSiteRole(role, contractors);

		List<AccountSkill> skills = skillService.getSkillsForRole(role);
		skills.addAll(skillService.getRequiredSkillsForSiteAndCorporates(siteId));
		skills = ListUtil.removeDuplicatesAndSort(skills);

		Table<Employee, AccountSkill, AccountSkillProfile> accountSkillProfiles =
				accountSkillProfileService.buildTable(employeesAssignedToRole, skills);

		List<EmployeeSiteAssignmentModel> employeeSiteAssignmentModels =
				ViewModelFactory.getEmployeeSiteAssignmentModelFactory().create(
						employeesAssignedToRole, skills, accountSkillProfiles, contractors);

		List<Employee> employeesAtSite = employeeService.getEmployeesAssignedToSite(contractors.keySet(), siteId);
		Map<RoleInfo, Integer> roleCounts = buildRoleCounts(siteId, employeesAtSite);

		List<EntityInfo> skillInfos = ViewModelFactory.getEntityInfoFactory().create(skills);

		siteAssignmentModel = ViewModelFactory.getOperatorSiteAssignmentModelFactory().create(
				employeesAtSite.size(),
				employeeSiteAssignmentModels,
				roleCounts,
				skillInfos);

		return "role";
	}

	private List<Employee> getEmployeesAssignedToSiteRole(Role role, Map<Integer, AccountModel> contractors) {
		if (MapUtils.isEmpty(contractors)) {
			return Collections.emptyList();
		}

		return employeeService.getEmployeesAssignedToSiteRole(contractors.keySet(), siteId, role);
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public AccountModel getSite() {
		return site;
	}

	public SiteAssignmentModel getSiteAssignmentModel() {
		return siteAssignmentModel;
	}
}
