package com.picsauditing.actions.converters;

import com.picsauditing.mail.SubscriptionTimePeriod;

@SuppressWarnings("unchecked")
public class SubscriptionTimePeriodConverter extends EnumConverter {
	public SubscriptionTimePeriodConverter() {
		enumClass = SubscriptionTimePeriod.class;
	}
}
