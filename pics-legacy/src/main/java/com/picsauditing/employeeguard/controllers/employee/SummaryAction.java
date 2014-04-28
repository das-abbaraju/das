package com.picsauditing.employeeguard.controllers.employee;

import com.google.gson.Gson;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.controllers.helper.AccountHelper;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.ProfileAssignmentModel;
import com.picsauditing.employeeguard.models.ProfileModel;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.engine.SkillEngine;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.GroupEntityService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@SuppressWarnings("serial")
public class SummaryAction extends PicsRestActionSupport {

	@Autowired
	private AccountService accountService;
	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private GroupEntityService groupEntityService;
	@Autowired
	private ProfileEntityService profileEntityService;
	@Autowired
	private RoleEntityService roleEntityService;
	@Autowired
	private SkillEngine skillEngine;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	/* pages */

	public String employeeInfo() {
		Profile profile = profileEntityService.findByAppUserId(permissions.getAppUserID());
		ProfileModel profileModel = ModelFactory.getProfileModelFactory().create(profile);
		jsonString = new Gson().toJson(profileModel);

		return JSON_STRING;
	}

	public String assignments() {
		Profile profile = profileEntityService.findByAppUserId(permissions.getAppUserID());
		List<Employee> employees = profile.getEmployees();

		Map<Integer, AccountModel> contractors = accountService.getContractorMapForProfile(profile);
		Map<Integer, AccountModel> sites = accountService.getOperatorMapForContractors(contractors.keySet());
		Map<Integer, AccountModel> allAccounts = new HashMap<>(contractors);
		allAccounts.putAll(sites);

		// Site IDs
		Map<Integer, Set<Role>> employeeSiteRoles = roleEntityService.getSiteRolesForEmployees(employees);

		// Contractor IDs
		Map<Integer, Set<Group>> contractorGroups = groupEntityService.getGroupsByContractorId(employees);

		Map<Project, SkillStatus> projectStatuses = getProjectStatuses(profile);

		Map<Integer, Set<Employee>> employeeSiteAssignments = PicsCollectionUtil
				.invertMapOfSet(employeeEntityService.getEmployeeSiteAssignments(employees));

		Map<AccountModel, Set<Employee>> siteAssignments = AccountHelper.convertMap(allAccounts, employeeSiteAssignments);
		Map<Employee, Set<AccountSkill>> employeeSiteSkills = skillEngine.getEmployeeSkillsForSites(siteAssignments);
		Map<Employee, Set<AccountSkill>> employeeContractorSkills = skillEngine
				.getAllContractorRequiredSkillsForEmployees(siteAssignments, profile.getEmployees());

		Map<Employee, SkillStatus> employeeStatusForSites = statusCalculatorService.getEmployeeStatusRollUpForSkills(employeeSiteSkills);
		Map<Employee, SkillStatus> employeeStatusForContractors = statusCalculatorService.getEmployeeStatusRollUpForSkills(employeeContractorSkills);

		Map<Integer, List<SkillStatus>> siteStatuses = PicsCollectionUtil.reduceMaps(employeeSiteAssignments, employeeStatusForSites);
		Map<Integer, SkillStatus> siteStatus = statusCalculatorService.getOverallStatusPerEntity(siteStatuses);
		Map<Integer, Employee> contractorEmployees = employeeEntityService.getContractorEmployees(profile);
		Map<Integer, List<SkillStatus>> contractorStatuses = PicsCollectionUtil.reduceMap(contractorEmployees, employeeStatusForContractors);
		Map<Integer, SkillStatus> contractStatusSummary = statusCalculatorService.getOverallStatusPerEntity(contractorStatuses);

		siteStatus = PicsCollectionUtil.mergeMaps(siteStatus, contractStatusSummary);

		List<ProfileAssignmentModel> models = ModelFactory.getProfileAssignmentModelFactory()
				.create(allAccounts, employeeSiteRoles, contractorGroups, siteStatus, projectStatuses);

		jsonString = new Gson().toJson(models);

		return JSON_STRING;
	}

	private Map<Project, SkillStatus> getProjectStatuses(final Profile profile) {
		Map<Project, Map<Employee, Set<AccountSkill>>> projectEmployeeSkills =
				skillEngine.getProjectEmployeeSkills(profile.getEmployees());
		Map<Project, List<SkillStatus>> projectStatusList =
				statusCalculatorService.getAllSkillStatusesForEntity(projectEmployeeSkills);

		return statusCalculatorService.getOverallStatusPerEntity(projectStatusList);
	}
}
