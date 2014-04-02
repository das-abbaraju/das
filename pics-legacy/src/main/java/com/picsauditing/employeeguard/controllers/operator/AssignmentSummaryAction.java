package com.picsauditing.employeeguard.controllers.operator;

import com.google.gson.Gson;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.engine.SkillEngine;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.exceptions.NoOperatorForCorporateException;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.ProjectAssignmentModel;
import com.picsauditing.employeeguard.models.StatusSummary;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssignmentSummaryAction extends PicsRestActionSupport {

	@Autowired
	private AccountService accountService;
	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private ProjectEntityService projectEntityService;
	@Autowired
	private SkillEngine skillEngine;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	public String summary() throws NoRightsException {
		if (!permissions.isOperatorCorporate()) {
			throw new NoRightsException("Must be an client site or corporate user");
		}

		StatusSummary statusSummary = buildStatusSummary(getSiteIdForSummary());
		jsonString = new Gson().toJson(statusSummary);

		return JSON_STRING;
	}

	public String projects() throws NoRightsException {
		if (!permissions.isOperatorCorporate()) {
			throw new NoRightsException("Must be an client site or corporate user");
		}

		List<ProjectAssignmentModel> projectAssignments = buildProjectAssignmentModels(getSiteIdForSummary());
		jsonString = new Gson().toJson(projectAssignments);

		return JSON_STRING;
	}

	private StatusSummary buildStatusSummary(final int siteId) {
		AccountModel accountModel = accountService.getAccountById(siteId);
		List<Integer> contractorIds = accountService.getContractorIds(siteId);
		List<Employee> employees = employeeEntityService.getEmployeesAssignedToSite(contractorIds, siteId);

		Map<Employee, Set<AccountSkill>> employeeSkills = skillEngine.getEmployeeSkillsMapForAccount(employees, accountModel);
		Map<Employee, SkillStatus> employeeStatuses = statusCalculatorService.getEmployeeStatusRollUpForSkills(employees, employeeSkills);

		return ModelFactory.getStatusSummaryFactory().create(employeeStatuses);
	}

	private List<ProjectAssignmentModel> buildProjectAssignmentModels(int siteId) {
		List<Project> projects = projectEntityService.getAllProjectsForSite(siteId);
		Map<Project, Set<Employee>> projectEmployees = employeeEntityService.getEmployeesByProjects(projects);
		Set<Employee> employees = PicsCollectionUtil.mergeCollectionOfCollections(projectEmployees.values());

		Map<Project, Map<Employee, Set<AccountSkill>>> projectEmployeeSkills =
				skillEngine.getEmployeeSkillsMapForProjects(employees, projects);

		Map<Project, List<SkillStatus>> projectStatuses =
				statusCalculatorService.getAllSkillStatusesForEntity(projectEmployeeSkills);

		return ModelFactory.getProjectAssignmentModelFactory().createList(projectStatuses);
	}

	private int getSiteIdForSummary() throws NoOperatorForCorporateException, NoRightsException {
		int siteId = NumberUtils.toInt(id);
		if (permissions.isOperator()) {
			if (siteId > 0 && permissions.getAccountId() != siteId) {
				throw new NoRightsException("Corporate");
			}

			return permissions.getAccountId();
		}

		List<Integer> siteIds = accountService.getChildOperatorIds(permissions.getAccountId());
		if (siteIds.contains(siteId)) {
			return siteId;
		}

		throw new NoOperatorForCorporateException("Site " + siteId + " not viewable by Corporate " + permissions.getAccountId());
	}

}
