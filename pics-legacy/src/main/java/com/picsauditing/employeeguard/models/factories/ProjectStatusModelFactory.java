package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.ProjectStatusModel;
import com.picsauditing.employeeguard.models.RoleModel;
import com.picsauditing.employeeguard.models.SkillModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ProjectStatusModelFactory extends ProjectModelFactory {

	public List<ProjectStatusModel> create(final List<Project> projects,
										   final Map<Integer, List<RoleModel>> projectRoles,
										   final Map<Integer, List<SkillModel>> projectSkills,
										   final Map<Integer, SkillStatus> projectStatuses) {
		if (CollectionUtils.isEmpty(projects)) {
			return Collections.emptyList();
		}

		List<ProjectStatusModel> projectStatusModels = new ArrayList<>();
		for (Project project : projects) {
			int projectId = project.getId();
			projectStatusModels.add(create(project, projectRoles.get(projectId), projectSkills.get(projectId), projectStatuses.get(projectId)));
		}

		return projectStatusModels;
	}

	public ProjectStatusModel create(final Project project,
									 final List<RoleModel> roles,
									 final List<SkillModel> skills,
									 final SkillStatus status) {
		ProjectStatusModel projectStatusModel = new ProjectStatusModel(super.create(project, roles, skills));
		projectStatusModel.setStatus(status);
		return projectStatusModel;
	}

}
