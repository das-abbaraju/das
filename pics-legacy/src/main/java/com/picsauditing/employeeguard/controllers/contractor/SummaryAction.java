package com.picsauditing.employeeguard.controllers.contractor;

import com.google.gson.Gson;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.ContractorSummary;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.process.EmployeeSkillDataProcess;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.entity.employee.EmployeeEntityService;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.status.StatusCalculatorService;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SummaryAction extends PicsRestActionSupport {

	@Autowired
	private AccountService accountService;
	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private StatusCalculatorService statusCalculatorService;
	@Autowired
	private EmployeeSkillDataProcess employeeSkillDataProcess;

	public String index() {
		int contractorAccountId = permissions.getAccountId();

		Set<Employee> employees = new HashSet<>(employeeEntityService.getEmployeesForAccount(contractorAccountId));
		Map<Employee, Set<Integer>> employeeSiteAssignments = employeeEntityService.getEmployeeSiteAssignments(employees);
		Map<AccountModel, Set<AccountModel>> siteHierarchy = accountService
				.getSiteParentAccounts(PicsCollectionUtil
						.flattenCollectionOfCollection(employeeSiteAssignments.values()));

		Map<Employee, Set<AccountSkill>> employeeSkillsForContractor =
				employeeSkillDataProcess.allEmployeeSkillsForContractorAndSites(contractorAccountId, employees, siteHierarchy);
		Map<Employee, SkillStatus> employeeOverallStatus =
				statusCalculatorService.getEmployeeStatusRollUpForSkills(employeeSkillsForContractor);

		ContractorSummary contractorSummary = ModelFactory.getContractorSummaryFactory()
				.create(employeeOverallStatus, employeeEntityService.getRequestedEmployeeCount(contractorAccountId));

		jsonString = new Gson().toJson(contractorSummary);

		return JSON_STRING;
	}
}
