package com.picsauditing.jpa.entities;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.FeeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
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
    private int ID_FOR_MOCKS;

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
    @Mock
    private InvoiceFeeCountry mockInvoiceFeeCountry;

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

        List<InvoiceFeeCountry> amountOverrides = new ArrayList<InvoiceFeeCountry>();
        when(mockInvoiceFeeCountry.getInvoiceFee()).thenReturn(mockFee);
        when(mockInvoiceFeeCountry.getAmount()).thenReturn(new BigDecimal(10));
        amountOverrides.add(mockInvoiceFeeCountry);

        when(mockCountry.getAmountOverrides()).thenReturn(amountOverrides);
        ID_FOR_MOCKS = 100;
	}

    /*
        Tests for isAllExclusionsApplicable are not complete as I just read that we will be removing
        the exclusion logic (any time after 1/1/2013 -
     */
    @Test
    public void testIsAllExclusionsApplicable_NotUpgradeNotExclusion() throws Exception {
        ContractorFee currentLevel = commonSetupCurrentContractorFee(FeeClass.InsureGUARD);
        InvoiceFee newLevel = commonSetupNewInvoiceFee(FeeClass.InsureGUARD);

        OperatorAccount topOperatorAccount = operatorAccount(unusedIdNotInExclusions());

        Set<OperatorAccount> operators = commonSetupOperatorAccountsWithTopAccount(topOperatorAccount);

        when(currentLevel.willBeUpgradedBy(newLevel)).thenReturn(false);

        boolean result = FeeClass.InsureGUARD.isAllExclusionsApplicable(mockContractor, newLevel, operators);

        assertFalse(result);
    }

    @Test
    public void testIsAllExclusionsApplicable_IsUpgradeNotExclusion() throws Exception {
        ContractorFee currentLevel = commonSetupCurrentContractorFee(FeeClass.InsureGUARD);
        InvoiceFee newLevel = commonSetupNewInvoiceFee(FeeClass.InsureGUARD);

        OperatorAccount topOperatorAccount = operatorAccount(unusedIdNotInExclusions());

        Set<OperatorAccount> operators = commonSetupOperatorAccountsWithTopAccount(topOperatorAccount);

        when(currentLevel.willBeUpgradedBy(newLevel)).thenReturn(true);

        boolean result = FeeClass.InsureGUARD.isAllExclusionsApplicable(mockContractor, newLevel, operators);

        assertFalse(result);
    }

    private Set<OperatorAccount> commonSetupOperatorAccountsWithTopAccount(OperatorAccount topOperatorAccount) {
        OperatorAccount operator = mock(OperatorAccount.class);
        Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
        operators.add(operator);
        when(operator.getTopAccount()).thenReturn(topOperatorAccount);
        return operators;
    }

    private InvoiceFee commonSetupNewInvoiceFee(FeeClass feeClass) {
        InvoiceFee newLevel = mock(InvoiceFee.class);
        when(newLevel.getFeeClass()).thenReturn(feeClass);
        return newLevel;
    }

    private ContractorFee commonSetupCurrentContractorFee(FeeClass feeClass) {
        Map<FeeClass, ContractorFee> fees = new HashMap<FeeClass, ContractorFee>();
        ContractorFee currentLevel = mock(ContractorFee.class);
        fees.put(feeClass, currentLevel);
        when(mockContractor.getFees()).thenReturn(fees);
        return currentLevel;
    }

    private OperatorAccount operatorAccount(int id) {
        OperatorAccount operatorAccount = mock(OperatorAccount.class);
        when(operatorAccount.getId()).thenReturn(id);
        return  operatorAccount;
    }

    private int unusedIdNotInExclusions() {
        Map<Integer, Date> exclusions = FeeClass.InsureGUARD.getExclusions();
        while (exclusions.containsKey(ID_FOR_MOCKS)) {
            ID_FOR_MOCKS++;
        }
        return ID_FOR_MOCKS;
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
