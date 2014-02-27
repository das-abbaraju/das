package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectRole;
import com.picsauditing.employeeguard.entities.ProjectSkill;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.viewmodel.employee.ProjectDetailModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProjectDetailModelFactory {

	public List<ProjectDetailModel> create(final Map<Project, SkillStatus> projectStatusMap) {
		List<ProjectDetailModel> projects = new ArrayList<>();
		for (Project project : projectStatusMap.keySet()) {
			projects.add(create(project, projectStatusMap.get(project)));
		}

		return projects;
	}

	public ProjectDetailModel create(final Project project, final SkillStatus skillStatus) {
		Set<Integer> skillIds = Utilities.getIdsFromCollection(project.getSkills(), new Utilities.Identitifable<ProjectSkill, Integer>() {
			@Override
			public Integer getId(ProjectSkill element) {
				return element.getSkill().getId();
			}
		});

		Set<Integer> roleIds = Utilities.getIdsFromCollection(project.getRoles(), new Utilities.Identitifable<ProjectRole, Integer>() {
			@Override
			public Integer getId(ProjectRole element) {
				return element.getRole().getId();
			}
		});

		return new ProjectDetailModel.Builder()
				.id(project.getId())
				.name(project.getName())
				.roles(roleIds)
				.skills(skillIds)
				.build();
	}
}
