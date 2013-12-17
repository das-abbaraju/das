package com.picsauditing.actions.cron;

public class AuditCategoryJobException extends CronTaskException {

	public AuditCategoryJobException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuditCategoryJobException(String message) {
		super(message);
	}
}
