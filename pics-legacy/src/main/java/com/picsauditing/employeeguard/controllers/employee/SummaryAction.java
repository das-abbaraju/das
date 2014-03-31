package com.picsauditing.employeeguard.controllers.employee;

import com.google.gson.Gson;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.ProfileAssignmentModel;
import com.picsauditing.employeeguard.models.ProfileModel;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.engine.SkillEngine;
import com.picsauditing.employeeguard.services.entity.GroupEntityService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@SuppressWarnings("serial")
public class SummaryAction extends PicsRestActionSupport {

	@Autowired
	private AccountService accountService;
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

	public String index() {
//		profile = profileEntityService.findByAppUserId(permissions.getAppUserID());

		return "dashboard";
	}

	public String employeeInfo() {
		Profile profile = profileEntityService.findByAppUserId(permissions.getAppUserID());
		ProfileModel profileModel = ModelFactory.getProfileModelFactory().create(profile);
		jsonString = new Gson().toJson(profileModel);

		return JSON_STRING;
	}

	public String assignments() {
		Profile profile = profileEntityService.findByAppUserId(permissions.getAppUserID());

		Map<Integer, AccountModel> contractors = accountService.getContractorMapForProfile(profile);
		Map<Integer, AccountModel> sites = accountService.getOperatorMapForContractors(contractors.keySet());
		Map<Integer, AccountModel> allAccounts = new HashMap<>(contractors);
		allAccounts.putAll(sites);

		// Site IDs
		Map<Integer, Set<Role>> siteRoles = roleEntityService.getRolesForSites(sites.keySet());
		// Contractor IDs
		Map<Integer, Set<Group>> contractorGroups = groupEntityService.getGroupsByContractorId(profile.getEmployees());

		Map<Project, SkillStatus> projectStatuses = getProjectStatuses(profile);
		Map<Integer, SkillStatus> siteStatus = getSiteStatuses(projectStatuses);

		List<ProfileAssignmentModel> models = ModelFactory.getProfileAssignmentModelFactory()
				.create(allAccounts, siteRoles, contractorGroups, siteStatus, projectStatuses);

		jsonString = new Gson().toJson(models);

		return JSON_STRING;
	}

	private Map<Project, SkillStatus> getProjectStatuses(Profile profile) {
		Map<Project, Map<Employee, Set<AccountSkill>>> projectEmployeeSkills =
				skillEngine.getProjectEmployeeSkills(profile.getEmployees());
		Map<Project, List<SkillStatus>> projectStatusList =
				statusCalculatorService.getAllSkillStatusesForEntity(projectEmployeeSkills);

		return statusCalculatorService.getOverallStatusPerEntity(projectStatusList);
	}

	private Map<Integer, SkillStatus> getSiteStatuses(final Map<Project, SkillStatus> projectStatuses) {
		Map<Integer, List<SkillStatus>> siteStatusList = new HashMap<>();
		// include site assignments as well???
		for (Map.Entry<Project, SkillStatus> entry : projectStatuses.entrySet()) {
			Project project = entry.getKey();

			if (!siteStatusList.containsKey(project.getAccountId())) {
				siteStatusList.put(project.getAccountId(), new ArrayList<SkillStatus>());
			}

			siteStatusList.get(project.getAccountId()).add(entry.getValue());
		}

		return statusCalculatorService.getOverallStatusPerEntity(siteStatusList);
	}

	/* other methods */

	/* getters + setters */

}
