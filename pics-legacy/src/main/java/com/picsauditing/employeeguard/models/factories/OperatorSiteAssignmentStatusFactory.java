package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.OperatorSiteAssignmentStatus;
import com.picsauditing.employeeguard.models.ProjectAssignmentModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.List;
import java.util.Map;

public class OperatorSiteAssignmentStatusFactory {

	public OperatorSiteAssignmentStatus create(final int siteId,
											   final String siteName,
											   final int employeeCount,
											   final List<ProjectAssignmentModel> projects,
											   final Map<Employee, SkillStatus> employeeStatuses) {

		OperatorSiteAssignmentStatus status = new OperatorSiteAssignmentStatus();
		status.setId(siteId);
		status.setName(siteName);
		status.setProjects(projects);

		StatusSummaryDecorator.addStatusSummary(status, employeeStatuses);
		status.setEmployees(employeeCount);

		return status;
	}

}
