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

	static public InvoiceFee calculateAnnualFee(ContractorAccount contractor) {

		List<ContractorOperator> contractorOperators = contractor.getOperators();

		List<OperatorAccount> payingOperators = new Vector<OperatorAccount>();
		for (ContractorOperator contractorOperator : contractorOperators) {
			OperatorAccount operator = contractorOperator.getOperatorAccount();
			if (operator.getDoContractorsPay() != null && !operator.getDoContractorsPay().equals("No"))
				payingOperators.add(operator);
		}

		// Contractors with no paying facilities are free
		InvoiceFee theDefault = new InvoiceFee();
		theDefault.setId(InvoiceFee.FREE);
		if (payingOperators.size() == 0)
			return theDefault;

		// If only one facility is selected and it's a "multiple"
		// like Empire or BP Pipelines, then it's free too
		if (payingOperators.size() == 1 && payingOperators.get(0).getDoContractorsPay().equals("Multiple"))
			return theDefault;

		boolean audited = false;
		int billable = 0;
		for (OperatorAccount operator : payingOperators) {
			if (operator.getDoContractorsPay().equals("Yes") || operator.getDoContractorsPay().equals("Multiple")) {
				billable++;
				
				if (!audited) {
					// See if this operator requires this contractor to be audited
					for (AuditOperator audit : operator.getAudits()) {
						if (audit.isCanSee() 
								&& audit.getMinRiskLevel() > 0
								&& audit.getMinRiskLevel() <= contractor.getRiskLevel().ordinal()) {
							// This operator requires this audit and can see it
							if (audit.getAuditType().getId() == AuditType.DA && "Yes".equals(contractor.getOqEmployees())) {
								audited = true;
							}
	
							if (audit.getAuditType().getId() == AuditType.DESKTOP
									|| audit.getAuditType().getId() == AuditType.OFFICE
									|| audit.getAuditType().getClassType() == AuditTypeClass.IM) {
								audited = true;
							}
						}
					}
				}
			}
		}
		Integer feeId = InvoiceFee.PQFONLY;
		
		if (audited) {
			feeId = calculatePriceTier(billable);
		}

		InvoiceFee fee = new InvoiceFee();
		fee.setId(feeId);

		return fee;

	}

	/**
	 * 
	 * @param billable
	 *            the number of billable facilities
	 * @return the InvoiceFee.id for the annual membership level
	 */
	static private Integer calculatePriceTier(int billable) {
		if (billable >= 20) return InvoiceFee.FACILITIES20;
		if (billable >= 13) return InvoiceFee.FACILITIES13;
		if (billable >= 9) return InvoiceFee.FACILITIES9;
		if (billable >= 5) return InvoiceFee.FACILITIES5;
		if (billable >= 2) return InvoiceFee.FACILITIES2;
		return InvoiceFee.FACILITIES1;
	}
}
