package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectRole;
import com.picsauditing.employeeguard.entities.ProjectSkill;

import java.util.List;

public class ProjectBuilder {
	private Project project;

	public ProjectBuilder() {
		this.project = new Project();
	}

	public ProjectBuilder name(String name) {
		project.setName(name);

		return this;
	}

	public ProjectBuilder location(String location) {
		project.setLocation(location);

		return this;
	}

    public ProjectBuilder skills(List<ProjectSkill> projectSkills) {
        project.setSkills(projectSkills);
        return this;
    }

    public ProjectBuilder roles(List<ProjectRole> projectRoles) {
        project.setRoles(projectRoles);
        return this;
    }

	public Project build() {
		return project;
	}
}
