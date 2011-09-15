package com.picsauditing.mail;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.Translatable;
import com.picsauditing.jpa.entities.TranslatableString;

public enum Subscription implements Translatable {
	ContractorRegistration(62, true, false, false),
	ContractorDeactivation(51,
			new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event }, true, false,
			false),
	ContractorAdded(107, true, false, false),
	ContractorFinished(63, new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event },
			true, false, false),
	ForcedFlags(165, true, false, false),
	FlagChanges(60, true, false, false),
	RedFlags(65, true, false, false),
	AmberFlags(65, true, false, false),
	GreenFlags(65, true, false, false),
	PendingInsuranceCerts(61, OpPerms.InsuranceCerts, true, false, false),
	VerifiedInsuranceCerts(61, OpPerms.InsuranceApproval, true, false, false),
	TrialContractorAccounts(71, OpPerms.ViewTrialAccounts, true, false, false),
	ContractorInvoices(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event },
			false, true, false),
	InsuranceExpiration(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event },
			false, true, false),
	AuditOpenRequirements(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event },
			false, true, false),
	FinishPICSProcess(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event },
			false, true, false),
	PICSSystemNotifications(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event },
			true, false, false),
	OQChanges(130, new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Monthly },
			OpPerms.ViewTrialAccounts, false, false, false, true, false),
	Webinar(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event }, false, true,
			false),
	// Please use nightly_updates.sql for controling opt-out subscription inserts
	OpenTasks(168, new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Monthly }, false,
			true, false),
	RegistrationRequests(new SubscriptionTimePeriod[] { SubscriptionTimePeriod.None, SubscriptionTimePeriod.Event },
			true, false, false);

	private int templateID;
	private TranslatableString description;
	private TranslatableString longDescription;
	private SubscriptionTimePeriod[] supportedTimePeriods = { SubscriptionTimePeriod.None,
			SubscriptionTimePeriod.Daily, SubscriptionTimePeriod.Weekly, SubscriptionTimePeriod.Monthly };
	private boolean requiredForOperator = true;
	private boolean requiredForContractor = true;
	private boolean requiredForAdmin = true;
	private OpPerms requiredPerms = null;
	private boolean requiresOQ = false;

	Subscription(int templateID, boolean requiredForOperator, boolean requiredForContractor, boolean requiredForAdmin) {
		this.templateID = templateID;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
		this.requiredForAdmin = requiredForAdmin;
	}

	Subscription(int templateID, OpPerms requiredPerms, boolean requiredForOperator, boolean requiredForContractor,
			boolean requiredForAdmin) {
		this.templateID = templateID;
		this.requiredPerms = requiredPerms;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
		this.requiredForAdmin = requiredForAdmin;
	}

	Subscription(SubscriptionTimePeriod[] supportedTimePeriods, boolean requiredForOperator,
			boolean requiredForContractor, boolean requiredForAdmin) {
		this.supportedTimePeriods = supportedTimePeriods;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
		this.requiredForAdmin = requiredForAdmin;
	}

	Subscription(int templateID, SubscriptionTimePeriod[] supportedTimePeriods, boolean requiredForOperator,
			boolean requiredForContractor, boolean requiredForAdmin) {
		this.templateID = templateID;
		this.supportedTimePeriods = supportedTimePeriods;
		this.requiredForOperator = requiredForOperator;
		this.requiredForContractor = requiredForContractor;
		this.requiredForAdmin = requiredForAdmin;
	}

	Subscription(int templateID, SubscriptionTimePeriod[] supportedTimePeriods, OpPerms requiredPerms,
			boolean requiredForOperator, boolean requiredForContractor, boolean requiredForAdmin, boolean requiresOQ,
			boolean optIn) {
		this.templateID = templateID;
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

	public TranslatableString getDescription() {
		return description;
	}

	public TranslatableString getLongDescription() {
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

	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.name();
	}

	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
