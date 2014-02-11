package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusCalculatorService {

	public Map<Employee, SkillStatus> getEmployeeStatusRollUpForSkills(final List<Employee> employees,
																	   final List<AccountSkill> skills) {
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyMap();
		}

		Map<Employee, SkillStatus> employeeSkillStatus = new HashMap<>();
		for (Employee employee : employees) {
			employeeSkillStatus.put(employee, SkillStatus.Expired);
		}

		return employeeSkillStatus;
	}
}
