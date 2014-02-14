package com.picsauditing.employeeguard.viewmodel.operator.factory;

import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.viewmodel.operator.EmployeeNav;
import com.picsauditing.employeeguard.viewmodel.operator.NavItem;

import java.util.List;

public class EmployeeNavFactory {

	public EmployeeNav create(final SkillStatus overallStatus,
							  final List<NavItem> roleNavItems,
							  final List<NavItem> projectNavItems) {
		return new EmployeeNav.Builder()
				.overallStatus(overallStatus)
				.roles(roleNavItems)
				.projects(projectNavItems)
				.build();
	}

}
