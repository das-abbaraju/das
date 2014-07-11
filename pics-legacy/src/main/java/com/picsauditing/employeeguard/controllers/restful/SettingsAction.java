package com.picsauditing.employeeguard.controllers.restful;

import com.google.gson.GsonBuilder;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.models.MSettingsManager;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.ProfileDocumentService;
import com.picsauditing.employeeguard.services.SettingsService;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.util.PhotoUtil;
import com.picsauditing.employeeguard.validators.employee.EmployeePhotoFormValidator;
import com.picsauditing.employeeguard.validators.profile.ProfileEditFormValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SettingsAction extends PicsRestActionSupport {
	private static Logger log = LoggerFactory.getLogger(SettingsAction.class);

	private static final long serialVersionUID = -490476912556033616L;

	@Autowired
	private SettingsService settingsService;


	private String data;

	public String index() {
		int appUserId = permissions.getAppUserID();
		try{
			MSettingsManager.MSettings mSettings = settingsService.extractSettings(appUserId);

			if(mSettings!=null){
				jsonString = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(mSettings);
			}

		} catch (ReqdInfoMissingException e) {
			log.error("Failed to get Settings - Required information missing -  ", e);
		}

		return JSON_STRING;
	}


	public String insert() throws Exception {
		int appUserId = permissions.getAppUserID();

		if(data!=null) {

			MSettingsManager.MSettings mSettings = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(data, MSettingsManager.MSettings.class);

			settingsService.updateSettings(mSettings, appUserId);

			jsonString = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(mSettings);

			permissions.setLocale(mSettings.prepareLocale());

		}

		return JSON_STRING;
	}



	/* getters + setters */


	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
