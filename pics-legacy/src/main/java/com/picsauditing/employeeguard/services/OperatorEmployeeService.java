package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.CompanyModel;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.factories.OperatorEmployeeModelFactory;
import com.picsauditing.employeeguard.process.EmployeeSiteStatusProcess;
import com.picsauditing.employeeguard.process.EmployeeSiteStatusResult;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.entity.EmployeeEntityService;
import com.picsauditing.employeeguard.services.status.StatusCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Map;

public class OperatorEmployeeService {

	@Autowired
	private AccountService accountService;
	@Autowired
	private EmployeeEntityService employeeEntityService;
	@Autowired
	private EmployeeSiteStatusProcess employeeSiteStatusProcess;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	public OperatorEmployeeModelFactory.OperatorEmployeeModel buildModel(final int siteId, final int employeeId) {

		Employee employee = employeeEntityService.find(employeeId);

		AccountModel contractor = accountService.getAccountById(employee.getAccountId());

		CompanyModel companyModel = ModelFactory.getCompanyModelFactory().create(contractor, employee.getPositionName());

		EmployeeSiteStatusResult employeeSiteStatusResult = employeeSiteStatusProcess
				.getEmployeeSiteStatusResult(employeeId, siteId, accountService.getTopmostCorporateAccountIds(siteId));

		SkillStatus employeeStatus = getEmployeeStatus(employeeSiteStatusResult.getSkillStatus());

		return ModelFactory.getOperatorEmployeeModelFactory().build(employee, employeeStatus,
				Arrays.asList(companyModel));
	}

	public SkillStatus getEmployeeStatus(Map<AccountSkill, SkillStatus> skillStatuses) {
		return statusCalculatorService.calculateOverallStatus(skillStatuses.values(), SkillStatus.Completed);
	}
}
