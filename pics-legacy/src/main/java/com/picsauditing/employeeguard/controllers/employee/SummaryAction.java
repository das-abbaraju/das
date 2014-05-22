package com.picsauditing.employeeguard.controllers.employee;

import com.google.gson.Gson;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.ProfileAssignmentModel;
import com.picsauditing.employeeguard.models.ProfileModel;
import com.picsauditing.employeeguard.process.ProfileSkillData;
import com.picsauditing.employeeguard.process.ProfileSkillStatusProcess;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SuppressWarnings("serial")
public class SummaryAction extends PicsRestActionSupport {

	@Autowired
	private ProfileEntityService profileEntityService;
	@Autowired
	private ProfileSkillStatusProcess profileSkillStatusProcess;

	/* pages */

	public String employeeInfo() {
		ProfileModel profileModel = ModelFactory.getProfileModelFactory().create(getProfile());

		jsonString = new Gson().toJson(profileModel);

		return JSON_STRING;
	}

	private Profile getProfile() {
		return profileEntityService.findByAppUserId(permissions.getAppUserID());
	}

	public String assignments() {
		ProfileSkillData profileSkillData = profileSkillStatusProcess.buildProfileSkillData(getProfile());

		List<ProfileAssignmentModel> models = ModelFactory.getProfileAssignmentModelFactory()
				.create(profileSkillData.getSiteAccounts(), profileSkillData.getSiteStatuses(),
						profileSkillData.getAccountGroups(), profileSkillData.getAccountRoles(),
						profileSkillData.getProjectStatuses());

		jsonString = new Gson().toJson(models);

		return JSON_STRING;
	}
}
