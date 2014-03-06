package com.picsauditing.employeeguard.models;

public class ProjectAssignmentModel extends ProjectModel implements StatusSummarizable {

	private StatusSummary status;

	@Override
	public StatusSummary getStatus() {
		return status;
	}

	@Override
	public void setStatus(StatusSummary status) {
		this.status = status;
	}
}
