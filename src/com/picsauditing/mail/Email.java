package com.picsauditing.mail;

@Deprecated
/**
 * use jpa.entities.EmailQueue from now on
 */
public class Email {
	private String fromAddress = "";
	private String toAddress = "";
	private String ccAddress;
	private String bccAddress;
	private String subject;
	private String body;
	private boolean htmlBody = false;
	
	/////////////// GETTERS/SETTERS //////////////////////
	public String getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	public String getToAddress() {
		return toAddress;
	}
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	public String getCcAddress() {
		return ccAddress;
	}
	public void setCcAddress(String ccAddress) {
		this.ccAddress = ccAddress;
	}
	public String getBccAddress() {
		return bccAddress;
	}
	public void setBccAddress(String bccAddress) {
		this.bccAddress = bccAddress;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public boolean isHtmlBody() {
		return htmlBody;
	}
	public void setHtmlBody(boolean htmlBody) {
		this.htmlBody = htmlBody;
	}
}
