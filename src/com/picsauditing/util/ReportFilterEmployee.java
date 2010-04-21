package com.picsauditing.util;

public class ReportFilterEmployee extends ReportFilter {

	protected boolean showAccountName = true;
	protected boolean showFirstName = true;
	protected boolean showLastName = true;
	protected boolean showEmail = true;
	protected boolean showSsn = true;

	protected String accountName;
	protected String firstName;
	protected String lastName;
	protected String email;
	protected String ssn;

	public boolean isShowAccountName() {
		return showAccountName;
	}

	public void setShowAccountName(boolean showAccountName) {
		this.showAccountName = showAccountName;
	}

	public boolean isShowFirstName() {
		return showFirstName;
	}

	public void setShowFirstName(boolean showFirstName) {
		this.showFirstName = showFirstName;
	}

	public boolean isShowLastName() {
		return showLastName;
	}

	public void setShowLastName(boolean showLastName) {
		this.showLastName = showLastName;
	}

	public boolean isShowEmail() {
		return showEmail;
	}

	public void setShowEmail(boolean showEmail) {
		this.showEmail = showEmail;
	}

	public boolean isShowSsn() {
		return showSsn;
	}

	public void setShowSsn(boolean showSsn) {
		this.showSsn = showSsn;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String employeeFN) {
		this.firstName = employeeFN;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String employeeLN) {
		this.lastName = employeeLN;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		ssn = ssn.replaceAll("[^X0-9]", "");
		if (ssn.length() <= 9)
			this.ssn = ssn;
	}
}
