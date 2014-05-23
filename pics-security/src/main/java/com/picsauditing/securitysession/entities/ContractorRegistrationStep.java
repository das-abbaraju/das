package com.picsauditing.securitysession.entities;

import com.picsauditing.securitysession.service.AccountService;

import java.util.ArrayList;
import java.util.List;

public enum ContractorRegistrationStep {
	Register, Clients, Risk, // a.k.a. Service Evaluation
	Payment, // a.k.a. Join
	Done;

    protected static final List<String> stepPages = new ArrayList() {{
        add("Registration");
        add("RegistrationAddClientSite");
        add("RegistrationServiceEvaluation");
        add("RegistrationMakePayment");
    }};

	static public ContractorRegistrationStep getStep(ContractorAccount contractor) {
		if (contractor == null || contractor.getId() == 0) {
			return Register;
		} else if (contractor.getStatus() == AccountStatus.Demo || contractor.getStatus() == AccountStatus.Active) {
			return Done;
		} else if (!containsAtLeastOneClientSiteForGCFree(contractor)) {
			return Clients;
		} else if (!containsOperator(contractor.getOperators())) {
			return Clients;
		} else if (contractor.getSafetyRisk().equals(LowMedHigh.None) && !AccountService.isMaterialSupplierOnly(contractor)
				&& !contractor.isTransportationServices()) {
			return Risk;
		} else if (contractor.isMaterialSupplier() && contractor.getProductRisk().equals(LowMedHigh.None)) {
			return Risk;
		} else if (!AccountService.isHasFreeMembership(contractor) && contractor.getStatus().isPendingRequestedOrDeactivated()) {
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
			if (AccountService.isOperator(co.getOperatorAccount())) {
				return true;
			}
		}

		return false;
	}

	static public boolean containsAtLeastOneClientSiteForGCFree(ContractorAccount contractor) {
		for (OperatorAccount generalContractor : AccountService.getGeneralContractorOperatorAccounts(contractor)) {
			if ("No".equals(generalContractor.getDoContractorsPay())) {
				List<OperatorAccount> intersection = new ArrayList<>(
						AccountService.getLinkedClientSites(generalContractor));
				intersection.retainAll(AccountService.getOperatorAccounts(contractor));

				if (intersection.isEmpty()) {
					return false;
				}
			}
		}

		return true;
	}

    public static boolean pageIsARegistrationStep(String actionName) {
        return stepPages.contains(actionName);
    }

//    public String getUrl() {
//
//		switch (this) {
//		case Register:
//			return "Registration.action";
//		case Clients:
//			return "RegistrationAddClientSite.action";
//		case Risk:
//			return "RegistrationServiceEvaluation.action";
//		case Payment:
//			return "RegistrationMakePayment.action";
//		case Done:
//		default:
//			return "ContractorView.action";
//
//		}
//	}
//
	public boolean isDone() {
		return this == Done;
	}

//	public boolean isHasNext() {
//		return this.ordinal() < Done.ordinal();
//	}
//
//	public boolean isHasPrevious() {
//		return this.ordinal() > Clients.ordinal();
//	}
//
//	@Deprecated
//	public boolean isShowTop() {
//		return Clients == this || Payment == this;
//	}
//
//	@Deprecated
//	public boolean isShowBottom() {
//		return Risk == this;
//	}
}