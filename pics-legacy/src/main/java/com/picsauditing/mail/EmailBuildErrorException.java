package com.picsauditing.mail;

import com.picsauditing.exception.PicsException;
import com.picsauditing.jpa.entities.EmailQueue;

public class EmailBuildErrorException extends PicsException {
	private EmailQueue emailQueue;

	public EmailBuildErrorException(String message, Throwable cause, EmailQueue emailQueue) {
		super(message, cause);
		this.emailQueue = emailQueue;
	}

	public EmailQueue getEmailQueue() {
		return emailQueue;
	}
}
