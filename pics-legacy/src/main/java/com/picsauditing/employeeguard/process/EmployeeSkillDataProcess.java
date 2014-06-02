package com.picsauditing.employeeguard.process;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.status.StatusCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class EmployeeSkillDataProcess {

	@Autowired
	private ProcessHelper processHelper;
	@Autowired
	private StatusCalculatorService statusCalculatorService;

	public Map<Employee, Set<AccountSkill>> allEmployeeSkillsForContractorAndSites(final int contractorId,
																				   final Collection<Employee> employees,
																				   final Map<AccountModel, Set<AccountModel>> siteHierarchy) {
		return processHelper.getAllSkillsForEmployees(contractorId, employees, siteHierarchy);
	}

	public Table<Employee, String, Integer> buildEmployeeSkillStatuses(final int contractorId,
																	   final Collection<Employee> employees,
																	   final Map<AccountModel, Set<AccountModel>> siteHierarchy) {
		Table<Employee, String, Integer> employeeSkillStatuses = TreeBasedTable.create();
		Map<Employee, Set<AccountSkill>> allEmployeeSkills = processHelper.getAllSkillsForEmployees(contractorId, employees, siteHierarchy);
		Map<Employee, List<SkillStatus>> employeeStatuses = statusCalculatorService
				.getEmployeeSkillStatusList(allEmployeeSkills);

		for (Employee employee : employees) {
			for (SkillStatus skillStatus : SkillStatus.values()) {
				employeeSkillStatuses.put(employee, skillStatus.getDisplayValue(), 0);
			}

			if (!employeeStatuses.containsKey(employee)) {
				continue;
			}

			for (SkillStatus skillStatus : employeeStatuses.get(employee)) {
				employeeSkillStatuses.put(employee, skillStatus.getDisplayValue(),
						employeeSkillStatuses.get(employee, skillStatus.getDisplayValue()) + 1);
			}
		}

		return employeeSkillStatuses;
	}

	public EmployeeSkillData buildEmployeeSkillData(final int contractorId,
													final Employee employee,
													final Map<AccountModel, Set<AccountModel>> siteHierarchy) {
		Map<Employee, Set<AccountSkill>> employeeSkills = processHelper.getAllSkillsForEmployees(contractorId,
				Arrays.asList(employee), siteHierarchy);
		Set<AccountSkill> accountSkills = employeeSkills.get(employee);
		Map<AccountSkill, SkillStatus> skillStatuses = statusCalculatorService.getSkillStatuses(employee, accountSkills);

		EmployeeSkillData employeeSkillData = new EmployeeSkillData();

		employeeSkillData.setAccountSkills(accountSkills);
		employeeSkillData.setSkillStatuses(skillStatuses);

		return employeeSkillData;
	}
}
