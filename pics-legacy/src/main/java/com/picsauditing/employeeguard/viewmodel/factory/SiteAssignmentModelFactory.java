package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Group;
import com.picsauditing.employeeguard.viewmodel.contractor.EmployeeSiteAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.contractor.SiteAssignmentModel;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SiteAssignmentModelFactory {

	public SiteAssignmentModel create(List<EmployeeSiteAssignmentModel> employeeSiteAssignmentModels, Map<Group, List<Employee>> roleEmployees) {
		Map<String, Integer> roleEmployeeCount = new TreeMap<>();

		for (Map.Entry<Group, List<Employee>> entry : roleEmployees.entrySet()) {
			roleEmployeeCount.put(entry.getKey().getName(), entry.getValue().size());
		}

		return new SiteAssignmentModel.Builder()
				.employeeSiteAssignmentModels(employeeSiteAssignmentModels)
				.totalEmployeesAssignedToSite(employeeSiteAssignmentModels.size())
				.roleEmployeeCount(roleEmployeeCount)
				.build();
	}

}
