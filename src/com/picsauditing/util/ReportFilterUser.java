package com.picsauditing.util;

import com.picsauditing.jpa.entities.ListType;

public class ReportFilterUser extends ReportFilter {

	// /////// Filter Visibility /////////////
	protected boolean showContact = true;
	protected boolean showPhone = true;
	protected boolean showEmail = true;
	protected boolean showUser = true;
	protected boolean showCompanyName = true;
	
	// /////// Parameter Values /////////////////
	protected String startsWith;
	protected String contactName;
	protected String phoneNumber;
	protected String emailAddress;
	protected String userName;
	protected String companyName;
	protected String search;
	
	// Email Builder
	protected boolean showEmailTemplate = false;
	protected ListType emailListType;

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

	public boolean isShowCompanyName() {
		return showCompanyName;
	}

	public void setShowCompanyName(boolean showCompanyName) {
		this.showCompanyName = showCompanyName;
	}

	public String getStartsWith() {
		return startsWith;
	}

	public void setStartsWith(String startsWith) {
		this.startsWith = startsWith;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public boolean isShowEmailTemplate() {
		return showEmailTemplate;
	}

	public void setShowEmailTemplate(boolean showEmailTemplate) {
		this.showEmailTemplate = showEmailTemplate;
	}

	public ListType getEmailListType() {
		return emailListType;
	}

	public void setEmailListType(ListType emailListType) {
		this.emailListType = emailListType;
	}
}
