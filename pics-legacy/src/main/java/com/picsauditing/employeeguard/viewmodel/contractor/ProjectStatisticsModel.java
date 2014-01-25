package com.picsauditing.employeeguard.viewmodel.contractor;

import com.picsauditing.employeeguard.forms.contractor.ContractorProjectForm;

public class ProjectStatisticsModel {

	private final ContractorProjectForm project;
	private final ProjectAssignmentBreakdown assignments;

	public ProjectStatisticsModel(final Builder builder) {
		this.project = builder.project;
		this.assignments = builder.assignments;
	}

	public ContractorProjectForm getProject() {
		return project;
	}

	public ProjectAssignmentBreakdown getAssignments() {
		return assignments;
	}

	public static class Builder {
		private ContractorProjectForm project;
		private ProjectAssignmentBreakdown assignments;

		public Builder project(ContractorProjectForm project) {
			this.project = project;
			return this;
		}

		public Builder assignments(ProjectAssignmentBreakdown assignments) {
			this.assignments = assignments;
			return this;
		}

		public ProjectStatisticsModel build() {
			return new ProjectStatisticsModel(this);
		}
	}
}
