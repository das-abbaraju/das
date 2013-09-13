package com.picsauditing.access;

public class ReportPermissionException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReportPermissionException() {
		super("Problem with report permissions.");
	}

	public ReportPermissionException(String message) {
		super(message);
	}

}
