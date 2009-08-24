package com.picsauditing.gwt.shared;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class LoginLogDTO implements IsSerializable {

	private Date loginDate;
	private String remoteAddress;
	private String adminName;
	private String adminAccountName ;
	private char successful;

	public Date getLoginDate() {
		return loginDate;
	}
	
	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}
	
	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public String getAdminName() {
		return adminName;
	}
	
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getAdminAccountName() {
		return adminAccountName;
	}
	
	public void setAdminAccountName(String adminAccountName) {
		this.adminAccountName = adminAccountName;
	}

	public char getSuccessful() {
		return successful;
	}

	public void setSuccessful(char successful) {
		this.successful = successful;
	}
}
