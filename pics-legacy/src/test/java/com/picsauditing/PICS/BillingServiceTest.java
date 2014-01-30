package com.picsauditing.PICS;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.model.billing.AccountingSystemSynchronization;
import com.picsauditing.model.billing.InvoiceModel;
import com.picsauditing.util.SapAppPropertyUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class BillingServiceTest extends PicsTranslationTest {
	private BillingService billingService;

	private static Date twoHundredDaysFromNow = DateBean.addDays(new Date(), 200);

	private List<Invoice> invoices;
	private List<InvoiceItem> invoiceItems;
	private Map<FeeClass, ContractorFee> fees;
	private BigDecimal invoiceTotal;

    @Mock
    private ContractorAccountDAO accountDAO;
	@Mock
	private ContractorAccount mockContractor;
	@Mock
	private User user;
	@Mock
	private Country country;
	@Mock
	private BusinessUnit businessUnit;
    @Mock
    private FeeService feeService;
	@Mock
	private TaxService taxService;
	@Mock
	private InvoiceFeeDAO invoiceFeeDAO;
	@Mock
	private InvoiceItem invoiceItem;
	@Mock
	private ContractorFee bidOnlyFee;
	@Mock
	private ContractorFee listOnlyFee;
    @Mock
    private ContractorFee contractorFee;
	@Mock
	private InvoiceFee bidOnlyInvoiceFee;
	@Mock
	private InvoiceFee listOnlyinvoiceFee;
	@Mock
	private InvoiceFee invoiceFee;
	@Mock
	private ContractorOperator contractorOperator;
	@Mock
	private OperatorAccount operator;
	@Mock
	private InvoiceModel invoiceModel;
	@Mock
	private AuditDataDAO auditDataDAO;
	@Mock
	private SapAppPropertyUtil sapAppPropertyUtil;
	@Mock
	private InvoiceFeeCountry override;
	@Mock
	private Invoice mockInvoice;
	@Mock
	private Invoice invoice;
	@Mock
	private InvoiceItem previousInvoiceItem;
	@Mock
	private InvoiceItemDAO invoiceItemDAO;
    @Mock
    private CreditMemoAppliedToInvoice creditMemoAppliedToInvoice;
    @Mock
    private CreditMemoAppliedToInvoice creditMemoAppliedToInvoice2;
    @Mock
    private ContractorFee upgradeFee;
    @Mock
    private InvoiceFee upgradeinvoiceFee;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		billingService = new BillingService();
		Whitebox.setInternalState(billingService, "taxService", taxService);
		Whitebox.setInternalState(billingService, "feeService", feeService);
		Whitebox.setInternalState(billingService, "feeDAO", invoiceFeeDAO);
		Whitebox.setInternalState(billingService, "invoiceModel", invoiceModel);
		Whitebox.setInternalState(billingService, "accountDao", accountDAO);
		Whitebox.setInternalState(billingService, "auditDataDAO", auditDataDAO);
		Whitebox.setInternalState(billingService, "invoiceItemDAO", invoiceItemDAO);
		AccountingSystemSynchronization.setSapAppPropertyUtil(sapAppPropertyUtil);
		assert (OAMocksSet.isEmpty());

		setupInvoiceAndItems();
		setupStandardFees(true, true);
		calculateInvoiceTotal();
		when(mockContractor.getCountry()).thenReturn(country);
        when(country.getBusinessUnit()).thenReturn(businessUnit);
		when(businessUnit.getId()).thenReturn(2);
	}

	private void setupInvoiceAndItems() {
		invoices = new ArrayList<>();
		invoiceItems = new ArrayList<>();
		invoiceItems.add(invoiceItem);
		when(invoiceItem.getAmount()).thenReturn(new BigDecimal(199.00));
        when(invoiceItem.getOriginalAmount()).thenReturn(new BigDecimal(199.00));
		when(invoiceItem.getInvoiceFee()).thenReturn(invoiceFee);
	}

	private void setupStandardFees(boolean bidOnlyIsFree, boolean listOnlyIsFree) {
		fees = new HashMap<>();
		// all contractors have bid only and list only fees, they may just be
		// hidden and $0
		fees.put(FeeClass.BidOnly, bidOnlyFee);
		fees.put(FeeClass.ListOnly, listOnlyFee);
		when(bidOnlyFee.getCurrentLevel()).thenReturn(bidOnlyInvoiceFee);
		when(listOnlyFee.getCurrentLevel()).thenReturn(listOnlyinvoiceFee);
        when(bidOnlyInvoiceFee.isBidonly()).thenReturn(true);
        when(bidOnlyInvoiceFee.isFree()).thenReturn(bidOnlyIsFree);
		when(listOnlyinvoiceFee.isFree()).thenReturn(listOnlyIsFree);
	}

	private void setupCreateInvoiceWithItemsTestsCommon() {
		when(mockContractor.getId()).thenReturn(123);
		when(mockContractor.getCountry()).thenReturn(country);
		when(mockContractor.getFees()).thenReturn(fees);
		when(mockContractor.getStatus()).thenReturn(AccountStatus.Active);
		when(mockContractor.getInvoices()).thenReturn(invoices);
		when(mockContractor.getPaymentExpires()).thenReturn(twoHundredDaysFromNow);

		when(invoiceFee.isMembership()).thenReturn(true);

		List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
        contractorOperators.add(contractorOperator);
		when(mockContractor.getNonCorporateOperators()).thenReturn(contractorOperators);
		when(contractorOperator.getOperatorAccount()).thenReturn(operator);
		when(operator.getDoContractorsPay()).thenReturn("Yes");
		when(operator.getName()).thenReturn("Test Operator");
	}

	private void calculateInvoiceTotal() {
		invoiceTotal = BigDecimal.ZERO.setScale(2);
		for (InvoiceItem item : invoiceItems) {
			invoiceTotal = invoiceTotal.add(item.getAmount());
		}
	}

    @Test
    public void testSyncBalance_requested() throws Exception {
        billingService.syncBalance(mockContractor);
        verify(feeService).getRuleCache();
    }

    @Test
	public void testCalculateInvoiceTotal() throws Exception {
		InvoiceItem anotherItem = mock(InvoiceItem.class);
		when(anotherItem.getAmount()).thenReturn(new BigDecimal(249.00));
		invoiceItems.add(anotherItem);
		calculateInvoiceTotal();

		assertTrue(invoiceTotal.equals(billingService.calculateInvoiceTotal(invoiceItems)));
	}

	@Test
	public void testExecute_Create_RenewIsSetForBidOnlyAccount() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(bidOnlyInvoiceFee.isFree()).thenReturn(false);
		when(listOnlyinvoiceFee.isFree()).thenReturn(true);

		billingService.createInvoiceWithItems(mockContractor, invoiceItems, user);

		verify(mockContractor).setRenew(true);
	}

	@Test
	public void testExecute_Create_RenewIsSetForListOnlyAccount() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(bidOnlyInvoiceFee.isFree()).thenReturn(true);
		when(listOnlyinvoiceFee.isFree()).thenReturn(false);

		billingService.createInvoiceWithItems(mockContractor, invoiceItems, user);

		verify(mockContractor).setRenew(true);
	}

	@Test
	public void testCreateInvoiceWithItems_Activation_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Activation).when(spy).billingStatus(mockContractor);

		Invoice invoice = spy.createInvoiceWithItems(mockContractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 7)));
	}

	@Test
	public void testCreateInvoiceWithItems_Reactivation_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Reactivation).when(spy).billingStatus(mockContractor);

		Invoice invoice = spy.createInvoiceWithItems(mockContractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 7)));
	}

	@Test
	public void testCreateInvoiceWithItems_Upgrade_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Upgrade).when(spy).billingStatus(mockContractor);

		Invoice invoice = spy.createInvoiceWithItems(mockContractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 7)));
	}

	@Test
	public void testCreateInvoiceWithItems_Renewal_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Renewal).when(spy).billingStatus(mockContractor);

		Invoice invoice = spy.createInvoiceWithItems(mockContractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), mockContractor.getPaymentExpires()));
	}

	@Test
	public void testCreateInvoiceWithItems_RenewalOverdue_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.RenewalOverdue).when(spy).billingStatus(mockContractor);

		Invoice invoice = spy.createInvoiceWithItems(mockContractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), mockContractor.getPaymentExpires()));
	}

	@Test
	public void testCreateInvoiceWithItems_Current_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Current).when(spy).billingStatus(mockContractor);

		Invoice invoice = spy.createInvoiceWithItems(mockContractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 30)));
	}

	@Test
	public void testCreateInvoiceWithItems_PICS_8597_RenewalBidOnly_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(bidOnlyInvoiceFee.isFree()).thenReturn(false);
		when(listOnlyinvoiceFee.isFree()).thenReturn(true);
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Renewal).when(spy).billingStatus(mockContractor);

		Invoice invoice = spy.createInvoiceWithItems(mockContractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), mockContractor.getPaymentExpires()));
	}

	@Test
	public void testCreateInvoiceWithItems_PICS_8597_RenewalListOnly_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(bidOnlyInvoiceFee.isFree()).thenReturn(true);
		when(listOnlyinvoiceFee.isFree()).thenReturn(false);
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Renewal).when(spy).billingStatus(mockContractor);

		Invoice invoice = spy.createInvoiceWithItems(mockContractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), mockContractor.getPaymentExpires()));
	}

	@Test
	public void testCreateInvoiceWithItems_PICS_8597_ActivationListOnly_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(bidOnlyInvoiceFee.isFree()).thenReturn(true);
		when(listOnlyinvoiceFee.isFree()).thenReturn(false);
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Activation).when(spy).billingStatus(mockContractor);

		Invoice invoice = spy.createInvoiceWithItems(mockContractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 7)));
	}

	@Test
	public void testCreateInvoiceWithItems_PICS_8597_ActivationBidOnly_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(bidOnlyInvoiceFee.isFree()).thenReturn(false);
		when(listOnlyinvoiceFee.isFree()).thenReturn(true);
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Activation).when(spy).billingStatus(mockContractor);

		Invoice invoice = spy.createInvoiceWithItems(mockContractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 7)));
	}

	@Test
	public void testCreateInvoiceWithItems_InvoicePropertiesAreSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		doCallRealMethod().when(invoiceModel).getSortedClientSiteList(any(ContractorAccount.class));
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Activation).when(spy).billingStatus(mockContractor);

		Invoice invoice = spy.createInvoiceWithItems(mockContractor, invoiceItems, user);

		assertThat("Test Operator", is(equalTo(invoice.getNotes())));
		assertThat(mockContractor, is(equalTo(invoice.getAccount())));
		assertThat(country.getCurrency(), is(equalTo(invoice.getCurrency())));
		assertThat(invoiceItems, is(equalTo(invoice.getItems())));
		assertThat(invoiceTotal, is(equalTo(invoice.getTotalAmount())));
		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 7)));
	}

	@Test
	public void testCreateInvoiceWithItems_MembershipTrueResultsInInvoiceNotesBeingSet() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		doCallRealMethod().when(invoiceModel).getSortedClientSiteList(any(ContractorAccount.class));
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Activation).when(spy).billingStatus(mockContractor);

		Invoice invoice = spy.createInvoiceWithItems(mockContractor, invoiceItems, user);

		assertThat("Test Operator", is(equalTo(invoice.getNotes())));
	}

	@Test
	public void testCreateInvoice_ZeroDollarInvoiceIsNull() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		// this will skip an activation fee
		when(mockContractor.getAccountLevel()).thenReturn(AccountLevel.BidOnly);
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Current).when(spy).billingStatus(mockContractor);

		Invoice invoice = spy.createInvoice(mockContractor, user);

		assertTrue(invoice == null);
	}

	@Mock
	OperatorAccount mockOA1;
	@Mock
	OperatorAccount mockOA2;
	Set<OperatorAccount> OAMocksSet = new HashSet<OperatorAccount>();

	@After
	public void clean() {
		OAMocksSet.clear();
	}

	@Test
	public void createInvoice_shouldCallApplyTax() throws Exception {
		BigDecimal amount = new BigDecimal(100);
        List<InvoiceFeeCountry> amountOverrides = new ArrayList<InvoiceFeeCountry>();
        when(override.getInvoiceFee()).thenReturn(invoiceFee);
        when(override.getAmount()).thenReturn(amount);
        amountOverrides.add(override);

        when(country.getAmountOverrides()).thenReturn(amountOverrides);

		when(invoiceFee.getFeeClass()).thenReturn(FeeClass.Activation);

		ContractorFee contractorFee = mock(ContractorFee.class);
		when(contractorFee.getCurrentLevel()).thenReturn(invoiceFee);
		when(invoiceFeeDAO.findByNumberOfOperatorsAndClass(any(FeeClass.class), anyInt())).thenReturn(invoiceFee);

		Map<FeeClass, ContractorFee> contractorFees = new HashMap<FeeClass, ContractorFee>();
		contractorFees.put(FeeClass.BidOnly, contractorFee);
		when(mockContractor.getAccountLevel()).thenReturn(AccountLevel.Full);
		when(mockContractor.getCountry()).thenReturn(country);
		when(mockContractor.getFees()).thenReturn(contractorFees);
		when(mockContractor.getStatus()).thenReturn(AccountStatus.Pending);

		when(auditDataDAO.findContractorAuditAnswers(anyInt(), anyInt(), anyInt())).thenReturn(null);
		preparePriorHistoryForContractor();
		billingService.createInvoice(mockContractor, BillingStatus.Current, user);

		verify(taxService).applyTax(any(Invoice.class));
	}

	@Test
	public void testAddRevRecIfAppropriate_Activation() throws Exception {
		Date invoiceCreationDate = new Date();
		InvoiceType invoiceType = InvoiceType.Activation;
		Date paymentExpiresDate = DateBean.addYears(invoiceCreationDate, 1);

		testAddRevRecIfAppropriate(invoiceCreationDate, invoiceType, paymentExpiresDate);

		validateInvoiceItemsForRevRec(invoiceCreationDate,paymentExpiresDate, invoiceType);
	}

	@Test
	public void testAddRevRecIfAppropriate_Upgrade() throws Exception {
		Date invoiceCreationDate = new Date();
		InvoiceType invoiceType = InvoiceType.Upgrade;
		Date paymentExpiresDate = twoHundredDaysFromNow;

		testAddRevRecIfAppropriate(invoiceCreationDate, invoiceType, paymentExpiresDate);

		validateInvoiceItemsForRevRec(invoiceCreationDate,paymentExpiresDate, invoiceType);
	}

	@Test
	 public void testAddRevRecIfAppropriate_Renewal() throws Exception {
		Date invoiceCreationDate = new Date();
		InvoiceType invoiceType = InvoiceType.Renewal;
		Date paymentExpiresDate = DateBean.addYears(invoiceCreationDate, 1);

		testAddRevRecIfAppropriate(invoiceCreationDate, invoiceType, paymentExpiresDate);

		validateInvoiceItemsForRevRec(invoiceCreationDate,paymentExpiresDate, invoiceType);
	}

	@Test
	public void testAddRevRecIfAppropriate_LateFee() throws Exception {
		Date invoiceCreationDate = new Date();
		InvoiceType invoiceType = InvoiceType.LateFee;
		Date paymentExpiresDate = DateBean.addMonths(invoiceCreationDate, 9);

		testAddRevRecIfAppropriate(invoiceCreationDate, invoiceType, paymentExpiresDate);

		validateInvoiceItemsForRevRec(invoiceCreationDate,paymentExpiresDate, invoiceType);
	}

	@Test
	public void testAddRevRecIfAppropriate_OtherFees() throws Exception {
		Date invoiceCreationDate = new Date();
		InvoiceType invoiceType = InvoiceType.OtherFees;
		Date paymentExpiresDate = twoHundredDaysFromNow;

		testAddRevRecIfAppropriate(invoiceCreationDate, invoiceType, paymentExpiresDate);

		validateInvoiceItemsForRevRec(invoiceCreationDate,paymentExpiresDate, invoiceType);
	}

	private void testAddRevRecIfAppropriate(Date invoiceCreationDate, InvoiceType invoiceType, Date paymentExpiresDate) throws Exception {
		prepForRevRecTesting(invoiceCreationDate);
		when(mockInvoice.getInvoiceType()).thenReturn(invoiceType);
		when(mockContractor.getPaymentExpires()).thenReturn(paymentExpiresDate);
		billingService.addRevRecInfoIfAppropriateToItems(mockInvoice);
	}

	private void validateInvoiceItemsForRevRec(Date invoiceCreationDate, Date paymentExpiresDate, InvoiceType invoiceType) {
		for (InvoiceItem invoiceItem1 : invoiceItems) {
			if (!FeeService.isRevRecDeferred(invoiceItem1.getInvoiceFee())) {
				assertNull(invoiceItem1.getRevenueStartDate());
				assertNull(invoiceItem1.getRevenueFinishDate());
				continue;
			}
			if (mockInvoice.getInvoiceType() == InvoiceType.Renewal) {
				assertEquals(DateBean.addMonths(invoiceCreationDate, 1),invoiceItem1.getRevenueStartDate());
			} else {
				assertEquals(invoiceCreationDate,invoiceItem1.getRevenueStartDate());
			}

			assertEquals(paymentExpiresDate,invoiceItem1.getRevenueFinishDate());
		}
	}

	private void prepForRevRecTesting(Date invoiceCreationDate) {
		invoiceItems.clear();

		InvoiceItem agItem = buildInvoiceItem(FeeClass.AuditGUARD);
		invoiceItems.add(agItem);

		InvoiceItem dgItem = buildInvoiceItem(FeeClass.DocuGUARD);
		invoiceItems.add(dgItem);

		InvoiceItem igItem = buildInvoiceItem(FeeClass.InsureGUARD);
		invoiceItems.add(igItem);

		InvoiceItem lateFeeItem = buildInvoiceItem(FeeClass.LateFee);
		invoiceItems.add(lateFeeItem);

		InvoiceItem taxItem = buildInvoiceItem(FeeClass.CanadianTax);
		invoiceItems.add(taxItem);

		InvoiceItem freeItem = buildInvoiceItem(FeeClass.Free);
		invoiceItems.add(freeItem);

		when(mockInvoice.getItems()).thenReturn(invoiceItems);

		when(mockInvoice.getCreationDate()).thenReturn(invoiceCreationDate);
		when(mockInvoice.getAccount()).thenReturn(mockContractor);
		preparePriorHistoryForContractor();
	}

	private void preparePriorHistoryForContractor() {
		List<Invoice> previousInvoiceListActivation = new ArrayList<Invoice>();
		List<InvoiceItem> previousInvoiceItemList = new ArrayList<InvoiceItem>();
		previousInvoiceListActivation.add(invoice);
		previousInvoiceItemList.add(previousInvoiceItem);
		when(invoice.getItems()).thenReturn(previousInvoiceItemList);
		when(previousInvoiceItem.getRevenueFinishDate()).thenReturn(DateBean.addMonths(new Date(), 10));
		when(invoice.getInvoiceType()).thenReturn(InvoiceType.Activation);
		when(mockContractor.getSortedInvoices()).thenReturn(previousInvoiceListActivation);
	}

	private InvoiceItem buildInvoiceItem(FeeClass feeClass) {
		InvoiceItem item = new InvoiceItem();
		InvoiceFee invoiceFee = new InvoiceFee();
		item.setInvoiceFee(invoiceFee);
		invoiceFee.setFeeClass(feeClass);
		return item;
	}

    @Test
    public void testHasCreditMemosForFullAmount_NoCreditMemos() throws Exception {
        assertFalse(BillingService.hasCreditMemosForFullAmount(mockInvoice));
    }

    @Test
    public void testHasCreditMemosForFullAmount_PartialCreditMemo() throws Exception {
        List<CreditMemoAppliedToInvoice> creditMemos = new ArrayList<>();
        creditMemos.add(creditMemoAppliedToInvoice);
        when(mockInvoice.getCreditMemos()).thenReturn(creditMemos);
        when(mockInvoice.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(creditMemoAppliedToInvoice.getAmount()).thenReturn(BigDecimal.ONE);
        assertFalse(BillingService.hasCreditMemosForFullAmount(mockInvoice));
    }

    @Test
    public void testHasCreditMemosForFullAmount_FullCreditMemo() throws Exception {
        List<CreditMemoAppliedToInvoice> creditMemos = new ArrayList<>();
        creditMemos.add(creditMemoAppliedToInvoice);
        when(mockInvoice.getCreditMemos()).thenReturn(creditMemos);
        when(mockInvoice.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(creditMemoAppliedToInvoice.getAmount()).thenReturn(BigDecimal.TEN);
        assertTrue(BillingService.hasCreditMemosForFullAmount(mockInvoice));
    }

    @Test
    public void testHasCreditMemosForFullAmount_MultipleCreditMemo() throws Exception {
        List<CreditMemoAppliedToInvoice> creditMemos = new ArrayList<>();
        creditMemos.add(creditMemoAppliedToInvoice);
        creditMemos.add(creditMemoAppliedToInvoice2);
        when(mockInvoice.getCreditMemos()).thenReturn(creditMemos);
        when(mockInvoice.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(creditMemoAppliedToInvoice.getAmount()).thenReturn(BigDecimal.ONE);
        when(creditMemoAppliedToInvoice2.getAmount()).thenReturn(new BigDecimal(9));
        assertTrue(BillingService.hasCreditMemosForFullAmount(mockInvoice));
    }

    @Test
    public void testConvertBillingStatusToInvoiceType_ActivationItem() throws Exception {
        when(mockInvoice.getItems()).thenReturn(invoiceItems);
        when(invoiceFee.getFeeClass()).thenReturn(FeeClass.Activation);
        InvoiceType invoiceType = Whitebox.invokeMethod(billingService, "convertBillingStatusToInvoiceType", mockInvoice, BillingStatus.Renewal);

        assertEquals(InvoiceType.Activation, invoiceType);
    }

    @Test
    public void testConvertBillingStatusToInvoiceType_ReactivationItem() throws Exception {
        when(mockInvoice.getItems()).thenReturn(invoiceItems);
        when(invoiceFee.getFeeClass()).thenReturn(FeeClass.Reactivation);
        InvoiceType invoiceType = Whitebox.invokeMethod(billingService, "convertBillingStatusToInvoiceType", mockInvoice, BillingStatus.Renewal);

        assertEquals(InvoiceType.Activation, invoiceType);
    }

    @Test
    public void testgetUpgradedFees() throws Exception {
        setupCreateInvoiceWithItemsTestsCommon();
        fees.put(FeeClass.AuditGUARD, upgradeFee);
        fees.put(FeeClass.DocuGUARD, new ContractorFee());

        when(upgradeFee.getNewLevel()).thenReturn(upgradeinvoiceFee);
        when(upgradeFee.isUpgrade()).thenReturn(true);
        when(upgradeFee.getFeeClass()).thenReturn(FeeClass.AuditGUARD);
        when(upgradeinvoiceFee.isFree()).thenReturn(false);

        List<ContractorFee> upgrades = Whitebox.invokeMethod(billingService, "getUpgradedFees", mockContractor);

        assertEquals(1,upgrades.size());
        ContractorFee upgradedAG = upgrades.get(0);
        assertEquals(FeeClass.AuditGUARD, upgradedAG.getFeeClass());
    }

    @Test
    public void testGetBillingStatus_IsUpgrade() throws Exception {
        upgradeTrueTestsCommon();

        BillingStatus billingStatus = billingService.billingStatus(mockContractor);

        assertTrue(billingStatus.isUpgrade());
    }

    @Test
    public void testGetBillingStatus_HasUpgradeFeeClassButIsBidOnly() throws Exception {
        upgradeTrueTestsCommon();

        when(mockContractor.getAccountLevel()).thenReturn(AccountLevel.BidOnly);
        when(bidOnlyInvoiceFee.isFree()).thenReturn(false);

        BillingStatus billingStatus = billingService.billingStatus(mockContractor);

        assertTrue(billingStatus.isRenewal());
    }

    private void upgradeTrueTestsCommon() {
        billingStatusCommon(46);

        when(mockInvoice.getStatus()).thenReturn(TransactionStatus.Unpaid);
        when(mockInvoice.getDueDate()).thenReturn(DateBean.addDays(new Date(), -10));

        when(upgradeFee.isUpgrade()).thenReturn(true);
        fees.put(FeeClass.EmployeeGUARD, upgradeFee);
    }

    @Test
    public void testGetBillingStatus_NoPastDueInvoices_RenewalOverdue() throws Exception {
        billingStatusCommon(-10);

        BillingStatus billingStatus = billingService.billingStatus(mockContractor);

        assertTrue(billingStatus.isRenewalOverdue());
    }

    @Test
    public void testGetBillingStatus_NoPastDueInvoices_Renewal() throws Exception {
        billingStatusCommon(10);

        BillingStatus billingStatus = billingService.billingStatus(mockContractor);

        assertTrue(billingStatus.isRenewal());
    }

    @Test
    public void testGetBillingStatus_NoPastDueInvoices_CurrentOver45DaysOut() throws Exception {
        billingStatusCommon(46);

        BillingStatus billingStatus = billingService.billingStatus(mockContractor);

        assertTrue(billingStatus.isCurrent());
    }

    @Test
    public void testGetBillingStatus_PastDueInvoicesIsPastDue() throws Exception {
        billingStatusCommon(46);
        when(mockContractor.hasPastDueInvoice()).thenReturn(true);

        BillingStatus billingStatus = billingService.billingStatus(mockContractor);

        assertTrue(billingStatus.isPastDue());
    }

    @Test
    public void testGetBillingStatus_90DaysPastRenewalDateAndRenewFalseIsCancelled() throws Exception {
        billingStatusCommon(-91);
        when(mockContractor.isRenew()).thenReturn(false);

        BillingStatus billingStatus = billingService.billingStatus(mockContractor);

        assertTrue(billingStatus.isCancelled());
    }

    @Test
    public void testGetBillingStatus_90DaysPastRenewalDateAndRenewTrueIsReactivation() throws Exception {
        billingStatusCommon(-91);

        BillingStatus billingStatus = billingService.billingStatus(mockContractor);

        assertTrue(billingStatus.isReactivation());
    }

    @Test
    public void testGetBillingStatus_DeactivatedRenewIsReactivation() throws Exception {
        billingStatusCommon(30);
        when(mockContractor.getStatus()).thenReturn(AccountStatus.Deactivated);
        when(mockContractor.pendingOrActive()).thenReturn(false);

        BillingStatus billingStatus = billingService.billingStatus(mockContractor);

        assertTrue(billingStatus.isReactivation());
    }

    @Test
    public void testGetBillingStatus_DeactivatedNonRenewIsCancelled() throws Exception {
        billingStatusCommon(30);
        when(mockContractor.getStatus()).thenReturn(AccountStatus.Deactivated);
        when(mockContractor.pendingOrActive()).thenReturn(false);
        when(mockContractor.isRenew()).thenReturn(false);

        BillingStatus billingStatus = billingService.billingStatus(mockContractor);

        assertTrue(billingStatus.isCancelled());
    }

    @Test
    public void testGetBillingStatus_ActiveFullNewMemberIsActivation() throws Exception {
        billingStatusCommon(30);
        when(mockContractor.newMember()).thenReturn(true);

        BillingStatus billingStatus = billingService.billingStatus(mockContractor);

        assertTrue(billingStatus.isActivation());
    }

    @Test
    public void testGetBillingStatus_PendingFullNewMemberIsActivation() throws Exception {
        billingStatusCommon(30);
        when(mockContractor.newMember()).thenReturn(true);
        when(mockContractor.getStatus()).thenReturn(AccountStatus.Pending);

        BillingStatus billingStatus = billingService.billingStatus(mockContractor);

        assertTrue(billingStatus.isActivation());
    }

    @Test
    public void testGetBillingStatus_NoMustPayIsCurrent() throws Exception {
        billingStatusCommon(30);
        when(mockContractor.isMustPayB()).thenReturn(false);

        BillingStatus billingStatus = billingService.billingStatus(mockContractor);

        assertTrue(billingStatus.isCurrent());
    }

    @Test
    public void testGetBillingStatus_NoPayingFacilitiesIsCurrent() throws Exception {
        billingStatusCommon(30);
        when(mockContractor.getPayingFacilities()).thenReturn(0);

        BillingStatus billingStatus = billingService.billingStatus(mockContractor);

        assertTrue(billingStatus.isCurrent());
    }

    @Test
    public void testGetBillingStatus_DemoAccountIsCurrent() throws Exception {
        billingStatusCommon(30);
        when(mockContractor.getStatus()).thenReturn(AccountStatus.Demo);
        when(mockContractor.pendingOrActive()).thenReturn(false);

        BillingStatus billingStatus = billingService.billingStatus(mockContractor);

        assertTrue(billingStatus.isCurrent());
    }

    @Test
    public void testGetBillingStatus_DeletedAccountIsCurrent() throws Exception {
        billingStatusCommon(30);
        when(mockContractor.getStatus()).thenReturn(AccountStatus.Deleted);

        BillingStatus billingStatus = billingService.billingStatus(mockContractor);

        assertTrue(billingStatus.isCurrent());
    }

    private void billingStatusCommon(int paymentExpiresDays) {
        when(mockInvoice.getStatus()).thenReturn(TransactionStatus.Paid);
        when(mockInvoice.getDueDate()).thenReturn(DateBean.addDays(new Date(), -10));

        when(mockContractor.getPaymentExpires()).thenReturn(DateBean.addDays(new Date(), paymentExpiresDays));
        // must have at least one paying facility or it'll be current
        when(mockContractor.getPayingFacilities()).thenReturn(1);
        when(mockContractor.getStatus()).thenReturn(AccountStatus.Active);
        when(mockContractor.pendingRequestedOrActive()).thenReturn(true);
        when(mockContractor.isRenew()).thenReturn(true);
        when(mockContractor.isMustPayB()).thenReturn(true);
        when(mockContractor.getFees()).thenReturn(fees);
        when(mockContractor.getAccountLevel()).thenReturn(AccountLevel.Full);

        when(upgradeFee.getCurrentLevel()).thenReturn(upgradeinvoiceFee);
    }

    @Test
    public void testGenerateInvoice() throws Exception {
        when(mockContractor.getPayingFacilities()).thenReturn(20);
        Invoice invoice1 = Whitebox.invokeMethod(billingService, "generateInvoice", mockContractor, invoiceItems, user, BillingStatus.Renewal, BigDecimal.TEN, BigDecimal.TEN);

        assertEquals(20, invoice1.getPayingFacilities());
    }

    @Test
    public void testCreateLineItem() throws Exception {
        when(invoiceFeeDAO.findByNumberOfOperatorsAndClass(any(FeeClass.class), anyInt())).thenReturn(invoiceFee);
        when(invoiceFee.getFeeClass()).thenReturn(FeeClass.Activation);
        InvoiceItem item = Whitebox.invokeMethod(billingService, "createLineItem", mockContractor, FeeClass.Activation, 20);

        assertEquals(item.getAmount(), item.getOriginalAmount());
    }

    @Test
    public void testAddYearlyItems() throws Exception {
        invoiceItems = new ArrayList<>();
        when(mockContractor.getFees()).thenReturn(fees);
        when(invoiceFee.getFeeClass()).thenReturn(FeeClass.AuditGUARD);

        fees.remove(FeeClass.ListOnly);
        fees.remove(FeeClass.BidOnly);
        ContractorFee dg = new ContractorFee();
        dg.setNewLevel(invoiceFee);
        dg.setNewAmount(BigDecimal.TEN);
        fees.put(FeeClass.DocuGUARD, dg);
        ContractorFee ig = new ContractorFee();
        ig.setNewLevel(invoiceFee);
        ig.setNewAmount(BigDecimal.TEN);
        fees.put(FeeClass.InsureGUARD, ig);
        ContractorFee ag = new ContractorFee();
        ag.setNewLevel(invoiceFee);
        ag.setNewAmount(BigDecimal.TEN);
        fees.put(FeeClass.AuditGUARD, ag);
        ContractorFee eg = new ContractorFee();
        eg.setNewLevel(invoiceFee);
        eg.setNewAmount(BigDecimal.TEN);
        fees.put(FeeClass.EmployeeGUARD, eg);

        Whitebox.invokeMethod(billingService, "addYearlyItems", invoiceItems, mockContractor, new Date(), BillingStatus.Renewal);

        assertEquals(4, invoiceItems.size());
        for (InvoiceItem item : invoiceItems) {
            assertEquals(BigDecimal.TEN, item.getAmount());
            assertEquals(item.getAmount(), item.getOriginalAmount());
        }
    }

    @Test
    public void testAddProratedUpgradeItems() throws Exception {
        invoiceItems = new ArrayList<>();
        List<ContractorFee> upgrades = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -6);
        when(mockContractor.getLastUpgradeDate()).thenReturn(cal.getTime());
        cal.add(Calendar.MONTH, 6);
        when(mockContractor.getPaymentExpires()).thenReturn(cal.getTime());
        when(mockContractor.getAccountLevel()).thenReturn(AccountLevel.Full);
        when(mockContractor.getCountry()).thenReturn(country);
        when(country.getCurrency()).thenReturn(Currency.USD);

        ContractorFee dg = new ContractorFee();
        dg.setNewLevel(invoiceFee);
        dg.setNewAmount(BigDecimal.TEN);
        dg.setFeeClass(FeeClass.DocuGUARD);
        upgrades.add(dg);
        ContractorFee ig = new ContractorFee();
        ig.setNewLevel(invoiceFee);
        ig.setNewAmount(BigDecimal.TEN);
        ig.setFeeClass(FeeClass.InsureGUARD);
        upgrades.add(ig);

        Whitebox.invokeMethod(billingService, "addProratedUpgradeItems", mockContractor, invoiceItems, upgrades, user);

        assertEquals(2, invoiceItems.size());
        for (InvoiceItem item : invoiceItems) {
            assertEquals(new BigDecimal(5), item.getAmount());
            assertEquals(BigDecimal.TEN, item.getOriginalAmount());
        }
    }

	@Test
    public void testBillingStatus_HasActivationFee() throws Exception {
        ContractorAccount account = new ContractorAccount();
        account.setMustPay("Yes");
        account.setPayingFacilities(1);
        account.setInvoices(invoices);
        account.setStatus(AccountStatus.Requested);
        account.setAccountLevel(AccountLevel.Full);

        BillingStatus status = billingService.billingStatus(account);

        assertEquals(BillingStatus.Activation, status);
    }

	private Date calculateInvoiceItemRevRecFinishDateForTest(Date testInvoiceCreationDate, Date paymentExpires, InvoiceType invoiceType) throws Exception {
		when(mockContractor.getPaymentExpires()).thenReturn(paymentExpires);
		when(mockInvoice.getCreationDate()).thenReturn(testInvoiceCreationDate);
		when(mockInvoice.getInvoiceType()).thenReturn(invoiceType);
		return billingService.calculateInvoiceItemRevRecFinishDateFor(mockInvoice,mockContractor);
	}

	@Test
	public void testCalculateInvoiceItemRevRecFinishDateFor_ActivationInvoice_PaymentExpiresIsNull() throws Exception {
		Date testInvoiceCreationDate = new Date();
		Date paymentExpires = null;
		InvoiceType invoiceType = InvoiceType.Activation;
		Date expected = DateBean.addYears(testInvoiceCreationDate,1);
		Date actual = calculateInvoiceItemRevRecFinishDateForTest(testInvoiceCreationDate, paymentExpires, invoiceType);
		assertEquals(expected,actual);
	}

	@Test
	public void testCalculateInvoiceItemRevRecFinishDateFor_ActivationInvoice_PaymentExpiresIsNotNull() throws Exception {
		Date testInvoiceCreationDate = new Date();
		Date paymentExpires = DateBean.addYears(testInvoiceCreationDate, 1);
		InvoiceType invoiceType = InvoiceType.Activation;
		Date expected = paymentExpires;
		Date actual = calculateInvoiceItemRevRecFinishDateForTest(testInvoiceCreationDate, paymentExpires, invoiceType);
		assertEquals(expected,actual);
	}

	@Test
	public void testCalculateInvoiceItemRevRecFinishDateFor_RenewalInvoice_PaymentExpiresAlreadyUpdatedAndIsMoreThanAYearOut() throws Exception {
		Date testInvoiceCreationDate = new Date();
		Date paymentExpires = DateBean.addDays(DateBean.addYears(testInvoiceCreationDate, 1),30);
		InvoiceType invoiceType = InvoiceType.Renewal;
		Date expected = paymentExpires;
		Date actual = calculateInvoiceItemRevRecFinishDateForTest(testInvoiceCreationDate, paymentExpires, invoiceType);
		assertEquals(expected,actual);
	}

	@Test
	 public void testCalculateInvoiceItemRevRecFinishDateFor_RenewalInvoice_PaymentExpiresNotYetUpdatedAndIsTwentyDaysOut() throws Exception {
		Date testInvoiceCreationDate = new Date();
		Date paymentExpires = DateBean.addDays(testInvoiceCreationDate,20);
		InvoiceType invoiceType = InvoiceType.Renewal;
		Date expected = DateBean.addYears(paymentExpires,1);
		Date actual = calculateInvoiceItemRevRecFinishDateForTest(testInvoiceCreationDate, paymentExpires, invoiceType);
		assertEquals(expected,actual);
	}

	@Test
	public void testCalculateInvoiceItemRevRecFinishDateFor_RenewalInvoice_PaymentExpiresNotYetUpdatedAndIsThirtyDaysOut() throws Exception {
		Date testInvoiceCreationDate = new Date();
		Date paymentExpires = DateBean.addDays(testInvoiceCreationDate,30);
		Date expected = DateBean.addYears(paymentExpires,1);
		InvoiceType invoiceType = InvoiceType.Renewal;
		Date actual = calculateInvoiceItemRevRecFinishDateForTest(testInvoiceCreationDate, paymentExpires, invoiceType);
		assertEquals(expected,actual);
	}

	@Test
	public void testCalculateInvoiceItemRevRecFinishDateFor_UpgradeInvoice_PaymentExpiresIsSixtyDaysOut() throws Exception {
		Date testInvoiceCreationDate = new Date();
		Date paymentExpires = DateBean.addDays(testInvoiceCreationDate,60);
		Date expected = paymentExpires;
		InvoiceType invoiceType = InvoiceType.Upgrade;
		Date actual = calculateInvoiceItemRevRecFinishDateForTest(testInvoiceCreationDate, paymentExpires, invoiceType);
		assertEquals(expected,actual);
	}

	@Test
	public void testCalculateInvoiceItemRevRecFinishDateFor_UpgradeInvoice_PaymentExpiresIsTwoHundredDaysOut() throws Exception {
		Date testInvoiceCreationDate = new Date();
		Date paymentExpires = DateBean.addDays(testInvoiceCreationDate,200);
		Date expected = paymentExpires;
		InvoiceType invoiceType = InvoiceType.Upgrade;
		Date actual = calculateInvoiceItemRevRecFinishDateForTest(testInvoiceCreationDate, paymentExpires, invoiceType);
		assertEquals(expected,actual);
	}

	@Test
	public void testCalculateInvoiceItemRevRecFinishDateFor_UpgradeInvoice_PaymentExpiresIsMoreThanAYearOut() throws Exception {
		Date testInvoiceCreationDate = new Date();
		Date paymentExpires = DateBean.addDays(testInvoiceCreationDate,380);
		Date expected = paymentExpires;
		InvoiceType invoiceType = InvoiceType.Upgrade;
		Date actual = calculateInvoiceItemRevRecFinishDateForTest(testInvoiceCreationDate, paymentExpires, invoiceType);
		assertEquals(expected,actual);
	}

	@Test
	public void testCalculateInvoiceItemRevRecFinishDateFor_OtherFeesInvoice_PaymentExpiresIsSixtyDaysOut() throws Exception {
		Date testInvoiceCreationDate = new Date();
		Date paymentExpires = DateBean.addDays(testInvoiceCreationDate,60);
		Date expected = paymentExpires;
		InvoiceType invoiceType = InvoiceType.OtherFees;
		Date actual = calculateInvoiceItemRevRecFinishDateForTest(testInvoiceCreationDate, paymentExpires, invoiceType);
		assertEquals(expected,actual);
	}

	@Test
	public void testCalculateInvoiceItemRevRecFinishDateFor_OtherFeesInvoice_PaymentExpiresIsTwoHundredDaysOut() throws Exception {
		Date testInvoiceCreationDate = new Date();
		Date paymentExpires = DateBean.addDays(testInvoiceCreationDate,200);
		Date expected = paymentExpires;
		InvoiceType invoiceType = InvoiceType.OtherFees;
		Date actual = calculateInvoiceItemRevRecFinishDateForTest(testInvoiceCreationDate, paymentExpires, invoiceType);
		assertEquals(expected,actual);
	}

	@Test
	public void testCalculateInvoiceItemRevRecFinishDateFor_OtherFeesInvoice_PaymentExpiresIsMoreThanAYearOut() throws Exception {
		Date testInvoiceCreationDate = new Date();
		Date paymentExpires = DateBean.addDays(testInvoiceCreationDate,380);
		Date expected = paymentExpires;
		InvoiceType invoiceType = InvoiceType.OtherFees;
		Date actual = calculateInvoiceItemRevRecFinishDateForTest(testInvoiceCreationDate, paymentExpires, invoiceType);
		assertEquals(expected,actual);
	}

	@Test
	public void testCalculateInvoiceItemRevRecFinishDateFor_LateFeeInvoice_PaymentExpiresIs275DaysOut() throws Exception {
		Date testInvoiceCreationDate = new Date();
		Date paymentExpires = DateBean.addDays(testInvoiceCreationDate,275);
		Date expected = paymentExpires;
		InvoiceType invoiceType = InvoiceType.LateFee;
		Date actual = calculateInvoiceItemRevRecFinishDateForTest(testInvoiceCreationDate, paymentExpires, invoiceType);
		assertEquals(expected,actual);
	}

    @Test
    public void testActivateContractor_DeclinedAccount() throws Exception {
        billingStatusCommon(30);
        setupInvoiceAndItems();
        when(mockContractor.getStatus()).thenReturn(AccountStatus.Declined);
        when(invoice.getStatus()).thenReturn(TransactionStatus.Paid);
        when(invoice.getItems()).thenReturn(invoiceItems);
        when(invoiceFee.isActivation()).thenReturn(true);

        boolean activated = billingService.activateContractor(mockContractor, invoice);

        assertTrue(activated);
        verify(accountDAO).save(mockContractor);
    }

}
