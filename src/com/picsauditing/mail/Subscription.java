package com.picsauditing.mail;

public enum Subscription {
	PICSSystemNotification("PICS System Notification", new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
			SubscriptionTimePeriod.Event }),
	PICSAnnouncements("PICS Announcements", new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
			SubscriptionTimePeriod.Event }),
	ContractorRegistration("Contractor Registration", true, false),
	ContractorFinished("Contractors completes the PICS process", new SubscriptionTimePeriod[] {
			SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event }, true, false),
	FlagChanges("FlagChanges", true, false),
	RedFlags("Red Flags", true, false),
	AmberFlags("Amber Flags", true, false),
	GreenFlags("Green Flags", true, false),
	PendingInsuranceCerts("Pending Insurance Certs", true, false),
	VerifiedInsuranceCerts("Verified Insurance Certs", true, false),
	QuarterlyExecutiveReport("Quarterly Executive Report", new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,
			SubscriptionTimePeriod.Quarterly }, true, false),
	ContractorInvoices("Contractor Invoices", false, true),
	InsuranceExpiration("Insurance Expiration", false, true),
	AuditOpenRequirements("Audit Open Requirements", false, true),
	FinishPICSProcess("Contractor Completes the PICS process", false, true);

	private String description;
	private SubscriptionTimePeriod[] supportedTimePeriods = { SubscriptionTimePeriod.None,
			SubscriptionTimePeriod.Daily, SubscriptionTimePeriod.Weekly, SubscriptionTimePeriod.Monthly };
	private boolean requiredForOperator = true;
	private boolean requiredForContractor = true;

	Subscription(String description) {
		this.description = description;
	}

	Subscription(String description, SubscriptionTimePeriod[] supportedTimePeriods) {
		this.description = description;
		this.supportedTimePeriods = supportedTimePeriods;
	}

	Subscription(String description, boolean requiredForOperator, boolean requiredForContractor) {
		this.description = description;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
	}

	Subscription(String description, SubscriptionTimePeriod[] supportedTimePeriods, boolean requiredForOperator,
			boolean requiredForContractor) {
		this.description = description;
		this.supportedTimePeriods = supportedTimePeriods;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
	}

	public String getDescription() {
		return description;
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
