package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.CompanyEmployeeModel;
import com.picsauditing.employeeguard.models.CompanyModel;
import com.picsauditing.employeeguard.models.ProjectModel;
import com.picsauditing.employeeguard.models.RoleModel;

import java.util.List;

public class CompanyEmployeeModelFactory {

	public CompanyEmployeeModel create(final Employee employee,
									   final List<CompanyModel> companies,
									   final List<ProjectModel> projects,
									   final List<RoleModel> roles) {
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
