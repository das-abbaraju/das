package com.picsauditing.employeeguard.viewmodel.operator.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.viewmodel.contractor.EmployeeSiteAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.operator.SiteAssignmentModel;

import java.util.List;
import java.util.Map;

public class OperatorSiteAssignmentModelFactory {

    public SiteAssignmentModel create(final List<Employee> employeesAtSite,
                                      final List<EmployeeSiteAssignmentModel> employeeSiteAssignments,
                                      final Map<RoleInfo, Integer> roleCounts) {
        return new SiteAssignmentModel.Builder()
                .totalEmployeesAssignedToSite(employeesAtSite.size())
                .employeeSiteAssignmentModels(employeeSiteAssignments)
                .roleEmployeeCount(roleCounts)
                .build();
    }
}
