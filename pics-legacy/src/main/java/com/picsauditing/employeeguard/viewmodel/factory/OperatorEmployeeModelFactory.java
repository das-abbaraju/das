package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.viewmodel.employee.OperatorEmployeeModel;

import java.util.Map;

public class OperatorEmployeeModelFactory {
	public OperatorEmployeeModel create(final Employee employee,
	                                    final Map<Project, SkillStatus> projectStatusMap,
	                                    final Map<Role, SkillStatus> roleStatusMap,
	                                    final Map<AccountSkill, SkillStatus> skillStatusMap,
	                                    final SkillStatus overallStatus) {
		return null;
	}
}
