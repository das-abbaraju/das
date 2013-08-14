package com.picsauditing.PICS;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.*;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.jpa.entities.Currency;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.model.billing.InvoiceModel;

public class BillingServiceTest extends PicsTranslationTest {
	private BillingService billingService;

	private static Date twoHundredDaysFromNow = DateBean.addDays(new Date(), 200);

	private List<Invoice> invoices;
	private List<InvoiceItem> invoiceItems;
	private Map<FeeClass, ContractorFee> fees;
	private Country country;
	private BigDecimal invoiceTotal;

	@Mock
	private ContractorAccount contractor;
	@Mock
	private User user;
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

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		billingService = new BillingService();
		Whitebox.setInternalState(billingService, "taxService", taxService);
		Whitebox.setInternalState(billingService, "feeDAO", invoiceFeeDAO);
		Whitebox.setInternalState(billingService, "invoiceModel", invoiceModel);
		Whitebox.setInternalState(billingService, "auditDataDAO", auditDataDAO);

		assert (OAMocksSet.isEmpty());

		setupInvoiceAndItems();
		setupStandardFees();
		setupCountry();
		calculateInvoiceTotal();
	}

	private void setupInvoiceAndItems() {
		invoices = new ArrayList<Invoice>();
		invoiceItems = new ArrayList<InvoiceItem>();
		invoiceItems.add(invoiceItem);
		when(invoiceItem.getAmount()).thenReturn(new BigDecimal(199.00));
		when(invoiceItem.getInvoiceFee()).thenReturn(invoiceFee);
	}

	private void setupStandardFees() {
		fees = new HashMap<FeeClass, ContractorFee>();
		// all contractors have bid only and list only fees, they may just be
		// hidden and $0
		fees.put(FeeClass.BidOnly, bidOnlyFee);
		fees.put(FeeClass.ListOnly, listOnlyFee);
		when(bidOnlyFee.getCurrentLevel()).thenReturn(bidOnlyInvoiceFee);
		when(listOnlyFee.getCurrentLevel()).thenReturn(listOnlyinvoiceFee);
		when(bidOnlyInvoiceFee.isFree()).thenReturn(true);
		when(listOnlyinvoiceFee.isFree()).thenReturn(true);
	}

	private void setupCreateInvoiceWithItemsTestsCommon() {
		when(contractor.getId()).thenReturn(123);
		when(contractor.getCountry()).thenReturn(country);
		when(contractor.getFees()).thenReturn(fees);
		when(contractor.getStatus()).thenReturn(AccountStatus.Active);
		when(contractor.getInvoices()).thenReturn(invoices);
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.Activation);
		when(contractor.getPaymentExpires()).thenReturn(twoHundredDaysFromNow);

		when(invoiceFee.isMembership()).thenReturn(true);

		List<ContractorOperator> generalContractors = new ArrayList<ContractorOperator>();
		generalContractors.add(contractorOperator);
		when(contractor.getNonCorporateOperators()).thenReturn(generalContractors);
		when(contractorOperator.getOperatorAccount()).thenReturn(operator);
		when(operator.getDoContractorsPay()).thenReturn("Yes");
		when(operator.getName()).thenReturn("Test Operator");
	}

	private void setupCountry() {
		country = new Country();
		country.setCurrency(Currency.USD);
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

		billingService.createInvoiceWithItems(contractor, invoiceItems, user, contractor.getBillingStatus());

		verify(contractor).setRenew(true);
	}

	@Test
	public void testExecute_Create_RenewIsSetForListOnlyAccount() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(bidOnlyInvoiceFee.isFree()).thenReturn(true);
		when(listOnlyinvoiceFee.isFree()).thenReturn(false);

		billingService.createInvoiceWithItems(contractor, invoiceItems, user, contractor.getBillingStatus());

		verify(contractor).setRenew(true);
	}

	@Test
	public void testCreateInvoiceWithItems_Activation_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();

		Invoice invoice = billingService.createInvoiceWithItems(contractor, invoiceItems, user, contractor.getBillingStatus());

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 7)));
	}

	@Test
	public void testCreateInvoiceWithItems_Reactivation_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.Reactivation);

		Invoice invoice = billingService.createInvoiceWithItems(contractor, invoiceItems, user, contractor.getBillingStatus());

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 7)));
	}

	@Test
	public void testCreateInvoiceWithItems_Upgrade_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.Upgrade);

		Invoice invoice = billingService.createInvoiceWithItems(contractor, invoiceItems, user, contractor.getBillingStatus());

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 7)));
	}

	@Test
	public void testCreateInvoiceWithItems_Renewal_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.Renewal);

		Invoice invoice = billingService.createInvoiceWithItems(contractor, invoiceItems, user, contractor.getBillingStatus());

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), contractor.getPaymentExpires()));
	}

	@Test
	public void testCreateInvoiceWithItems_RenewalOverdue_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.RenewalOverdue);

		Invoice invoice = billingService.createInvoiceWithItems(contractor, invoiceItems, user, contractor.getBillingStatus());

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), contractor.getPaymentExpires()));
	}

	@Test
	public void testCreateInvoiceWithItems_Current_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.Current);

		Invoice invoice = billingService.createInvoiceWithItems(contractor, invoiceItems, user, contractor.getBillingStatus());

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 30)));
	}

	@Test
	public void testCreateInvoiceWithItems_PICS_8597_RenewalBidOnly_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.Renewal);
		when(bidOnlyInvoiceFee.isFree()).thenReturn(false);
		when(listOnlyinvoiceFee.isFree()).thenReturn(true);

		Invoice invoice = billingService.createInvoiceWithItems(contractor, invoiceItems, user, contractor.getBillingStatus());

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), contractor.getPaymentExpires()));
	}

	@Test
	public void testCreateInvoiceWithItems_PICS_8597_RenewalListOnly_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.Renewal);
		when(bidOnlyInvoiceFee.isFree()).thenReturn(true);
		when(listOnlyinvoiceFee.isFree()).thenReturn(false);

		Invoice invoice = billingService.createInvoiceWithItems(contractor, invoiceItems, user, contractor.getBillingStatus());

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), contractor.getPaymentExpires()));
	}

	@Test
	public void testCreateInvoiceWithItems_PICS_8597_ActivationListOnly_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.Activation);
		when(bidOnlyInvoiceFee.isFree()).thenReturn(true);
		when(listOnlyinvoiceFee.isFree()).thenReturn(false);

		Invoice invoice = billingService.createInvoiceWithItems(contractor, invoiceItems, user, contractor.getBillingStatus());

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 7)));
	}

	@Test
	public void testCreateInvoiceWithItems_PICS_8597_ActivationBidOnly_InvoiceDueDateSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.Activation);
		when(bidOnlyInvoiceFee.isFree()).thenReturn(false);
		when(listOnlyinvoiceFee.isFree()).thenReturn(true);

		Invoice invoice = billingService.createInvoiceWithItems(contractor, invoiceItems, user, contractor.getBillingStatus());

		assertTrue(DateBean.isSameDate(invoice.getDueDate(), DateBean.addDays(new Date(), 7)));
	}

	@Test
	public void testCreateInvoiceWithItems_InvoicePropertiesAreSetAsExpected() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		doCallRealMethod().when(invoiceModel).getSortedClientSiteList(any(ContractorAccount.class));

		Invoice invoice = billingService.createInvoiceWithItems(contractor, invoiceItems, user, contractor.getBillingStatus());

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

		Invoice invoice = billingService.createInvoiceWithItems(contractor, invoiceItems, user, contractor.getBillingStatus());

		assertThat("Test Operator", is(equalTo(invoice.getNotes())));
	}

	@Test
	public void testCreateInvoice_ZeroDollarInvoiceIsNull() throws Exception {
		setupCreateInvoiceWithItemsTestsCommon();
		// this will skip an activation fee
		when(contractor.getAccountLevel()).thenReturn(AccountLevel.BidOnly);
		// currently this will result in zero invoice items which will be a zero
		// dollar invoice
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.Current);

		Invoice invoice = billingService.createInvoice(contractor, contractor.getBillingStatus(), user);

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
		Country country = mock(Country.class);
		BigDecimal amount = new BigDecimal(100);
        List<InvoiceFeeCountry> amountOverrides = new ArrayList<InvoiceFeeCountry>();
        InvoiceFeeCountry override = mock(InvoiceFeeCountry.class);
        InvoiceFee invoiceFee = mock(InvoiceFee.class);
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
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.Activation);

		when(auditDataDAO.findContractorAuditAnswers(anyInt(), anyInt(), anyInt())).thenReturn(null);

		billingService.createInvoice(contractor, BillingStatus.Current, user);

		verify(taxService).applyTax(any(Invoice.class));
	}

}