package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.ProjectStatusModel;
import com.picsauditing.employeeguard.models.RequiredSkills;
import com.picsauditing.employeeguard.models.RoleStatusModel;
import com.picsauditing.employeeguard.models.SkillStatusModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class ProjectStatusModelFactory extends ProjectModelFactory {

	public List<ProjectStatusModel> create(final Collection<Project> projects,
										   final Map<Integer, List<RoleStatusModel>> projectRoles,
										   final Map<Integer, RequiredSkills> requiredSkillsMap,
										   final Map<Project, SkillStatus> projectStatuses) {
		if (CollectionUtils.isEmpty(projects)) {
			return Collections.emptyList();
		}

		List<ProjectStatusModel> projectStatusModels = new ArrayList<>();
		for (Project project : projects) {
			int projectId = project.getId();
			projectStatusModels.add(create(
					project,
					projectRoles.get(projectId),
					requiredSkillsMap.get(projectId),
					projectStatuses.get(project)
			));
		}

		return projectStatusModels;
	}

	public ProjectStatusModel create(final Project project,
									 final List<RoleStatusModel> roles,
									 final RequiredSkills requiredSkills,
									 final SkillStatus status) {
		ProjectStatusModel projectStatusModel = new ProjectStatusModel();
		projectStatusModel.setId(project.getId());
		projectStatusModel.setName(project.getName());
		projectStatusModel.setRoles(roles);
		projectStatusModel.setSkills(requiredSkills);
		projectStatusModel.setStatus(status);
		return projectStatusModel;
	}

}
