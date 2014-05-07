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
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
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

		Map<AccountModel, RequiredSkills> requiredSkillsMap = buildRequiredSkillsMap();
		Set<CompanyStatusModelFactory.CompanyStatusModel> companyStatusModels =
				ModelFactory.getCompanyStatusModelFactory().create(profileSkillData.getSiteStatuses(),
						requiredSkillsMap, accountCompanyProjectModelMap);

		return ModelFactory.getEmployeeSkillsModelFactory().create(profileSkillData.getOverallStatus(), companyStatusModels);
	}

	private Map<AccountModel, RequiredSkills> buildRequiredSkillsMap() {
		return null;
	}

	private Map<AccountModel, Set<CompanyProjectModelFactory.CompanyProjectModel>> buildAccountCompanyProjectModelMap(
			final ProfileSkillData profileSkillData) {

		Map<Project, Set<SkillStatusModel>> projectSkillStatusMap = buildProjectSkillStatusMap(profileSkillData);
		Set<CompanyProjectModelFactory.CompanyProjectModel> companyProjectModels =
				ModelFactory.getCompanyProjectModelFactory().create(profileSkillData.getProjectStatuses(),
						projectSkillStatusMap);

		return buildAccountProjectsMap(profileSkillData.getSiteAndCorporateAccounts(), companyProjectModels);
	}

	private Map<AccountModel, Set<CompanyProjectModelFactory.CompanyProjectModel>> buildAccountProjectsMap(
			final Map<Integer, AccountModel> accountModelMap,
			final Set<CompanyProjectModelFactory.CompanyProjectModel> companyProjectModels) {

		Map<AccountModel, Set<CompanyProjectModelFactory.CompanyProjectModel>> accountProjectsMap = new HashMap<>();
		for (CompanyProjectModelFactory.CompanyProjectModel companyProjectModel : companyProjectModels) {
			AccountModel accountModel = accountModelMap.get(companyProjectModel.getId());
			if (!accountProjectsMap.containsKey(accountModel)) {
				accountProjectsMap.put(accountModel, new HashSet<CompanyProjectModelFactory.CompanyProjectModel>());
			}

			accountProjectsMap.get(accountModel).add(companyProjectModel);
		}

		return accountProjectsMap;
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
