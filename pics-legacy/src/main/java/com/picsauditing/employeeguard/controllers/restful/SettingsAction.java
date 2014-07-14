package com.picsauditing.employeeguard.controllers.restful;

import com.google.gson.GsonBuilder;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.MSettingsManager;
import com.picsauditing.employeeguard.services.SettingsService;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SettingsAction extends PicsRestActionSupport {
	private static Logger log = LoggerFactory.getLogger(SettingsAction.class);

	private static final long serialVersionUID = -490476912556033616L;

	@Autowired
	private SettingsService settingsService;

	private MSettingsManager.MSettings mSettings;


	public String index() {
		int appUserId = permissions.getAppUserID();
		try {
			MSettingsManager.MSettings mSettings = settingsService.extractSettings(appUserId);

			if (mSettings != null) {
				jsonString = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(mSettings);
			}

		} catch (ReqdInfoMissingException e) {
			log.error("Failed to get Settings - Required information missing -  ", e);
		}

		return JSON_STRING;
	}


	public String insert() throws Exception {
		return updateSettings();
	}

	public String update() throws Exception {
		return updateSettings();
	}

	private String updateSettings() throws Exception {

		mSettings = populateSettings();

		if (mSettings != null) {
			int appUserId = permissions.getAppUserID();

			settingsService.updateSettings(mSettings, appUserId);

			jsonString = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(mSettings);

			permissions.setLocale(mSettings.prepareLocale());

		}

		return JSON_STRING;
	}

	private MSettingsManager.MSettings populateSettings() {
		JSONObject jsonObject = getJsonFromRequestPayload();
		return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(jsonObject.toJSONString(), MSettingsManager.MSettings.class);
	}

	/* getters + setters */

	public MSettingsManager.MSettings getmSettings() {
		return mSettings;
	}

	public void setmSettings(MSettingsManager.MSettings mSettings) {
		this.mSettings = mSettings;
	}
}
