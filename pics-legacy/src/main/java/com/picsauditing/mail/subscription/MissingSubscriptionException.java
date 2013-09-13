package com.picsauditing.mail.subscription;

@SuppressWarnings("serial")
public class MissingSubscriptionException extends Exception {

	public MissingSubscriptionException() {
		super("No mapping exists for this Subscription.");
	}
}
