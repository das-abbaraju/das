package com.picsauditing.employeeguard.services.email;

public class CannotCreateEmailHashException extends RuntimeException {

	public CannotCreateEmailHashException() {
		super();
	}

	public CannotCreateEmailHashException(String message) {
		super(message);
	}
}
