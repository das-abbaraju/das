package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.ProjectModel;
import com.picsauditing.employeeguard.models.RoleModel;
import com.picsauditing.employeeguard.models.SkillModel;
import com.picsauditing.employeeguard.services.models.AccountModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ProjectModelFactory {

	public List<ProjectModel> create(final List<Project> projects,
	                                 final Map<Integer, List<RoleModel>> projectIdRoleMap,
	                                 final Map<Integer, List<SkillModel>> projectIdToSkillMap) {
		return createWithSiteNames(projects, projectIdRoleMap, projectIdToSkillMap, null);
	}

	public List<ProjectModel> createWithSiteNames(final List<Project> projects,
	                                              final Map<Integer, List<RoleModel>> projectIdRoleMap,
	                                              final Map<Integer, List<SkillModel>> projectIdToSkillMap,
	                                              final Map<Integer, AccountModel> accountModels) {
		if (CollectionUtils.isEmpty(projects)) {
			return Collections.emptyList();
		}

		List<ProjectModel> projectModels = new ArrayList<>();
		for (Project project : projects) {
			projectModels.add(createWithSiteName(
					project,
					projectIdRoleMap != null ? projectIdRoleMap.get(project.getId()) : null,
					projectIdToSkillMap != null ? projectIdToSkillMap.get(project.getId()) : null,
					accountModels != null ? accountModels.get(project.getAccountId()) : null
			));
		}

		return projectModels;
	}

	public ProjectModel create(final Project project, final List<? extends RoleModel> roles, final List<? extends SkillModel> skills) {
		return createWithSiteName(project, roles, skills, null);
	}

	public ProjectModel createWithSiteName(final Project project,
	                                       final List<? extends RoleModel> roles,
	                                       final List<? extends SkillModel> skills,
	                                       final AccountModel accountModel) {

		ProjectModel projectModel = new ProjectModel();

		projectModel.setId(project.getId());
		projectModel.setName(project.getName());
		projectModel.setRoles(roles);
		projectModel.setSkills(skills);
		projectModel.setStartDate(project.getStartDate());
		projectModel.setEndDate(project.getEndDate());

		if (accountModel != null) {
			projectModel.setSite(accountModel.getName());
		}

		return projectModel;
	}

}
