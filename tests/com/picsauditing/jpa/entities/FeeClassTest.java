package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Test;

import com.picsauditing.EntityFactory;

/**
 * The logic in FeeClass needs to be pulled out into a service to make it
 * properly testable.
 * 
 * @author TJB
 * 
 */
public class FeeClassTest {
	FeeClass feeClass;

	@Test
	public void testIsInsuranceExcludedFor_ListOnlyContractor() {
		feeClass = FeeClass.InsureGUARD;

		InvoiceFee newInsuranceLevel = createInsuranceFee();

		ContractorAccount listOnlyContractor = EntityFactory.makeContractor();
		listOnlyContractor.setAccountLevel(AccountLevel.ListOnly);

		EntityFactory.addContractorOperator(listOnlyContractor, new OperatorAccount());

		assertTrue(feeClass.isExcludedFor(listOnlyContractor, newInsuranceLevel, new HashSet<OperatorAccount>(
				listOnlyContractor.getOperatorAccounts())));
	}

	@Test
	public void testIsInsuranceExcludedFor_BidOnlyContractor() {
		feeClass = FeeClass.InsureGUARD;

		InvoiceFee newInsuranceLevel = createInsuranceFee();

		ContractorAccount listOnlyContractor = EntityFactory.makeContractor();
		listOnlyContractor.setAccountLevel(AccountLevel.BidOnly);

		EntityFactory.addContractorOperator(listOnlyContractor, new OperatorAccount());

		assertTrue(feeClass.isExcludedFor(listOnlyContractor, newInsuranceLevel, new HashSet<OperatorAccount>(
				listOnlyContractor.getOperatorAccounts())));

	}

	private InvoiceFee createInsuranceFee() {
		InvoiceFee insuranceFee = new InvoiceFee();
		insuranceFee.setFeeClass(FeeClass.InsureGUARD);

		return insuranceFee;
	}

}
