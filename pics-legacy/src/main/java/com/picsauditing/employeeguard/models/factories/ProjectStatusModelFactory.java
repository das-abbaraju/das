package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.ProjectStatusModel;
import com.picsauditing.employeeguard.models.RoleModel;
import com.picsauditing.employeeguard.models.SkillModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.List;

public class ProjectStatusModelFactory extends ProjectModelFactory {

	public ProjectStatusModel create(final Project project,
									 final List<RoleModel> roles,
									 final List<SkillModel> skills,
									 final SkillStatus status) {
		ProjectStatusModel projectStatusModel = new ProjectStatusModel(super.create(project, roles, skills));
		projectStatusModel.setStatus(status);
		return projectStatusModel;
	}

}
