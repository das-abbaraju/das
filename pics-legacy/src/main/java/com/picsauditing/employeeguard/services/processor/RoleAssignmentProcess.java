package com.picsauditing.employeeguard.services.processor;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;

import java.util.Map;
import java.util.Set;

public final class RoleAssignmentProcess {

	public Map<Role, Set<Employee>> getCorporateRoleEmployees(final Map<Role, Set<Employee>> projectEmployees,
															  final Map<Role, Set<Employee>> siteRoleEmployees) {
		return PicsCollectionUtil.mergeMapOfSets(projectEmployees, siteRoleEmployees);
	}

}
