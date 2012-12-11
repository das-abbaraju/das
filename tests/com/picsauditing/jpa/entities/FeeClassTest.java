package com.picsauditing.jpa.entities;

import com.picsauditing.EntityFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

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

    @Mock
    private ContractorAccount mockContractor;
    @Mock
    private OperatorAccount mockOperatorWithDiscount;
    @Mock
    private OperatorAccount mockOperatorWithLesserDiscount;
    @Mock
    private OperatorAccount mockOperatorWithoutDiscount;
    @Mock
    private InvoiceFee mockFee;
    @Mock
    private Country mockCountry;

    private static final BigDecimal FULL_AMOUNT = BigDecimal.valueOf(10);
    private static final BigDecimal DISCOUNTED_AMOUNT = BigDecimal.valueOf(7);
    private static final BigDecimal LESS_DISCOUNTED_AMOUNT = BigDecimal.valueOf(9);
    private static final BigDecimal DISCOUNT_PERCENTAGE = BigDecimal.valueOf(0.3);
    private static final BigDecimal LESSER_DISCOUNT_PERCENTAGE = BigDecimal.valueOf(0.1);
    private List<OperatorAccount> operatorList;
	
	@Before
	public void setup() {
        MockitoAnnotations.initMocks(this);
		contractor = EntityFactory.makeContractor();
        operatorList = new ArrayList<OperatorAccount>(2);
        when(mockOperatorWithDiscount.isHasDiscount()).thenReturn(true);
        when(mockOperatorWithLesserDiscount.isHasDiscount()).thenReturn(true);
        when(mockOperatorWithoutDiscount.isHasDiscount()).thenReturn(false);
        when(mockOperatorWithDiscount.getDiscountPercent()).thenReturn(DISCOUNT_PERCENTAGE);
        when(mockOperatorWithLesserDiscount.getDiscountPercent()).thenReturn(LESSER_DISCOUNT_PERCENTAGE);
        when(mockContractor.getOperatorAccounts()).thenReturn(operatorList);
        when(mockContractor.getCountry()).thenReturn(mockCountry);
        when(mockCountry.getAmount(mockFee)).thenReturn(FULL_AMOUNT);
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

    @Test
    public void testActivation_getAdjustedFeeAmount_operatorWithDiscount() {
        operatorList.add(mockOperatorWithDiscount);
        assertEquals(DISCOUNTED_AMOUNT, FeeClass.Activation.getAdjustedFeeAmountIfNecessary(mockContractor, mockFee));
    }

    @Test
    public void testActivation_getAdjustedFeeAmount_operatorWithoutDiscount() {
        operatorList.add(mockOperatorWithoutDiscount);
        assertEquals(FULL_AMOUNT, FeeClass.Activation.getAdjustedFeeAmountIfNecessary(mockContractor, mockFee));
    }

    @Test
    public void testActivation_getAdjustedFeeAmount_noOperators() {
        assertEquals(FULL_AMOUNT, FeeClass.Activation.getAdjustedFeeAmountIfNecessary(mockContractor, mockFee));
    }

    @Test
    public void testActivation_getAdjustedFeeAmount_multipleDiscountedOperators() {
        operatorList.add(mockOperatorWithDiscount);
        operatorList.add(mockOperatorWithLesserDiscount);
        assertEquals(LESS_DISCOUNTED_AMOUNT, FeeClass.Activation.getAdjustedFeeAmountIfNecessary(mockContractor, mockFee));
    }

    @Test
    public void testActivation_getAdjustedFeeAmount_variedOperators() {
        operatorList.add(mockOperatorWithDiscount);
        operatorList.add(mockOperatorWithLesserDiscount);
        operatorList.add(mockOperatorWithoutDiscount);
        assertEquals(FULL_AMOUNT, FeeClass.Activation.getAdjustedFeeAmountIfNecessary(mockContractor, mockFee));
    }
}
