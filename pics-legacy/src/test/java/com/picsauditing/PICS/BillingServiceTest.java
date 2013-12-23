package com.picsauditing.PICS;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.InvoiceItemDAO;
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
import java.math.RoundingMode;
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
	private ContractorAccount contractor;
	@Mock
	private User user;
	@Mock
	private Country country;
	@Mock
	private BusinessUnit businessUnit;
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
	private Invoice invoice;
	@Mock
	private Invoice previousInvoiceActivation;
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
		Whitebox.setInternalState(billingService, "feeDAO", invoiceFeeDAO);
		Whitebox.setInternalState(billingService, "invoiceModel", invoiceModel);
		Whitebox.setInternalState(billingService, "auditDataDAO", auditDataDAO);
		Whitebox.setInternalState(billingService, "invoiceItemDAO", invoiceItemDAO);
		AccountingSystemSynchronization.setSapAppPropertyUtil(sapAppPropertyUtil);
		assert (OAMocksSet.isEmpty());

		setupInvoiceAndItems();
		setupStandardFees(true, true);
		calculateInvoiceTotal();
		when(contractor.getCountry()).thenReturn(country);
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
		when(contractor.getId()).thenReturn(123);
		when(contractor.getCountry()).thenReturn(country);
		when(contractor.getFees()).thenReturn(fees);
		when(contractor.getStatus()).thenReturn(AccountStatus.Active);
		when(contractor.getInvoices()).thenReturn(invoices);
		when(contractor.getPaymentExpires()).thenReturn(twoHundredDaysFromNow);

		when(invoiceFee.isMembership()).thenReturn(true);

		List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>();
        contractorOperators.add(contractorOperator);
		when(contractor.getNonCorporateOperators()).thenReturn(contractorOperators);
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

		billingService.createInvoiceWithItems(contractor, invoiceItems, user);

		verify(contractor).setRenew(true);
	}

	@Test
	public void testExecute_Create_RenewIsSetForListOnlyAccount() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(bidOnlyInvoiceFee.isFree()).thenReturn(true);
		when(listOnlyinvoiceFee.isFree()).thenReturn(false);

		billingService.createInvoiceWithItems(contractor, invoiceItems, user);

		verify(contractor).setRenew(true);
	}

	@Test
	public void testCreateInvoiceWithItems_Activation_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Activation).when(spy).billingStatus(contractor);

		Invoice invoice = spy.createInvoiceWithItems(contractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 7)));
	}

	@Test
	public void testCreateInvoiceWithItems_Reactivation_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Reactivation).when(spy).billingStatus(contractor);

		Invoice invoice = spy.createInvoiceWithItems(contractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 7)));
	}

	@Test
	public void testCreateInvoiceWithItems_Upgrade_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Upgrade).when(spy).billingStatus(contractor);

		Invoice invoice = spy.createInvoiceWithItems(contractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 7)));
	}

	@Test
	public void testCreateInvoiceWithItems_Renewal_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Renewal).when(spy).billingStatus(contractor);

		Invoice invoice = spy.createInvoiceWithItems(contractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), contractor.getPaymentExpires()));
	}

	@Test
	public void testCreateInvoiceWithItems_RenewalOverdue_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.RenewalOverdue).when(spy).billingStatus(contractor);

		Invoice invoice = spy.createInvoiceWithItems(contractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), contractor.getPaymentExpires()));
	}

	@Test
	public void testCreateInvoiceWithItems_Current_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Current).when(spy).billingStatus(contractor);

		Invoice invoice = spy.createInvoiceWithItems(contractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 30)));
	}

	@Test
	public void testCreateInvoiceWithItems_PICS_8597_RenewalBidOnly_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(bidOnlyInvoiceFee.isFree()).thenReturn(false);
		when(listOnlyinvoiceFee.isFree()).thenReturn(true);
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Renewal).when(spy).billingStatus(contractor);

		Invoice invoice = spy.createInvoiceWithItems(contractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), contractor.getPaymentExpires()));
	}

	@Test
	public void testCreateInvoiceWithItems_PICS_8597_RenewalListOnly_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(bidOnlyInvoiceFee.isFree()).thenReturn(true);
		when(listOnlyinvoiceFee.isFree()).thenReturn(false);
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Renewal).when(spy).billingStatus(contractor);

		Invoice invoice = spy.createInvoiceWithItems(contractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), contractor.getPaymentExpires()));
	}

	@Test
	public void testCreateInvoiceWithItems_PICS_8597_ActivationListOnly_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(bidOnlyInvoiceFee.isFree()).thenReturn(true);
		when(listOnlyinvoiceFee.isFree()).thenReturn(false);
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Activation).when(spy).billingStatus(contractor);

		Invoice invoice = spy.createInvoiceWithItems(contractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 7)));
	}

	@Test
	public void testCreateInvoiceWithItems_PICS_8597_ActivationBidOnly_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(bidOnlyInvoiceFee.isFree()).thenReturn(false);
		when(listOnlyinvoiceFee.isFree()).thenReturn(true);
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Activation).when(spy).billingStatus(contractor);

		Invoice invoice = spy.createInvoiceWithItems(contractor, invoiceItems, user);

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 7)));
	}

	@Test
	public void testCreateInvoiceWithItems_InvoicePropertiesAreSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		doCallRealMethod().when(invoiceModel).getSortedClientSiteList(any(ContractorAccount.class));
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Activation).when(spy).billingStatus(contractor);

		Invoice invoice = spy.createInvoiceWithItems(contractor, invoiceItems, user);

		assertThat("Test Operator", is(equalTo(invoice.getNotes())));
		assertThat(contractor, is(equalTo(invoice.getAccount())));
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
        doReturn(BillingStatus.Activation).when(spy).billingStatus(contractor);

		Invoice invoice = spy.createInvoiceWithItems(contractor, invoiceItems, user);

		assertThat("Test Operator", is(equalTo(invoice.getNotes())));
	}

	@Test
	public void testCreateInvoice_ZeroDollarInvoiceIsNull() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		// this will skip an activation fee
		when(contractor.getAccountLevel()).thenReturn(AccountLevel.BidOnly);
        BillingService spy = spy(billingService);
        doReturn(BillingStatus.Current).when(spy).billingStatus(contractor);

		Invoice invoice = spy.createInvoice(contractor, user);

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
		when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);
		when(contractor.getCountry()).thenReturn(country);
		when(contractor.getFees()).thenReturn(contractorFees);
		when(contractor.getStatus()).thenReturn(AccountStatus.Pending);

		when(auditDataDAO.findContractorAuditAnswers(anyInt(), anyInt(), anyInt())).thenReturn(null);
		preparePriorHistoryForContractor();
		billingService.createInvoice(contractor, BillingStatus.Current, user);

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
		when(invoice.getInvoiceType()).thenReturn(invoiceType);
		when(contractor.getPaymentExpires()).thenReturn(paymentExpiresDate);
		billingService.addRevRecInfoIfAppropriateToItems(invoice);
	}

	private void validateInvoiceItemsForRevRec(Date invoiceCreationDate, Date paymentExpiresDate, InvoiceType invoiceType) {
		for (InvoiceItem invoiceItem1 : invoiceItems) {
			if (!FeeService.isRevRecDeferred(invoiceItem1.getInvoiceFee())) {
				assertNull(invoiceItem1.getRevenueStartDate());
				assertNull(invoiceItem1.getRevenueFinishDate());
				continue;
			}
			if (invoice.getInvoiceType() == InvoiceType.Renewal) {
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

		when(invoice.getItems()).thenReturn(invoiceItems);

		when(invoice.getCreationDate()).thenReturn(invoiceCreationDate);
		when(invoice.getAccount()).thenReturn(contractor);
		preparePriorHistoryForContractor();
	}

	private void preparePriorHistoryForContractor() {
		List<Invoice> previousInvoiceListActivation = new ArrayList<Invoice>();
		List<InvoiceItem> previousInvoiceItemList = new ArrayList<InvoiceItem>();
		previousInvoiceListActivation.add(previousInvoiceActivation);
		previousInvoiceItemList.add(previousInvoiceItem);
		when(previousInvoiceActivation.getItems()).thenReturn(previousInvoiceItemList);
		when(previousInvoiceItem.getRevenueFinishDate()).thenReturn(DateBean.addMonths(new Date(), 10));
		when(previousInvoiceActivation.getInvoiceType()).thenReturn(InvoiceType.Activation);
		when(contractor.getSortedInvoices()).thenReturn(previousInvoiceListActivation);
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
        assertFalse(BillingService.hasCreditMemosForFullAmount(invoice));
    }

    @Test
    public void testHasCreditMemosForFullAmount_PartialCreditMemo() throws Exception {
        List<CreditMemoAppliedToInvoice> creditMemos = new ArrayList<>();
        creditMemos.add(creditMemoAppliedToInvoice);
        when(invoice.getCreditMemos()).thenReturn(creditMemos);
        when(invoice.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(creditMemoAppliedToInvoice.getAmount()).thenReturn(BigDecimal.ONE);
        assertFalse(BillingService.hasCreditMemosForFullAmount(invoice));
    }

    @Test
    public void testHasCreditMemosForFullAmount_FullCreditMemo() throws Exception {
        List<CreditMemoAppliedToInvoice> creditMemos = new ArrayList<>();
        creditMemos.add(creditMemoAppliedToInvoice);
        when(invoice.getCreditMemos()).thenReturn(creditMemos);
        when(invoice.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(creditMemoAppliedToInvoice.getAmount()).thenReturn(BigDecimal.TEN);
        assertTrue(BillingService.hasCreditMemosForFullAmount(invoice));
    }

    @Test
    public void testHasCreditMemosForFullAmount_MultipleCreditMemo() throws Exception {
        List<CreditMemoAppliedToInvoice> creditMemos = new ArrayList<>();
        creditMemos.add(creditMemoAppliedToInvoice);
        creditMemos.add(creditMemoAppliedToInvoice2);
        when(invoice.getCreditMemos()).thenReturn(creditMemos);
        when(invoice.getTotalAmount()).thenReturn(BigDecimal.TEN);
        when(creditMemoAppliedToInvoice.getAmount()).thenReturn(BigDecimal.ONE);
        when(creditMemoAppliedToInvoice2.getAmount()).thenReturn(new BigDecimal(9));
        assertTrue(BillingService.hasCreditMemosForFullAmount(invoice));
    }

    @Test
    public void testConvertBillingStatusToInvoiceType_ActivationItem() throws Exception {
        when(invoice.getItems()).thenReturn(invoiceItems);
        when(invoiceFee.getFeeClass()).thenReturn(FeeClass.Activation);
        InvoiceType invoiceType = Whitebox.invokeMethod(billingService, "convertBillingStatusToInvoiceType", invoice, BillingStatus.Renewal);

        assertEquals(InvoiceType.Activation, invoiceType);
    }

    @Test
    public void testConvertBillingStatusToInvoiceType_ReactivationItem() throws Exception {
        when(invoice.getItems()).thenReturn(invoiceItems);
        when(invoiceFee.getFeeClass()).thenReturn(FeeClass.Reactivation);
        InvoiceType invoiceType = Whitebox.invokeMethod(billingService, "convertBillingStatusToInvoiceType", invoice, BillingStatus.Renewal);

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

        List<ContractorFee> upgrades = Whitebox.invokeMethod(billingService, "getUpgradedFees", contractor);

        assertEquals(1,upgrades.size());
        ContractorFee upgradedAG = upgrades.get(0);
        assertEquals(FeeClass.AuditGUARD, upgradedAG.getFeeClass());
    }

    @Test
    public void testGetBillingStatus_IsUpgrade() throws Exception {
        upgradeTrueTestsCommon();

        BillingStatus billingStatus = billingService.billingStatus(contractor);

        assertTrue(billingStatus.isUpgrade());
    }

    @Test
    public void testGetBillingStatus_HasUpgradeFeeClassButIsBidOnly() throws Exception {
        upgradeTrueTestsCommon();

        when(contractor.getAccountLevel()).thenReturn(AccountLevel.BidOnly);
        when(bidOnlyInvoiceFee.isFree()).thenReturn(false);

        BillingStatus billingStatus = billingService.billingStatus(contractor);

        assertTrue(billingStatus.isRenewal());
    }

    private void upgradeTrueTestsCommon() {
        billingStatusCommon(46);

        when(invoice.getStatus()).thenReturn(TransactionStatus.Unpaid);
        when(invoice.getDueDate()).thenReturn(DateBean.addDays(new Date(), -10));

        when(upgradeFee.isUpgrade()).thenReturn(true);
        fees.put(FeeClass.EmployeeGUARD, upgradeFee);
    }

    @Test
    public void testGetBillingStatus_NoPastDueInvoices_RenewalOverdue() throws Exception {
        billingStatusCommon(-10);

        BillingStatus billingStatus = billingService.billingStatus(contractor);

        assertTrue(billingStatus.isRenewalOverdue());
    }

    @Test
    public void testGetBillingStatus_NoPastDueInvoices_Renewal() throws Exception {
        billingStatusCommon(10);

        BillingStatus billingStatus = billingService.billingStatus(contractor);

        assertTrue(billingStatus.isRenewal());
    }

    @Test
    public void testGetBillingStatus_NoPastDueInvoices_CurrentOver45DaysOut() throws Exception {
        billingStatusCommon(46);

        BillingStatus billingStatus = billingService.billingStatus(contractor);

        assertTrue(billingStatus.isCurrent());
    }

    @Test
    public void testGetBillingStatus_PastDueInvoicesIsPastDue() throws Exception {
        billingStatusCommon(46);
        when(contractor.hasPastDueInvoice()).thenReturn(true);

        BillingStatus billingStatus = billingService.billingStatus(contractor);

        assertTrue(billingStatus.isPastDue());
    }

    @Test
    public void testGetBillingStatus_90DaysPastRenewalDateAndRenewFalseIsCancelled() throws Exception {
        billingStatusCommon(-91);
        when(contractor.isRenew()).thenReturn(false);

        BillingStatus billingStatus = billingService.billingStatus(contractor);

        assertTrue(billingStatus.isCancelled());
    }

    @Test
    public void testGetBillingStatus_90DaysPastRenewalDateAndRenewTrueIsReactivation() throws Exception {
        billingStatusCommon(-91);

        BillingStatus billingStatus = billingService.billingStatus(contractor);

        assertTrue(billingStatus.isReactivation());
    }

    @Test
    public void testGetBillingStatus_DeactivatedRenewIsReactivation() throws Exception {
        billingStatusCommon(30);
        when(contractor.getStatus()).thenReturn(AccountStatus.Deactivated);
        when(contractor.pendingOrActive()).thenReturn(false);

        BillingStatus billingStatus = billingService.billingStatus(contractor);

        assertTrue(billingStatus.isReactivation());
    }

    @Test
    public void testGetBillingStatus_DeactivatedNonRenewIsCancelled() throws Exception {
        billingStatusCommon(30);
        when(contractor.getStatus()).thenReturn(AccountStatus.Deactivated);
        when(contractor.pendingOrActive()).thenReturn(false);
        when(contractor.isRenew()).thenReturn(false);

        BillingStatus billingStatus = billingService.billingStatus(contractor);

        assertTrue(billingStatus.isCancelled());
    }

    @Test
    public void testGetBillingStatus_ActiveFullNewMemberIsActivation() throws Exception {
        billingStatusCommon(30);
        when(contractor.newMember()).thenReturn(true);

        BillingStatus billingStatus = billingService.billingStatus(contractor);

        assertTrue(billingStatus.isActivation());
    }

    @Test
    public void testGetBillingStatus_PendingFullNewMemberIsActivation() throws Exception {
        billingStatusCommon(30);
        when(contractor.newMember()).thenReturn(true);
        when(contractor.getStatus()).thenReturn(AccountStatus.Pending);

        BillingStatus billingStatus = billingService.billingStatus(contractor);

        assertTrue(billingStatus.isActivation());
    }

    @Test
    public void testGetBillingStatus_NoMustPayIsCurrent() throws Exception {
        billingStatusCommon(30);
        when(contractor.isMustPayB()).thenReturn(false);

        BillingStatus billingStatus = billingService.billingStatus(contractor);

        assertTrue(billingStatus.isCurrent());
    }

    @Test
    public void testGetBillingStatus_NoPayingFacilitiesIsCurrent() throws Exception {
        billingStatusCommon(30);
        when(contractor.getPayingFacilities()).thenReturn(0);

        BillingStatus billingStatus = billingService.billingStatus(contractor);

        assertTrue(billingStatus.isCurrent());
    }

    @Test
    public void testGetBillingStatus_DemoAccountIsCurrent() throws Exception {
        billingStatusCommon(30);
        when(contractor.getStatus()).thenReturn(AccountStatus.Demo);
        when(contractor.pendingOrActive()).thenReturn(false);

        BillingStatus billingStatus = billingService.billingStatus(contractor);

        assertTrue(billingStatus.isCurrent());
    }

    @Test
    public void testGetBillingStatus_DeletedAccountIsCurrent() throws Exception {
        billingStatusCommon(30);
        when(contractor.getStatus()).thenReturn(AccountStatus.Deleted);

        BillingStatus billingStatus = billingService.billingStatus(contractor);

        assertTrue(billingStatus.isCurrent());
    }

    private void billingStatusCommon(int paymentExpiresDays) {
        when(invoice.getStatus()).thenReturn(TransactionStatus.Paid);
        when(invoice.getDueDate()).thenReturn(DateBean.addDays(new Date(), -10));

        when(contractor.getPaymentExpires()).thenReturn(DateBean.addDays(new Date(), paymentExpiresDays));
        // must have at least one paying facility or it'll be current
        when(contractor.getPayingFacilities()).thenReturn(1);
        when(contractor.getStatus()).thenReturn(AccountStatus.Active);
        when(contractor.pendingRequestedOrActive()).thenReturn(true);
        when(contractor.isRenew()).thenReturn(true);
        when(contractor.isMustPayB()).thenReturn(true);
        when(contractor.getFees()).thenReturn(fees);
        when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);

        when(upgradeFee.getCurrentLevel()).thenReturn(upgradeinvoiceFee);
    }

    @Test
    public void testGenerateInvoice() throws Exception {
        when(contractor.getPayingFacilities()).thenReturn(20);
        Invoice invoice1 = Whitebox.invokeMethod(billingService, "generateInvoice", contractor, invoiceItems, user, BillingStatus.Renewal, BigDecimal.TEN, BigDecimal.TEN);

        assertEquals(20, invoice1.getPayingFacilities());
    }

    @Test
    public void testCreateLineItem() throws Exception {
        when(invoiceFeeDAO.findByNumberOfOperatorsAndClass(any(FeeClass.class), anyInt())).thenReturn(invoiceFee);
        when(invoiceFee.getFeeClass()).thenReturn(FeeClass.Activation);
        InvoiceItem item = Whitebox.invokeMethod(billingService, "createLineItem", contractor, FeeClass.Activation, 20);

        assertEquals(item.getAmount(), item.getOriginalAmount());
    }

    @Test
    public void testAddYearlyItems() throws Exception {
        invoiceItems = new ArrayList<>();
        when(contractor.getFees()).thenReturn(fees);
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

        Whitebox.invokeMethod(billingService, "addYearlyItems", invoiceItems, contractor, new Date(), BillingStatus.Renewal);

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
        when(contractor.getLastUpgradeDate()).thenReturn(cal.getTime());
        cal.add(Calendar.MONTH, 6);
        when(contractor.getPaymentExpires()).thenReturn(cal.getTime());
        when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);
        when(contractor.getCountry()).thenReturn(country);
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

        Whitebox.invokeMethod(billingService, "addProratedUpgradeItems", contractor, invoiceItems, upgrades, user);

        assertEquals(2, invoiceItems.size());
        for (InvoiceItem item : invoiceItems) {
            assertEquals(new BigDecimal(5), item.getAmount());
            assertEquals(BigDecimal.TEN, item.getOriginalAmount());
        }
    }

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
}
