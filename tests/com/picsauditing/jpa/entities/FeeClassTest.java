package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Before;
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
	private FeeClass feeClass;
	private ContractorAccount contractor;
	
	@Before
	public void setup() {
		contractor = EntityFactory.makeContractor();
	}

	@Test
	public void testIsInsuranceExcludedFor_ListOnlyContractor() {
		feeClass = FeeClass.InsureGUARD;

		contractor.setAccountLevel(AccountLevel.ListOnly);
		setupContractorInsureguardFees(contractor);
		EntityFactory.addContractorOperator(contractor, new OperatorAccount());

		assertTrue(feeClass.isExcludedFor(contractor, createTier2InsuranceFee(), new HashSet<OperatorAccount>(
				contractor.getOperatorAccounts())));
	}

	@Test
	public void testIsInsuranceExcludedFor_BidOnlyContractor() {
		feeClass = FeeClass.InsureGUARD;

		contractor.setAccountLevel(AccountLevel.BidOnly);
		setupContractorInsureguardFees(contractor);
		EntityFactory.addContractorOperator(contractor, new OperatorAccount());

		assertTrue(feeClass.isExcludedFor(contractor, createTier2InsuranceFee(), new HashSet<OperatorAccount>(
				contractor.getOperatorAccounts())));
	}
	
	@Test
	public void testIsInsuranceExcludedFor_OneSiteSuncorOnlySoleProprietorContractor() {
		feeClass = FeeClass.InsureGUARD;

		contractor.setSoleProprietor(true);
		setupContractorInsureguardFees(contractor);
		OperatorAccount suncorOperator = EntityFactory.makeSuncorOperator();
		EntityFactory.addContractorOperator(contractor, suncorOperator);

		assertTrue(feeClass.isExcludedFor(contractor, createTier2InsuranceFee(), new HashSet<OperatorAccount>(
				contractor.getOperatorAccounts())));
	}


	private InvoiceFee createTier1InsuranceFee() {
		InvoiceFee insuranceFee = new InvoiceFee();
		insuranceFee.setFeeClass(FeeClass.InsureGUARD);
		insuranceFee.setMinFacilities(1);
		insuranceFee.setMaxFacilities(1);

		return insuranceFee;
	}

	private InvoiceFee createTier2InsuranceFee() {
		InvoiceFee insuranceFee = new InvoiceFee();
		insuranceFee.setFeeClass(FeeClass.InsureGUARD);
		insuranceFee.setMinFacilities(2);
		insuranceFee.setMaxFacilities(4);

		return insuranceFee;
	}

	private void setupContractorInsureguardFees(ContractorAccount contractor) {
		ContractorFee contractorFee = new ContractorFee();
		contractorFee.setFeeClass(FeeClass.InsureGUARD);
		contractorFee.setCurrentLevel(createTier1InsuranceFee());
		contractorFee.setNewLevel(createTier2InsuranceFee());
		
		Map<FeeClass, ContractorFee> fees = new HashMap<FeeClass, ContractorFee>();
		fees.put(FeeClass.InsureGUARD, contractorFee);
		contractor.setFees(fees);
	}
}
