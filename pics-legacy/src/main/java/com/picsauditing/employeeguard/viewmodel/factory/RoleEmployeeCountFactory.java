package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.ProjectRoleEmployee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.RoleEmployee;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RoleEmployeeCountFactory {

	public Map<RoleInfo, Integer> create(final List<RoleInfo> corporateRoles,
	                                     final Map<Role, Role> corporateToSiteRoles,
	                                     final List<Employee> employees) {
		Map<RoleInfo, Integer> roleEmployeeCount = new TreeMap<>();

		for (RoleInfo corporateRole : corporateRoles) {
			int roleCount = 0;
			Role siteRole = corporateToSiteRoles.get(corporateRole);

			for (Employee employee : employees) {
				if (employeeHasRole(employee, corporateRole, siteRole)) {
					roleCount++;
				}
			}

			roleEmployeeCount.put(corporateRole, roleCount);
		}

		return Collections.unmodifiableMap(roleEmployeeCount);
	}

	private boolean employeeHasRole(Employee employee, RoleInfo corporateRole, Role siteRole) {
		for (RoleEmployee roleEmployee : employee.getRoles()) {
			int roleId = roleEmployee.getRole().getId();

			if (corporateRole.getId() == roleId) {
				return true;
			}

			if (siteRole != null && siteRole.getId() == roleId) {
				return true;
			}
		}

		for (ProjectRoleEmployee projectRoleEmployee : employee.getProjectRoles()) {
			if (corporateRole.getId() == projectRoleEmployee.getProjectRole().getRole().getId()) {
				return true;
			}
		}

		return false;
	}
}
