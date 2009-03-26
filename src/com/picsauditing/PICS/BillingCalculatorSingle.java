package com.picsauditing.PICS;

import java.util.List;
import java.util.Vector;

import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.OperatorAccount;

public class BillingCalculatorSingle {

	static public void setPayingFacilities(ContractorAccount contractor) {

		List<OperatorAccount> payingOperators = new Vector<OperatorAccount>();
		for (ContractorOperator contractorOperator : contractor.getOperators()) {
			OperatorAccount operator = contractorOperator.getOperatorAccount();
			if (operator.getDoContractorsPay() != null && !"No".equals(operator.getDoContractorsPay()))
				payingOperators.add(operator);
		}

		if (payingOperators.size() == 1) {
			// Only one operator, let's see if it's a multiple
			if (payingOperators.get(0).getDoContractorsPay().equals("Multiple")) {
				contractor.setPayingFacilities(0);
				return;
			}
		}

		contractor.setPayingFacilities(payingOperators.size());
	}

	static public InvoiceFee calculateAnnualFee(ContractorAccount contractor) {
		setPayingFacilities(contractor);

		InvoiceFee fee = new InvoiceFee();

		if (contractor.getPayingFacilities() == 0) {
			// Contractors with no paying facilities are free
			fee.setId(InvoiceFee.FREE); // $0
			return fee;
		}

		if (isAudited(contractor))
			// Audited Contractors have a tiered pricing scheme
			fee.setId(calculatePriceTier(contractor.getPayingFacilities()));
		else
			fee.setId(InvoiceFee.PQFONLY); // $99

		return fee;

	}

	static private boolean isAudited(ContractorAccount contractor) {
		// We have at least one paying operator, let's see if they need to be audited

		for (ContractorOperator contractorOperator : contractor.getOperators()) {
			OperatorAccount operator = contractorOperator.getOperatorAccount();
			if (operator.getDoContractorsPay() != null && !operator.getDoContractorsPay().equals("No")) {
				// See if this operator requires this contractor to be audited
				for (AuditOperator audit : operator.getAudits()) {
					if (audit.isRequiredFor(contractor)) {
						// This operator requires this audit and can see it
						if (audit.getAuditType().getId() == AuditType.DA && "Yes".equals(contractor.getOqEmployees()))
							return true;

						if (audit.getAuditType().getId() == AuditType.DESKTOP
								|| audit.getAuditType().getId() == AuditType.OFFICE
								|| audit.getAuditType().getClassType() == AuditTypeClass.IM)
							return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param billable
	 *            the number of billable facilities
	 * @return the InvoiceFee.id for the annual membership level
	 */
	static private Integer calculatePriceTier(int billable) {
		if (billable >= 20)
			return InvoiceFee.FACILITIES20;
		if (billable >= 13)
			return InvoiceFee.FACILITIES13;
		if (billable >= 9)
			return InvoiceFee.FACILITIES9;
		if (billable >= 5)
			return InvoiceFee.FACILITIES5;
		if (billable >= 2)
			return InvoiceFee.FACILITIES2;
		return InvoiceFee.FACILITIES1;
	}
}
