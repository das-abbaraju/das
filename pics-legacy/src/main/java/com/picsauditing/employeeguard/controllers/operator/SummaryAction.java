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
import com.picsauditing.employeeguard.models.UserModel;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.ProjectAssignmentService;
import com.picsauditing.employeeguard.services.status.StatusCalculatorService;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.models.AccountType;
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
		jsonString = new Gson().toJson(buildOperatorSiteAssignmentStatus(permissions.getAccountId()));

		return JSON_STRING;
	}

	public String whoAmI() throws NoRightsException {
		if (!permissions.isOperatorCorporate()) {
			throw new NoRightsException("Operator or Corporate");
		}

		AccountType accountType = AccountType.OPERATOR;
		if (permissions.isCorporate()) {
			accountType = AccountType.CORPORATE;
		}

		UserModel userModel = ModelFactory.getUserModelFactory().create(
				permissions.getAppUserID(),
				permissions.getAccountId(),
				permissions.getName(),
				accountType);

		jsonString = new Gson().toJson(userModel);

		return JSON_STRING;
	}

	/**
	 * This method is deprecated in favor of having separate actions for the Project List and another
	 * for the overall Employee Status
	 *
	 * @param siteId
	 * @return
	 */
	@Deprecated
	private OperatorSiteAssignmentStatus buildOperatorSiteAssignmentStatus(int siteId) {
		Map<Employee, Set<AccountSkill>> allEmployeeSkillsForSite = assignmentService.getEmployeeSkillsForSite(siteId);
		Map<Employee, SkillStatus> employeeStatuses = statusCalculatorService
				.getEmployeeStatusRollUpForSkills(allEmployeeSkillsForSite.keySet(), allEmployeeSkillsForSite);

		Map<Project, Map<Employee, Set<AccountSkill>>> projectEmployeeSkills = projectAssignmentService
				.getEmployeeSkillsForProjectsUnderSite(siteId);
		Map<Project, List<SkillStatus>> projectSkillStatuses = statusCalculatorService
				.getAllSkillStatusesForEntity(projectEmployeeSkills);

		List<ProjectAssignmentModel> projectAssignments = ModelFactory.getProjectAssignmentModelFactory()
				.createList(projectSkillStatuses);

		return ModelFactory.getOperatorSiteAssignmentStatusFactory().create(
				siteId,
				permissions.getAccountName(),
				allEmployeeSkillsForSite.size(),
				projectAssignments,
				employeeStatuses);
	}

}
