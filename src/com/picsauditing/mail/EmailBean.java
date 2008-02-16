package com.picsauditing.mail;

import java.util.HashMap;

import com.picsauditing.PICS.DataBean;
import com.picsauditing.access.Permissions;

public class EmailBean extends DataBean {
	protected EmailMerge merge = new EmailMerge();
	protected Email email = new Email();
	protected Permissions permissions;
	protected String toAddress;
	protected String ccAddress;
	protected boolean testMode = false;
	
	public EmailBean() {
	}
	
	public void addTokens(String key, String value) {
		if (this.merge == null)
			this.merge = new EmailMerge();
		merge.addTokens(key, value);
	}
	protected void setMerge(String name) throws Exception {
		this.merge.setFromAppProperty(name);
	}
	public void setMerge(EmailTemplates name) throws Exception {
		this.merge.setFromAppProperty(name.toString());
	}
	public EmailMerge getMerge() {
		return merge;
	}
	
	public void sendMail() throws Exception {
		buildEmail();
		
		EmailSender sender = new EmailSender();
		sender.sendMail(email);
	}
	
	protected void buildEmail() {
		this.email = merge.createEmail();
		email.setToAddress(toAddress);
		email.setCcAddress(ccAddress);
	}
	
	public String getSentTo() {
		return email.getToAddress();
	}

	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}
}
