package com.picsauditing.employeeguard.viewmodel.operator.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.viewmodel.operator.SiteAssignmentModel;

import java.util.List;

public class OperatorSiteAssignmentModelFactory {

	public SiteAssignmentModel create(final List<Employee> employeesAtSite) {
		return new SiteAssignmentModel.Builder()
				.totalEmployeesAssignedToSite(employeesAtSite.size())
				.build();
	}
}
