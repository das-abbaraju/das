package com.picsauditing.actions.report;

import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectUserUnion;

public class ReportUser extends ReportActionSupport {

	public static final String DEFAULT_NAME = "- Contact Name - ";
	public static final String DEFAULT_PHONE = "- Phone Number -";
	public static final String DEFAULT_EMAIL = "- Email Address -";
	public static final String DEFAULT_USERNAME = "- UserName -";

	protected boolean forwardSingleResults = false;
	protected boolean skipPermissions = false;

	protected String startsWith;
	protected String ContactName = DEFAULT_NAME;
	protected String PhoneNumber = DEFAULT_PHONE;
	protected String EmailAddress = DEFAULT_EMAIL;
	protected String UserName = DEFAULT_USERNAME;

	protected boolean filterContact = true;
	protected boolean filterPhone = true;
	protected boolean filterEmail = true;
	protected boolean filterUser = true;

	protected SelectUserUnion sql = new SelectUserUnion();

	public SelectUserUnion getSql() {
		return sql;
	}

	public void setSql(SelectUserUnion sql) {
		this.sql = sql;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		if (!skipPermissions)
			sql.setPermissions(permissions);

		sql.addField("u.tableType");
		sql.addField("u.accountID");
		sql.addField("u.name");
		sql.addField("u.dateCreated");
		sql.addField("u.lastLogin");
		sql.addField("u.username");
		sql.addJoin("JOIN accounts a ON a.id = u.accountID");
		sql.addField("a.name AS companyName");
		sql.addWhere("u.isActive = 'Yes'");
		sql.addOrderBy("u.name");

		toggleFilters();

		if (isFiltered())
			this.run(sql);

		return SUCCESS;
	}

	protected void toggleFilters() {
	}

	// Getters and setters for filter criteria
	public String getStartsWith() {
		return startsWith;
	}

	public void setStartsWith(String startsWith) {
		report.addFilter(new SelectFilter("name", "u.name LIKE '?%'", startsWith));
		this.startsWith = startsWith;
	}

	public boolean isfilterContact() {
		return filterContact;
	}

	public boolean isfilterPhone() {
		return filterPhone;
	}

	public boolean isfilterEmail() {
		return filterEmail;
	}

	public boolean isFilterUser() {
		return filterUser;
	}

	public String getContactName() {
		return ContactName;
	}

	public void setContactName(String contactName) {
		if (contactName == null || contactName.length() == 0)
			contactName = DEFAULT_NAME;
		report.addFilter(new SelectFilter("ContactName", "u.name LIKE '%?%'", contactName, DEFAULT_NAME, DEFAULT_NAME));
		ContactName = contactName;
	}

	public String getPhoneNumber() {
		return PhoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		if (phoneNumber == null || phoneNumber.length() == 0)
			phoneNumber = DEFAULT_PHONE;
		report.addFilter(new SelectFilter("PhoneNumber", "u.phone LIKE '%?%'", phoneNumber, DEFAULT_PHONE,
				DEFAULT_PHONE));
		PhoneNumber = phoneNumber;
	}

	public String getEmailAddress() {
		return EmailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		if (emailAddress == null || emailAddress.length() == 0)
			emailAddress = DEFAULT_EMAIL;
		report.addFilter(new SelectFilter("EmailAddress", "u.email LIKE '%?%'", emailAddress, DEFAULT_EMAIL,
				DEFAULT_EMAIL));
		EmailAddress = emailAddress;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		if (userName == null || userName.length() == 0)
			userName = DEFAULT_USERNAME;
		report.addFilter(new SelectFilter("UserName", "u.username LIKE '%?%'", userName, DEFAULT_USERNAME,
				DEFAULT_USERNAME));
		UserName = userName;
	}

}
