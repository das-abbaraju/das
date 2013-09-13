package com.picsauditing.mail;

@SuppressWarnings("serial")
public class NoUsersDefinedException extends Exception {
	public NoUsersDefinedException() {
	}
	
	public NoUsersDefinedException(String message) {
		super(message);
	}
}
