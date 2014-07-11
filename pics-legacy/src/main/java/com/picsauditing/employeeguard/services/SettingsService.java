package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettingsService {
	private static Logger log = LoggerFactory.getLogger(SettingsService.class);

	@Autowired
	private ProfileEntityService profileEntityService;

	public MSettingsManager.MSettings extractSettings(int appUserId) throws ReqdInfoMissingException {

		Profile profile = profileEntityService.findByAppUserId(appUserId);
		if(profile==null)
			return null;

		MModels.fetchSettingsManager().operations().copyLocale();

		return MModels.fetchSettingsManager().copyProfile(profile);

	}

	public Profile updateSettings(MSettingsManager.MSettings mSettings, int appUserId){
		Profile profile = profileEntityService.findByAppUserId(appUserId);
		if(profile==null || mSettings==null)
			return null;

		Locale locale = mSettings.prepareLocale();
		profile.getSettings().setLocale(locale);

		return profileEntityService.update(profile,EntityAuditInfo.newEntityAuditInfo(appUserId));
	}

}
