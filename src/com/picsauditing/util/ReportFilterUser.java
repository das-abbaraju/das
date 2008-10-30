package com.picsauditing.util;


public class ReportFilterUser extends ReportFilter {
	public static final String DEFAULT_NAME = "- Contact Name - ";
	public static final String DEFAULT_PHONE = "- Phone Number -";
	public static final String DEFAULT_EMAIL = "- Email Address -";
	public static final String DEFAULT_USERNAME = "- UserName -";

	// /////// Filter Visibility /////////////
	protected boolean showContact = true;
	protected boolean showPhone = true;
	protected boolean showEmail = true;
	protected boolean showUser = true;

	// /////// Parameter Values /////////////////
	protected String startsWith;
	protected String ContactName = DEFAULT_NAME;
	protected String PhoneNumber = DEFAULT_PHONE;
	protected String EmailAddress = DEFAULT_EMAIL;
	protected String UserName = DEFAULT_USERNAME;

	public boolean isShowContact() {
		return showContact;
	}

	public void setShowContact(boolean showContact) {
		this.showContact = showContact;
	}

	public boolean isShowPhone() {
		return showPhone;
	}

	public void setShowPhone(boolean showPhone) {
		this.showPhone = showPhone;
	}

	public boolean isShowEmail() {
		return showEmail;
	}

	public void setShowEmail(boolean showEmail) {
		this.showEmail = showEmail;
	}

	public boolean isShowUser() {
		return showUser;
	}

	public void setShowUser(boolean showUser) {
		this.showUser = showUser;
	}

	public String getStartsWith() {
		return startsWith;
	}

	public void setStartsWith(String startsWith) {
		this.startsWith = startsWith;
	}

	public String getContactName() {
		return ContactName;
	}

	public void setContactName(String contactName) {
		ContactName = contactName;
	}

	public String getPhoneNumber() {
		return PhoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		PhoneNumber = phoneNumber;
	}

	public String getEmailAddress() {
		return EmailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		EmailAddress = emailAddress;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

}
