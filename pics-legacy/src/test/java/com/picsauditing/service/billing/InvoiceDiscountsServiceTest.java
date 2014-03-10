package com.picsauditing.service.billing;

import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.contractor.TopLevelOperatorFinder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.MockitoAnnotations;
import org.mockito.MockitoAnnotations.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.util.*;

import static com.picsauditing.service.billing.InvoiceDiscountsService.SUNCOR;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class InvoiceDiscountsServiceTest {
    @Mock
    private InvoiceFeeDAO invoiceFeeDAO;
    @Mock
    private TopLevelOperatorFinder topLevelOperatorFinder;

    public static final int TEST_MIN_FACILITIES = 5;
    public static final OperatorAccount NOT_SUNCOR = OperatorAccount.builder().id(5).build();

    private InvoiceDiscountsService invoiceDiscountsService;
    private ContractorAccount contractor;

    @Test
    public void testApplyDiscounts_WorksForOnlySuncor() throws Exception {
        List<InvoiceItem> invoiceItems = buildInvoiceItems();


        when(topLevelOperatorFinder.findAllTopLevelOperators(contractor)).thenReturn(Arrays.asList(SUNCOR));

        List<InvoiceItem> discounts = invoiceDiscountsService.applyDiscounts(contractor, invoiceItems);

        assertEquals(3, discounts.size());

        Map<FeeClass, BigDecimal> expectedAmountsByFeeClass = new HashMap<>();
        expectedAmountsByFeeClass.put(FeeClass.DocuGUARD, new BigDecimal(-100));
        expectedAmountsByFeeClass.put(FeeClass.InsureGUARD, new BigDecimal(-300));
        expectedAmountsByFeeClass.put(FeeClass.AuditGUARD, new BigDecimal(-800));

        verifyDiscountAmounts(expectedAmountsByFeeClass, discounts);
    }

    @Test
    public void testApplyDiscounts_DoesNotWorkForSuncor() throws Exception {
        List<InvoiceItem> invoiceItems = buildInvoiceItems();

        when(topLevelOperatorFinder.findAllTopLevelOperators(contractor)).thenReturn(Arrays.asList(NOT_SUNCOR));

        List<InvoiceItem> discounts = invoiceDiscountsService.applyDiscounts(contractor, invoiceItems);

        assertEquals(0, discounts.size());
    }

    @Test
    public void testApplyDiscounts_WorksForSuncorAndAnotherCompany() throws Exception {
        List<InvoiceItem> invoiceItems = buildInvoiceItems();


        when(topLevelOperatorFinder.findAllTopLevelOperators(contractor)).thenReturn(Arrays.asList(SUNCOR, NOT_SUNCOR));

        List<InvoiceItem> discounts = invoiceDiscountsService.applyDiscounts(contractor, invoiceItems);

        assertEquals(0, discounts.size());
    }

    @Test
    public void testApplyProratedDiscounts_AllFeeClasses_OneSiteToTwoSites() throws Exception {
        runSuncorUpgradeTest(1, 2, 272, 0, -60, -75);
    }

    @Test
    public void testApplyProratedDiscounts_AllFeeClasses_FiveSitesToNineSites() throws Exception {
        runSuncorUpgradeTest(5, 9, 120, 0, 16, 99);
    }

    @Test
    public void testApplyProratedDiscounts_AllFeeClasses_ThreeSitesToNineSites() throws Exception {
        runSuncorUpgradeTest(3, 9, 200, 0, -66, -164);
    }

    @Test
    public void testApplyProratedDiscounts_WorksForSuncorAndAnotherCompany() {
        when(topLevelOperatorFinder.findAllTopLevelOperators(contractor)).thenReturn(Arrays.asList(SUNCOR, NOT_SUNCOR));

        List<ContractorFee> upgradeFees = buildBasicContractorUpgradeFees(2, 4);

        List<InvoiceItem> discounts = invoiceDiscountsService.applyProratedDiscounts(contractor, upgradeFees, 43);

        assertEquals(0, discounts.size());
    }

    @Test
    public void testApplyProratedDiscounts_DoesNotWorkForSuncor() {
        when(topLevelOperatorFinder.findAllTopLevelOperators(contractor)).thenReturn(Arrays.asList(NOT_SUNCOR));

        List<ContractorFee> upgradeFees = buildBasicContractorUpgradeFees(2, 4);

        List<InvoiceItem> discounts = invoiceDiscountsService.applyProratedDiscounts(contractor, upgradeFees, 43);

        assertEquals(0, discounts.size());
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        invoiceDiscountsService = new InvoiceDiscountsService();
        Whitebox.setInternalState(invoiceDiscountsService, "topLevelOperatorFinder", topLevelOperatorFinder);
        Whitebox.setInternalState(invoiceDiscountsService, "invoiceFeeDAO", invoiceFeeDAO);

        doAnswer(INVOICE_FEE_ANSWER).when(invoiceFeeDAO).findDiscountByNumberOfOperatorsAndClass(Matchers.argThat(any(FeeClass.class)), anyInt(), anyInt());
        contractor = new ContractorAccount();
    }


    private void runSuncorUpgradeTest(int currentNumberOfClientSites, int newNumberOfClientSites, int daysUntilExpiration,
                                      int expectedDocuGuardDiscount, int expectedInsureGuardDiscount, int expectedAuditGuardDiscount) {

        int currentMinFacilities = findMinFacilitiesBasedOnNumberOfClientSites(currentNumberOfClientSites);

        int newMinFacilities = findMinFacilitiesBasedOnNumberOfClientSites(newNumberOfClientSites);

        List<ContractorFee> upgradeFees = buildBasicContractorUpgradeFees(currentMinFacilities, newMinFacilities);

        when(topLevelOperatorFinder.findAllTopLevelOperators(contractor)).thenReturn(Arrays.asList(SUNCOR));

        List<InvoiceItem> discounts = invoiceDiscountsService.applyProratedDiscounts(contractor, upgradeFees, daysUntilExpiration);

        Map<FeeClass, BigDecimal> expectedAmountsByFeeClass = new HashMap<>();
        expectedAmountsByFeeClass.put(FeeClass.DocuGUARD, new BigDecimal(expectedDocuGuardDiscount));
        expectedAmountsByFeeClass.put(FeeClass.InsureGUARD, new BigDecimal(expectedInsureGuardDiscount));
        expectedAmountsByFeeClass.put(FeeClass.AuditGUARD, new BigDecimal(expectedAuditGuardDiscount));

        assertEquals(3, discounts.size());

        verifyDiscountAmounts(expectedAmountsByFeeClass, discounts);
    }

    private void verifyDiscountAmounts(Map<FeeClass, BigDecimal> expectedAmountsByFeeClass, List<InvoiceItem> discounts) {
        for (InvoiceItem discount : discounts) {
            assertEquals(expectedAmountsByFeeClass.get(discount.getInvoiceFee().getFeeClass()), discount.getAmount());
        }
    }

    private List<ContractorFee> buildBasicContractorUpgradeFees(int currentLevelMinFacilities, int newLevelMinFacilities) {
        List<ContractorFee> upgradeFees = new ArrayList<>();

        upgradeFees.add(ContractorFee.builder()
                .feeClass(FeeClass.DocuGUARD)
                .currentLevel(InvoiceFee.builder()
                        .feeClass(FeeClass.DocuGUARD)
                        .minFacilities(currentLevelMinFacilities)
                        .build())
                .newLevel(InvoiceFee.builder()
                        .feeClass(FeeClass.DocuGUARD)
                        .minFacilities(newLevelMinFacilities)
                        .build())
                .build());

        upgradeFees.add(ContractorFee.builder()
                .feeClass(FeeClass.InsureGUARD)
                .currentLevel(InvoiceFee.builder()
                        .feeClass(FeeClass.InsureGUARD)
                        .minFacilities(currentLevelMinFacilities)
                        .build())
                .newLevel(InvoiceFee.builder()
                        .feeClass(FeeClass.InsureGUARD)
                        .minFacilities(newLevelMinFacilities)
                        .build())
                .build());

        upgradeFees.add(ContractorFee.builder()
                .feeClass(FeeClass.AuditGUARD)
                .currentLevel(InvoiceFee.builder()
                        .feeClass(FeeClass.AuditGUARD)
                        .minFacilities(currentLevelMinFacilities)
                        .build())
                .newLevel(InvoiceFee.builder()
                        .feeClass(FeeClass.AuditGUARD)
                        .minFacilities(newLevelMinFacilities)
                        .build())
                .build());
        return upgradeFees;
    }

    private List<InvoiceItem> buildInvoiceItems() {
        return Arrays.asList(
                InvoiceItem.builder()
                        .invoiceFee(InvoiceFee.builder()
                                .feeClass(FeeClass.DocuGUARD)
                                .minFacilities(TEST_MIN_FACILITIES)
                                .build())
                        .build(),
                InvoiceItem.builder()
                        .invoiceFee(InvoiceFee.builder()
                                .feeClass(FeeClass.InsureGUARD)
                                .minFacilities(TEST_MIN_FACILITIES)
                                .build())
                        .build(),
                InvoiceItem.builder()
                        .invoiceFee(InvoiceFee.builder()
                                .feeClass(FeeClass.AuditGUARD)
                                .minFacilities(TEST_MIN_FACILITIES)
                                .build())
                        .build(),
                InvoiceItem.builder()
                        .invoiceFee(InvoiceFee.builder()
                                .feeClass(FeeClass.Activation)
                                .minFacilities(TEST_MIN_FACILITIES)
                                .build())
                        .build()
        );
    }

    private int findMinFacilitiesBasedOnNumberOfClientSites(int currentNumberOfClientSites) {
        int minFacilities = 0;

        if (currentNumberOfClientSites >= FIRST_TIER_MIN_FACILITIES) {
            if (currentNumberOfClientSites >= SECOND_TIER_MIN_FACILITIES) {
                if (currentNumberOfClientSites >= THIRD_TIER_MIN_FACILITIES) {
                    if (currentNumberOfClientSites >= FOURTH_TIER_MIN_FACILITIES) {
                        if (currentNumberOfClientSites >= FIFTH_TIER_MIN_FACILITIES) {
                            if (currentNumberOfClientSites >= SIXTH_TIER_MIN_FACILITIES) {
                                if (currentNumberOfClientSites >= SEVENTH_TIER_MIN_FACILITIES) {
                                    minFacilities = SEVENTH_TIER_MIN_FACILITIES;
                                } else {
                                    minFacilities = SIXTH_TIER_MIN_FACILITIES;
                                }
                            } else {
                                minFacilities = FIFTH_TIER_MIN_FACILITIES;
                            }
                        } else {
                            minFacilities = FOURTH_TIER_MIN_FACILITIES;
                        }
                    } else {
                        minFacilities = THIRD_TIER_MIN_FACILITIES;
                    }
                } else {
                    minFacilities = SECOND_TIER_MIN_FACILITIES;
                }
            } else {
                minFacilities = FIRST_TIER_MIN_FACILITIES;
            }
        }
        return minFacilities;
    }

    private static final List<Suncor2013Discount> SUNCOR_2013_DISCOUNT_LIST = new ArrayList<>();
    public static final Answer<InvoiceFee> INVOICE_FEE_ANSWER = new Answer<InvoiceFee>() {
        @Override
        public InvoiceFee answer(InvocationOnMock invocation) throws Throwable {
            FeeClass feeClass = (FeeClass) invocation.getArguments()[0];
            int minFacilities = (Integer) invocation.getArguments()[1];
            int operatorId = (Integer) invocation.getArguments()[2];

            InvoiceFee invoiceFee = null;

            BigDecimal discountAmount = new BigDecimal(0);
            for (Suncor2013Discount suncor2013Discount : SUNCOR_2013_DISCOUNT_LIST) {
                if (suncor2013Discount.getFeeClass() == feeClass &&
                        suncor2013Discount.getMinFacilities() == minFacilities) {
                    discountAmount = suncor2013Discount.getAmount();
                    break;
                }
            }

            if (operatorId == OperatorAccount.SUNCOR && feeClass != FeeClass.Activation) {
                invoiceFee = InvoiceFee.builder().feeClass(feeClass).minFacilities(minFacilities).amount(discountAmount).build();
            }

            return invoiceFee;
        }
    };

    private static final int FIRST_TIER_MIN_FACILITIES = 1;
    private static final int SECOND_TIER_MIN_FACILITIES = 2;
    private static final int THIRD_TIER_MIN_FACILITIES = 5;
    private static final int FOURTH_TIER_MIN_FACILITIES = 9;
    private static final int FIFTH_TIER_MIN_FACILITIES = 13;
    private static final int SIXTH_TIER_MIN_FACILITIES = 20;
    private static final int SEVENTH_TIER_MIN_FACILITIES = 50;

    static {
        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.DocuGUARD, FIRST_TIER_MIN_FACILITIES, new BigDecimal(100)));
        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.DocuGUARD, SECOND_TIER_MIN_FACILITIES, new BigDecimal(100)));
        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.DocuGUARD, THIRD_TIER_MIN_FACILITIES, new BigDecimal(100)));
        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.DocuGUARD, FOURTH_TIER_MIN_FACILITIES, new BigDecimal(100)));
        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.DocuGUARD, FIFTH_TIER_MIN_FACILITIES, new BigDecimal(100)));
        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.DocuGUARD, SIXTH_TIER_MIN_FACILITIES, new BigDecimal(100)));

        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.InsureGUARD, FIRST_TIER_MIN_FACILITIES, new BigDecimal(50)));
        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.InsureGUARD, SECOND_TIER_MIN_FACILITIES, new BigDecimal(130)));
        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.InsureGUARD, THIRD_TIER_MIN_FACILITIES, new BigDecimal(300)));
        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.InsureGUARD, FOURTH_TIER_MIN_FACILITIES, new BigDecimal(250)));
        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.InsureGUARD, FIFTH_TIER_MIN_FACILITIES, new BigDecimal(200)));
        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.InsureGUARD, SIXTH_TIER_MIN_FACILITIES, new BigDecimal(0)));

        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.AuditGUARD, FIRST_TIER_MIN_FACILITIES, new BigDecimal(100)));
        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.AuditGUARD, SECOND_TIER_MIN_FACILITIES, new BigDecimal(200)));
        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.AuditGUARD, THIRD_TIER_MIN_FACILITIES, new BigDecimal(800)));
        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.AuditGUARD, FOURTH_TIER_MIN_FACILITIES, new BigDecimal(500)));
        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.AuditGUARD, FIFTH_TIER_MIN_FACILITIES, new BigDecimal(0)));
        SUNCOR_2013_DISCOUNT_LIST.add(new Suncor2013Discount(FeeClass.AuditGUARD, SIXTH_TIER_MIN_FACILITIES, new BigDecimal(0)));
    }

    private static class Suncor2013Discount {
        private final FeeClass feeClass;
        private final int minFacilities;
        private final BigDecimal amount;

        public Suncor2013Discount(FeeClass feeClass, int minFacilities, BigDecimal amount) {
            this.feeClass = feeClass;
            this.minFacilities = minFacilities;
            this.amount = amount;
        }

        public FeeClass getFeeClass() {
            return feeClass;
        }

        public int getMinFacilities() {
            return minFacilities;
        }

        public BigDecimal getAmount() {
            return amount;
        }
    }

}
