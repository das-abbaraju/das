package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.ProjectAssignmentModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class ProjectAssignmentModelFactory {

	public ProjectAssignmentModel create(final Project project, final Map<Employee, SkillStatus> employeeStatuses) {
		ProjectAssignmentModel projectAssignmentModel = createProjectAssignmentModel(project);

		StatusSummaryDecorator.addStatusSummary(projectAssignmentModel, employeeStatuses);

		return projectAssignmentModel;
	}

	private ProjectAssignmentModel createProjectAssignmentModel(Project project) {
		ProjectAssignmentModel projectAssignmentModel = new ProjectAssignmentModel();

		projectAssignmentModel.setId(project.getId());
		projectAssignmentModel.setName(project.getName());
		projectAssignmentModel.setLocation(project.getLocation());
		projectAssignmentModel.setStartDate(project.getStartDate());
		projectAssignmentModel.setEndDate(project.getEndDate());

		return projectAssignmentModel;
	}

	public List<ProjectAssignmentModel> createList(final Map<Project, Set<Employee>> projectEmployees,
												   final Map<Employee, SkillStatus> employeeStatuses) {
		if (MapUtils.isEmpty(projectEmployees)) {
			return Collections.emptyList();
		}

		List<ProjectAssignmentModel> models = new ArrayList<>();
		for (Project project : projectEmployees.keySet()) {
			Map<Employee, SkillStatus> projectEmployeeStatuses = new HashMap<>(employeeStatuses);
			projectEmployeeStatuses.keySet().retainAll(projectEmployees.get(project));

			models.add(create(project, projectEmployeeStatuses));
		}

		return models;
	}

	public List<ProjectAssignmentModel> createList(Map<Project, List<SkillStatus>> projectSkillStatuses) {
		if (MapUtils.isEmpty(projectSkillStatuses)) {
			return Collections.emptyList();
		}

		List<ProjectAssignmentModel> models = new ArrayList<>();
		for (Project project : projectSkillStatuses.keySet()) {
			ProjectAssignmentModel model = createProjectAssignmentModel(project);

			models.add(StatusSummaryDecorator.addStatusSummaryRollup(model, projectSkillStatuses.get(project)));
		}

		Collections.sort(models);
		return models;
	}
}
