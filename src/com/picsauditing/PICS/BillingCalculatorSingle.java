package com.picsauditing.PICS;

import java.math.BigDecimal;
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
		if (payingOperators.size() == 0) return null;
		
		// If only one facility is selected and it's a "multiple" 
		// like Empire or BP Pipelines, then it's free too
		if (payingOperators.size()==1 && payingOperators.get(0).getDoContractorsPay().equals("Multiple"))
			return null;

		
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

		BigDecimal price = calculatePriceTier(billable);
		
		InvoiceFee fee = new InvoiceFee();
		fee.setAmount(price.intValue());
		fee.setVisible(true);
		fee.setFee("fee");
		
		return fee;
		
	}

	private BigDecimal calculatePriceTier(int billable) {
		@SuppressWarnings("serial")
		Map<Integer, BigDecimal> priceTiers = new TreeMap<Integer, BigDecimal>() {{
			put( 0, new BigDecimal( 99 ) );
			put( 1, new BigDecimal( 399 ) );
			put( 2, new BigDecimal( 699 ) );
			put( 5, new BigDecimal( 999 ) );
			put( 9, new BigDecimal( 1299 ) );
			put( 13, new BigDecimal( 1699 ) );
			put( 14, new BigDecimal( 1999 ) );
		}};

		BigDecimal last = null;
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
