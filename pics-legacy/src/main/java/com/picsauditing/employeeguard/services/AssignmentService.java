package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class AssignmentService {

	public Map<Employee, Set<AccountSkill>> getEmployeeSkillsForSite(final int siteId) {
		return Collections.emptyMap();
	}

	public Set<Employee> getEmployeesAssignedToSite(final int siteId) {
		return Collections.emptySet();
	}

	public Map<Employee, Set<Role>> getAllEmployeeRolesForSite(final int siteId) {
		return Collections.emptyMap();
	}

}
