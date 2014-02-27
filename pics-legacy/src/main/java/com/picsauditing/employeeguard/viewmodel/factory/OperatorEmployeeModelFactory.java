package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.viewmodel.IdNameTitleModel;
import com.picsauditing.employeeguard.viewmodel.RoleModel;
import com.picsauditing.employeeguard.viewmodel.employee.OperatorEmployeeModel;
import com.picsauditing.employeeguard.viewmodel.employee.ProjectDetailModel;
import com.picsauditing.employeeguard.viewmodel.operator.OperatorEmployeeSkillModel;

import java.util.List;

import static com.picsauditing.employeeguard.util.UrlUtils.IMAGE_LINK;
import static com.picsauditing.employeeguard.util.UrlUtils.buildUrl;

public class OperatorEmployeeModelFactory {

	public OperatorEmployeeModel create(final Employee employee,
										final List<IdNameTitleModel> companies,
										final List<ProjectDetailModel> projects,
										final List<RoleModel> roles,
										final List<OperatorEmployeeSkillModel> skills,
										final SkillStatus overallStatus) {
		return new OperatorEmployeeModel.Builder()
				.id(employee.getId())
				.name(employee.getName())
				.image(buildUrl(IMAGE_LINK, employee.getAccountId(), employee.getId()))
				.companies(companies)
				.projects(projects)
				.roles(roles)
				.skills(skills)
				.overallStatus(overallStatus)
				.build();
	}
}
