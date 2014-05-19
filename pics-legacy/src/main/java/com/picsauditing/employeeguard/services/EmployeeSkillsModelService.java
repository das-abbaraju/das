package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.RequiredSkills;
import com.picsauditing.employeeguard.models.SkillStatusModel;
import com.picsauditing.employeeguard.models.factories.CompanyProjectModelFactory;
import com.picsauditing.employeeguard.models.factories.CompanyStatusModelFactory;
import com.picsauditing.employeeguard.models.factories.EmployeeSkillsModelFactory;
import com.picsauditing.employeeguard.process.ProfileSkillData;
import com.picsauditing.employeeguard.process.ProfileSkillStatusProcess;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EmployeeSkillsModelService {

	@Autowired
	private ProfileSkillStatusProcess profileSkillStatusProcess;

	public EmployeeSkillsModelFactory.EmployeeSkillsModel buildEmployeeSkillsModel(final Profile profile) {
		ProfileSkillData profileSkillData = profileSkillStatusProcess.buildProfileSkillData(profile);

		Map<AccountModel, Set<CompanyProjectModelFactory.CompanyProjectModel>> accountCompanyProjectModelMap =
				buildAccountCompanyProjectModelMap(profileSkillData);

		Map<AccountModel, RequiredSkills> requiredSkillsMap = buildRequiredSkillsMap(profileSkillData);
		Set<CompanyStatusModelFactory.CompanyStatusModel> companyStatusModels =
				ModelFactory.getCompanyStatusModelFactory().create(profileSkillData.getSiteStatuses(),
						requiredSkillsMap, accountCompanyProjectModelMap);

		return ModelFactory.getEmployeeSkillsModelFactory().create(profileSkillData.getOverallStatus(), companyStatusModels);
	}

	private Map<AccountModel, RequiredSkills> buildRequiredSkillsMap(final ProfileSkillData profileSkillData) {
		Map<AccountModel, Set<AccountSkill>> requiredSkills = profileSkillData.getAllRequiredSkills();

		Map<AccountModel, RequiredSkills> accountRequiredSkills = new HashMap<>();
		for (AccountModel accountModel : requiredSkills.keySet()) {
			accountRequiredSkills.put(accountModel, new RequiredSkills(ModelFactory.getSkillStatusModelFactory()
					.create(requiredSkills.get(accountModel), profileSkillData.getSkillStatusMap())));
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
		Map<Project, Set<AccountSkill>> projectSkills = profileSkillData.getAllProjectSkills();
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
}
