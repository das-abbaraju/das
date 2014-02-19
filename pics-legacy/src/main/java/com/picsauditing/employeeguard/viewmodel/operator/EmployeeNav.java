package com.picsauditing.employeeguard.viewmodel.operator;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class EmployeeNav {

	private final SkillStatus overallStatus;
	private final List<NavItem> roles;
	private final List<NavItem> projects;

	public EmployeeNav(final Builder builder) {
		this.overallStatus = builder.overallStatus;

		this.roles = CollectionUtils.isEmpty(builder.roles)
				? Collections.<NavItem>emptyList() : builder.roles;

		this.projects = CollectionUtils.isEmpty(builder.projects)
				? Collections.<NavItem>emptyList() : builder.projects;
	}

	public SkillStatus getOverallStatus() {
		return overallStatus;
	}

	public List<NavItem> getRoles() {
		return roles;
	}

	public List<NavItem> getProjects() {
		return projects;
	}

	public static class Builder {

		private SkillStatus overallStatus;
		private List<NavItem> roles;
		private List<NavItem> projects;

		public Builder overallStatus(SkillStatus overallStatus) {
			this.overallStatus = overallStatus;
			return this;
		}

		public Builder roles(List<NavItem> roles) {
			this.roles = roles;
			return this;
		}

		public Builder projects(List<NavItem> projects) {
			this.projects = projects;
			return this;
		}

		public EmployeeNav build() {
			return new EmployeeNav(this);
		}
	}

}
