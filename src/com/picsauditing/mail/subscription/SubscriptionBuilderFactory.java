package com.picsauditing.mail.subscription;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.mail.Subscription;

public class SubscriptionBuilderFactory {
	@Autowired
	ContractorAddedSubscription contractorAdded;
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
	@Autowired
	CancelledScheduledAuditsSubscription cancelledScheduleAudits;

	// Subscription => Builder

	/**
	 * Subscription Classes Missing From Here: ContractorDeactivation,
	 * ContractorAdded, ContractorFinished, ContractorInvoices,
	 * InsuranceExpiration, AuditOpenRequirements, FinishPICSProcess,
	 * PICSSystemNotifications, Webinar, RegistrationRequests
	 * 
	 * @throws MissingSubscriptionException
	 */
	public SubscriptionBuilder getBuilder(Subscription subscription) throws MissingSubscriptionException {
		switch (subscription) {
		case ContractorAdded:
			return contractorAdded;
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
		case CancelledScheduledAudits:
			return cancelledScheduleAudits;
		default:
			throw new MissingSubscriptionException();
		}
	}
}
