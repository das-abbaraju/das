package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.ProjectAssignmentModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class ProjectAssignmentModelFactory {

	public ProjectAssignmentModel create(final Project project, final Map<Employee, SkillStatus> employeeStatuses) {
		ProjectAssignmentModel projectAssignmentModel = new ProjectAssignmentModel();

		projectAssignmentModel.setId(project.getId());
		projectAssignmentModel.setName(project.getName());
		projectAssignmentModel.setLocation(project.getLocation());
		projectAssignmentModel.setStartDate(project.getStartDate());
		projectAssignmentModel.setEndDate(project.getEndDate());

		StatusSummaryDecorator.addStatusSummary(projectAssignmentModel, employeeStatuses);

		return projectAssignmentModel;
	}

	public List<ProjectAssignmentModel> createList(final Map<Project, Set<Employee>> projectEmployees,
												   final Map<Employee, SkillStatus> employeeStatuses) {
		if (MapUtils.isEmpty(projectEmployees)) {
			return Collections.emptyList();
		}

		List<ProjectAssignmentModel> models = new ArrayList<>();
		for (Project project : projectEmployees.keySet()) {
			models.add(create(project, employeeStatuses));
		}

		return models;
	}

}
