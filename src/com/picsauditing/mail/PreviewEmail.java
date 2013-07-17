package com.picsauditing.mail;

import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.User;

import java.util.Locale;

public class PreviewEmail extends PicsActionSupport {
	private ContractorAccount contractor;
	private User user;
	private Locale locale;
	private int templateID;
	private EmailQueue email;

	@Anonymous
	public String execute() throws Exception {
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(templateID);
		emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);

		//TODO
		//emailBuilder.setLocale(locale);

		email = emailBuilder.build();

		return SUCCESS;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public int getTemplateID() {
		return templateID;
	}

	public void setTemplateID(int templateID) {
		this.templateID = templateID;
	}

	public EmailQueue getEmail() {
		return email;
	}

	public void setEmail(EmailQueue email) {
		this.email = email;
	}
}
