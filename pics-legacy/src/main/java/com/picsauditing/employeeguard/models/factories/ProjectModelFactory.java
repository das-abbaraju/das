package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.ProjectModel;
import com.picsauditing.employeeguard.models.RoleModel;
import com.picsauditing.employeeguard.models.SkillModel;

import java.util.List;

public class ProjectModelFactory {

	public ProjectModel create(final Project project, final List<RoleModel> roles, final List<SkillModel> skills) {
		ProjectModel projectModel = new ProjectModel();
		projectModel.setId(project.getId());
		projectModel.setName(project.getName());
		projectModel.setRoles(roles);
		projectModel.setSkills(skills);
		return projectModel;
	}

}
