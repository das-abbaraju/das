package com.picsauditing.access;

public class NotLoggedInException extends Exception {

	private static final long serialVersionUID = -4171420963632667616L;

	public NotLoggedInException() {

	}

	public NotLoggedInException(String message) {
		super(message);
	}
}
