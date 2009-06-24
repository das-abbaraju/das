package com.picsauditing.jpa.entities;

import java.util.HashSet;
import java.util.Set;

public enum Subscription {
	PICS_SYSTEM_NOTIFICATION("Temp Description");

	private String description;
	private SubscriptionTimePeriod[] supportedTimePeriods;
	private boolean requiredForOperator;
	private boolean requiredForContractor;

	Subscription() {
		this.supportedTimePeriods = SubscriptionTimePeriod.values();
	}

	Subscription(String description) {
		this.description = description;
		this.supportedTimePeriods = SubscriptionTimePeriod.values();
	}

	Subscription(String description, SubscriptionTimePeriod[] supportedTimePeriods) {
		this.description = description;
		this.supportedTimePeriods = supportedTimePeriods;
	}

	Subscription(String description, boolean requiredForOperator, boolean requiredForContractor) {
		this.description = description;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
		this.supportedTimePeriods = SubscriptionTimePeriod.values();
	}

	Subscription(String description, SubscriptionTimePeriod[] supportedTimePeriods, boolean requiredForOperator,
			boolean requiredForContractor) {
		this.description = description;
		this.supportedTimePeriods = supportedTimePeriods;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
	}
}
