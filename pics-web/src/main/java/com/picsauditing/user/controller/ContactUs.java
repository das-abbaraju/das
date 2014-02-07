package com.picsauditing.user.controller;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.user.model.ContactUsInfo;
import com.picsauditing.user.service.ContactUsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class ContactUs extends PicsActionSupport {

	@Autowired
	private ContactUsService contactUsService;
	private ContactUsInfo contactUsInfo;
	private String subject;
	private String message;

	public String execute() {

		User user = getLoggedInUser();
		if (user == null) {
			return LOGIN_AJAX;
		}

		contactUsInfo = contactUsService.getContactUsInfo(user);

		return SUCCESS;
	}

	public String sendMessage() {

		User user = getLoggedInUser();
		if (user == null) {
			return LOGIN_AJAX;
		}

		if (fieldsAreValid()) {
			boolean success = contactUsService.sendMessageToCsr(subject, message, user);
			if (success) {
				addActionMessage("Your email was sent successfully.");
			} else {
				addActionError("There was an error when sending the email to the CSR.");
			}
		}

		contactUsInfo = contactUsService.getContactUsInfo(user);

		return SUCCESS;

	}

	private User getLoggedInUser() {
		loadPermissions(false);
		if (permissions.isLoggedIn()) {
			return getUser();
		}
		return null;
	}

	private boolean fieldsAreValid() {
		return StringUtils.isNotBlank(subject) && StringUtils.isNotBlank(message);
	}

	public ContactUsInfo getContactUsInfo() {
		return contactUsInfo;
	}

	public void setContactUsInfo(ContactUsInfo contactUsInfo) {
		this.contactUsInfo = contactUsInfo;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

