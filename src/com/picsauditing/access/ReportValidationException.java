package com.picsauditing.access;

import com.picsauditing.jpa.entities.Report;

public class ReportValidationException extends Exception {

	private static final long serialVersionUID = 430602478558095531L;

	public ReportValidationException(String message, Report report) {
		super(message + " Report# " + report.getId());
	}

	public ReportValidationException(Exception e, Report report) {
		super("Report# " + report.getId(), e);
	}
	
	public ReportValidationException() {
		super();
	}
	
	public ReportValidationException(String message) {
		super(message);
	}
}
