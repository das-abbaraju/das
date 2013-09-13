package com.picsauditing.model.billing.helper;

import com.picsauditing.jpa.entities.ContractorAccount;

public class ContractorResetter {

	/**
	 * Enforce singleton nature of this class.
	 */
	private ContractorResetter() {
	}

	public static void resetContractor(ContractorAccount contractor, ContractorInvoiceState contractorState) {
		resetForActivation(contractor, contractorState);
		resetForRenewal(contractor, contractorState);
		resetForUpgrade(contractor, contractorState);
		resetForReactivation(contractor, contractorState);
	}

	private static void resetForActivation(ContractorAccount contractor, ContractorInvoiceState contractorState) {
		if (contractorState.isActivation()) {
			contractor.setMembershipDate(null);
			contractor.setPaymentExpires(null);
		}
	}

	/*
	 * I am currently leaving the three methods, even though they do the same
	 * thing, because we may determine through testing that more needs to be
	 * done to the contractor account for these two cases.
	 */

	private static void resetForRenewal(ContractorAccount contractor, ContractorInvoiceState contractorState) {
		if (contractorState.isRenewal()) {
			contractor.setPaymentExpires(contractorState.getPaymentExpiresDate());
			contractor.setRenew(true);
		}
	}

	private static void resetForUpgrade(ContractorAccount contractor, ContractorInvoiceState contractorState) {
		if (contractorState.isUpgrade()) {
			contractor.setPaymentExpires(contractorState.getPaymentExpiresDate());
		}
	}

	private static void resetForReactivation(ContractorAccount contractor, ContractorInvoiceState contractorState) {
		if (contractorState.isReactivation()) {
			contractor.setPaymentExpires(contractorState.getPaymentExpiresDate());
		}
	}

}
