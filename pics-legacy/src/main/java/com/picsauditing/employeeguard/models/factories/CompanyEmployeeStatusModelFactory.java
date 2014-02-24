package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.CompanyEmployeeStatusModel;
import com.picsauditing.employeeguard.models.CompanyModel;
import com.picsauditing.employeeguard.models.ProjectModel;
import com.picsauditing.employeeguard.models.RoleModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.List;

public class CompanyEmployeeStatusModelFactory extends CompanyEmployeeModelFactory {

	public CompanyEmployeeStatusModel create(final Employee employee,
											 final List<CompanyModel> companies,
											 final List<ProjectModel> projects,
											 final List<RoleModel> roles,
											 final SkillStatus status) {
		CompanyEmployeeStatusModel companyEmployeeStatusModel =
				new CompanyEmployeeStatusModel(super.create(employee, companies, projects, roles));
		companyEmployeeStatusModel.setStatus(status);
		return companyEmployeeStatusModel;
	}
}
