package com.picsauditing.employeeguard.controllers.operator;

import com.google.gson.Gson;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.OperatorSiteAssignmentStatus;
import com.picsauditing.employeeguard.models.ProjectAssignmentModel;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.ProjectAssignmentService;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.IdNameModel;
import net.sf.json.JSONArray;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
public class SummaryAction extends PicsRestActionSupport {

	@Autowired
	private AccountService accountService;
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private ProjectAssignmentService projectAssignmentService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	/* pages */

	public String index() {
		OperatorSiteAssignmentStatus siteStatus = buildOperatorSiteAssignmentStatus(permissions.getAccountId());

		jsonString = new Gson().toJson(siteStatus);

		return JSON_STRING;
	}

	private OperatorSiteAssignmentStatus buildOperatorSiteAssignmentStatus(int siteId) {
		Map<Employee, Set<AccountSkill>> allEmployeeSkillsForSite = assignmentService.getEmployeeSkillsForSite(siteId);
		Map<Employee, SkillStatus> employeeStatuses = statusCalculatorService.getEmployeeStatusRollUpForSkills(allEmployeeSkillsForSite.keySet(), allEmployeeSkillsForSite);

		Map<Project, Map<Employee, Set<AccountSkill>>> projectEmployeeSkills = projectAssignmentService.getEmployeeSkillsForProjectsUnderSite(siteId);
		Map<Project, List<SkillStatus>> projectSkillStatuses = statusCalculatorService.getAllSkillStatusesForEntity(projectEmployeeSkills);

		List<ProjectAssignmentModel> projectAssignments = ModelFactory.getProjectAssignmentModelFactory().createList(projectSkillStatuses);

		return ModelFactory.getOperatorSiteAssignmentStatusFactory().create(
				siteId,
				permissions.getAccountName(),
				allEmployeeSkillsForSite.size(),
				projectAssignments,
				employeeStatuses);
	}

	public String list() throws NoRightsException {
		if (permissions.isOperator()) {
			jsonString = "[]";
			return JSON_STRING;
		}

		if (!permissions.isCorporate()) {
			throw new NoRightsException("You must be a corporate user");
		}

		List<IdNameModel> idNameModels = getIdNameModels();

		jsonString = new Gson().toJson(idNameModels);

		return JSON_STRING;
	}

	private List<IdNameModel> getIdNameModels() {
		List<AccountModel> childOperators = accountService.getChildOperators(permissions.getAccountId());

		List<IdNameModel> idNameModels = new ArrayList<>();
		for (AccountModel childOperator : childOperators) {
			idNameModels.add(new IdNameModel.Builder()
					.id(Integer.toString(childOperator.getId()))
					.name(childOperator.getName())
					.build());
		}
		return idNameModels;
	}

	public String summary() throws NoRightsException {
		int siteId = NumberUtils.toInt(id);
		if (permissions.isCorporate() && permissions.getOperatorChildren().contains(siteId)) {
			OperatorSiteAssignmentStatus siteStatus = buildOperatorSiteAssignmentStatus(siteId);
			jsonString = new Gson().toJson(siteStatus);

			return JSON_STRING;
		}

		throw new NoRightsException("You must be part of the corporate umbrella");
	}
}
