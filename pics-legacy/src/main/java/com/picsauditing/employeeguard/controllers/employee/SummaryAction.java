package com.picsauditing.employeeguard.controllers.employee;

import com.google.gson.Gson;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.ProfileModel;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.services.entity.RoleEntityService;
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
	private ProfileEntityService profileEntityService;
	@Autowired
	private ProjectEntityService projectEntityService;
	@Autowired
	private RoleEntityService roleEntityService;

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

		// project to accounts
		Map<Project, AccountModel> projectAccounts = getProjectAccounts(profile);

		// foreach project
		// project to roles
		// project to skills
		// role to skills
		// employee to skills
		// employee to status
		// worstof all statuses

		Map<Project, SkillStatus> projectStatuses = calculateProjectStatuses(projectAccounts, profile.getEmployees());

		Map<AccountModel, Set<Role>> siteRoles;
		Map<AccountModel, Set<Group>> contractorGroups;

		Map<AccountModel, Set<AccountSkill>> skills; // all the skills for contractors/sites
		Map<AccountModel, SkillStatus> siteStatus; // the roll-up of skills for contractors/sites

		Map<Project, Set<AccountSkill>> allProjectSkills;
		Map<Project, Set<Role>> projectRoles;

		// TODO: Use this information to build the model
		// Map<AccountModel, Set<Role>>,
		// Map<AccountModel, Set<Group>>,
		// Map<AccountModel, SkillStatus>,
		// Map<Project, SkillStatus>

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

	private Map<Project, SkillStatus> calculateProjectStatuses(final Map<Project, AccountModel> projectAccounts,
	                                                           final List<Employee> employees) {
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
