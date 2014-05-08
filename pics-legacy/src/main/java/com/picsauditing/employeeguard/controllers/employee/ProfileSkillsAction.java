package com.picsauditing.employeeguard.controllers.employee;

import com.google.gson.Gson;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.models.factories.EmployeeSkillsModelFactory;
import com.picsauditing.employeeguard.services.EmployeeSkillsModelService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import org.springframework.beans.factory.annotation.Autowired;

public class ProfileSkillsAction extends PicsRestActionSupport {

	@Autowired
	private EmployeeSkillsModelService employeeSkillsModelService;
	@Autowired
	private ProfileEntityService profileEntityService;

	public String skills() {
		Profile profile = profileEntityService.findByAppUserId(permissions.getAppUserID());

		EmployeeSkillsModelFactory.EmployeeSkillsModel employeeSkillsModel =
				employeeSkillsModelService.buildEmployeeSkillsModel(profile);

		jsonString = new Gson().toJson(employeeSkillsModel);

		return JSON_STRING;
	}

}
