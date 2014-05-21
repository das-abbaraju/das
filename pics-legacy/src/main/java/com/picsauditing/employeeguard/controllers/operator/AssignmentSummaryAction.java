package com.picsauditing.employeeguard.controllers.operator;

import com.google.gson.Gson;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.exceptions.NoOperatorForCorporateException;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.StatusSummary;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.status.StatusCalculatorService;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

public class AssignmentSummaryAction extends PicsRestActionSupport {

	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	public String summary() throws NoRightsException {
		jsonString = new Gson().toJson(buildStatusSummary(getSiteIdForSummary()));

		return JSON_STRING;
	}

	private StatusSummary buildStatusSummary(final int siteId) {
		Map<Employee, Set<AccountSkill>> allEmployeeSkillsForSite = assignmentService.getEmployeeSkillsForSite(siteId);
		Map<Employee, SkillStatus> employeeStatuses = statusCalculatorService
				.getEmployeeStatusRollUpForSkills(allEmployeeSkillsForSite.keySet(), allEmployeeSkillsForSite);

		StatusSummary summary = ModelFactory.getStatusSummaryFactory().create(employeeStatuses);
        summary.setId(siteId);

        return summary;
	}

	private int getSiteIdForSummary() throws NoOperatorForCorporateException, NoRightsException {
		if (!permissions.isOperatorCorporate()) {
			throw new NoRightsException("Operator or Corporate");
		}

		int siteId = NumberUtils.toInt(id);
		if (permissions.isOperator()) {
			if (siteId > 0 && permissions.getAccountId() != siteId) {
				throw new NoRightsException("Corporate");
			}

			return permissions.getAccountId();
		}

		if (permissions.getOperatorChildren().contains(siteId)) {
			return siteId;
		}

		throw new NoOperatorForCorporateException("Site " + siteId + " not viewable by Corporate " + permissions.getAccountId());
	}

}
