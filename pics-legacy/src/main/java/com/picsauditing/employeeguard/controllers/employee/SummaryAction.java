package com.picsauditing.employeeguard.controllers.employee;

import com.google.gson.Gson;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.engine.SkillEngine;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.ProfileAssignmentModel;
import com.picsauditing.employeeguard.models.ProfileModel;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.entity.*;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.apache.commons.collections.MapUtils;
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
	private ProjectEntityService projectEntityService;
	@Autowired
	private RoleEntityService roleEntityService;
	@Autowired
	private SkillEngine skillEngine;

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

		Map<Project, SkillStatus> projectStatuses = calculateProjectStatuses(profile);

		Map<Integer, AccountModel> contractors = accountService.getContractorMapForProfile(profile);
		Map<Integer, AccountModel> sites = accountService.getOperatorMapForContractors(contractors.keySet());
		Map<Integer, AccountModel> allAccounts = new HashMap<>(contractors);
		allAccounts.putAll(sites);

		// Site IDs
		Map<Integer, Set<Role>> siteRoles = roleEntityService.getRolesForSites(sites.keySet());
		// Contractor IDs
		Map<Integer, Set<Group>> contractorGroups = groupEntityService.getGroupsByContractorId(profile.getEmployees());

		Map<Integer, Set<AccountSkill>> skills = Collections.emptyMap(); // all the skills for contractors/sites
		Map<Integer, SkillStatus> siteStatus = Collections.emptyMap(); // the roll-up of skills for contractors/sites

		Map<Project, Set<AccountSkill>> allProjectSkills = Collections.emptyMap();
		Map<Project, Set<Role>> projectRoles = Collections.emptyMap();

		List<ProfileAssignmentModel> models = ModelFactory.getProfileAssignmentModelFactory()
				.create(allAccounts, siteRoles, contractorGroups, siteStatus, projectStatuses);

		jsonString = new Gson().toJson(models);

		return JSON_STRING;
	}

	private Map<Project, AccountModel> getProjectAccounts(final Profile profile) {
		Map<Employee, Set<Project>> employeeProjects = projectEntityService.getProjectsForEmployees(profile.getEmployees());
		if (MapUtils.isEmpty(employeeProjects)) {
			return Collections.emptyMap();
		}

		Set<Project> allProjectsForEmployees = PicsCollectionUtil.mergeCollectionOfCollections(employeeProjects.values());
		Map<Integer, AccountModel> accountIdToModel = getAccountIdToModelMapForProjects(allProjectsForEmployees);

		Map<Project, AccountModel> projectAccounts = new HashMap<>();
		for (Project project : allProjectsForEmployees) {
			projectAccounts.put(project, accountIdToModel.get(project.getAccountId()));
		}

		return projectAccounts;
	}

	private Map<Project, SkillStatus> calculateProjectStatuses(final Profile profile) {
		Map<Project, AccountModel> projectAccounts = getProjectAccounts(profile);

		return null;
	}

	private Map<Integer, AccountModel> getAccountIdToModelMapForProjects(final Set<Project> allProjectsForEmployees) {
		Set<Integer> projectAccountIds = PicsCollectionUtil.extractPropertyToSet(
				allProjectsForEmployees,
				new PicsCollectionUtil.PropertyExtractor<Project, Integer>() {
					@Override
					public Integer getProperty(Project project) {
						return project.getAccountId();
					}
				});

		return accountService.getIdToAccountModelMap(projectAccountIds);
	}

	/* other methods */

	/* getters + setters */

}
