package com.picsauditing.service.authentication;

public class UsernameNotAvailableException extends RuntimeException {

	public UsernameNotAvailableException() {
	}

	public UsernameNotAvailableException(String message) {
		super(message);
	}
}
