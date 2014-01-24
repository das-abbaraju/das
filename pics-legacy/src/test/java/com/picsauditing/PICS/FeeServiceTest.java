package com.picsauditing.PICS;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.employeeguard.daos.AccountEmployeeGuardDAO;
import com.picsauditing.jpa.entities.*;
import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class FeeServiceTest extends PicsTranslationTest {
	private FeeService feeService;

    private static final BigDecimal FULL_AMOUNT = BigDecimal.valueOf(10);
    private static final BigDecimal DISCOUNTED_AMOUNT = BigDecimal.valueOf(7);
    private static final BigDecimal LESS_DISCOUNTED_AMOUNT = BigDecimal.valueOf(9);
    private static final BigDecimal DISCOUNT_PERCENTAGE = BigDecimal.valueOf(0.3);
    private static final BigDecimal LESSER_DISCOUNT_PERCENTAGE = BigDecimal.valueOf(0.1);

    private List<ContractorOperator> operatorList;
    private ContractorAccount contractor;

    @Mock
    private ContractorAccount contractorAccount;
    @Mock
    private AccountEmployeeGuardDAO accountEmployeeGuardDAO;
    @Mock
	private InvoiceFeeDAO feeDAO;
    @Mock
    private Invoice invoice;
    @Mock
    private InvoiceItem invoiceItem1;
    @Mock
    private InvoiceItem invoiceItem2;
    @Mock
    private InvoiceItem invoiceItem3;
    @Mock
    private InvoiceFee invoiceFee1;
    @Mock
    private InvoiceFee invoiceFee2;
    @Mock
    private InvoiceFee invoiceFee3;
    @Mock
    private Country country;
    @Mock
    private PaymentAppliedToInvoice paymentAppliedToInvoice;
    @Mock
    private ContractorOperator contractorOperator1;
    @Mock
    private ContractorOperator contractorOperator2;
    @Mock
    private ContractorOperator contractorOperator3;
    @Mock
    private OperatorAccount operatorAccount;
    @Mock
    private ContractorTrade contractorTrade;
    @Mock
    private Trade trade;
    @Mock
    private AuditTypeRuleCache ruleCache;
    @Mock
    private AuditTypeRule auditTypeRule;
    @Mock
    private AuditType auditType;
    @Mock
    private ContractorAudit contractorAudit;
    @Mock
    private ContractorTag contractorTag;
    @Mock
    private OperatorTag operatorTag;

    @Mock
    private OperatorAccount mockOperatorWithDiscount;
    @Mock
    private OperatorAccount mockOperatorWithLesserDiscount;
    @Mock
    private OperatorAccount mockOperatorWithoutDiscount;
    @Mock
    OperatorAccount mockOA1;
    @Mock
    OperatorAccount mockOA2;
    @Mock
    private BillingService billingService;

    Set<OperatorAccount> OAMocksSet = new HashSet<>();

    @Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
        operatorList = new ArrayList<ContractorOperator>(2);

        when(mockOperatorWithDiscount.isHasDiscount()).thenReturn(true);
        when(mockOperatorWithLesserDiscount.isHasDiscount()).thenReturn(true);
        when(mockOperatorWithoutDiscount.isHasDiscount()).thenReturn(false);
        when(mockOperatorWithDiscount.getDiscountPercent()).thenReturn(DISCOUNT_PERCENTAGE);
        when(mockOperatorWithLesserDiscount.getDiscountPercent()).thenReturn(LESSER_DISCOUNT_PERCENTAGE);

        feeService = new FeeService();
        FeeService.ruleCache = ruleCache;
        Whitebox.setInternalState(feeService, "feeDAO", feeDAO);
        Whitebox.setInternalState(feeService, "accountEmployeeGuardDAO", accountEmployeeGuardDAO);
        Whitebox.setInternalState(feeService, "billingService", billingService);

        setUpContractor();

        when(billingService.billingStatus(contractor)).thenReturn(BillingStatus.Upgrade);

		assert (OAMocksSet.isEmpty());
	}

    private void setUpContractor() {
        contractor = new ContractorAccount();
        contractor.setOperators(operatorList);
        contractor.setCountry(country);
        ArrayList<ContractorTag> tags = new ArrayList<ContractorTag>();
        tags.add(contractorTag);
        contractor.setOperatorTags(tags);
        when(contractorTag.getTag()).thenReturn(operatorTag);
    }

	@After
	public void clean() {
		OAMocksSet.clear();
	}

	@Test
	public void InsureGuardQualificationTest_yes() {
		OAMocksSet.add(mockOA1);
		OAMocksSet.add(mockOA2);
		when(mockOA1.getId()).thenReturn(333);
		when(mockOA2.getId()).thenReturn(555);
		assertTrue(feeService.qualifiesForInsureGuard(OAMocksSet));
	}

	@Test
	public void InsureGuardQualificationTest_no() {
		OAMocksSet.add(mockOA1);
		when(mockOA1.getId()).thenReturn(OperatorAccount.CINTAS_CANADA);
		assertFalse(feeService.qualifiesForInsureGuard(OAMocksSet));
	}

	@Test
	public void InsureGuardQualificationTest_no2() {
		OAMocksSet.add(mockOA1);
		OAMocksSet.add(mockOA2);
		when(mockOA1.getId()).thenReturn(333);
		when(mockOA2.getId()).thenReturn(OperatorAccount.OLDCASTLE);
		assertTrue(feeService.qualifiesForInsureGuard(OAMocksSet));
	}

    @Test
    public void testActivation_getAdjustedFeeAmount_operatorWithDiscount() {
        operatorList.add(contractorOperator1);
        when(contractorOperator1.getOperatorAccount()).thenReturn(mockOperatorWithDiscount);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.Activation);

        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(DISCOUNTED_AMOUNT);

        assertEquals(DISCOUNTED_AMOUNT, FeeService.getAdjustedFeeAmountIfNecessary(contractor, invoiceFee1));
    }

    @Test
    public void testActivation_getAdjustedFeeAmount_operatorWithoutDiscount() {
        operatorList.add(contractorOperator1);
        when(contractorOperator1.getOperatorAccount()).thenReturn(mockOperatorWithoutDiscount);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.Activation);

        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);

        assertEquals(FULL_AMOUNT, FeeService.getAdjustedFeeAmountIfNecessary(contractor, invoiceFee1));
    }

    @Test
    public void testActivation_getAdjustedFeeAmount_noOperators() {
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.Activation);

        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(FULL_AMOUNT);

        assertEquals(FULL_AMOUNT, FeeService.getAdjustedFeeAmountIfNecessary(contractor, invoiceFee1));
    }

    @Test
    public void testActivation_getAdjustedFeeAmount_multipleDiscountedOperators() {
        operatorList.add(contractorOperator1);
        when(contractorOperator1.getOperatorAccount()).thenReturn(mockOperatorWithDiscount);
        operatorList.add(contractorOperator2);
        when(contractorOperator2.getOperatorAccount()).thenReturn(mockOperatorWithLesserDiscount);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.Activation);

        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(LESS_DISCOUNTED_AMOUNT);

        assertEquals(LESS_DISCOUNTED_AMOUNT, FeeService.getAdjustedFeeAmountIfNecessary(contractor, invoiceFee1));
    }

    @Test
    public void testActivation_getAdjustedFeeAmount_variedOperators() {
        operatorList.add(contractorOperator1);
        when(contractorOperator1.getOperatorAccount()).thenReturn(mockOperatorWithDiscount);
        operatorList.add(contractorOperator2);
        when(contractorOperator2.getOperatorAccount()).thenReturn(mockOperatorWithLesserDiscount);
        operatorList.add(contractorOperator3);
        when(contractorOperator3.getOperatorAccount()).thenReturn(mockOperatorWithoutDiscount);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.Activation);

        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);

        assertEquals(FULL_AMOUNT, FeeService.getAdjustedFeeAmountIfNecessary(contractor, invoiceFee1));
    }

    @Test
    public void syncMembershipFees_ContractorWithInvoiceBidOnlyWithActivation() {
        List<Invoice> invoices = new ArrayList<Invoice>();
        invoices.add(invoice);

        List<PaymentAppliedToInvoice> paymentAppliedToInvoices = new ArrayList<PaymentAppliedToInvoice>();
        paymentAppliedToInvoices.add(paymentAppliedToInvoice);

        List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
        invoiceItems.add(invoiceItem1);
        invoiceItems.add(invoiceItem2);

        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.BidOnly, new ContractorFee());

        contractor.setPayingFacilities(100);
        contractor.setInvoices(invoices);
        when(invoice.getStatus()).thenReturn(TransactionStatus.Unpaid);
        when(invoice.getItems()).thenReturn(invoiceItems);
        when(invoice.getInvoiceType()).thenReturn(InvoiceType.Activation);
        when(invoice.getPayingFacilities()).thenReturn(50);

        when(invoiceItem1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceItem1.getOriginalAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceItem1.getInvoiceFee()).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.Activation);
        when(invoiceItem1.getPaymentExpires()).thenReturn(new Date());
        when(invoiceFee1.isActivation()).thenReturn(true);
        when(invoice.getPayments()).thenReturn(paymentAppliedToInvoices);

        when(invoiceItem2.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceItem2.getOriginalAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceItem2.getInvoiceFee()).thenReturn(invoiceFee2);
        when(invoiceFee2.getFeeClass()).thenReturn(FeeClass.BidOnly);
        when(invoiceFee2.isMembership()).thenReturn(true);
        when(invoiceItem2.getPaymentExpires()).thenReturn(new Date());

        contractor.setFees(contractorFees);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.BidOnly,50)).thenReturn(invoiceFee2);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee2.getAmount()).thenReturn(BigDecimal.TEN);

        feeService.syncMembershipFees(contractor);

        assertEquals(contractor.getFees().get(FeeClass.BidOnly).getCurrentAmount(), BigDecimal.TEN);
        assertEquals(contractor.getFees().get(FeeClass.BidOnly).getNewAmount(), BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.BidOnly).getCurrentFacilityCount(),50);
        assertEquals(contractor.getFees().get(FeeClass.BidOnly).getNewFacilityCount(), 0);
    }

    @Test
    public void syncMembershipFees_ContractorWithInvoiceListOnlyNotInFees() {
        List<Invoice> invoices = new ArrayList<Invoice>();
        invoices.add(invoice);

        List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
        invoiceItems.add(invoiceItem1);

        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.BidOnly, new ContractorFee());

        contractor.setPayingFacilities(100);
        contractor.setInvoices(invoices);
        when(invoice.getStatus()).thenReturn(TransactionStatus.Unpaid);
        when(invoice.getItems()).thenReturn(invoiceItems);
        when(invoice.getInvoiceType()).thenReturn(InvoiceType.Activation);
        when(invoice.getPayingFacilities()).thenReturn(50);

        when(invoiceItem1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceItem1.getOriginalAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceItem1.getInvoiceFee()).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.ListOnly);
        when(invoiceFee1.isMembership()).thenReturn(true);
        when(invoiceItem1.getPaymentExpires()).thenReturn(new Date());
        contractor.setFees(contractorFees);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.ListOnly, 50)).thenReturn(invoiceFee1);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);

        feeService.syncMembershipFees(contractor);

        assertEquals(contractor.getFees().get(FeeClass.BidOnly).getCurrentAmount(), BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.BidOnly).getNewAmount(), BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.ListOnly).getCurrentAmount(), BigDecimal.TEN);
        assertEquals(contractor.getFees().get(FeeClass.ListOnly).getNewAmount(), BigDecimal.TEN);
        assertEquals(contractor.getFees().get(FeeClass.BidOnly).getCurrentFacilityCount(),0);
        assertEquals(contractor.getFees().get(FeeClass.BidOnly).getNewFacilityCount(), 0);
        assertEquals(contractor.getFees().get(FeeClass.ListOnly).getCurrentFacilityCount(),50);
        assertEquals(contractor.getFees().get(FeeClass.ListOnly).getNewFacilityCount(), 50);
    }

    @Test
    public void syncMembershipFees_ContractorWithDocuGUARD() {
        List<Invoice> invoices = new ArrayList<Invoice>();
        invoices.add(invoice);

        List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
        invoiceItems.add(invoiceItem1);
        invoiceItems.add(invoiceItem2);

        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.DocuGUARD, new ContractorFee());
        contractorFees.put(FeeClass.InsureGUARD, new ContractorFee());

        contractor.setPayingFacilities(100);
        contractor.setInvoices(invoices);
        when(invoice.getStatus()).thenReturn(TransactionStatus.Unpaid);
        when(invoice.getItems()).thenReturn(invoiceItems);
        when(invoice.getInvoiceType()).thenReturn(InvoiceType.Renewal);
        when(invoice.getPayingFacilities()).thenReturn(50);
        contractor.setFees(contractorFees);

        when(invoiceItem1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceItem1.getOriginalAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceItem1.getInvoiceFee()).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.DocuGUARD);
        when(invoiceFee1.isLegacyMembership()).thenReturn(true);
        when(invoiceFee1.isMembership()).thenReturn(true);
        when(invoiceItem1.getPaymentExpires()).thenReturn(new Date());

        when(invoiceItem2.getAmount()).thenReturn(new BigDecimal(20));
        when(invoiceItem2.getOriginalAmount()).thenReturn(new BigDecimal(20));
        when(invoiceItem2.getInvoiceFee()).thenReturn(invoiceFee2);
        when(invoiceFee2.getFeeClass()).thenReturn(FeeClass.DocuGUARD);
        when(invoiceFee2.isLegacyMembership()).thenReturn(true);
        when(invoiceFee2.isMembership()).thenReturn(true);
        when(invoiceItem2.getPaymentExpires()).thenReturn(new Date());

        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD, 50)).thenReturn(invoiceFee1);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.InsureGUARD, 50)).thenReturn(invoiceFee2);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceFee2.getAmount()).thenReturn(new BigDecimal(20));
        when(invoiceFee2.getFeeClass()).thenReturn(FeeClass.InsureGUARD);

        feeService.syncMembershipFees(contractor);

        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getCurrentAmount(), BigDecimal.TEN);
        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getNewAmount(), BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getCurrentAmount(),new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getNewAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getCurrentFacilityCount(),50);
        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getNewFacilityCount(), 0);
        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getCurrentFacilityCount(),50);
        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getNewFacilityCount(), 0);
    }

    @Test
    public void syncMembershipFees_ContractorWithAuditGUARD() {
        List<Invoice> invoices = new ArrayList<Invoice>();
        invoices.add(invoice);

        List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
        invoiceItems.add(invoiceItem1);
        invoiceItems.add(invoiceItem2);
        invoiceItems.add(invoiceItem3);

        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.DocuGUARD, new ContractorFee());
        contractorFees.put(FeeClass.InsureGUARD, new ContractorFee());
        contractorFees.put(FeeClass.AuditGUARD, new ContractorFee());

        contractor.setPayingFacilities(100);
        contractor.setInvoices(invoices);
        when(invoice.getStatus()).thenReturn(TransactionStatus.Unpaid);
        when(invoice.getItems()).thenReturn(invoiceItems);
        when(invoice.getInvoiceType()).thenReturn(InvoiceType.Renewal);
        when(invoice.getPayingFacilities()).thenReturn(50);
        contractor.setFees(contractorFees);

        when(invoiceItem1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceItem1.getOriginalAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceItem1.getInvoiceFee()).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.AuditGUARD);
        when(invoiceFee1.isLegacyMembership()).thenReturn(true);
        when(invoiceFee1.getId()).thenReturn(11);
        when(invoiceFee1.isMembership()).thenReturn(true);
        when(invoiceItem1.getPaymentExpires()).thenReturn(new Date());

        when(invoiceItem2.getAmount()).thenReturn(new BigDecimal(20));
        when(invoiceItem2.getOriginalAmount()).thenReturn(new BigDecimal(20));
        when(invoiceItem2.getInvoiceFee()).thenReturn(invoiceFee2);
        when(invoiceFee2.getFeeClass()).thenReturn(FeeClass.AuditGUARD);
        when(invoiceFee2.isLegacyMembership()).thenReturn(true);
        when(invoiceFee2.getId()).thenReturn(11);
        when(invoiceFee2.isMembership()).thenReturn(true);
        when(invoiceItem2.getPaymentExpires()).thenReturn(new Date());

        when(invoiceItem3.getAmount()).thenReturn(new BigDecimal(30));
        when(invoiceItem3.getOriginalAmount()).thenReturn(new BigDecimal(30));
        when(invoiceItem3.getInvoiceFee()).thenReturn(invoiceFee3);
        when(invoiceFee3.getFeeClass()).thenReturn(FeeClass.AuditGUARD);
        when(invoiceFee3.isLegacyMembership()).thenReturn(true);
        when(invoiceFee3.getId()).thenReturn(11);
        when(invoiceFee3.isMembership()).thenReturn(true);
        when(invoiceItem3.getPaymentExpires()).thenReturn(new Date());

        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD, 50)).thenReturn(invoiceFee3);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.InsureGUARD, 50)).thenReturn(invoiceFee2);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.AuditGUARD, 50)).thenReturn(invoiceFee1);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceFee2.getAmount()).thenReturn(new BigDecimal(20));
        when(invoiceFee3.getAmount()).thenReturn(new BigDecimal(30));
        when(invoiceFee2.getFeeClass()).thenReturn(FeeClass.InsureGUARD);
        when(invoiceFee3.getFeeClass()).thenReturn(FeeClass.DocuGUARD);

        feeService.syncMembershipFees(contractor);

        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getCurrentAmount(),new BigDecimal(30));
        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getNewAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getCurrentAmount(),new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getNewAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.AuditGUARD).getCurrentAmount(),BigDecimal.TEN);
        assertEquals(contractor.getFees().get(FeeClass.AuditGUARD).getNewAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getCurrentFacilityCount(),50);
        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getNewFacilityCount(), 0);
        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getCurrentFacilityCount(),50);
        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getNewFacilityCount(), 0);
        assertEquals(contractor.getFees().get(FeeClass.AuditGUARD).getCurrentFacilityCount(),50);
        assertEquals(contractor.getFees().get(FeeClass.AuditGUARD).getNewFacilityCount(), 0);
    }

    @Test
    public void syncMembershipFees_ContractorWithInsureGUARD() {
        List<Invoice> invoices = new ArrayList<Invoice>();
        invoices.add(invoice);

        List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
        invoiceItems.add(invoiceItem1);

        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.InsureGUARD, new ContractorFee());
        contractorFees.put(FeeClass.EmployeeGUARD, new ContractorFee());

        contractor.setPayingFacilities(100);
        contractor.setInvoices(invoices);
        when(invoice.getStatus()).thenReturn(TransactionStatus.Unpaid);
        when(invoice.getItems()).thenReturn(invoiceItems);
        when(invoice.getInvoiceType()).thenReturn(InvoiceType.Renewal);
        when(invoice.getPayingFacilities()).thenReturn(50);
        contractor.setFees(contractorFees);

        when(invoiceItem1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceItem1.getOriginalAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceItem1.getInvoiceFee()).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.InsureGUARD);
        when(invoiceFee1.isMembership()).thenReturn(true);
        when(invoiceItem1.getPaymentExpires()).thenReturn(new Date());

        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.InsureGUARD, 50)).thenReturn(invoiceFee1);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);

        feeService.syncMembershipFees(contractor);

        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getCurrentAmount(),BigDecimal.TEN);
        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getNewAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getCurrentAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getNewAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getCurrentFacilityCount(),50);
        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getNewFacilityCount(), 0);
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getCurrentFacilityCount(),0);
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getNewFacilityCount(), 0);
    }

    @Test
    public void syncMembershipFees_ContractorWithEmployeeGUARD() {
        List<Invoice> invoices = new ArrayList<Invoice>();
        invoices.add(invoice);

        List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
        invoiceItems.add(invoiceItem1);

        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.InsureGUARD, new ContractorFee());
        contractorFees.put(FeeClass.EmployeeGUARD, new ContractorFee());

        contractor.setPayingFacilities(100);
        contractor.setInvoices(invoices);
        when(invoice.getStatus()).thenReturn(TransactionStatus.Unpaid);
        when(invoice.getItems()).thenReturn(invoiceItems);
        when(invoice.getInvoiceType()).thenReturn(InvoiceType.Renewal);
        when(invoice.getPayingFacilities()).thenReturn(50);
        contractor.setFees(contractorFees);

        when(invoiceItem1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceItem1.getOriginalAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceItem1.getInvoiceFee()).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.EmployeeGUARD);
        when(invoiceFee1.isMembership()).thenReturn(true);
        when(invoiceItem1.getPaymentExpires()).thenReturn(new Date());

        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.EmployeeGUARD,50)).thenReturn(invoiceFee1);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);

        feeService.syncMembershipFees(contractor);

        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getCurrentAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getNewAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getCurrentAmount(),BigDecimal.TEN);
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getNewAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getCurrentFacilityCount(),0);
        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getNewFacilityCount(), 0);
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getCurrentFacilityCount(),50);
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getNewFacilityCount(), 0);
    }

    @Test
    public void syncMembershipFees_ContractorWithImportFees() {
        List<Invoice> invoices = new ArrayList<Invoice>();
        invoices.add(invoice);

        List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
        invoiceItems.add(invoiceItem1);

        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.ImportFee, new ContractorFee());
        contractorFees.put(FeeClass.EmployeeGUARD, new ContractorFee());

        contractor.setPayingFacilities(100);
        contractor.setInvoices(invoices);
        when(invoice.getStatus()).thenReturn(TransactionStatus.Unpaid);
        when(invoice.getItems()).thenReturn(invoiceItems);
        when(invoice.getInvoiceType()).thenReturn(InvoiceType.Renewal);
        when(invoice.getPayingFacilities()).thenReturn(50);
        contractor.setFees(contractorFees);

        when(invoiceItem1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceItem1.getOriginalAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceItem1.getInvoiceFee()).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.ImportFee);

        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.ImportFee,50)).thenReturn(invoiceFee1);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);

        feeService.syncMembershipFees(contractor);

        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getCurrentAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getNewAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.ImportFee).getCurrentAmount(),BigDecimal.TEN);
        assertEquals(contractor.getFees().get(FeeClass.ImportFee).getNewAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getCurrentFacilityCount(),0);
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getNewFacilityCount(), 0);
        assertEquals(contractor.getFees().get(FeeClass.ImportFee).getCurrentFacilityCount(),50);
        assertEquals(contractor.getFees().get(FeeClass.ImportFee).getNewFacilityCount(), 0);
    }

    @Test
    public void calcMembershipFees_contractorRequestedSkip() {
        contractor.setStatus(AccountStatus.Requested);

        boolean skipRequestedStatusContractors = true;

        feeService.calculateContractorInvoiceFees(contractor, skipRequestedStatusContractors);

        assertTrue(contractor.getFees().isEmpty());
    }

    @Test
    public void calcMembershipFees_contractorNoOperators() {

        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.AuditGUARD, new ContractorFee());

        contractor.setStatus(AccountStatus.Active);
        contractor.setOperators(new ArrayList<ContractorOperator>());
        contractor.setFees(contractorFees);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.AuditGUARD,0)).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.AuditGUARD);

        feeService.calculateContractorInvoiceFees(contractor, true);

        assertFalse(contractor.getFees().containsKey(FeeClass.ListOnly));
        assertFalse(contractor.getFees().containsKey(FeeClass.BidOnly));
        assertFalse(contractor.getFees().containsKey(FeeClass.DocuGUARD));
        assertFalse(contractor.getFees().containsKey(FeeClass.InsureGUARD));
        assertTrue(contractor.getFees().containsKey(FeeClass.AuditGUARD));
        assertFalse(contractor.getFees().containsKey(FeeClass.EmployeeGUARD));
        assertFalse(contractor.getFees().containsKey(FeeClass.ImportFee));
    }

    @Test
    public void calcMembershipFees_contractorOneMultipleOperator() {

        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.AuditGUARD, new ContractorFee());

        ArrayList<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
        contractorOperators.add(contractorOperator1);

        contractor.setStatus(AccountStatus.Active);
        contractor.setOperators(contractorOperators);
        when(contractorOperator1.getOperatorAccount()).thenReturn(operatorAccount);
        when(operatorAccount.getStatus()).thenReturn(AccountStatus.Active);
        when(operatorAccount.getDoContractorsPay()).thenReturn("Multiple");
        contractor.setFees(contractorFees);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.AuditGUARD, 0)).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.AuditGUARD);

        feeService.calculateContractorInvoiceFees(contractor, true);

        assertFalse(contractor.getFees().containsKey(FeeClass.ListOnly));
        assertFalse(contractor.getFees().containsKey(FeeClass.BidOnly));
        assertFalse(contractor.getFees().containsKey(FeeClass.DocuGUARD));
        assertFalse(contractor.getFees().containsKey(FeeClass.InsureGUARD));
        assertTrue(contractor.getFees().containsKey(FeeClass.AuditGUARD));
        assertFalse(contractor.getFees().containsKey(FeeClass.EmployeeGUARD));
        assertFalse(contractor.getFees().containsKey(FeeClass.ImportFee));
    }

    @Test
    public void calcMembershipFees_contractorListOnly() {
        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        ContractorFee listOnlyContractorFee = new ContractorFee();
        listOnlyContractorFee.setCurrentLevel(invoiceFee1);
        when(invoiceFee1.isListonly()).thenReturn(true);
        contractorFees.put(FeeClass.ListOnly, listOnlyContractorFee);

        List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
        contractorOperators.add(contractorOperator1);

        List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
        operators.add(operatorAccount);

        Set<ContractorTrade> contractorTrades = new HashSet<ContractorTrade>();
        contractorTrades.add(contractorTrade);

        List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
        rules.add(auditTypeRule);

        contractor.setStatus(AccountStatus.Active);
        contractor.setOperators(contractorOperators);
        contractor.setFees(contractorFees);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.ListOnly, 0)).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.ListOnly);
        when(contractorOperator1.getOperatorAccount()).thenReturn(operatorAccount);
        when(operatorAccount.getStatus()).thenReturn(AccountStatus.Active);
        when(operatorAccount.getDoContractorsPay()).thenReturn("Yes");
        contractor.setTrades(contractorTrades);
        when(contractorTrade.getTrade()).thenReturn(trade);
        contractor.setOnsiteServices(true);

        contractor.setAccountLevel(AccountLevel.ListOnly);

        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.ListOnly, 1)).thenReturn(invoiceFee1);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);

        feeService.calculateContractorInvoiceFees(contractor, true);

        assertEquals(contractor.getFees().get(FeeClass.ListOnly).getCurrentAmount(), BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.ListOnly).getNewAmount(), BigDecimal.TEN);
    }

    @Test
    public void calcMembershipFees_contractorBidOnly() {
        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        ContractorFee bidOnlyContractorFee = new ContractorFee();
        bidOnlyContractorFee.setCurrentLevel(invoiceFee1);
        when(invoiceFee1.isListonly()).thenReturn(true);
        contractorFees.put(FeeClass.BidOnly, bidOnlyContractorFee);

        List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
        contractorOperators.add(contractorOperator1);

        List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
        operators.add(operatorAccount);

        Set<ContractorTrade> contractorTrades = new HashSet<ContractorTrade>();
        contractorTrades.add(contractorTrade);

        List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
        rules.add(auditTypeRule);

        contractor.setStatus(AccountStatus.Active);
        contractor.setOperators(contractorOperators);
        contractor.setFees(contractorFees);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.BidOnly, 0)).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.BidOnly);
        when(contractorOperator1.getOperatorAccount()).thenReturn(operatorAccount);
        when(operatorAccount.getStatus()).thenReturn(AccountStatus.Active);
        when(operatorAccount.getDoContractorsPay()).thenReturn("Yes");
        contractor.setTrades(contractorTrades);
        when(contractorTrade.getTrade()).thenReturn(trade);
        contractor.setOnsiteServices(true);

        contractor.setAccountLevel(AccountLevel.BidOnly);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.BidOnly,1)).thenReturn(invoiceFee1);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);

        feeService.calculateContractorInvoiceFees(contractor, true);

        assertEquals(contractor.getFees().get(FeeClass.BidOnly).getCurrentAmount(), BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.BidOnly).getNewAmount(), BigDecimal.TEN);
    }

    @Test
    public void calcMembershipFees_contractorDocuGUARD() {
        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.DocuGUARD, new ContractorFee());

        List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
        contractorOperators.add(contractorOperator1);

        List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
        operators.add(operatorAccount);

        Set<ContractorTrade> contractorTrades = new HashSet<ContractorTrade>();
        contractorTrades.add(contractorTrade);

        List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
        rules.add(auditTypeRule);

        contractor.setStatus(AccountStatus.Active);
        contractor.setOperators(contractorOperators);
        contractor.setFees(contractorFees);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD, 0)).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.DocuGUARD);
        when(contractorOperator1.getOperatorAccount()).thenReturn(operatorAccount);
        when(operatorAccount.getStatus()).thenReturn(AccountStatus.Active);
        when(operatorAccount.getDoContractorsPay()).thenReturn("Yes");
        contractor.setTrades(contractorTrades);
        when(contractorTrade.getTrade()).thenReturn(trade);
        contractor.setOnsiteServices(true);

        contractor.setAccountLevel(AccountLevel.Full);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD,1)).thenReturn(invoiceFee1);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);

        feeService.calculateContractorInvoiceFees(contractor, true);

        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getCurrentAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getNewAmount(),BigDecimal.TEN);
    }

    @Test
    public void calcMembershipFees_contractorAuditGUARD() {
        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.AuditGUARD, new ContractorFee());

        List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
        contractorOperators.add(contractorOperator1);

        List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
        operators.add(operatorAccount);

        Set<ContractorTrade> contractorTrades = new HashSet<ContractorTrade>();
        contractorTrades.add(contractorTrade);

        List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
        rules.add(auditTypeRule);

        contractor.setStatus(AccountStatus.Active);
        contractor.setOperators(contractorOperators);
        contractor.setFees(contractorFees);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.AuditGUARD, 0)).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.AuditGUARD);
        when(contractorOperator1.getOperatorAccount()).thenReturn(operatorAccount);
        when(operatorAccount.getStatus()).thenReturn(AccountStatus.Active);
        when(operatorAccount.getDoContractorsPay()).thenReturn("Yes");
        contractor.setTrades(contractorTrades);
        when(contractorTrade.getTrade()).thenReturn(trade);
        contractor.setOnsiteServices(true);
        contractor.setAccountLevel(AccountLevel.Full);

        when(ruleCache.getRules(contractor)).thenReturn(rules);
        when(auditTypeRule.isInclude()).thenReturn(true);
        when(auditTypeRule.getAuditType()).thenReturn(auditType);
        when(auditTypeRule.isApplies(trade)).thenReturn(true);
        when(auditTypeRule.isApplies(ContractorType.Onsite)).thenReturn(true);
        when(auditTypeRule.isApplies(operatorAccount)).thenReturn(true);
        when(operatorAccount.isOperator()).thenReturn(true);
        when(auditTypeRule.isMoreSpecific(any(AuditTypeRule.class))).thenReturn(true);

        when(auditType.isAlwaysBilledForAuditGUARD()).thenReturn(true);
        when(auditType.getClassType()).thenReturn(AuditTypeClass.Audit);

        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.AuditGUARD,1)).thenReturn(invoiceFee1);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD,1)).thenReturn(invoiceFee2);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceFee2.getAmount()).thenReturn(new BigDecimal(20));
        when(invoiceFee2.getFeeClass()).thenReturn(FeeClass.DocuGUARD);

        feeService.calculateContractorInvoiceFees(contractor, true);

        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getCurrentAmount(),new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getNewAmount(),new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.AuditGUARD).getCurrentAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.AuditGUARD).getNewAmount(),BigDecimal.TEN);
    }

    @Test
    public void calcMembershipFees_contractorAuditGUARDIecSuncor() {
        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.AuditGUARD, new ContractorFee());

        List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
        contractorOperators.add(contractorOperator1);

        List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
        operators.add(operatorAccount);

        Set<ContractorTrade> contractorTrades = new HashSet<ContractorTrade>();
        contractorTrades.add(contractorTrade);

        List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
        rules.add(auditTypeRule);

        contractor.setStatus(AccountStatus.Active);
        contractor.setOperators(contractorOperators);
        contractor.setFees(contractorFees);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.AuditGUARD, 0)).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.AuditGUARD);
        when(contractorOperator1.getOperatorAccount()).thenReturn(operatorAccount);
        when(operatorAccount.getStatus()).thenReturn(AccountStatus.Active);
        when(operatorAccount.getDoContractorsPay()).thenReturn("Yes");
        contractor.setTrades(contractorTrades);
        when(contractorTrade.getTrade()).thenReturn(trade);
        contractor.setOnsiteServices(true);
        contractor.setAccountLevel(AccountLevel.Full);

        when(operatorAccount.isDescendantOf(OperatorAccount.SUNCOR)).thenReturn(true);

        when(ruleCache.getRules(contractor)).thenReturn(rules);
        when(auditTypeRule.isInclude()).thenReturn(true);
        when(auditTypeRule.getAuditType()).thenReturn(auditType);
        when(auditTypeRule.isApplies(trade)).thenReturn(true);
        when(auditTypeRule.isApplies(ContractorType.Onsite)).thenReturn(true);
        when(auditTypeRule.isApplies(operatorAccount)).thenReturn(true);
        when(operatorAccount.isOperator()).thenReturn(true);
        when(auditTypeRule.isMoreSpecific(any(AuditTypeRule.class))).thenReturn(true);

        when(auditType.isIec()).thenReturn(true);
        when(auditType.getClassType()).thenReturn(AuditTypeClass.Audit);

        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.AuditGUARD,1)).thenReturn(invoiceFee1);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD,1)).thenReturn(invoiceFee2);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceFee2.getAmount()).thenReturn(new BigDecimal(20));
        when(invoiceFee2.getFeeClass()).thenReturn(FeeClass.DocuGUARD);

        feeService.calculateContractorInvoiceFees(contractor, true);

        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getCurrentAmount(),new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getNewAmount(),new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.AuditGUARD).getCurrentAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.AuditGUARD).getNewAmount(),BigDecimal.TEN);
    }

    @Test
    public void calcMembershipFees_contractorInsureGUARD() {
        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        ContractorFee contractorFee = new ContractorFee();
        contractorFee.setCurrentLevel(invoiceFee2);
        contractorFees.put(FeeClass.InsureGUARD, contractorFee);

        List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
        contractorOperators.add(contractorOperator1);

        List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
        operators.add(operatorAccount);

        Set<ContractorTrade> contractorTrades = new HashSet<ContractorTrade>();
        contractorTrades.add(contractorTrade);

        List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
        rules.add(auditTypeRule);

        contractor.setStatus(AccountStatus.Active);
        contractor.setOperators(contractorOperators);
        contractor.setFees(contractorFees);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.InsureGUARD, 0)).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.InsureGUARD);
        when(contractorOperator1.getOperatorAccount()).thenReturn(operatorAccount);
        when(operatorAccount.getStatus()).thenReturn(AccountStatus.Active);
        when(operatorAccount.getDoContractorsPay()).thenReturn("Yes");
        contractor.setTrades(contractorTrades);
        when(contractorTrade.getTrade()).thenReturn(trade);
        contractor.setOnsiteServices(true);
        contractor.setAccountLevel(AccountLevel.Full);

        when(ruleCache.getRules(contractor)).thenReturn(rules);
        when(auditTypeRule.isInclude()).thenReturn(true);
        when(auditTypeRule.getAuditType()).thenReturn(auditType);
        when(auditTypeRule.isApplies(trade)).thenReturn(true);
        when(auditTypeRule.isApplies(ContractorType.Onsite)).thenReturn(true);
        when(auditTypeRule.isApplies(operatorAccount)).thenReturn(true);
        when(operatorAccount.isOperator()).thenReturn(true);
        when(auditTypeRule.isMoreSpecific(any(AuditTypeRule.class))).thenReturn(true);

        when(auditType.getClassType()).thenReturn(AuditTypeClass.Policy);

        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.InsureGUARD, 1)).thenReturn(invoiceFee1);
        contractorFee.setFeeClass(FeeClass.InsureGUARD);
        when(invoiceFee1.getMinFacilities()).thenReturn(100);
        when(invoiceFee2.getMaxFacilities()).thenReturn(1);
        when(operatorAccount.getTopAccount()).thenReturn(operatorAccount);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD, 1)).thenReturn(invoiceFee2);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceFee2.getAmount()).thenReturn(new BigDecimal(20));
        when(invoiceFee2.getFeeClass()).thenReturn(FeeClass.DocuGUARD);

        feeService.calculateContractorInvoiceFees(contractor, true);

        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getCurrentAmount(),new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getNewAmount(),new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getCurrentAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getNewAmount(),BigDecimal.TEN);
    }

    @Test
    public void calcMembershipFees_contractorInsureGUARDExcluded() {
        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        ContractorFee contractorFee = new ContractorFee();
        contractorFee.setCurrentLevel(invoiceFee2);
        contractorFees.put(FeeClass.InsureGUARD, contractorFee);

        List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
        contractorOperators.add(contractorOperator1);

        List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
        operators.add(operatorAccount);

        Set<ContractorTrade> contractorTrades = new HashSet<ContractorTrade>();
        contractorTrades.add(contractorTrade);

        List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
        rules.add(auditTypeRule);

        contractor.setStatus(AccountStatus.Active);
        contractor.setOperators(contractorOperators);
        contractor.setFees(contractorFees);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.InsureGUARD, 0)).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.InsureGUARD);
        when(contractorOperator1.getOperatorAccount()).thenReturn(operatorAccount);
        when(operatorAccount.getStatus()).thenReturn(AccountStatus.Active);
        when(operatorAccount.getDoContractorsPay()).thenReturn("Yes");
        contractor.setTrades(contractorTrades);
        when(contractorTrade.getTrade()).thenReturn(trade);
        contractor.setOnsiteServices(true);
        contractor.setAccountLevel(AccountLevel.Full);

        when(ruleCache.getRules(contractor)).thenReturn(rules);
        when(auditTypeRule.isInclude()).thenReturn(true);
        when(auditTypeRule.getAuditType()).thenReturn(auditType);
        when(auditTypeRule.isApplies(trade)).thenReturn(true);
        when(auditTypeRule.isApplies(ContractorType.Onsite)).thenReturn(true);
        when(auditTypeRule.isApplies(operatorAccount)).thenReturn(true);
        when(operatorAccount.isOperator()).thenReturn(true);
        when(auditTypeRule.isMoreSpecific(any(AuditTypeRule.class))).thenReturn(true);

        when(auditType.getClassType()).thenReturn(AuditTypeClass.Policy);

        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.InsureGUARD,1)).thenReturn(invoiceFee1);
        contractorFee.setFeeClass(FeeClass.InsureGUARD);
        when(invoiceFee1.getMinFacilities()).thenReturn(100);
        when(invoiceFee2.getMaxFacilities()).thenReturn(1);
        when(operatorAccount.getTopAccount()).thenReturn(operatorAccount);
        when(operatorAccount.getId()).thenReturn(OperatorAccount.BASF);
        contractor.setLastUpgradeDate(DateBean.parseDate("01/01/2000", "MM/dd/yyyy"));
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD, 1)).thenReturn(invoiceFee1);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);

        feeService.calculateContractorInvoiceFees(contractor, true);

        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getCurrentAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.InsureGUARD).getNewAmount(), BigDecimal.ZERO);
    }

    @Test
    public void calcMembershipFees_contractorEmployeeGUARDAuditPlus() {
        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.EmployeeGUARD, new ContractorFee());

        List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
        contractorOperators.add(contractorOperator1);

        List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
        operators.add(operatorAccount);

        Set<ContractorTrade> contractorTrades = new HashSet<ContractorTrade>();
        contractorTrades.add(contractorTrade);

        List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
        rules.add(auditTypeRule);

        when(accountEmployeeGuardDAO.hasEmployeeGUARDOperator(anyString())).thenReturn(false);
        contractor.setStatus(AccountStatus.Active);
        contractor.setOperators(contractorOperators);
        contractor.setFees(contractorFees);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.EmployeeGUARD, 0)).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.EmployeeGUARD);
        when(contractorOperator1.getOperatorAccount()).thenReturn(operatorAccount);
        when(operatorAccount.getStatus()).thenReturn(AccountStatus.Active);
        when(operatorAccount.getDoContractorsPay()).thenReturn("Yes");
        contractor.setTrades(contractorTrades);
        when(contractorTrade.getTrade()).thenReturn(trade);
        contractor.setOnsiteServices(true);
        contractor.setAccountLevel(AccountLevel.Full);

        when(ruleCache.getRules(contractor)).thenReturn(rules);
        when(auditTypeRule.isInclude()).thenReturn(true);
        when(auditTypeRule.getAuditType()).thenReturn(auditType);
        when(auditTypeRule.isApplies(trade)).thenReturn(true);
        when(auditTypeRule.isApplies(ContractorType.Onsite)).thenReturn(true);
        when(auditTypeRule.isApplies(operatorAccount)).thenReturn(true);
        when(operatorAccount.isOperator()).thenReturn(true);
        when(auditTypeRule.isMoreSpecific(any(AuditTypeRule.class))).thenReturn(true);

        when(auditType.getClassType()).thenReturn(AuditTypeClass.Audit);
        when(auditType.getId()).thenReturn(AuditType.IMPLEMENTATIONAUDITPLUS);

        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.EmployeeGUARD, 1)).thenReturn(invoiceFee1);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD, 1)).thenReturn(invoiceFee2);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.EmployeeGUARD);
        when(invoiceFee2.getFeeClass()).thenReturn(FeeClass.DocuGUARD);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceFee2.getAmount()).thenReturn(new BigDecimal(20));

        feeService.calculateContractorInvoiceFees(contractor, true);

        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getCurrentAmount(), new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getNewAmount(), new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getCurrentAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getNewAmount(),BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP));
    }

    @Test
    public void calcMembershipFees_contractorEmployeeGUARDEmployeeAudits() {
        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.EmployeeGUARD, new ContractorFee());

        List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
        contractorOperators.add(contractorOperator1);

        List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
        operators.add(operatorAccount);

        Set<ContractorTrade> contractorTrades = new HashSet<ContractorTrade>();
        contractorTrades.add(contractorTrade);

        List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
        rules.add(auditTypeRule);

        when(accountEmployeeGuardDAO.hasEmployeeGUARDOperator(anyString())).thenReturn(false);
        contractor.setStatus(AccountStatus.Active);
        contractor.setOperators(contractorOperators);
        contractor.setFees(contractorFees);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.EmployeeGUARD, 0)).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.EmployeeGUARD);
        when(contractorOperator1.getOperatorAccount()).thenReturn(operatorAccount);
        when(operatorAccount.getStatus()).thenReturn(AccountStatus.Active);
        when(operatorAccount.getDoContractorsPay()).thenReturn("Yes");
        contractor.setTrades(contractorTrades);
        when(contractorTrade.getTrade()).thenReturn(trade);
        contractor.setOnsiteServices(true);
        contractor.setAccountLevel(AccountLevel.Full);

        when(ruleCache.getRules(contractor)).thenReturn(rules);
        when(auditTypeRule.isInclude()).thenReturn(true);
        when(auditTypeRule.getAuditType()).thenReturn(auditType);
        when(auditTypeRule.isApplies(trade)).thenReturn(true);
        when(auditTypeRule.isApplies(ContractorType.Onsite)).thenReturn(true);
        when(auditTypeRule.isApplies(operatorAccount)).thenReturn(true);
        when(operatorAccount.isOperator()).thenReturn(true);
        when(auditTypeRule.isMoreSpecific(any(AuditTypeRule.class))).thenReturn(true);

        when(auditType.getClassType()).thenReturn(AuditTypeClass.Employee);

        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.EmployeeGUARD, 1)).thenReturn(invoiceFee1);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD, 1)).thenReturn(invoiceFee2);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.EmployeeGUARD);
        when(invoiceFee2.getFeeClass()).thenReturn(FeeClass.DocuGUARD);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceFee2.getAmount()).thenReturn(new BigDecimal(20));

        feeService.calculateContractorInvoiceFees(contractor, true);

        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getCurrentAmount(), new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getNewAmount(), new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getCurrentAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getNewAmount(),BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP));
    }

    @Test
    public void calcMembershipFees_contractorEmployeeGUARDHSE() {
        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.InsureGUARD, new ContractorFee());

        List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
        contractorOperators.add(contractorOperator1);

        List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
        operators.add(operatorAccount);

        Set<ContractorTrade> contractorTrades = new HashSet<ContractorTrade>();
        contractorTrades.add(contractorTrade);

        List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
        rules.add(auditTypeRule);

        contractor.setStatus(AccountStatus.Active);
        contractor.setOperators(contractorOperators);
        contractor.setFees(contractorFees);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.AuditGUARD, 0)).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.AuditGUARD);
        when(contractorOperator1.getOperatorAccount()).thenReturn(operatorAccount);
        when(operatorAccount.getStatus()).thenReturn(AccountStatus.Active);
        when(operatorAccount.getDoContractorsPay()).thenReturn("Yes");
        contractor.setTrades(contractorTrades);
        when(contractorTrade.getTrade()).thenReturn(trade);
        contractor.setOnsiteServices(true);
        contractor.setAccountLevel(AccountLevel.Full);

        when(ruleCache.getRules(contractor)).thenReturn(rules);
        when(auditTypeRule.isInclude()).thenReturn(true);
        when(auditTypeRule.getAuditType()).thenReturn(auditType);
        when(auditTypeRule.isApplies(trade)).thenReturn(true);
        when(auditTypeRule.isApplies(ContractorType.Onsite)).thenReturn(true);
        when(auditTypeRule.isApplies(operatorAccount)).thenReturn(true);
        when(operatorAccount.isOperator()).thenReturn(true);
        when(auditTypeRule.isMoreSpecific(any(AuditTypeRule.class))).thenReturn(true);

        when(auditType.getClassType()).thenReturn(AuditTypeClass.Audit);
        when(auditType.getId()).thenReturn(AuditType.HSE_COMPETENCY);

        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.EmployeeGUARD, 1)).thenReturn(invoiceFee1);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD, 1)).thenReturn(invoiceFee2);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.EmployeeGUARD);
        when(invoiceFee2.getFeeClass()).thenReturn(FeeClass.DocuGUARD);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceFee2.getAmount()).thenReturn(new BigDecimal(20));

        feeService.calculateContractorInvoiceFees(contractor, true);

        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getCurrentAmount(),new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getNewAmount(),new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getCurrentAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getNewAmount(),BigDecimal.TEN);
    }

    @Test
    public void calcMembershipFees_contractorEmployeeGUARDOQ() {
        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.EmployeeGUARD, new ContractorFee());

        List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
        contractorOperators.add(contractorOperator1);

        List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
        operators.add(operatorAccount);

        Set<ContractorTrade> contractorTrades = new HashSet<ContractorTrade>();
        contractorTrades.add(contractorTrade);

        List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
        rules.add(auditTypeRule);

        when(accountEmployeeGuardDAO.hasEmployeeGUARDOperator(anyString())).thenReturn(false);
        contractor.setStatus(AccountStatus.Active);
        contractor.setOperators(contractorOperators);
        contractor.setFees(contractorFees);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.EmployeeGUARD, 0)).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.EmployeeGUARD);
        when(contractorOperator1.getOperatorAccount()).thenReturn(operatorAccount);
        when(operatorAccount.getStatus()).thenReturn(AccountStatus.Active);
        when(operatorAccount.getDoContractorsPay()).thenReturn("Yes");
        contractor.setTrades(contractorTrades);
        when(contractorTrade.getTrade()).thenReturn(trade);
        contractor.setOnsiteServices(true);
        contractor.setAccountLevel(AccountLevel.Full);

        when(operatorAccount.isRequiresOQ()).thenReturn(true);

        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.EmployeeGUARD, 1)).thenReturn(invoiceFee1);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD, 1)).thenReturn(invoiceFee2);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.EmployeeGUARD);
        when(invoiceFee2.getFeeClass()).thenReturn(FeeClass.DocuGUARD);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceFee2.getAmount()).thenReturn(new BigDecimal(20));

        feeService.calculateContractorInvoiceFees(contractor, true);

        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getCurrentAmount(),new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getNewAmount(),new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getCurrentAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getNewAmount(),BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_UP));
    }

    @Test
    public void calcMembershipFees_contractorAccountEmployeeGUARD() {
        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.EmployeeGUARD, new ContractorFee());

        List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
        contractorOperators.add(contractorOperator1);

        List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
        operators.add(operatorAccount);

        Set<ContractorTrade> contractorTrades = new HashSet<ContractorTrade>();
        contractorTrades.add(contractorTrade);

        List<AuditTypeRule> rules = new ArrayList<AuditTypeRule>();
        rules.add(auditTypeRule);

        when(accountEmployeeGuardDAO.hasEmployeeGUARDOperator(anyString())).thenReturn(true);
        contractor.setStatus(AccountStatus.Active);
        contractor.setOperators(contractorOperators);
        contractor.setFees(contractorFees);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.EmployeeGUARD, 0)).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.EmployeeGUARD);
        when(contractorOperator1.getOperatorAccount()).thenReturn(operatorAccount);
        when(operatorAccount.getStatus()).thenReturn(AccountStatus.Active);
        when(operatorAccount.getDoContractorsPay()).thenReturn("Yes");
        contractor.setTrades(contractorTrades);
        when(contractorTrade.getTrade()).thenReturn(trade);
        contractor.setOnsiteServices(true);
        contractor.setAccountLevel(AccountLevel.Full);

        when(operatorAccount.isRequiresOQ()).thenReturn(true);

        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.EmployeeGUARD, 1)).thenReturn(invoiceFee1);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD, 1)).thenReturn(invoiceFee2);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.EmployeeGUARD);
        when(invoiceFee2.getFeeClass()).thenReturn(FeeClass.DocuGUARD);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceFee2.getAmount()).thenReturn(new BigDecimal(20));

        feeService.calculateContractorInvoiceFees(contractor, true);

        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getCurrentAmount(),new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getNewAmount(),new BigDecimal(20));
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getCurrentAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.EmployeeGUARD).getNewAmount(),BigDecimal.TEN);
    }

    @Test
    public void calcMembershipFees_contractorImportFee() {
        Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
        contractorFees.put(FeeClass.DocuGUARD, new ContractorFee());

        List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
        contractorOperators.add(contractorOperator1);

        List<OperatorAccount> operators = new ArrayList<OperatorAccount>();
        operators.add(operatorAccount);

        Set<ContractorTrade> contractorTrades = new HashSet<ContractorTrade>();
        contractorTrades.add(contractorTrade);

        List<ContractorAudit> contractorAudits = new ArrayList<ContractorAudit>();
        contractorAudits.add(contractorAudit);

        contractor.setStatus(AccountStatus.Active);
        contractor.setOperators(contractorOperators);
        when(contractorOperator1.getOperatorAccount()).thenReturn(operatorAccount);
        when(operatorAccount.getStatus()).thenReturn(AccountStatus.Active);
        when(operatorAccount.getDoContractorsPay()).thenReturn("Yes");
        contractor.setFees(contractorFees);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD, 0)).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.DocuGUARD);
        contractor.setTrades(contractorTrades);
        when(contractorTrade.getTrade()).thenReturn(trade);
        contractor.setOnsiteServices(true);
        contractor.setAccountLevel(AccountLevel.Full);

        contractor.setAudits(contractorAudits);
        when(contractorAudit.getAuditType()).thenReturn(auditType);
        when(contractorAudit.isExpired()).thenReturn(false);
        when(auditType.getId()).thenReturn(AuditType.IMPORT_PQF);

        when(feeDAO.find(InvoiceFee.IMPORTFEE)).thenReturn(invoiceFee2);
        when(feeDAO.find(InvoiceFee.IMPORTFEEZEROLEVEL)).thenReturn(invoiceFee2);
        when(invoiceFee2.getFeeClass()).thenReturn(FeeClass.ImportFee);
        when(feeDAO.findByNumberOfOperatorsAndClass(FeeClass.DocuGUARD, 1)).thenReturn(invoiceFee1);
        when(invoiceFee1.getFeeClass()).thenReturn(FeeClass.DocuGUARD);
        when(country.getAmountOverrides()).thenReturn(new ArrayList<InvoiceFeeCountry>());
        when(invoiceFee1.getAmount()).thenReturn(BigDecimal.TEN);
        when(invoiceFee2.getAmount()).thenReturn(BigDecimal.TEN);

        feeService.calculateContractorInvoiceFees(contractor, true);
        for (FeeClass feeClass : contractor.getFees().keySet()) {
            ContractorFee fee = contractor.getFees().get(feeClass);
        }

        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getCurrentAmount(),BigDecimal.ZERO);
        assertEquals(contractor.getFees().get(FeeClass.DocuGUARD).getNewAmount(),BigDecimal.TEN);
        assertEquals(contractor.getFees().get(FeeClass.ImportFee).getCurrentAmount(),BigDecimal.TEN);
        assertEquals(contractor.getFees().get(FeeClass.ImportFee).getNewAmount(),BigDecimal.TEN);
    }

    @Test
    public void calculateUpgradeDate_setUpgradeDate() {
        when(billingService.billingStatus(contractorAccount)).thenReturn(BillingStatus.Current);
        feeService.calculateUpgradeDate(contractorAccount, BillingStatus.Upgrade);
        verify(contractorAccount, times(1)).setLastUpgradeDate(null);
    }

    @Test
    public void calculateUpgradeDate_saveUpgradeDate() {
        when(billingService.billingStatus(contractorAccount)).thenReturn(BillingStatus.Upgrade);
        feeService.calculateUpgradeDate(contractorAccount, BillingStatus.Upgrade);
        verify(contractorAccount, never()).setLastUpgradeDate(any(Date.class));
    }

    @Test
    public void calculateUpgradeDate_clearUpgradeDate() {
        when(billingService.billingStatus(contractorAccount)).thenReturn(BillingStatus.Upgrade);
        feeService.calculateUpgradeDate(contractorAccount, BillingStatus.Current);
        verify(contractorAccount, times(1)).setLastUpgradeDate(any(Date.class));
    }


    @Test
    public void testDropBlockSSManualAuditTagIfUpgrading_NoTag() throws Exception {
        when(operatorTag.isBlockSSManualAudit()).thenReturn(false);
        Whitebox.invokeMethod(feeService, "dropBlockSSManualAuditTagIfUpgrading", contractor, BillingStatus.Upgrade);
    }

    @Test
    public void testDropBlockSSManualAuditTagIfUpgrading_Tagged() throws Exception {
        when(operatorTag.isBlockSSManualAudit()).thenReturn(true);

        Whitebox.invokeMethod(feeService, "dropBlockSSManualAuditTagIfUpgrading", contractor, BillingStatus.Upgrade);
    }
}