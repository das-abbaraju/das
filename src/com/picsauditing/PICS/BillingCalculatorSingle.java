package com.picsauditing.PICS;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
		for(ContractorOperator contractorOperator : contractorOperators) {
			OperatorAccount operator = contractorOperator.getOperatorAccount();
			if (operator.getDoContractorsPay() != null && !operator.getDoContractorsPay().equals("No"))
				payingOperators.add(operator);
		}

		
		// Contractors with no paying facilities are free
		InvoiceFee theDefault = new InvoiceFee();
		theDefault.setId(InvoiceFee.FREE);
		if (payingOperators.size() == 0) return theDefault;
		
		// If only one facility is selected and it's a "multiple" 
		// like Empire or BP Pipelines, then it's free too
		if (payingOperators.size()==1 && payingOperators.get(0).getDoContractorsPay().equals("Multiple"))
			return theDefault;

		
		int billable = 0;
		for( OperatorAccount operator : payingOperators ) {
			for( AuditOperator audit : operator.getAudits() ) {
				if (audit.getMinRiskLevel() > 0 ) {
					if (audit.getMinRiskLevel() <= contractor.getRiskLevel().ordinal() ) {
						if (audit.getAuditType().getId() == AuditType.DA
								&& "Yes".equals(contractor.getOqEmployees())) {
							billable++;
							break;
						}
						
						if (audit.getAuditType().getId() == AuditType.DESKTOP 
								|| audit.getAuditType().getId() == AuditType.OFFICE 
								|| audit.getAuditType().getClassType() == AuditTypeClass.IM) {
							billable++;
							break;
						}
					}
				}
			}
		}

		Integer feeId = calculatePriceTier(billable);
		
		InvoiceFee fee = new InvoiceFee();
		fee.setId(feeId);
		
		return fee;
		
	}

	/**
	 * 
	 * @param billable the number of billable facilities
	 * @return the InvoiceFee.id for the annual membership level
	 */
	static private Integer calculatePriceTier(int billable) {
		@SuppressWarnings("serial")
		Map<Integer, Integer> priceTiers = new TreeMap<Integer, Integer>() {{
			put( 0, InvoiceFee.PQFONLY );
			put( 1, InvoiceFee.FACILITIES1 );
			put( 2, InvoiceFee.FACILITIES2 );
			put( 5, InvoiceFee.FACILITIES5 );
			put( 9, InvoiceFee.FACILITIES9 );
			put( 13, InvoiceFee.FACILITIES13 );
			put( 20, InvoiceFee.FACILITIES20 );
		}};

		Integer last = null;
		for( int bottomOfTier : priceTiers.keySet() ) {
			if( billable >= bottomOfTier ) {
				last = priceTiers.get(bottomOfTier);
			}
			else {
				break;
			}
		}
		
		if( last == null ) {
			last = priceTiers.get(0);
		}
		
		return last;
	}
}
