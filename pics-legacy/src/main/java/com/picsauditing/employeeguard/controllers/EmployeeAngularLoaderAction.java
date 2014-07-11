package com.picsauditing.employeeguard.controllers;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.MSettingsManager;
import com.picsauditing.employeeguard.services.SettingsService;
import com.picsauditing.employeeguard.util.EmployeeGUARDUrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class EmployeeAngularLoaderAction extends PicsRestActionSupport {
	private static Logger log = LoggerFactory.getLogger(EmployeeAngularLoaderAction.class);

	@Autowired
	private SettingsService settingsService;

	public String load()  throws Exception {

		try {
			MSettingsManager.MSettings mSettings = settingsService.extractSettings(permissions.getAppUserID());

			permissions.setLocale(mSettings.prepareLocale());

		} catch (ReqdInfoMissingException e) {
			log.error("Failed to get Settings - Required information missing -  ", e);
		}

		return setUrlForRedirect(EmployeeGUARDUrlUtils.EMPLOYEE_SUMMARY_ANGULAR);
		//return BLANK;
	}

}
