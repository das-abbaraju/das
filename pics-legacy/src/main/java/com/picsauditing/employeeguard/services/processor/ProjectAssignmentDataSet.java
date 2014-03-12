package com.picsauditing.employeeguard.services.processor;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ProjectAssignmentDataSet {

	private final List<Project> projects;
	private final Map<Project, Set<Employee>> projectEmployees;
	private final Map<Role, Set<Employee>> roleEmployees;
	private final Map<Project, Set<AccountSkill>> projectRequiredSkills;
	private final Map<Project, Set<Role>> projectRoles;
	private final Map<Role, Set<AccountSkill>> projectRoleSkills;
	private final Set<AccountSkill> siteAndCorporateRequiredSkills;

	public ProjectAssignmentDataSet(final Builder builder) {
		this.projects = Collections.unmodifiableList(builder.projects);
		this.projectEmployees = Collections.unmodifiableMap(builder.projectEmployees);
		this.roleEmployees = Collections.unmodifiableMap(builder.roleEmployees);
		this.projectRequiredSkills = Collections.unmodifiableMap(builder.projectRequiredSkills);
		this.projectRoles = Collections.unmodifiableMap(builder.projectRoles);
		this.projectRoleSkills = Collections.unmodifiableMap(builder.projectRoleSkills);
		this.siteAndCorporateRequiredSkills = Collections.unmodifiableSet(builder.siteAndCorporateRequiredSkills);
	}

	public List<Project> getProjects() {
		return projects;
	}

	public Map<Project, Set<Employee>> getProjectEmployees() {
		return projectEmployees;
	}

	public Map<Role, Set<Employee>> getRoleEmployees() {
		return roleEmployees;
	}

	public Map<Project, Set<AccountSkill>> getProjectRequiredSkills() {
		return projectRequiredSkills;
	}

	public Map<Project, Set<Role>> getProjectRoles() {
		return projectRoles;
	}

	public Map<Role, Set<AccountSkill>> getProjectRoleSkills() {
		return projectRoleSkills;
	}

	public Set<AccountSkill> getSiteAndCorporateRequiredSkills() {
		return siteAndCorporateRequiredSkills;
	}

	public static class Builder {
		private List<Project> projects;
		private Map<Project, Set<Employee>> projectEmployees;
		private Map<Role, Set<Employee>> roleEmployees;
		private Map<Project, Set<AccountSkill>> projectRequiredSkills;
		private Map<Project, Set<Role>> projectRoles;
		private Map<Role, Set<AccountSkill>> projectRoleSkills;
		private Set<AccountSkill> siteAndCorporateRequiredSkills;

		public Builder projects(List<Project> projects) {
			this.projects = projects;
			return this;
		}

		public Builder projectEmployees(Map<Project, Set<Employee>> projectEmployees) {
			this.projectEmployees = projectEmployees;
			return this;
		}

		public Builder roleEmployees(Map<Role, Set<Employee>> roleEmployees) {
			this.roleEmployees = roleEmployees;
			return this;
		}

		public Builder projectRequiredSkills(Map<Project, Set<AccountSkill>> projectRequiredSkills) {
			this.projectRequiredSkills = projectRequiredSkills;
			return this;
		}

		public Builder projectRoles(Map<Project, Set<Role>> projectRoles) {
			this.projectRoles = projectRoles;
			return this;
		}

		public Builder projectRoleSkills(Map<Role, Set<AccountSkill>> projectRoleSkills) {
			this.projectRoleSkills = projectRoleSkills;
			return this;
		}

		public Builder siteAndCorporateRequiredSkills(Set<AccountSkill> siteAndCorporateRequiredSkills) {
			this.siteAndCorporateRequiredSkills = siteAndCorporateRequiredSkills;
			return this;
		}

		public ProjectAssignmentDataSet build() {
			return new ProjectAssignmentDataSet(this);
		}
	}
}
