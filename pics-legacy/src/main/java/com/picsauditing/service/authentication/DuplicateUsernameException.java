package com.picsauditing.service.authentication;

public class DuplicateUsernameException extends RuntimeException {

	public DuplicateUsernameException() {
		super();
	}

	public DuplicateUsernameException(String message) {
		super(message);
	}
}
