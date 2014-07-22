package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.SkillStatusModel;
import com.picsauditing.employeeguard.models.factories.CompanyProjectModelFactory;
import com.picsauditing.employeeguard.models.factories.CompanyStatusModelFactory;
import com.picsauditing.employeeguard.models.factories.EmployeeSkillsModelFactory;
import com.picsauditing.employeeguard.process.ProfileSkillData;
import com.picsauditing.employeeguard.process.ProfileSkillStatusProcess;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class EmployeeSkillsModelService {

	@Autowired
	private ProfileSkillStatusProcess profileSkillStatusProcess;

	public EmployeeSkillsModelFactory.EmployeeSkillsModel buildEmployeeSkillsModel(final Profile profile) {
		ProfileSkillData profileSkillData = profileSkillStatusProcess.buildProfileSkillData(profile);

		Map<AccountModel, Set<CompanyProjectModelFactory.CompanyProjectModel>> accountCompanyProjectModelMap =
				buildAccountCompanyProjectModelMap(profileSkillData);

		Map<AccountModel, Set<SkillStatusModel>> accountSkillsMap = buildAccountSkillsMap(profileSkillData);
		Set<CompanyStatusModelFactory.CompanyStatusModel> companyStatusModels =
				ModelFactory.getCompanyStatusModelFactory().create(profileSkillData.getSiteStatuses(),
						accountSkillsMap, accountCompanyProjectModelMap);

		return ModelFactory.getEmployeeSkillsModelFactory().create(profileSkillData.getOverallStatus(), companyStatusModels);
	}

	private Map<AccountModel, Set<SkillStatusModel>> buildAccountSkillsMap(final ProfileSkillData profileSkillData) {
		Map<AccountModel, Set<AccountSkill>> requiredSkills = profileSkillData.getAllRequiredSkills();
		Map<AccountModel, Set<AccountSkill>> groupSkills = PicsCollectionUtil
				.reduceMapOfCollections(profileSkillData.getAccountGroups(), profileSkillData.getGroupSkills());

		requiredSkills = PicsCollectionUtil.mergeMapOfSets(requiredSkills, groupSkills);

		Map<AccountModel, Set<SkillStatusModel>> accountRequiredSkills = new HashMap<>();
		for (AccountModel accountModel : requiredSkills.keySet()) {
			List<SkillStatusModel> skills = ModelFactory.getSkillStatusModelFactory()
					.create(requiredSkills.get(accountModel), profileSkillData.getSkillStatusMap());

			Collections.sort(skills);

			accountRequiredSkills.put(accountModel, new LinkedHashSet<>(skills));
		}

		return accountRequiredSkills;
	}

	private Map<AccountModel, Set<CompanyProjectModelFactory.CompanyProjectModel>> buildAccountCompanyProjectModelMap(
			final ProfileSkillData profileSkillData) {

		Map<Project, Set<SkillStatusModel>> projectSkillStatusMap = buildProjectSkillStatusMap(profileSkillData);
		Set<CompanyProjectModelFactory.CompanyProjectModel> companyProjectModels =
				ModelFactory.getCompanyProjectModelFactory().create(profileSkillData.getProjectStatuses(),
						projectSkillStatusMap);

		return buildAccountProjectsMap(profileSkillData.getSiteAccounts(), profileSkillData.getProjects(),
				companyProjectModels);
	}

	private Map<AccountModel, Set<CompanyProjectModelFactory.CompanyProjectModel>> buildAccountProjectsMap(
			final Map<Integer, AccountModel> accountModelMap,
			final Set<Project> projects,
			final Set<CompanyProjectModelFactory.CompanyProjectModel> companyProjectModels) {

		Map<AccountModel, Set<CompanyProjectModelFactory.CompanyProjectModel>> accountProjectsMap = new HashMap<>();
		for (CompanyProjectModelFactory.CompanyProjectModel companyProjectModel : companyProjectModels) {
			int accountId = getAccountIdFromProject(projects, companyProjectModel.getId());
			AccountModel accountModel = accountModelMap.get(accountId);
			if (!accountProjectsMap.containsKey(accountModel)) {
				accountProjectsMap.put(accountModel, new HashSet<CompanyProjectModelFactory.CompanyProjectModel>());
			}

			accountProjectsMap.get(accountModel).add(companyProjectModel);
		}

		return accountProjectsMap;
	}

	private int getAccountIdFromProject(final Set<Project> projects, final int projectId) {
		if (CollectionUtils.isEmpty(projects)) {
			return 0;
		}

		for (Project project : projects) {
			if (project.getId() == projectId) {
				return project.getAccountId();
			}
		}

		return 0;
	}

	private Map<Project, Set<SkillStatusModel>> buildProjectSkillStatusMap(final ProfileSkillData profileSkillData) {
		Map<Project, Set<AccountSkill>> projectSkills = appendSiteRequiredSkills(profileSkillData.getAllProjectSkills(),
				profileSkillData);
		Map<AccountSkill, SkillStatus> skillStatusMap = profileSkillData.getSkillStatusMap();

		Map<Project, Set<SkillStatusModel>> projectSkillStatusModels = new HashMap<>();
		for (Project project : projectSkills.keySet()) {
			if (!projectSkillStatusModels.containsKey(project)) {
				projectSkillStatusModels.put(project, new HashSet<SkillStatusModel>());
			}

			Set<SkillStatusModel> skillStatusModels = new HashSet<>(ModelFactory.getSkillStatusModelFactory()
					.create(projectSkills.get(project), skillStatusMap));
			projectSkillStatusModels.get(project).addAll(skillStatusModels);
		}

		return projectSkillStatusModels;
	}

	private Map<Project, Set<AccountSkill>> appendSiteRequiredSkills(final Map<Project, Set<AccountSkill>> projectSkills,
																	 final ProfileSkillData profileSkillData) {
		Map<Integer, AccountModel> siteAccounts = profileSkillData.getSiteAccounts();
		Map<AccountModel, Set<AccountSkill>> accountRequiredSkills = profileSkillData.getAllRequiredSkills();

		Map<Project, Set<AccountSkill>> allSkillsRequiredForProjects = PicsCollectionUtil.copyMapOfSets(projectSkills);
		for (Project project : projectSkills.keySet()) {
			if (!allSkillsRequiredForProjects.containsKey(project)) {
				allSkillsRequiredForProjects.put(project, new HashSet<AccountSkill>());
			}

			allSkillsRequiredForProjects.get(project)
					.addAll(accountRequiredSkills.get(siteAccounts.get(project.getAccountId())));
		}

		return allSkillsRequiredForProjects;
	}
}
