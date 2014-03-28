package com.picsauditing.employeeguard.controllers.contractor;

import com.google.gson.Gson;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.engine.SkillEngine;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.ContractorSummary;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.StatusCalculatorService;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
public class SummaryAction extends PicsRestActionSupport {

	/* pages */

	@Autowired
	private AccountService accountService;
	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private SkillEngine skillEngine;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	public String index() {
		int accountId = permissions.getAccountId();

		AccountModel accountModel = accountService.getAccountById(accountId);
		List<Employee> employees = employeeEntityService.getEmployeesForAccount(accountModel.getId());
		Map<Employee, Set<AccountSkill>> employeeSkillsForContractor =
				skillEngine.getEmployeeSkillsMapForAccount(employees, accountModel);
		Map<Employee, SkillStatus> employeeOverallStatus =
				statusCalculatorService.getEmployeeStatusRollUpForSkills(employees, employeeSkillsForContractor);

		ContractorSummary contractorSummary = ModelFactory.getContractorSummaryFactory()
				.create(employeeOverallStatus, employeeEntityService.getRequestedEmployeeCount(accountId));

		jsonString = new Gson().toJson(contractorSummary);

		return JSON_STRING;
	}

	/* other methods */

	/* getter + setters */
}
