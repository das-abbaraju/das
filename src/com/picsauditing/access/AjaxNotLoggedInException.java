package com.picsauditing.access;

public class AjaxNotLoggedInException extends Exception {

	private static final long serialVersionUID = -7370853264264241460L;

	public AjaxNotLoggedInException() {

	}

	public AjaxNotLoggedInException(String message) {
		super(message);
	}

}
