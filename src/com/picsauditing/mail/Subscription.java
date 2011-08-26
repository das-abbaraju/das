package com.picsauditing.mail;

import com.picsauditing.access.OpPerms;

public enum Subscription {
	ContractorRegistration(
			62,
			"Contractor Registration",
			"This email includes a list of contractors who have recently registered for PICS under your account. You can choose to receive this daily, weekly or monthly. If no contractor registers, then you will not receive an email.",
			true, false, false),
	ContractorDeactivation(
			51,
			"Contractor Deactivation",
			"This email includes a list of contractors who have recently been deactivated by PICS and are related to your account. You can choose to receive this daily, weekly or monthly. If no contractor deactivates, then you will not receive an email.",
			new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event }, true, false,
			false),
	ContractorAdded(
			107,
			"Contractor Added to Facility",
			"This email includes a list of contractors who have recently been added to your Facility. You can choose to receive this daily, weekly or monthly. If no contractors are added to your facility, then you will not receive an email.",
			true, false, false),
	ContractorFinished(63, "Contractor Completed the PICS Process",
			"This email notifies you when a contractor linked to your facility has completed the PICS process.",
			new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event }, true, false,
			false),
	ForcedFlags(
			165,
			"Forced Flags",
			"This email shows contractors whose flags have been forced, as well as who forced them. You can choose to receive this daily, weekly or monthly.",
			true, false, false),
	FlagChanges(
			60,
			"Contractor Flag Changes",
			"This email shows contractors who have recently had a flag colors upgraded or downgraded. You can choose to receive this daily, weekly or monthly.",
			true, false, false),
	RedFlags(
			65,
			"Red Flagged Contractors",
			"This email notifies users of all the contractors who have Red flags with their account. You can choose to receive this daily, weekly or monthly.",
			true, false, false),
	AmberFlags(
			65,
			"Amber Flagged Contractors",
			"This email notifies users of all the contractors who have Amber flags with their account. You can choose to receive this daily, weekly or monthly.",
			true, false, false),
	GreenFlags(
			65,
			"Green Flagged Contractors",
			"This email notifies users of all the contractors who have Green flags with their account. You can choose to receive this daily, weekly or monthly.",
			true, false, false),
	PendingInsuranceCerts(
			61,
			"Pending Insurance Certs",
			"This email notifies users of all the contractors with pending Insurance Certificates for their account. You can choose to receive this daily, weekly or monthly.",
			OpPerms.InsuranceCerts, true, false, false),
	VerifiedInsuranceCerts(
			61,
			"Verified Insurance Certs",
			"This email notifies users of all the contractors with completed Insurance Certificates for their account. You can choose to receive this daily, weekly or monthly.",
			OpPerms.InsuranceApproval, true, false, false),
	TrialContractorAccounts(
			71,
			"Bid Only Contractor Accounts",
			"This email includes a list of Bid Only contractor accounts who have recently registered at PICS with your account and are awaiting Approval. You can choose to receive this daily, weekly or monthly.",
			OpPerms.ViewTrialAccounts, true, false, false),
	ContractorInvoices("Contractor Invoices", "This email notifies the contractor about the PICS Invoices.",
			new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event }, false, true,
			false),
	InsuranceExpiration("Insurance Expiration",
			"This email notifies the contractor when the insurance certificate has expired in PICS.",
			new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event }, false, true,
			false),
	AuditOpenRequirements("Audit Open Requirements",
			"This email notifies the contractors with the Audit Open Requirements.", new SubscriptionTimePeriod[] {
					SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event }, false, true, false),
	FinishPICSProcess("Contractor Completes the PICS process",
			"This email notifies the contractor when they have completed the PICS process.",
			new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event }, false, true,
			false),
	PICSSystemNotifications("PICS System Notifications",
			"This email notifies the users when there has been updates or changes to the PICS system.",
			new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event }, true, false,
			false),
	OQChanges(130, "Recent Operator Qualification Changes",
			"This email notifies the contractors with recent OQ changes.", new SubscriptionTimePeriod[] {
					SubscriptionTimePeriod.None, SubscriptionTimePeriod.Monthly }, OpPerms.ViewTrialAccounts, false,
			false, false, true, false),
	Webinar("Webinar", "This email notifies the contractor of upcoming Webinars", new SubscriptionTimePeriod[] {
			SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event }, true, true, true),
	// Please use nightly_updates.sql for controling opt-out subscription inserts
	OpenTasks(168, "Contractor Open Tasks",
			"This email notifies the user of Open Tasks pending for their Account which require Action.",
			new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Monthly }, false, true,
			false),
	RegistrationRequests(
			"Registration Requests",
			"This email notifies users of changes to the registration requests they have submitted  You can choose Event to receive these emails.",
			new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event }, true, false,
			false);

	private int templateID;
	private String description;
	private String longDescription;
	private SubscriptionTimePeriod[] supportedTimePeriods = { SubscriptionTimePeriod.None,
			SubscriptionTimePeriod.Daily, SubscriptionTimePeriod.Weekly, SubscriptionTimePeriod.Monthly };
	private SubscriptionTimePeriod defaultTimePeriod = SubscriptionTimePeriod.Monthly;
	private boolean requiredForOperator = true;
	private boolean requiredForContractor = true;
	private boolean requiredForAdmin = true;
	private OpPerms requiredPerms = null;
	private boolean requiresOQ = false;

	Subscription(String description, String longDescription) {
		this.description = description;
		this.longDescription = longDescription;
	}

	Subscription(String description, String longDescription, SubscriptionTimePeriod[] supportedTimePeriods) {
		this.description = description;
		this.longDescription = longDescription;
		this.supportedTimePeriods = supportedTimePeriods;
	}

	Subscription(String description, String longDescription, boolean requiredForOperator,
			boolean requiredForContractor, boolean requiredForAdmin) {
		this.description = description;
		this.longDescription = longDescription;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
		this.requiredForAdmin = requiredForAdmin;
	}

	Subscription(int templateID, String description, String longDescription, boolean requiredForOperator,
			boolean requiredForContractor, boolean requiredForAdmin) {
		this.templateID = templateID;
		this.description = description;
		this.longDescription = longDescription;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
		this.requiredForAdmin = requiredForAdmin;
	}

	Subscription(String description, String longDescription, OpPerms requiredPerms, boolean requiredForOperator,
			boolean requiredForContractor, boolean requiredForAdmin) {
		this.description = description;
		this.longDescription = longDescription;
		this.requiredPerms = requiredPerms;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
		this.requiredForAdmin = requiredForAdmin;
	}

	Subscription(int templateID, String description, String longDescription, OpPerms requiredPerms,
			boolean requiredForOperator, boolean requiredForContractor, boolean requiredForAdmin) {
		this.templateID = templateID;
		this.description = description;
		this.longDescription = longDescription;
		this.requiredPerms = requiredPerms;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
		this.requiredForAdmin = requiredForAdmin;
	}

	Subscription(String description, String longDescription, SubscriptionTimePeriod[] supportedTimePeriods,
			boolean requiredForOperator, boolean requiredForContractor, boolean requiredForAdmin) {
		this.description = description;
		this.longDescription = longDescription;
		this.supportedTimePeriods = supportedTimePeriods;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
		this.requiredForAdmin = requiredForAdmin;
	}

	Subscription(String description, String longDescription, SubscriptionTimePeriod[] supportedTimePeriods,
			boolean requiredForOperator, boolean requiredForContractor, boolean requiredForAdmin, OpPerms requiredPerms) {
		this.description = description;
		this.longDescription = longDescription;
		this.supportedTimePeriods = supportedTimePeriods;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
		this.requiredForAdmin = requiredForAdmin;
		this.requiredPerms = requiredPerms;
	}

	Subscription(int templateID, String description, String longDescription,
			SubscriptionTimePeriod[] supportedTimePeriods, boolean requiredForOperator, boolean requiredForContractor,
			boolean requiredForAdmin) {
		this.templateID = templateID;
		this.description = description;
		this.longDescription = longDescription;
		this.supportedTimePeriods = supportedTimePeriods;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
		this.requiredForAdmin = requiredForAdmin;
	}

	Subscription(int templateID, String description, String longDescription,
			SubscriptionTimePeriod[] supportedTimePeriods, OpPerms requiredPerms, boolean requiredForOperator,
			boolean requiredForContractor, boolean requiredForAdmin, boolean requiresOQ, boolean optIn) {
		this.templateID = templateID;
		this.description = description;
		this.longDescription = longDescription;
		this.supportedTimePeriods = supportedTimePeriods;
		this.requiredPerms = requiredPerms;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
		this.requiredForAdmin = requiredForAdmin;
		this.requiresOQ = requiresOQ;
	}

	public int getTemplateID() {
		return templateID;
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

	public boolean isRequiredForAdmin() {
		return requiredForAdmin;
	}

	public void setRequiredForAdmin(boolean requiredForAdmin) {
		this.requiredForAdmin = requiredForAdmin;
	}

	public boolean isRequiresOQ() {
		return requiresOQ;
	}
}
