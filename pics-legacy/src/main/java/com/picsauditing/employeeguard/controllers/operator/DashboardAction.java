package com.picsauditing.employeeguard.controllers.operator;

import com.google.gson.Gson;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.OperatorSiteAssignmentStatus;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
public class DashboardAction extends PicsRestActionSupport {

	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	/* pages */

	public String index() {
		Map<Employee, Set<AccountSkill>> allEmployeeSkillsForSite = assignmentService.getEmployeeSkillsForSite(permissions.getAccountId());
		Map<Employee, SkillStatus> employeeStatuses = statusCalculatorService.getEmployeeStatusRollUpForSkills(allEmployeeSkillsForSite.keySet(), allEmployeeSkillsForSite);

		final List<OperatorSiteAssignmentStatus> statuses = ModelFactory.getOperatorSiteAssignmentStatusFactory().create(
				permissions.getAccountId(),
				permissions.getAccountName(),
				employeeStatuses);

		jsonString = new Gson().toJson(new Dashboard(statuses));

		return JSON_STRING;
	}

	private class Dashboard {
		private final List<OperatorSiteAssignmentStatus> sites;

		public Dashboard(final List<OperatorSiteAssignmentStatus> sites) {
			this.sites = sites;
		}

		public List<OperatorSiteAssignmentStatus> getSites() {
			return sites;
		}
	}
}
