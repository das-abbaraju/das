package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectCompany;
import com.picsauditing.employeeguard.entities.ProjectRole;
import com.picsauditing.employeeguard.entities.ProjectSkill;

import java.util.List;

public class ProjectBuilder extends AbstractBaseEntityBuilder<Project, ProjectBuilder> {

	public ProjectBuilder() {
		this.entity = new Project();
		that = this;
	}

	public ProjectBuilder accountId(int accountId) {
		entity.setAccountId(accountId);
		return this;
	}

	public ProjectBuilder name(String name) {
		entity.setName(name);

		return this;
	}

	public ProjectBuilder location(String location) {
		entity.setLocation(location);

		return this;
	}

	public ProjectBuilder skills(List<ProjectSkill> projectSkills) {
		entity.setSkills(projectSkills);
		return this;
	}

	public ProjectBuilder roles(List<ProjectRole> projectRoles) {
		entity.setRoles(projectRoles);
		return this;
	}

	public ProjectBuilder companies(List<ProjectCompany> companies) {
		entity.setCompanies(companies);
		return this;
	}

	public Project build() {
		return entity;
	}
}
