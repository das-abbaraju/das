package com.picsauditing.employeeguard.viewmodel.contractor;

import com.picsauditing.employeeguard.forms.contractor.ContractorProjectForm;

public class ProjectStatisticsModel {

	private final ContractorProjectForm project;
	private final ProjectAssignmentBreakdown assignments;

	public ProjectStatisticsModel(final ContractorProjectForm project, final ProjectAssignmentBreakdown assignments) {
		this.project = project;
		this.assignments = assignments;
	}

	public ContractorProjectForm getProject() {
		return project;
	}

	public ProjectAssignmentBreakdown getAssignments() {
		return assignments;
	}
}
