package com.picsauditing.mail.subscription;

import com.picsauditing.exception.PicsException;

public class SubscriptionValidationException extends PicsException {
	private int subscriptionId;

	public SubscriptionValidationException(String message, int subscriptionId) {
		super(message);
		this.subscriptionId = subscriptionId;
	}

	public int getSubscriptionId() {
		return subscriptionId;
	}
}
