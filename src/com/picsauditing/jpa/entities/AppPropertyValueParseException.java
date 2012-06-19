package com.picsauditing.jpa.entities;

public class AppPropertyValueParseException extends Exception {
	private static final long serialVersionUID = -451741987948473765L;

	public AppPropertyValueParseException() {
	}

	public AppPropertyValueParseException(String message) {
		super(message);
	}

	public AppPropertyValueParseException(Exception e) {
		super(e);
	}
}
