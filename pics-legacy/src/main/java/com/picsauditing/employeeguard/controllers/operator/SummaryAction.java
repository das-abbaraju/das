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
	private StatusCalculatorService statusCalculatorService;

	/* pages */

	public String index() {
		Map<Employee, Set<AccountSkill>> allEmployeeSkillsForSite = assignmentService.getEmployeeSkillsForSite(permissions.getAccountId());
		Map<Project, Set<Employee>> projectEmployees = assignmentService.getEmployeesAssignedToProjects(permissions.getAccountId());
		Map<Employee, SkillStatus> employeeStatuses = statusCalculatorService.getEmployeeStatusRollUpForSkills(allEmployeeSkillsForSite.keySet(), allEmployeeSkillsForSite);

		List<ProjectAssignmentModel> projectAssignments = ModelFactory.getProjectAssignmentModelFactory().createList(projectEmployees, employeeStatuses);

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
