package com.picsauditing.jpa.entities;

import java.util.List;

public enum ContractorRegistrationStep {
	Register, EditAccount, Trades, Risk, Facilities, Payment, Confirmation, Done;

	static public ContractorRegistrationStep getStep(ContractorAccount contractor) {
		if (contractor == null || contractor.getId() == 0)
			return Register;

		if (contractor.getStatus().isActive())
			return Done;

		if (contractor.getStatus().isDemo())
			return Done;

		if (contractor.getTrades().size() == 0)
			return Trades;
		if (contractor.getSafetyRisk().equals(LowMedHigh.None))
			return Risk;
		if (contractor.isMaterialSupplier() && contractor.getProductRisk().equals(LowMedHigh.None))
			return Risk;
		if (!containsOperator(contractor.getOperators()) || contractor.isNeedsToIndicateCompetitor())
			return Facilities;
		if (!contractor.isHasFreeMembership() && !contractor.isPaymentMethodStatusValid())
			return Payment;

		return Confirmation;
	}
	
	static private boolean containsOperator(List<ContractorOperator> cos) {
		if (cos == null)
			return false;
		
		for (ContractorOperator co : cos) {
			if (co.getOperatorAccount().isOperator())
				return true;
		}
		
		return false;
	}

	public String getUrl(int id) {

		switch (this) {
		case Register:
			return "ContractorRegistration.action?id=" + id;
		case Trades:
			return "ContractorTrades.action?id=" + id;
		case Risk:
			return "ContractorRegistrationServices.action?id=" + id;
		case Facilities:
			return "ContractorFacilities.action?id=" + id;
		case Payment:
			return "ContractorPaymentOptions.action?id=" + id;
		case Confirmation:
			return "ContractorRegistrationFinish.action?id=" + id;
		case EditAccount:
			return "ContractorEdit.action?id=" + id;
		case Done:
		default:
			return "Home.action";

		}
	}

	public boolean isDone() {
		return this == Done;
	}
	
	public boolean isHasNext() {
		return this.ordinal() < Confirmation.ordinal();
	}

	public boolean isHasPrevious() {
		return this.ordinal() > EditAccount.ordinal();
	}
	
	public boolean isShowTop() {
		return Trades == this || EditAccount == this || Facilities == this || Payment == this;
	}
	
	public boolean isShowBottom() {
		return Risk == this;
	}
}