package com.picsauditing.employeeguard.controllers.operator;

import com.google.gson.Gson;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.OperatorSiteAssignmentStatus;
import com.picsauditing.employeeguard.models.ProjectAssignmentModel;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.ProjectAssignmentService;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
public class SummaryAction extends PicsRestActionSupport {

	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private ProjectAssignmentService projectAssignmentService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	/* pages */

	public String index() {
		Map<Employee, Set<AccountSkill>> allEmployeeSkillsForSite = assignmentService.getEmployeeSkillsForSite(permissions.getAccountId());
		Map<Employee, SkillStatus> employeeStatuses = statusCalculatorService.getEmployeeStatusRollUpForSkills(allEmployeeSkillsForSite.keySet(), allEmployeeSkillsForSite);

		Map<Project, Map<Employee, Set<AccountSkill>>> projectEmployeeSkills = projectAssignmentService.getEmployeeSkillsForProjectsUnderSite(permissions.getAccountId());
		Map<Project, List<SkillStatus>> projectSkillStatuses = statusCalculatorService.getAllSkillStatusesForEntity(projectEmployeeSkills);

		List<ProjectAssignmentModel> projectAssignments = ModelFactory.getProjectAssignmentModelFactory().createList(projectSkillStatuses);

		OperatorSiteAssignmentStatus siteStatus = ModelFactory.getOperatorSiteAssignmentStatusFactory().create(
				permissions.getAccountId(),
				permissions.getAccountName(),
				allEmployeeSkillsForSite.size(),
				projectAssignments,
				employeeStatuses);

		jsonString = new Gson().toJson(siteStatus);

		return JSON_STRING;
	}
}
