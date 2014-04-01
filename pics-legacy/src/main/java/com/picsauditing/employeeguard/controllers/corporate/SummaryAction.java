package com.picsauditing.employeeguard.controllers.corporate;

import com.google.gson.Gson;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.StatusSummary;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

public class SummaryAction extends PicsRestActionSupport {

	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	public String summary() throws NoRightsException {
		int siteId = NumberUtils.toInt(id);
		if (!isCorporateOrCorporateOperator(siteId)) {
			throw new NoRightsException("You must be part of the corporate umbrella");
		}

		jsonString = new Gson().toJson(buildStatusSummary(siteId));

		return JSON_STRING;
	}

	private boolean isCorporateOrCorporateOperator(int siteId) {
		return permissions.isCorporate() && permissions.getOperatorChildren().contains(siteId);
	}

	private StatusSummary buildStatusSummary(final int siteId) {
		Map<Employee, Set<AccountSkill>> allEmployeeSkillsForSite = assignmentService.getEmployeeSkillsForSite(siteId);
		Map<Employee, SkillStatus> employeeStatuses = statusCalculatorService
				.getEmployeeStatusRollUpForSkills(allEmployeeSkillsForSite.keySet(), allEmployeeSkillsForSite);

		return ModelFactory.getStatusSummaryFactory().create(employeeStatuses);
	}

}
