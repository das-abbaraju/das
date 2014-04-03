package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class RoleEmployeeCountFactory {

	public Map<RoleInfo, Integer> create(final List<RoleInfo> roles,
	                                     final Collection<Employee> employeesAtSite,
	                                     final Map<Role, Set<Employee>> employeeRoles) {
		if (CollectionUtils.isEmpty(roles)) {
			return Collections.emptyMap();
		}

		Map<RoleInfo, Integer> roleEmployeeCount = new TreeMap<>();
		for (RoleInfo role : roles) {
			roleEmployeeCount.put(role, getEmployeeCount(role, employeeRoles, employeesAtSite));
		}

		return Collections.unmodifiableMap(roleEmployeeCount);
	}

	private int getEmployeeCount(final RoleInfo roleInfo, final Map<Role, Set<Employee>> employeeRoles,
	                             final Collection<Employee> employeesAtSite) {
		for (Role role : employeeRoles.keySet()) {
			if (role.getId() == roleInfo.getId()) {
				Set<Employee> employees = new HashSet<>(employeeRoles.get(role));
				employees.retainAll(employeesAtSite);

				return employees.size();
			}
		}

		return 0;
	}
}
