package com.picsauditing.employeeguard.services;

public class CannotCreateEmailHashException extends RuntimeException {

	public CannotCreateEmailHashException() {
		super();
	}

	public CannotCreateEmailHashException(String message) {
		super(message);
	}
}
