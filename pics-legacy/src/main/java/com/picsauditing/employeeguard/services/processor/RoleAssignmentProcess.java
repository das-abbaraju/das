package com.picsauditing.employeeguard.services.processor;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import org.apache.commons.collections.MapUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class RoleAssignmentProcess {

	public Map<Role, Set<Employee>> getCorporateRoleEmployees(final Map<Role, Set<Employee>> projectEmployees,
															  final Map<Role, Set<Employee>> siteRoleEmployees) {
		return Utilities.mergeMapOfSets(projectEmployees, siteRoleEmployees);
	}
}
