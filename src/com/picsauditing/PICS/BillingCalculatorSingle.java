package com.picsauditing.PICS;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.OperatorAccount;

public class BillingCalculatorSingle {
	
	public InvoiceFee calculateAnnualFee(ContractorAccount contractor) throws Exception {

		
		List<ContractorOperator> contractorOperators = contractor.getOperators();
		
		List<OperatorAccount> payingOperators = new Vector<OperatorAccount>();
		for(ContractorOperator contractorOperator : contractorOperators) {
			OperatorAccount operator = contractorOperator.getOperatorAccount();
			if (operator.getDoContractorsPay() != null && !operator.getDoContractorsPay().equals("No"))
				payingOperators.add(operator);
		}

		
		// Contractors with no paying facilities are free
		InvoiceFee theDefault = new InvoiceFee();
		theDefault.setId(19);
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
						
						if (audit.getAuditType().getId() == AuditType.DESKTOP || audit.getAuditType().getId() == AuditType.OFFICE) {
							billable++;
							break;
						}

						if (audit.getRequiredAuditStatus() == AuditStatus.Active) {
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
	private Integer calculatePriceTier(int billable) {
		@SuppressWarnings("serial")
		Map<Integer, Integer> priceTiers = new TreeMap<Integer, Integer>() {{
			put( 0, 14 );
			put( 1, 3 );
			put( 2, 4 );
			put( 5, 5 );
			put( 9, 6 );
			put( 13, 7 );
			put( 20, 8 );
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
