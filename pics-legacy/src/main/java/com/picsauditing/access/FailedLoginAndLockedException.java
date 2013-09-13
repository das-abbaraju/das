package com.picsauditing.access;

import javax.security.auth.login.LoginException;

public class FailedLoginAndLockedException extends LoginException {
	private String username;

	public FailedLoginAndLockedException(String message, String username) {
		super(message);
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
}
