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
															  final Map<Role, Set<Employee>> siteRoleEmployees,
															  final Map<Role, Role> siteToCorporateRoles) {
		Map<Role, Set<Employee>> corporateRoleToEmployees = mapSiteRolesToCorporateRoles(siteRoleEmployees, siteToCorporateRoles);
		return Utilities.mergeMapOfSets(projectEmployees, corporateRoleToEmployees);
	}

	private Map<Role, Set<Employee>> mapSiteRolesToCorporateRoles(final Map<Role, Set<Employee>> siteRoleEmployees,
																  final Map<Role, Role> siteToCorporateRoles) {
		if (MapUtils.isEmpty(siteRoleEmployees)) {
			return Collections.emptyMap();
		}

		Map<Role, Set<Employee>> corporateRoleEmployees = new HashMap<>();
		for (Role role : siteRoleEmployees.keySet()) {
			corporateRoleEmployees.put(siteToCorporateRoles.get(role), siteRoleEmployees.get(role));
		}

		return corporateRoleEmployees;
	}

}
