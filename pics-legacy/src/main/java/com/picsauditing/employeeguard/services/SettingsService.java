package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.models.MModels;
import com.picsauditing.employeeguard.models.MSettingsManager;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

public class SettingsService {

	@Autowired
	private ProfileEntityService profileEntityService;
	@Autowired
	private UserService userService;

	public MSettingsManager.MSettings extractSettings(int appUserId) throws ReqdInfoMissingException {
		MModels.fetchSettingsManager().operations().copyLocale();

		User user = userService.findByAppUserId(appUserId);
		if (user != null) {
			return MModels.fetchSettingsManager().copyUser(user);
		}

		return loadProfileSettings(appUserId);
	}

	private MSettingsManager.MSettings loadProfileSettings(int appUserId) throws ReqdInfoMissingException {
		Profile profile = profileEntityService.findByAppUserId(appUserId);
		if (profile == null)
			return null;

		return MModels.fetchSettingsManager().copyProfile(profile);
	}

	public Profile updateSettings(MSettingsManager.MSettings mSettings, int appUserId) {
		Profile profile = profileEntityService.findByAppUserId(appUserId);
		if (profile == null || mSettings == null)
			return null;

		Locale locale = mSettings.prepareLocale();
		profile.getSettings().setLocale(locale);

		return profileEntityService.update(profile, EntityAuditInfo.newEntityAuditInfo(appUserId));
	}
}
