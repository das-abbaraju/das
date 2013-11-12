package com.picsauditing.employeeguard.forms.operator;

import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectRole;
import org.apache.commons.lang3.ArrayUtils;

public class ProjectRolesForm {
	private String[] roles;

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	public Project buildProject(int accountId) {
		Project project = new Project();

		project.setAccountId(accountId);

		if (ArrayUtils.isNotEmpty(roles)) {
			for (String roleName : roles) {
				AccountGroup group = new AccountGroup();
				group.setName(roleName);

				ProjectRole projectRole = new ProjectRole(project, group);
				project.getRoles().add(projectRole);
			}
		}

		return project;
	}

	public static class Builder {
		private Project project;

		public Builder project(Project project) {
			this.project = project;

			return this;
		}

		public ProjectRolesForm build() {
			ProjectRolesForm projectForm = new ProjectRolesForm();

			if (project != null) {
				int counter = 0;

				projectForm.roles = new String[project.getRoles().size()];
				for (ProjectRole projectRole : project.getRoles()) {
					projectForm.roles[counter++] = projectRole.getRole().getName();
				}
			}

			return projectForm;
		}
	}
}