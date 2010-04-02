package com.picsauditing.mail;

import com.picsauditing.access.OpPerms;

public enum Subscription {
	ContractorRegistration(
			"Contractor Registration",
			"This email includes a list of contractors who have recently registered at PICS with your account. You can choose to receive this daily, weekly or monthly. If no contractor registers, then you will not receive an email.",
			true, false), 
	ContractorFinished(
			"Contractors Completes the PICS Process",
			"This email notifies users when a contractor completes PICS process for their account.",
			new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,	SubscriptionTimePeriod.Event }, 
			true, false), 
	FlagChanges(
			"Contractor Flag Changes",
			"This email shows contractors who have recently had a flag colors upgraded or downgraded. You can choose to receive this daily, weekly or monthly.",
			true, false), 
	RedFlags(
			"Red Flagged Contractors",
			"This email notifies users of all the contractors who have Red flags with their account. You can choose to receive this daily, weekly or monthly.",
			true, false), 
	AmberFlags(
			"Amber Flagged Contractors",
			"This email notifies users of all the contractors who have Amber flags with their account. You can choose to receive this daily, weekly or monthly.",
			true, false), 
	GreenFlags(
			"Green Flagged Contractors",
			"This email notifies users of all the contractors who have Green flags with their account. You can choose to receive this daily, weekly or monthly.",
			true, false),
	PendingInsuranceCerts(
			"Pending Insurance Certs",
			"This email notifies users of all the contractors with pending Insurance Certificates for their account. You can choose to receive this daily, weekly or monthly.",
			OpPerms.InsuranceCerts, true, false), 
	VerifiedInsuranceCerts(
			"Verified Insurance Certs",
			"This email notifies users of all the contractors with verified Insurance Certificates for their account. You can choose to receive this daily, weekly or monthly.",
			 OpPerms.InsuranceApproval, true, false), 
	TrialContractorAccounts(
			"Bid Only Contractor Accounts",
			"This email includes a list of Bid Only contractor accounts who have recently registered at PICS with your account and are awaiting Approval. You can choose to receive this daily, weekly or monthly.",
			OpPerms.ViewTrialAccounts, true, false), 
	ContractorInvoices(
			"Contractor Invoices",
			"This email notifies the contractor about the PICS Invoices.",
			false, true), 
	InsuranceExpiration(
			"Insurance Expiration",
			"This email notifies the contractor when the insurance certificate has expired in PICS.",
			false, true), 
	AuditOpenRequirements(
			"Audit Open Requirements",
			"This email notifies the contractors with the Audit Open Requirements.",
			false, true), 
	FinishPICSProcess(
			"Contractor Completes the PICS process",
			"This email notifies the contractor when they have completed the PICS process.",
			false, true),
	PICSSystemNotifications(
			"PICS System Notifications",
			"This email notifies the users when there has been updates or changes to the PICS system.",
			new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None,	SubscriptionTimePeriod.Event},
			true, false);

	private String description;
	private String longDescription;
	private SubscriptionTimePeriod[] supportedTimePeriods = {
			SubscriptionTimePeriod.None, SubscriptionTimePeriod.Daily,
			SubscriptionTimePeriod.Weekly, SubscriptionTimePeriod.Monthly };
	private boolean requiredForOperator = true;
	private boolean requiredForContractor = true;
	private OpPerms requiredPerms = null;

	Subscription(String description, String longDescription) {
		this.description = description;
		this.longDescription = longDescription;
	}

	Subscription(String description, String longDescription,
			SubscriptionTimePeriod[] supportedTimePeriods) {
		this.description = description;
		this.longDescription = longDescription;
		this.supportedTimePeriods = supportedTimePeriods;
	}

	Subscription(String description, String longDescription,
			boolean requiredForOperator, boolean requiredForContractor) {
		this.description = description;
		this.longDescription = longDescription;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
	}
	
	Subscription(String description, String longDescription,
			OpPerms requiredPerms, boolean requiredForOperator, boolean requiredForContractor) {
		this.description = description;
		this.longDescription = longDescription;
		this.requiredPerms = requiredPerms;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
	}

	Subscription(String description, String longDescription,
			SubscriptionTimePeriod[] supportedTimePeriods,
			boolean requiredForOperator, boolean requiredForContractor) {
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
	
	public OpPerms getRequiredPerms() {
		return requiredPerms;
	}

	public String getAppPropertyKey() {
		return "subscription." + this.toString();
	}
}
