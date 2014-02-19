package com.picsauditing.employeeguard.viewmodel.employee;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.Map;

public class EmployeeProjectAndRoleStatus {
	private final Map<Project, SkillStatus> projectStatuses;
	private final Map<Role, SkillStatus> roleStatuses;

	private EmployeeProjectAndRoleStatus(Builder builder) {
		this.projectStatuses = Utilities.unmodifiableMap(builder.projectStatuses);
		this.roleStatuses = Utilities.unmodifiableMap(builder.roleStatuses);
	}

	public Map<Project, SkillStatus> getProjectStatuses() {
		return projectStatuses;
	}

	public Map<Role, SkillStatus> getRoleStatuses() {
		return roleStatuses;
	}

	public static class Builder {
		private Map<Project, SkillStatus> projectStatuses;
		private Map<Role, SkillStatus> roleStatuses;

		public Builder projectStatuses(Map<Project, SkillStatus> projectStatuses) {
			this.projectStatuses = projectStatuses;
			return this;
		}

		public Builder roleStatuses(Map<Role, SkillStatus> roleStatuses) {
			this.roleStatuses = roleStatuses;
			return this;
		}

		public EmployeeProjectAndRoleStatus build() {
			return new EmployeeProjectAndRoleStatus(this);
		}
	}
}
