package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.models.OperatorEmployeeModel;
import com.picsauditing.employeeguard.models.ProjectStatusModel;
import com.picsauditing.employeeguard.models.RequiredSkills;
import com.picsauditing.employeeguard.models.RoleStatusModel;

import java.util.List;

public class OperatorEmployeeModelFactory {

	public OperatorEmployeeModel create(final RequiredSkills siteAndCorporateRequiredSkills,
										final List<ProjectStatusModel> projects,
										final List<RoleStatusModel> roles) {
		OperatorEmployeeModel operatorEmployeeModel = new OperatorEmployeeModel();

		operatorEmployeeModel.setRequired(siteAndCorporateRequiredSkills);
		operatorEmployeeModel.setProjects(projects);
		operatorEmployeeModel.setRoles(roles);

		return operatorEmployeeModel;
	}

}
