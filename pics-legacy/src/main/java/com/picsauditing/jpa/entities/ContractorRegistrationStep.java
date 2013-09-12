package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

public enum ContractorRegistrationStep {
	Register, Clients, Risk, // a.k.a. Service Evaluation
	Payment, // a.k.a. Join
	Done;

	static public ContractorRegistrationStep getStep(ContractorAccount contractor) {
		if (contractor == null || contractor.getId() == 0) {
			return Register;
		} else if (contractor.getStatus().isDemo() || contractor.getStatus().isActive()) {
			return Done;
		} else if (!containsAtLeastOneClientSiteForGCFree(contractor)) {
			return Clients;
		} else if (!containsOperator(contractor.getOperators())) {
			return Clients;
		} else if (contractor.getSafetyRisk().equals(LowMedHigh.None) && !contractor.isMaterialSupplierOnly()
				&& !contractor.isTransportationServices()) {
			return Risk;
		} else if (contractor.isMaterialSupplier() && contractor.getProductRisk().equals(LowMedHigh.None)) {
			return Risk;
		} else if (!contractor.isHasFreeMembership() && contractor.getStatus().isPendingRequestedOrDeactivated()) {
			return Payment;
		} else {
			return Done;
		}
	}

	static private boolean containsOperator(List<ContractorOperator> cos) {
		if (cos == null) {
			return false;
		}

		for (ContractorOperator co : cos) {
			if (co.getOperatorAccount().isOperator()) {
				return true;
			}
		}

		return false;
	}

	static public boolean containsAtLeastOneClientSiteForGCFree(ContractorAccount contractor) {
		for (OperatorAccount generalContractor : contractor.getGeneralContractorOperatorAccounts()) {
			if ("No".equals(generalContractor.getDoContractorsPay())) {
				List<OperatorAccount> intersection = new ArrayList<OperatorAccount>(
						generalContractor.getLinkedClientSites());
				intersection.retainAll(contractor.getOperatorAccounts());

				if (intersection.isEmpty()) {
					return false;
				}
			}
		}

		return true;
	}

	public String getUrl() {

		switch (this) {
		case Register:
			return "Registration.action";
		case Clients:
			return "RegistrationAddClientSite.action";
		case Risk:
			return "RegistrationServiceEvaluation.action";
		case Payment:
			return "RegistrationMakePayment.action";
		case Done:
		default:
			return "ContractorView.action";

		}
	}

	public boolean isDone() {
		return this == Done;
	}

	public boolean isHasNext() {
		return this.ordinal() < Done.ordinal();
	}

	public boolean isHasPrevious() {
		return this.ordinal() > Clients.ordinal();
	}

	@Deprecated
	public boolean isShowTop() {
		return Clients == this || Payment == this;
	}

	@Deprecated
	public boolean isShowBottom() {
		return Risk == this;
	}
}
