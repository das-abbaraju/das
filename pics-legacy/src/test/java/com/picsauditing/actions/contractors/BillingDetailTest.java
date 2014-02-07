package com.picsauditing.actions.contractors;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.picsauditing.PICS.FeeService;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.account.AccountStatusChanges;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.BillingService;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.model.billing.BillingNoteModel;
import com.picsauditing.model.billing.InvoiceModel;
import com.picsauditing.util.PermissionToViewContractor;
import com.picsauditing.util.Strings;

public class BillingDetailTest extends PicsActionTest {
	private BillingDetail billingDetail;

	private static final String NOTE_STRING = "Notes are cool";
	private static Date twoHundredDaysFromNow = DateBean.addDays(new Date(), 200);

	private List<InvoiceItem> invoiceItems;
	private List<Invoice> invoices;
	private Map<FeeClass, ContractorFee> fees;
	private Country country;
	private ArgumentCaptor<Note> noteCaptor;
	private BigDecimal invoiceTotal;

	@Mock
	private ContractorAccount contractor;
	@Mock
	private ContractorAccountDAO contractorAccountDao;
    @Mock
	private AccountDAO accountDAO;
    @Mock
	private NoteDAO noteDao;
    @Mock
	private BillingService billingService;
    @Mock
    private FeeService feeService;
    @Mock
	private User user;
	@Mock
	private PermissionToViewContractor permissionToViewContractor;
	@Mock
	private Invoice invoice;
	@Mock
	private InvoiceItem item;
	@Mock
	private InvoiceFee invoiceFee;
	@Mock
	private ContractorFee bidOnlyFee;
	@Mock
	private ContractorFee listOnlyFee;
	@Mock
	private InvoiceFee bidOnlyInvoiceFee;
	@Mock
	private InvoiceFee listOnlyinvoiceFee;
	@Mock
	private DataObservable saleCommissionDataObservable;
	@Mock
	private InvoiceModel invoiceModel;
	@Mock
	private BillingNoteModel billingNoteModel;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		setupCountry();
		setupBillingDetail();
		setupInvoiceItems();
		setupStandardFees();
		invoices = new ArrayList<>();
		stubMockBehaviors();
		setupInvoiceServiceToReturnArgumentOnSave();
		setupCaptors();
		calculateInvoiceTotal();
		// this has to go after calculateInvoiceTotal which goes after setupInvoiceItems
		when(billingService.calculateInvoiceTotal(invoiceItems)).thenReturn(invoiceTotal);
	}

	private void stubMockBehaviors() throws IOException {
		when(billingService.createInvoiceItems(contractor, BillingStatus.Activation, user)).thenReturn(invoiceItems);
		when(permissions.loginRequired((HttpServletResponse) any(), (HttpServletRequest) any())).thenReturn(true);
		when(permissions.getUserId()).thenReturn(123);
        when(permissions.isAdmin()).thenReturn(true);
		when(contractorAccountDao.find(any(int.class))).thenReturn(contractor);
		when(permissionToViewContractor.check(any(boolean.class))).thenReturn(true);
		when(item.getAmount()).thenReturn(new BigDecimal(199.00));
        when(billingService.billingStatus(contractor)).thenReturn(BillingStatus.Activation);
		when(contractor.getId()).thenReturn(123);
		when(contractor.getCountry()).thenReturn(country);
		when(contractor.getFees()).thenReturn(fees);
		when(contractor.getStatus()).thenReturn(AccountStatus.Active);
		when(contractor.getInvoices()).thenReturn(invoices);
		when(item.getInvoiceFee()).thenReturn(invoiceFee);
		when(billingNoteModel.findUserForPaymentNote(permissions)).thenReturn(user);
	}

	private void setupInvoiceItems() {
		invoiceItems = new ArrayList<>();
		invoiceItems.add(item);
        when(billingService.createInvoiceItems(contractor, user)).thenReturn(invoiceItems);
	}

	private void setupCountry() {
		country = new Country();
		country.setCurrency(Currency.USD);
	}

	private void setupBillingDetail() throws Exception, InstantiationException, IllegalAccessException {
		billingDetail = new BillingDetail();
		super.setUp(billingDetail);
		setupBillingDetailProperties();
	}

	@SuppressWarnings("rawtypes")
	private void setupInvoiceServiceToReturnArgumentOnSave() throws Exception {
		when(billingService.saveInvoice(any(Invoice.class))).thenAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				return args[0];
			}

		});
	}

	private void setupCaptors() {
		noteCaptor = ArgumentCaptor.forClass(Note.class);
	}

	private void calculateInvoiceTotal() {
		invoiceTotal = BigDecimal.ZERO.setScale(2);
		for (InvoiceItem item : invoiceItems) {
			invoiceTotal = invoiceTotal.add(item.getAmount());
		}
	}

	private void setupBillingDetailProperties() throws InstantiationException, IllegalAccessException {
		billingDetail.setId(123);
		PicsTestUtil.autowireDAOsFromDeclaredMocks(billingDetail, this);
		Whitebox.setInternalState(billingDetail, "billingService", billingService);
        Whitebox.setInternalState(billingDetail, "feeService", feeService);
		Whitebox.setInternalState(billingDetail, "user", user);
		Whitebox.setInternalState(billingDetail, "permissionToViewContractor", permissionToViewContractor);
		Whitebox.setInternalState(billingDetail, "saleCommissionDataObservable", saleCommissionDataObservable);
		Whitebox.setInternalState(billingDetail, "billingNoteModel", billingNoteModel);
	}

	private void setupStandardFees() {
		fees = new HashMap<FeeClass, ContractorFee>();
		// all contractors have bid only and list only fees, they may just be hidden and $0
		fees.put(FeeClass.BidOnly, bidOnlyFee);
		fees.put(FeeClass.ListOnly, listOnlyFee);
		when(bidOnlyFee.getCurrentLevel()).thenReturn(bidOnlyInvoiceFee);
		when(listOnlyFee.getCurrentLevel()).thenReturn(listOnlyinvoiceFee);
		when(bidOnlyInvoiceFee.isFree()).thenReturn(true);
		when(listOnlyinvoiceFee.isFree()).thenReturn(true);
	}

	private void setupForCreate() {
		billingDetail.setButton("Create");
        when(billingService.billingStatus(contractor)).thenReturn(BillingStatus.Activation);
        commonExecuteSetup();
	}

	private void setupForViewPage() {
		billingDetail.setButton(Strings.EMPTY_STRING);
        when(billingService.billingStatus(contractor)).thenReturn(BillingStatus.Reactivation);
		when(contractor.getStatus()).thenReturn(AccountStatus.Active);
        commonExecuteSetup();
	}

	private void setupForActivation() {
		billingDetail.setButton("Activate");
        when(billingService.billingStatus(contractor)).thenReturn(BillingStatus.Activation);
		when(contractor.getStatus()).thenReturn(AccountStatus.Pending);
        commonExecuteSetup();
	}

    private void commonExecuteSetup() {
        when(invoiceFee.isMembership()).thenReturn(true);
        when(invoiceModel.getSortedClientSiteList(contractor)).thenReturn(NOTE_STRING);
        when(contractor.getPaymentExpires()).thenReturn(twoHundredDaysFromNow);
        when(billingService.createInvoiceWithItems(contractor, invoiceItems, new User(permissions.getUserId())))
                .thenReturn(invoice);
    }

    @Test(expected = NoRightsException.class)
    public void testExecute_noPermissions() throws Exception {
        setupForCreate();
        when(permissions.isAdmin()).thenReturn(false);

        billingDetail.execute();
    }

    @Test
	public void testExecute_Create_HappyPath_NewInvoiceIsAddedToContractorInvoices() throws Exception {
		setupForCreate();

		billingDetail.execute();

		assertThat(contractor.getInvoices(), hasItem(invoice));
	}

	@Test
	public void testExecute_Create_HappyPath_NewInvoiceSyncsBalance() throws Exception {
		setupForCreate();

		billingDetail.execute();

		verify(billingService).syncBalance(contractor);
	}

	@Test
	public void testExecute_Create_HappyPath_ContractorIsSaved() throws Exception {
		setupForCreate();

		billingDetail.execute();

		verify(accountDAO).save(contractor);
	}

	@Test
	public void testExecute_Create_PositiveInvoiceTotalAddsNoteToContractor() throws Exception {
		setupForCreate();

		billingDetail.execute();

		verify(noteDao).save(noteCaptor.capture());
		Note note = noteCaptor.getValue();

		assertEquals(contractor, note.getAccount());
	}

	@Test
	public void testExecute_Create_ZeroDollarInvoiceIsAnActionErrorIfNoOpPermBilling() throws Exception {
		billingDetail.setButton("Create");
		invoiceItems.clear();
		when(billingService.calculateInvoiceTotal(invoiceItems)).thenReturn(BigDecimal.ZERO);
		when(permissions.hasPermission(OpPerms.Billing)).thenReturn(false);

		String actionResult = billingDetail.execute();

		assertTrue(billingDetail.getActionErrors().contains("Cannot create an Invoice for zero dollars"));
		assertEquals(Action.SUCCESS, actionResult);
	}

	@Test
	public void testExecute_Create_ZeroDollarInvoiceIsAllowedIfOpPermBilling() throws Exception {
		setupForCreate();
		billingDetail.setButton("Create");
		invoiceItems.clear();
		when(billingService.calculateInvoiceTotal(invoiceItems)).thenReturn(BigDecimal.ZERO);
		when(permissions.hasPermission(OpPerms.Billing)).thenReturn(true);

		String actionResult = billingDetail.execute();

		verify(billingService).createInvoiceWithItems(eq(contractor), eq(invoiceItems), any(User.class));
		assertEquals(PicsActionSupport.BLANK, actionResult);
	}

	@Test
	public void testExecute_ViewBillingDetailForBidOnlyContractor() throws Exception {
		setupForViewPage();
		when(contractor.getAccountLevel()).thenReturn(AccountLevel.BidOnly);

		String actionResult = billingDetail.execute();

		verify(contractor, times(1)).setReason(AccountStatusChanges.BID_ONLY_ACCOUNT_REASON);
		commonVerifications(actionResult);
	}

	@Test
	public void testExecute_ViewBillingDetail() throws Exception {
		setupForViewPage();
		when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);

		String actionResult = billingDetail.execute();

		commonVerifications(actionResult);
	}

	private void commonVerifications(String actionResult) {
		assertEquals(AccountStatus.Active, contractor.getStatus());
		assertEquals(ActionSupport.SUCCESS, actionResult);

		verify(billingService, times(1)).syncBalance(contractor);
		verify(accountDAO, times(1)).save(contractor);
	}

	@Test
	public void testExecute_ForActivation() throws Exception {
		setupForActivation();

		String actionResult = billingDetail.execute();

		assertEquals(ActionSupport.SUCCESS, actionResult);
		verify(contractor, times(1)).setStatus(AccountStatus.Active);
		verify(noteDao, times(1)).save(any(Note.class));
	}

    @Test
    public void testExecute_clearUpgradeDate() throws Exception {
        setupForCreate();
        when(invoice.getInvoiceType()).thenReturn(InvoiceType.Upgrade);

        String actionResult = billingDetail.execute();

        assertEquals(PicsActionSupport.BLANK, actionResult);
        verify(contractor,times(1)).setLastUpgradeDate(null);
	}

}
