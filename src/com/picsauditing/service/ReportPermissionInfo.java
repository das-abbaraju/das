package com.picsauditing.service;

/**
 * This is a POJO for showing data on the ReportAccess page.
 */
public class ReportPermissionInfo {

	private int id;
	private String userName;
	private boolean isOwner;
	private String accountName;
	private String location;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isOwner() {
		return isOwner;
	}

	public void setOwner(boolean owner) {
		isOwner = owner;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
