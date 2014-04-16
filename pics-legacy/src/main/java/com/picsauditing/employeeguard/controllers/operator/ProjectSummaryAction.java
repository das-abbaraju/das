package com.picsauditing.employeeguard.controllers.operator;

import com.google.gson.Gson;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.exceptions.NoOperatorForCorporateException;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.ProjectAssignmentModel;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProjectSummaryAction extends PicsRestActionSupport {

	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	public String summary() throws NoOperatorForCorporateException, NoRightsException {
		jsonString = new Gson().toJson(buildProjectAssignmentModels(getSiteIdForSummary()));

		return JSON_STRING;
	}

	private List<ProjectAssignmentModel> buildProjectAssignmentModels(final int siteId) {
		Map<Project, Map<Employee, Set<AccountSkill>>> projectEmployeeSkills = assignmentService
				.getEmployeeSkillsForProjectsUnderSite(siteId);
		Map<Project, List<SkillStatus>> projectSkillStatuses = statusCalculatorService
				.getAllSkillStatusesForEntity(projectEmployeeSkills);

		return ModelFactory.getProjectAssignmentModelFactory().createList(projectSkillStatuses);
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