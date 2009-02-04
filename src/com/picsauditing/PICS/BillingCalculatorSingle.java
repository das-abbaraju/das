package com.picsauditing.PICS;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.OperatorAccount;

public class BillingCalculatorSingle {
	
	private InvoiceFeeDAO feeDao = null;
	
	public BillingCalculatorSingle( InvoiceFeeDAO feeDao ) {
		this.feeDao = feeDao;
	}
	
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

		String feeName = calculatePriceTier(billable);
		
		InvoiceFee fee = feeDao.findByName(feeName);
		
		return fee;
		
	}

	private String calculatePriceTier(int billable) {
		@SuppressWarnings("serial")
		Map<Integer, String> priceTiers = new TreeMap<Integer, String>() {{
			put( 0, "Activation" );
			put( 1, "Facilities_1" );
			put( 2, "Facilities_2to4" );
			put( 5, "Facilities_5to8" );
			put( 9, "Facilities_9to12" );
			put( 13, "Facilities_13to19" );
			put( 20, "Facilities_20plus" );
		}};

		String last = null;
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
