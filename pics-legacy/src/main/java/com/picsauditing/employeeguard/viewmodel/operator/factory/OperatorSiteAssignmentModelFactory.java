package com.picsauditing.employeeguard.viewmodel.operator.factory;

import com.picsauditing.employeeguard.forms.EntityInfo;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.viewmodel.contractor.EmployeeSiteAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.operator.SiteAssignmentModel;

import java.util.List;
import java.util.Map;

public class OperatorSiteAssignmentModelFactory {

	public SiteAssignmentModel create(final int employeesAtSite,
	                                  final List<EmployeeSiteAssignmentModel> employeeSiteAssignments,
	                                  final Map<RoleInfo, Integer> roleCounts,
	                                  final List<EntityInfo> skills) {
		return new SiteAssignmentModel.Builder()
				.totalEmployeesAssignedToSite(employeesAtSite)
				.employeeSiteAssignmentModels(employeeSiteAssignments)
				.roleEmployeeCount(roleCounts)
				.skills(skills)
				.build();
	}
}
