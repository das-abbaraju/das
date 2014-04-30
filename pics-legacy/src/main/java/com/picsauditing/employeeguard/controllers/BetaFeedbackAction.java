package com.picsauditing.employeeguard.controllers;

import com.picsauditing.access.Permissions;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.services.email.EmailService;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class BetaFeedbackAction extends PicsRestActionSupport {

	@Autowired
	private EmailService emailService;

	public String feedbackComment() {
		JSONObject jsonObject = this.getJsonFromRequestPayload();

		String feedbackComment = (String) jsonObject.get("feedbackComment");

		Permissions permissions = SessionInfoProviderFactory.getSessionInfoProvider().getPermissions();
		boolean status = emailService.sendEGFeedBackEmail(feedbackComment, permissions.getAccountName(),
				permissions.getAppUserID(), permissions.getEmail());

		if (status)
			json.put("status", "SUCCESS");
		else
			json.put("status", "FAILURE");


		return JSON;
	}


}//--  BetaFeedbackAction