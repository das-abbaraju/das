package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.ProjectRole;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.viewmodel.employee.EmployeeSkillsModel;

import java.util.List;
import java.util.Map;

public class EmployeeSkillsModelFactory {

	public EmployeeSkillsModel create(final Employee employee,
	                                  final List<ProjectRole> siteProjectRoles,
	                                  final List<Role> siteRoles,
	                                  final Map<Role, Role> siteToCorporateRoles,
	                                  final List<AccountSkill> siteSkills) {
		return null;
	}
}
