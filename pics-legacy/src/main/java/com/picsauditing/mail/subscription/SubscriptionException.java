package com.picsauditing.mail.subscription;

import com.picsauditing.exception.PicsException;

public class SubscriptionException extends PicsException {
	private int subscriptionId;

	public SubscriptionException(String message, Throwable cause, int subscriptionId) {
		super(message, cause);
		this.subscriptionId = subscriptionId;
	}

	public int getSubscriptionId() {
		return subscriptionId;
	}
}
