package com.picsauditing.access;

public class UnauthorizedException extends Exception {
	private static final long serialVersionUID = -2530216392666266589L;
	private String referrer;

	public UnauthorizedException() {
		super("Unauthorized Exception");
	}

	public UnauthorizedException(String message) {
		super(message);
	}
}
