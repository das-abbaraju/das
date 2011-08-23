package com.picsauditing.mail.subscription;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.mail.Subscription;

public class SubscriptionBuilderFactory {
	@Autowired
	ContractorRegistrationSubscription contractorRegistration;
	@Autowired
	FlagChangesSubscription flagChanges;
	@Autowired
	FlagColorSubscription flagColor;
	@Autowired
	ForcedFlagsSubscription forcedFlags;
	@Autowired
	InsuranceCertificateSubscription insuranceCertificate;
	@Autowired
	OpenTasksSubscription openTasks;
	@Autowired
	OQChangesSubscription oqChanges;
	@Autowired
	TrialContractorAccountsSubscription trialContractorAccounts;

	// Subscription => Builder

	/**
	 * Subscription Classes Missing From Here: ContractorDeactivation, ContractorAdded, ContractorFinished,
	 * ContractorInvoices, InsuranceExpiration, AuditOpenRequirements, FinishPICSProcess, PICSSystemNotifications,
	 * Webinar, RegistrationRequests
	 */
	public SubscriptionBuilder getBuilder(Subscription subscription) {
		switch (subscription) {
		case ContractorRegistration:
			return contractorRegistration;
		case ForcedFlags:
			return forcedFlags;
		case FlagChanges:
			return flagChanges;
		case RedFlags:
		case AmberFlags:
		case GreenFlags:
			return flagColor;
		case PendingInsuranceCerts:
		case VerifiedInsuranceCerts:
			return insuranceCertificate;
		case TrialContractorAccounts:
			return trialContractorAccounts;
		case OQChanges:
			return oqChanges;
		case OpenTasks:
			return openTasks;
		default:
			return null;
		}
	}
}
