package com.picsauditing.access;

import javax.security.auth.login.AccountExpiredException;

public class PasswordExpiredException extends AccountExpiredException {
	private String username;

	public PasswordExpiredException(String message, String username) {
		super(message);
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
}
