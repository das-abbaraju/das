package com.picsauditing.jpa.entities;

public enum ContractorRegistrationStep {
	Register, EditAccount, Trades, Risk, Facilities, Payment, Confirmation, Done;

	static public ContractorRegistrationStep getStep(
			ContractorAccount contractor) {
		if (contractor == null || contractor.getId() == 0)
			return Register;

		if (contractor.getStatus().isActive())
			return Done;
		
		if (contractor.getStatus().isDemo())
			return Done;

		if (contractor.getTradesUpdated() == null)
			return Trades;
		if (contractor.getSafetyRisk() == null)
			return Risk;
		if (contractor.isMaterialSupplier()
				&& contractor.getProductRisk() == null)
			return Risk;
		if (contractor.getOperators().size() == 0)
			return Facilities;
		if (contractor.isMustPayB() && !contractor.isPaymentMethodStatusValid())
			return Payment;

		return Confirmation;
	}
//  static public boolean isActive(ContractorRegistrationStep currentStep, ContractorRegistrationStep activeStep)
}
