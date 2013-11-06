package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.Project;

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

	public Project build() {
		return project;
	}
}
