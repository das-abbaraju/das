package com.picsauditing.mail;

public enum Subscription {
	PICSSystemNotification("PICS System Notification", "This email notifies users of important website changes and upgrades. This will normally include less than five emails per month.", new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
			SubscriptionTimePeriod.Event }),
	PICSAnnouncements("PICS Announcements", "This email notifies users of important PICS announcements. This will normally include less than one email per month.", new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
			SubscriptionTimePeriod.Event }),
	ContractorRegistration("Contractor Registration", "This email notifies users of all the contractors who registered recently with their account. This will normally be based on a daily, weekly or monthly time period.", true, false),
	ContractorFinished("Contractors completes the PICS process", "This email notifies users when a contractor completes PICS process for their account.", new SubscriptionTimePeriod[] {
			SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event }, true, false),
	FlagChanges("FlagChanges", "This email notifies users of all the contractors who have changed their flag colors with their account. This will normally be based on a daily, weekly or monthly time period.", true, false),
	RedFlags("Red Flags", "This email notifies users of all the contractors who have Red flags with their account. This will normally be based on a daily, weekly or monthly time period.", true, false),
	AmberFlags("Amber Flags", "This email notifies users of all the contractors who have Amber flags with their account. This will normally be based on a daily, weekly or monthly time period.", true, false),
	GreenFlags("Green Flags", "This email notifies users of all the contractors who have Green flags with their account. This will normally be based on a daily, weekly or monthly time period.", true, false),
	PendingInsuranceCerts("Pending Insurance Certs", "This email notifies users of all the contractors with pending Insurance Certificates for their account. This will normally be based on a daily, weekly or monthly time period.", true, false),
	VerifiedInsuranceCerts("Verified Insurance Certs", "This email notifies users of all the contractors with verified Insurance Certificates for their account. This will normally be based on a daily, weekly or monthly time period.", true, false),
	QuarterlyExecutiveReport("Quarterly Executive Report", "This email notifies users of the Quarterly Executive Report. This will normally be based on one email a quarter.", new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
			SubscriptionTimePeriod.Quarterly }, true, false),
	ContractorInvoices("Contractor Invoices", "This email notifies the contractor about the PICS Invoices.", false, true),
	InsuranceExpiration("Insurance Expiration", "This email notifies the contractor when the insurance certificate has expired in PICS.", false, true),
	AuditOpenRequirements("Audit Open Requirements", "This email notifies the contractors with the Audit Open Requirements.", false, true),
	FinishPICSProcess("Contractor Completes the PICS process", "This email notifies the contractor when they have completed the PICS process.", false, true);

	private String description;
	private String longDescription;
	private SubscriptionTimePeriod[] supportedTimePeriods = { SubscriptionTimePeriod.None,
			SubscriptionTimePeriod.Daily, SubscriptionTimePeriod.Weekly, SubscriptionTimePeriod.Monthly };
	private boolean requiredForOperator = true;
	private boolean requiredForContractor = true;

	Subscription(String description, String longDescription) {
		this.description = description;
		this.longDescription = longDescription;
	}

	Subscription(String description, String longDescription, SubscriptionTimePeriod[] supportedTimePeriods) {
		this.description = description;
		this.longDescription = longDescription;
		this.supportedTimePeriods = supportedTimePeriods;
	}

	Subscription(String description, String longDescription, boolean requiredForOperator, boolean requiredForContractor) {
		this.description = description;
		this.longDescription = longDescription;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
	}

	Subscription(String description, String longDescription, SubscriptionTimePeriod[] supportedTimePeriods, boolean requiredForOperator,
			boolean requiredForContractor) {
		this.description = description;
		this.longDescription = longDescription;
		this.supportedTimePeriods = supportedTimePeriods;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
	}

	public String getDescription() {
		return description;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public SubscriptionTimePeriod[] getSupportedTimePeriods() {
		return supportedTimePeriods;
	}

	public boolean isRequiredForOperator() {
		return requiredForOperator;
	}

	public boolean isRequiredForContractor() {
		return requiredForContractor;
	}
	
	public String getAppPropertyKey() {
		return "subscription." + this.toString();
	}
}
