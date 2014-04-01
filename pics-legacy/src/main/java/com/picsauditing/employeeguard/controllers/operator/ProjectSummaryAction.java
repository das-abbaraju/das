package com.picsauditing.employeeguard.controllers.operator;

import com.google.gson.Gson;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.ProjectAssignmentModel;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProjectSummaryAction extends PicsRestActionSupport {

	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	public String summary() {
		jsonString = new Gson().toJson(buildProjectAssignmentModels(permissions.getAccountId()));

		return JSON_STRING;
	}

	private List<ProjectAssignmentModel> buildProjectAssignmentModels(final int siteId) {
		Map<Project, Map<Employee, Set<AccountSkill>>> projectEmployeeSkills = assignmentService
				.getEmployeeSkillsForProjectsUnderSite(siteId);
		Map<Project, List<SkillStatus>> projectSkillStatuses = statusCalculatorService
				.getAllSkillStatusesForEntity(projectEmployeeSkills);

		return ModelFactory.getProjectAssignmentModelFactory().createList(projectSkillStatuses);
	}

}
