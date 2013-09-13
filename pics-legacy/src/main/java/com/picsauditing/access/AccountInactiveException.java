package com.picsauditing.access;

import javax.security.auth.login.AccountException;


public class AccountInactiveException extends AccountException {
	private String username;

	public AccountInactiveException(String message, String username) {
		super(message);
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
}
