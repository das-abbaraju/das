package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.ContractorSummary;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.Map;

public class ContractorSummaryFactory {

	public ContractorSummary create(final Map<Employee, SkillStatus> employeeSkillStatuses,
									final int requestedEmployees) {
		ContractorSummary contractorSummary = new ContractorSummary();

		contractorSummary = StatusSummaryDecorator.addStatusSummary(contractorSummary, employeeSkillStatuses);
		contractorSummary.setRequested(requestedEmployees);

		return contractorSummary;
	}

}
