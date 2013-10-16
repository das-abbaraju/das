package com.picsauditing.access;

public class PageNotFoundException extends Exception {

	private static final long serialVersionUID = -4712829545670827032L;

	public PageNotFoundException() {
		super("Page Not Found Exception");
	}

	public PageNotFoundException(String message) {
		super(message);
	}
}
