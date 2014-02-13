package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.AccountSkillEmployeeDAO;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.util.generic.GenericPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class StatusCalculatorService {

	@Autowired
	private AccountSkillEmployeeDAO accountSkillEmployeeDAO;

	public Map<Employee, SkillStatus> getEmployeeStatusRollUpForSkills(final Collection<Employee> employees,
																	   final Map<Employee, Set<AccountSkill>> employeeRequiredSkills) {
		if (CollectionUtils.isEmpty(employees) || MapUtils.isEmpty(employeeRequiredSkills)) {
			return Collections.emptyMap();
		}

		Map<Employee, SkillStatus> employeeStatuses = new HashMap<>();
		for (final Employee employee : employees) {
			SkillStatus rollUp = SkillStatus.Expired;
			if (CollectionUtils.isNotEmpty(employeeRequiredSkills.get(employee))) {
				List<AccountSkillEmployee> employeeSkills = new ArrayList<>(employee.getSkills());
				CollectionUtils.filter(employeeSkills, new GenericPredicate<AccountSkillEmployee>() {
					@Override
					public boolean evaluateEntity(AccountSkillEmployee accountSkillEmployee) {
						return employeeRequiredSkills.get(employee).contains(accountSkillEmployee.getSkill());
					}
				});

				if (CollectionUtils.isNotEmpty(employeeSkills)) {
					rollUp = SkillStatusCalculator.calculateStatusRollUp(employeeSkills);
				}
			}

			employeeStatuses.put(employee, rollUp);
		}

		return employeeStatuses;
	}

	public Map<Employee, List<SkillStatus>> getEmployeeStatusRollUpForSkills(final Collection<Employee> employees,
																			 final List<AccountSkill> orderedSkills) {
		List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeDAO
				.findByEmployeesAndSkills(employees, orderedSkills);
		Map<Employee, Set<AccountSkillEmployee>> employeeSetMap = convertToMap(accountSkillEmployees);
		return buildSkillStatusMap(employeeSetMap, orderedSkills);
	}

	private Map<Employee, Set<AccountSkillEmployee>> convertToMap(final List<AccountSkillEmployee> accountSkillEmployees) {
		return Utilities.convertToMapOfSets(accountSkillEmployees, new Utilities.MapConvertable<Employee, AccountSkillEmployee>() {
			@Override
			public Employee getKey(AccountSkillEmployee accountSkillEmployee) {
				return accountSkillEmployee.getEmployee();
			}
		});
	}

	private Map<Employee, List<SkillStatus>> buildSkillStatusMap(final Map<Employee, Set<AccountSkillEmployee>> employeeMap,
																 final List<AccountSkill> orderedSkills) {
		if (MapUtils.isEmpty(employeeMap) || CollectionUtils.isEmpty(orderedSkills)) {
			return Collections.emptyMap();
		}

		Map<Employee, List<SkillStatus>> employees = new HashMap<>();
		for (Employee employee : employeeMap.keySet()) {
			employees.put(employee, buildOrderedSkillStatusList(employeeMap.get(employee), orderedSkills));
		}

		return Collections.emptyMap();
	}

	private List<SkillStatus> buildOrderedSkillStatusList(final Set<AccountSkillEmployee> accountSkillEmployees,
														  final List<AccountSkill> orderedSkills) {
		List<SkillStatus> skillStatusList = new ArrayList<>(orderedSkills.size());
		for (AccountSkillEmployee accountSkillEmployee : accountSkillEmployees) {
			int index = orderedSkills.indexOf(accountSkillEmployee.getSkill());
			skillStatusList.add(index, SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee));
		}

		return skillStatusList;
	}
}
