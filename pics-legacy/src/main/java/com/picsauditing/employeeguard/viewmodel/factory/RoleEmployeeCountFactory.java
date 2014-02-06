package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.ProjectRoleEmployee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.RoleEmployee;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class RoleEmployeeCountFactory {

	public Map<RoleInfo, Integer> create(final List<RoleInfo> corporateRoles,
	                                     final Map<Role, Role> corporateToSiteRoles,
	                                     final Collection<Employee> employees) {
		if (CollectionUtils.isEmpty(corporateRoles) || CollectionUtils.isEmpty(employees)) {
			return Collections.emptyMap();
		}

		Map<RoleInfo, Integer> roleEmployeeCount = new TreeMap<>();
		for (RoleInfo corporateRole : corporateRoles) {
			int roleCount = 0;
			Role siteRole = getSiteRole(corporateToSiteRoles, corporateRole);

			for (Employee employee : employees) {
				if (employeeHasRole(employee, corporateRole, siteRole)) {
					roleCount++;
				}
			}

			roleEmployeeCount.put(corporateRole, roleCount);
		}

		return Collections.unmodifiableMap(roleEmployeeCount);
	}

	private Role getSiteRole(Map<Role, Role> corporateToSiteRoles, RoleInfo corporateRole) {
		if (MapUtils.isEmpty(corporateToSiteRoles)) {
			return null;
		}

		for (Map.Entry<Role, Role> corporateToSite : corporateToSiteRoles.entrySet()) {
			if (corporateToSite.getKey().getId() == corporateRole.getId()) {
				return corporateToSite.getValue();
			}
		}

		return null;
	}

	private boolean employeeHasRole(Employee employee, RoleInfo corporateRole, Role siteRole) {
		for (RoleEmployee roleEmployee : employee.getRoles()) {
			int roleId = roleEmployee.getRole().getId();

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
