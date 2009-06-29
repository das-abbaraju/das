package com.picsauditing.mail;

public enum Subscription {
	PICSSystemNotification("PICS System Notification"),
	PICSAnnouncements("PICS Announcements"),
	ContractorRegistration("Contractor Registration",true,false),
	ContractorFinished("Contractors completes the PICS process", true, false),
	FlagChanges("FlagChanges",true,false),
	PendingInsuranceCerts("Pending Insurance Certs",true,false),
	VerifiedInsuranceCerts("Verified Insurance Certs",true,false),
	QuarterlyExecutiveReport("Quarterly Executive Report",new SubscriptionTimePeriod[] {SubscriptionTimePeriod.Quarterly},true,false),
	ContractorInvoices("Contractor Invoices",false,true),
	InsuranceExpiration("Insurance Expiration",false,true),
	AuditOpenRequirements("Audit Open Requirements",false,true),
	FinishPICSProcess("Contractor Completes the PICS process", false, true);

	private String description;
	private SubscriptionTimePeriod[] supportedTimePeriods = {SubscriptionTimePeriod.Daily, SubscriptionTimePeriod.Monthly, SubscriptionTimePeriod.Weekly };

	// Whether or not contractor/operator account is required for this
	// subscription
	private boolean requiredForOperator = true;
	private boolean requiredForContractor = true;

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
}
