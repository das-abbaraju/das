package com.picsauditing.access;

import javax.security.auth.login.AccountException;

public class InvalidResetKeyException extends AccountException {
	private String username;

	public InvalidResetKeyException(String message, String username) {
		super(message);
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
}
