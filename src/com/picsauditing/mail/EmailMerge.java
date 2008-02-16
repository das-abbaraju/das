package com.picsauditing.mail;

import java.util.HashMap;

import com.picsauditing.PICS.AppPropertiesBean;


public class EmailMerge {
	private String subjectTemplate;
	private String bodyTemplate;
	
	private HashMap<String, String> tokens = new HashMap<String, String>();
	
	public EmailMerge() {
	}
	public EmailMerge(String appProperty) throws Exception {
		this.setFromAppProperty(appProperty);
	}

	/////////////// GETTERS/SETTERS //////////////////////
	public String getSubjectTemplate() {
		return subjectTemplate;
	}
	public void setSubjectTemplate(String subjectTemplate) {
		this.subjectTemplate = subjectTemplate;
	}
	public String getBodyTemplate() {
		return bodyTemplate;
	}
	public void setBodyTemplate(String bodyTemplate) {
		this.bodyTemplate = bodyTemplate;
	}
	public void addTokens(String key, String value) {
		if (this.tokens == null)
			this.tokens = new HashMap<String, String>();
		this.tokens.put(key, value);
	}
	public HashMap<String, String> getTokens() {
		return tokens;
	}
	/////////////// GETTERS/SETTERS //////////////////////
	
	public Email createEmail() {
		return createEmail("", "");
	}
	public Email createEmail(String toAddress) {
		return createEmail(toAddress, "");
	}
	public Email createEmail(String toAddress, String ccAddress) {
		Email email = new Email();
		email.setToAddress(toAddress);
		email.setCcAddress(ccAddress);
		
		email.setSubject(replaceTokens(subjectTemplate));
		email.setBody(replaceTokens(bodyTemplate));
		
		return email;
	}
	
	private String replaceTokens(String text) {
		for(String key : this.tokens.keySet()) {
			String value = this.tokens.get(key);
			value = (value==null) ? "" : value;
			text = text.replace("${"+key+"}", value);
		}
		return text;
	}
	
	public void setFromAppProperty(String name) throws Exception {
		final String signatureTag = "DEFAULT_SIGNATURE";
		if (name == null) return;
		if (name.length() < 1) return;
		
		AppPropertiesBean prop = new AppPropertiesBean();
		
		this.subjectTemplate = prop.get("email_"+name+"_subject");
		this.bodyTemplate = prop.get("email_"+name+"_body");
		if (this.bodyTemplate.contains(signatureTag)) {
			// The body needs an email signature
			this.bodyTemplate = this.bodyTemplate.replace(signatureTag, prop.get(signatureTag));
		}
	}
}
