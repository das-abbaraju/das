package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.models.CompanyEmployeeStatusModel;
import com.picsauditing.employeeguard.models.EmploymentInfoModel;
import com.picsauditing.employeeguard.models.ProjectStatusModel;
import com.picsauditing.employeeguard.models.RoleStatusModel;
import com.picsauditing.employeeguard.services.status.SkillStatus;

import java.util.List;

import static com.picsauditing.employeeguard.util.EmployeeGUARDUrlUtils.IMAGE_LINK;
import static com.picsauditing.employeeguard.util.EmployeeGUARDUrlUtils.buildUrl;

public class CompanyEmployeeStatusModelFactory extends CompanyEmployeeModelFactory {

	public CompanyEmployeeStatusModel create(final Employee employee,
											 final List<EmploymentInfoModel> companies,
											 final List<ProjectStatusModel> projects,
											 final List<RoleStatusModel> roles,
											 final SkillStatus status) {

		CompanyEmployeeStatusModel companyEmployeeStatusModel =
				new CompanyEmployeeStatusModel();

		companyEmployeeStatusModel.setId(employee.getId());
		companyEmployeeStatusModel.setFirstName(employee.getFirstName());
		companyEmployeeStatusModel.setLastName(employee.getLastName());
		companyEmployeeStatusModel.setTitle(employee.getPositionName());
		companyEmployeeStatusModel.setImage(buildUrl(IMAGE_LINK, employee.getAccountId(), employee.getId()));
		companyEmployeeStatusModel.setCompanies(companies);
		companyEmployeeStatusModel.setProjects(projects);
		companyEmployeeStatusModel.setRoles(roles);
		companyEmployeeStatusModel.setStatus(status);

		return companyEmployeeStatusModel;
	}
}
