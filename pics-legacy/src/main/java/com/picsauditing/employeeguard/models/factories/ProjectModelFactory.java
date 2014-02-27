package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.ProjectModel;
import com.picsauditing.employeeguard.models.RoleModel;
import com.picsauditing.employeeguard.models.SkillModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ProjectModelFactory {

	public List<ProjectModel> create(final List<Project> projects,
									 final Map<Integer, List<RoleModel>> projectIdRoleMap,
									 final Map<Integer, List<SkillModel>> projectIdToSkillMap) {
		if (CollectionUtils.isEmpty(projects)) {
			return Collections.emptyList();
		}

		List<ProjectModel> projectModels = new ArrayList<>();
		for (Project project : projects) {
			projectModels.add(create(project, projectIdRoleMap.get(project.getId()),
					projectIdToSkillMap.get(project.getId())));
		}

		return projectModels;
	}

	public ProjectModel create(final Project project, final List<? extends RoleModel> roles, final List<? extends SkillModel> skills) {
		ProjectModel projectModel = new ProjectModel();
		projectModel.setId(project.getId());
		projectModel.setName(project.getName());
		projectModel.setRoles(roles);
		projectModel.setSkills(skills);
		return projectModel;
	}

}
