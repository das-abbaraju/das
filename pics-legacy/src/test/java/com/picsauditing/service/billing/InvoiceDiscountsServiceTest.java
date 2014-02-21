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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

import static org.hamcrest.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static com.picsauditing.service.billing.InvoiceDiscountsService.SUNCOR;

public class InvoiceDiscountsServiceTest {
    @Mock
    private InvoiceFeeDAO invoiceFeeDAO;
    @Mock
    private TopLevelOperatorFinder topLevelOperatorFinder;

    public static final int TEST_MIN_FACILITIES = 5;
    public static final BigDecimal TEST_AMOUNT = new BigDecimal(1);
    public static final OperatorAccount NOT_SUNCOR = OperatorAccount.builder().id(5).build();
    public static final BigDecimal ZERO = new BigDecimal(0);
    
    private InvoiceDiscountsService invoiceDiscountsService;
    private ContractorAccount contractor;

    @Test
    public void testApplyDiscounts_WorksForOnlySuncor() throws Exception {
        contractor = new ContractorAccount();
        List<InvoiceItem> invoiceItems = buildInvoiceItems();


        when(topLevelOperatorFinder.findAllTopLevelOperators(contractor)).thenReturn(Arrays.asList(SUNCOR));

        List<InvoiceItem> discounts = invoiceDiscountsService.applyDiscounts(contractor, invoiceItems);

        assertEquals(3, discounts.size());

        for (InvoiceItem discount: discounts) {
            assertEquals(TEST_AMOUNT.negate(), discount.getAmount());
            assertEquals(ZERO, discount.getOriginalAmount());
        }
    }

    @Test
    public void testApplyDiscounts_DoesNotWorkForSuncor() throws Exception {
        contractor = new ContractorAccount();
        List<InvoiceItem> invoiceItems = buildInvoiceItems();


        when(topLevelOperatorFinder.findAllTopLevelOperators(contractor)).thenReturn(Arrays.asList(NOT_SUNCOR));

        List<InvoiceItem> discounts = invoiceDiscountsService.applyDiscounts(contractor, invoiceItems);

        assertEquals(0, discounts.size());
    }

    @Test
    public void testApplyDiscounts_WorksForSuncorAndAnotherCompany() throws Exception {
        contractor = new ContractorAccount();
        List<InvoiceItem> invoiceItems = buildInvoiceItems();


        when(topLevelOperatorFinder.findAllTopLevelOperators(contractor)).thenReturn(Arrays.asList(SUNCOR, NOT_SUNCOR));

        List<InvoiceItem> discounts = invoiceDiscountsService.applyDiscounts(contractor, invoiceItems);

        assertEquals(0, discounts.size());
    }

    @Test
    public void testApplyProratedDiscounts_AllFeeClassesWithADiscountThatProRatedToZero() throws Exception {
        contractor = new ContractorAccount();
        contractor.setLogoForSingleOperatorContractor(SUNCOR);
        List<ContractorFee> upgradeFees = buildBasicContractorFees();

        when(topLevelOperatorFinder.findAllTopLevelOperators(contractor)).thenReturn(Arrays.asList(SUNCOR, NOT_SUNCOR));

        int daysUntilExpiration = 272;
        List<InvoiceItem> discounts = invoiceDiscountsService.applyProratedDiscounts(contractor, upgradeFees, daysUntilExpiration);

        assertEquals(2, discounts.size());
        assertEquals(-60, discounts.get(0).getAmount().intValue());
        assertEquals(-75, discounts.get(1).getAmount().intValue());
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        invoiceDiscountsService = new InvoiceDiscountsService();
        Whitebox.setInternalState(invoiceDiscountsService, "topLevelOperatorFinder", topLevelOperatorFinder);
        Whitebox.setInternalState(invoiceDiscountsService, "invoiceFeeDAO", invoiceFeeDAO);

        doAnswer(new Answer<InvoiceFee>(){
            @Override
            public InvoiceFee answer(InvocationOnMock invocation) throws Throwable {
                FeeClass feeClass = (FeeClass) invocation.getArguments()[0];
                int minFacilities = (Integer) invocation.getArguments()[1];
                int operatorId = (Integer) invocation.getArguments()[2];

                InvoiceFee invoiceFee = null;

                if (operatorId == OperatorAccount.SUNCOR && feeClass != FeeClass.Activation) {
                    invoiceFee = InvoiceFee.builder().feeClass(feeClass).minFacilities(minFacilities).amount(TEST_AMOUNT).build();
                }

                return invoiceFee;
            }
        }).when(invoiceFeeDAO).findDiscountByNumberOfOperatorsAndClass(Matchers.argThat(any(FeeClass.class)), anyInt(), anyInt());
    }

    private List<ContractorFee> buildBasicContractorFees() {
        List<ContractorFee> upgradeFees = new ArrayList<>();

        addUpgradeFee(upgradeFees,
                InvoiceFee.builder()
                        .feeClass(FeeClass.DocuGUARD)
                        .minFacilities(1)
                        .amount(new BigDecimal(100))
                        .build(),
                InvoiceFee.builder()
                        .feeClass(FeeClass.DocuGUARD)
                        .amount(new BigDecimal((100)))
                        .minFacilities(2)
                        .build()
        );

        addUpgradeFee(upgradeFees,
                InvoiceFee.builder()
                        .feeClass(FeeClass.InsureGUARD)
                        .minFacilities(1)
                        .amount(new BigDecimal(50))
                        .build(),
                InvoiceFee.builder()
                        .feeClass(FeeClass.InsureGUARD)
                        .amount(new BigDecimal((130)))
                        .minFacilities(2)
                        .build()
        );

        addUpgradeFee(upgradeFees,
                InvoiceFee.builder()
                        .feeClass(FeeClass.AuditGUARD)
                        .minFacilities(1)
                        .amount(new BigDecimal(100))
                        .build(),
                InvoiceFee.builder()
                        .feeClass(FeeClass.AuditGUARD)
                        .amount(new BigDecimal((200)))
                        .minFacilities(2)
                        .build()
        );
        return upgradeFees;
    }

    private void addUpgradeFee(List<ContractorFee> upgradeFees, InvoiceFee currentLevel, InvoiceFee newLevel) {
        ContractorFee contractorFee = ContractorFee.builder()
                .currentLevel(currentLevel)
                .newLevel(newLevel)
                .build();

        when(invoiceFeeDAO.findDiscountByNumberOfOperatorsAndClass(eq(currentLevel.getFeeClass()),
                eq(currentLevel.getMinFacilities()), anyInt())).thenReturn(currentLevel);

        when(invoiceFeeDAO.findDiscountByNumberOfOperatorsAndClass(eq(newLevel.getFeeClass()),
                eq(newLevel.getMinFacilities()), anyInt())).thenReturn(newLevel);

        upgradeFees.add(contractorFee);
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
}
