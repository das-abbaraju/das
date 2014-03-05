package com.picsauditing.user.controller;

import com.picsauditing.access.UnauthorizedException;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.user.model.ContactUsInfo;
import com.picsauditing.user.service.ContactUsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class ContactUs extends PicsActionSupport {

    public static final String CONTACT_ACTION_URL = "/Contact.action";
    @Autowired
	private ContactUsService contactUsService;
	private ContactUsInfo contactUsInfo;
	private String subject;
	private String message;
    private boolean messageSent = false;

	public String execute() throws Exception {
        messageSent = getActionMessages().size() > 0;

		User user = getLoggedInUser();
		if (user == null) {
			return LOGIN_AJAX;
		}

        if (permissions.isContractor()) {
            ContractorAccount contractor = (ContractorAccount) user.getAccount();
            if (contractor.getCurrentCsr() == null) {
                setUrlForRedirect(CONTACT_ACTION_URL);

                return REDIRECT;
            }
        } else {
            setUrlForRedirect(CONTACT_ACTION_URL);

            return REDIRECT;
        }

		contactUsInfo = contactUsService.getContactUsInfo(user);

		return SUCCESS;
	}

	public String sendMessage() throws UnauthorizedException {
		User user = getLoggedInUser();
		if (user == null) {
			return LOGIN_AJAX;
		}

		boolean subjectIsValid = isValidSubject();
		boolean messageIsValid = isValidMessage();

		if (subjectIsValid && messageIsValid) {
			try {
				contactUsService.sendMessageToCsr(subject, message, user);
				addActionMessage(getText("JS.Validation.ContactUs.Success"));
			} catch (Exception e) {
				addActionError(getText("JS.Validation.ContactUs.Error"));
			}
		} else {
			if (!subjectIsValid) {
				addActionError(getText("JS.Validation.ContactUs.SubjectMissing"));
			}

			if (!messageIsValid) {
				addActionError(getText("JS.Validation.ContactUs.MessageMissing"));
			}
		}

		contactUsInfo = contactUsService.getContactUsInfo(user);

        messageSent = getActionMessages().size() > 0;

		return SUCCESS;
	}

	private User getLoggedInUser() {
		loadPermissions(false);
		if (permissions.isLoggedIn()) {
			return getUser();
		}
		return null;
	}

	private boolean isValidSubject() {
		return StringUtils.isNotBlank(subject);
	}

	private boolean isValidMessage() {
		return StringUtils.isNotBlank(message);
	}

    public boolean isMessageSent() {
        return messageSent;
    }

	public ContactUsInfo getContactUsInfo() {
		return contactUsInfo;
	}

	public void setContactUsInfo(ContactUsInfo contactUsInfo) {
		this.contactUsInfo = contactUsInfo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

