package com.picsauditing.PICS;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;

public class BillingCalculatorSingle {
	private Set<OperatorAccount> payingOperators;
	
	public BillingCalculatorSingle(List<OperatorAccount> operators) {
		payingOperators = new HashSet<OperatorAccount>();
		for(OperatorAccount operator : operators) {
			if (operator.getDoContractorsPay() != null && !operator.getDoContractorsPay().equals("No"))
				payingOperators.add(operator);
		}
		/*
		int facilityCount = countFacilities();
		
		// Contractors with no paying facilities are free
		if (facilityCount == 0) return 0;
		
		// If only one facility is selected and it's a "multiple" 
		// like Empire or BP Pipelines, then it's free too
		if (facilityCount==1 && cBean.getFacilities().get(0).doContractorsPay.equals("Multiple"))
			return 0;
		
		// if it doesn't require an audit then it's only $99
		cBean.isAudited(requiresAudit());
		if (cBean.isAudited()) {
			cBean.newBillingAmount = Integer.toString(priceNoAudit);
			return priceNoAudit;
		}
		// All others use the pricing matrix
		Integer newPrice = calculatePriceByFacilityCount(facilityCount);
		cBean.newBillingAmount = newPrice.toString();
		return newPrice;
*/
	}

	public void calculateAnnualFee(ContractorAccount contractor) {
		
	}

}
