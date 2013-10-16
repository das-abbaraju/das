package com.picsauditing.employeeguard.controllers.employee;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("serial")
public class DashboardAction extends PicsRestActionSupport {
	@Autowired
	private ProfileService profileService;

	private Profile profile;

	/* pages */

	public String index() {
		profile = profileService.findByAppUserId(permissions.getAppUserID());

		return "dashboard";
	}

	/* other methods */

	/* getters + setters */
	public Profile getProfile() {
		return profile;
	}
}
