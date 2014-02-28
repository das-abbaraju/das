package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.*;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CompanyEmployeeModelFactory {

	public List<CompanyEmployeeModel> create(final List<Employee> employees,
											 final Map<Integer, List<EmploymentInfoModel>> companies) {
		if (CollectionUtils.isEmpty(employees)) {
			return Collections.emptyList();
		}

		List<CompanyEmployeeModel> companyEmployeeModels = new ArrayList<>();
		for (Employee employee : employees) {
			companyEmployeeModels.add(create(employee, companies.get(employee.getId()),
					Collections.<ProjectModel>emptyList(), Collections.<RoleModel>emptyList()));
		}

		return companyEmployeeModels;
	}

	public CompanyEmployeeModel create(final Employee employee,
									   final List<EmploymentInfoModel> companies,
									   final List<? extends ProjectModel> projects,
									   final List<? extends RoleModel> roles) {
		CompanyEmployeeModel companyEmployeeModel = new CompanyEmployeeModel();
		companyEmployeeModel.setId(employee.getId());
		companyEmployeeModel.setFirstName(employee.getFirstName());
		companyEmployeeModel.setLastName(employee.getLastName());
		companyEmployeeModel.setTitle(employee.getPositionName());
		companyEmployeeModel.setCompanies(companies);
		companyEmployeeModel.setProjects(projects);
		companyEmployeeModel.setRoles(roles);

		return companyEmployeeModel;
	}

}
