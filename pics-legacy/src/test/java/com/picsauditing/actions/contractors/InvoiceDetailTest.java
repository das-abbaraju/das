package com.picsauditing.actions.contractors;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.PICS.BillingService;
import com.picsauditing.PICS.FeeService;
import com.picsauditing.PICS.InvoiceValidationException;
import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.PicsActionTest;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.account.AccountStatusChanges;
import com.picsauditing.model.billing.AccountingSystemSynchronization;
import com.picsauditing.model.billing.BillingNoteModel;
import com.picsauditing.util.SapAppPropertyUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class InvoiceDetailTest extends PicsActionTest {

	InvoiceDetail invoiceDetail;

	@Mock
	private Account account;
	@Mock
	private Invoice invoice;
    @Mock
    private InvoiceCreditMemo invoiceCreditMemo;
	@Mock
	private Country country;
	@Mock
	private BusinessUnit businessUnit;
	@Mock
	private ContractorAccount contractor;
    @Mock
    private BillingService billingService;
    @Mock
    private FeeService feeService;
	@Mock
	private ContractorAccountDAO contractorAccountDAO;
	@Mock
	private DataObservable salesCommissionDataObservable;
	@Mock
	private NoteDAO noteDAO;
    @Mock
    private InvoiceDAO invoiceDAO;
    @Mock
    private PaymentDAO paymentDAO;
	@Mock
	private BillingNoteModel billingNoteModel;
	@Mock
	private AppPropertyDAO appPropertyDAO;
	@Mock
	private SapAppPropertyUtil sapAppPropertyUtil;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		invoiceDetail = new InvoiceDetail();
		invoiceDetail.setContractor(contractor);

		super.setUp(invoiceDetail);

		Whitebox.setInternalState(invoiceDetail, "billingService", billingService);
        Whitebox.setInternalState(invoiceDetail, "feeService", feeService);
		Whitebox.setInternalState(invoiceDetail, "contractorAccountDao", contractorAccountDAO);
		Whitebox.setInternalState(invoiceDetail, "salesCommissionDataObservable", salesCommissionDataObservable);
		Whitebox.setInternalState(invoiceDetail, "noteDAO", noteDAO);
        Whitebox.setInternalState(invoiceDetail,"invoiceDAO",invoiceDAO);
        Whitebox.setInternalState(invoiceDetail,"paymentDAO",paymentDAO);
		Whitebox.setInternalState(invoiceDetail, "billingNoteModel", billingNoteModel);
		Whitebox.setInternalState(invoiceDetail,"appPropertyDAO",appPropertyDAO);
		Whitebox.setInternalState(invoiceDetail,"sapAppPropertyUtil",sapAppPropertyUtil);
		AccountingSystemSynchronization.setSapAppPropertyUtil(sapAppPropertyUtil);
	}

	// Go back and add in verification for the message
	@Test
	public void testExecute_NullInvoice() throws IOException, InvoiceValidationException, Exception {
		invoiceDetail.setInvoice(null);

		String actionResult = invoiceDetail.execute();

		assertEquals(PicsActionSupport.BLANK, actionResult);
	}

	private void commonSetupForExecuteTest() {
		when(invoice.getAccount()).thenReturn(account);
		invoiceDetail.setInvoice(invoice);
		invoiceDetail.setTransaction(invoice);
		when(permissions.getAccountId()).thenReturn(6987);
		when(account.getCountry()).thenReturn(country);
		when(invoice.getStatus()).thenReturn(TransactionStatus.Unpaid);
		when(country.getCurrency()).thenReturn(Currency.USD);
		when(contractor.getCountry()).thenReturn(country);
		when(invoice.getTotalAmount()).thenReturn(BigDecimal.valueOf(199.00));
		when(contractor.getStatus()).thenReturn(AccountStatus.Active);
        when(billingService.billingStatus(contractor)).thenReturn(BillingStatus.Reactivation);
		when(contractor.getAccountLevel()).thenReturn(AccountLevel.BidOnly);
        when(country.getBusinessUnit()).thenReturn(businessUnit);
		when(businessUnit.getId()).thenReturn(2);

	}

	@Test(expected = NoRightsException.class)
	public void testExecute_DoesNotHavePermissions() throws IOException, InvoiceValidationException, Exception {
		commonSetupForExecuteTest();
		when(account.getId()).thenReturn(4567);
		when(permissions.hasPermission(OpPerms.AllContractors)).thenReturn(false);

		invoiceDetail.execute();
	}

	@Test
	public void testExecute_WhenLoadingPage() throws Exception {
		commonSetupForExecuteTest();
		when(account.getId()).thenReturn(6987);
		when(permissions.hasPermission(OpPerms.AllContractors)).thenReturn(true);

		String actionResult = invoiceDetail.execute();

		commonVerificationForExecuteTest(ActionSupport.SUCCESS, actionResult);
	}

	@Test
	public void testExecute_CancelButton() throws IOException, InvoiceValidationException, Exception {
		invoiceDetail.setInvoice(invoice);
		invoiceDetail.setButton("cancel");
		commonSetupForExecuteTest();
		when(permissions.getAccountId()).thenReturn(6987);
		when(permissions.hasPermission(OpPerms.AllContractors)).thenReturn(true);

		String actionResult = invoiceDetail.execute();

		verify(contractor, times(1)).setReason(AccountStatusChanges.BID_ONLY_ACCOUNT_REASON);
		verify(invoice, times(1)).setStatus(TransactionStatus.Void);
		commonVerificationForExecuteTest(PicsActionSupport.REDIRECT, actionResult);
	}

    @Test
    public void testExecute_SaveButton() throws Exception {
        invoiceDetail.setInvoice(invoice);
        invoiceDetail.setButton("save");
        commonSetupForExecuteTest();
        when(permissions.getAccountId()).thenReturn(6987);
        when(permissions.hasPermission(OpPerms.AllContractors)).thenReturn(true);

        String actionResult = invoiceDetail.execute();

        verify(billingService, times(1)).addRevRecInfoIfAppropriateToItems(invoice);
        commonVerificationForExecuteTest(PicsActionSupport.REDIRECT, actionResult);
    }

    @Test
    public void testExecute_BadDebtButton() throws Exception {
        Invoice invoiceObj = new Invoice();
        invoiceObj.setAccount(contractor);
        BigDecimal fourThousand = new BigDecimal(4000.00);
        BigDecimal twoThousand = new BigDecimal(2000.00);
        invoiceObj.setTotalAmount(fourThousand);
        invoiceObj.setAmountApplied(twoThousand);

        commonSetupForExecuteTest();
        invoiceDetail.setInvoice(invoiceObj);
        invoiceDetail.setTransaction(invoiceObj);
        invoiceDetail.setButton("baddebt");
        when(contractor.getStatus()).thenReturn(AccountStatus.Deactivated);
        when(permissions.getAccountId()).thenReturn(6987);
        when(permissions.hasPermission(OpPerms.AllContractors)).thenReturn(true);
        when(sapAppPropertyUtil.isSAPBusinessUnitEnabledForObject(invoiceObj)).thenReturn(true);

        String actionResult = invoiceDetail.execute();

        verify(paymentDAO).save((Payment) any());
        assertEquals(twoThousand,invoiceDetail.getInvoice().getPayments().get(0).getPayment().getTotalAmount());
    }

    @Test
    public void testExecute_RefundCreditMemo_AccountBalanceIsNotNegative() throws IOException, InvoiceValidationException, Exception {
        invoiceDetail.setCreditMemo(invoiceCreditMemo);
        invoiceDetail.setTransaction(invoiceCreditMemo);
        invoiceDetail.setButton("refund");

        when(sapAppPropertyUtil.isSAPBusinessUnitEnabledForObject(invoiceCreditMemo)).thenReturn(true);
        when(contractor.getBalance()).thenReturn(BigDecimal.ZERO);

        when(permissions.getAccountId()).thenReturn(6987);
        when(permissions.hasPermission(OpPerms.AllContractors)).thenReturn(true);

        invoiceDetail.execute();

        verify(contractorAccountDAO, never()).save(contractor);
        verify(invoiceDAO, never()).save(any(RefundAppliedToCreditMemo.class));
    }

    @Test
    public void testExecute_RefundCreditMemo_NoRefundAllowed() throws IOException, InvoiceValidationException, Exception {
        invoiceDetail.setCreditMemo(invoiceCreditMemo);
        invoiceDetail.setTransaction(invoiceCreditMemo);
        invoiceDetail.setButton("refund");

        when(sapAppPropertyUtil.isSAPBusinessUnitEnabledForObject(invoiceCreditMemo)).thenReturn(true);
        when(contractor.getBalance()).thenReturn(new BigDecimal(-2000));
        when(invoiceCreditMemo.getCreditLeft()).thenReturn(new BigDecimal(-100));

        when(permissions.getAccountId()).thenReturn(6987);
        when(permissions.hasPermission(OpPerms.AllContractors)).thenReturn(true);

        invoiceDetail.execute();

        verify(contractorAccountDAO, never()).save(contractor);
        verify(invoiceDAO, never()).save(any(RefundAppliedToCreditMemo.class));
    }

    @Test
    public void testExecute_RefundCreditMemo_NoRefundsNeeded() throws IOException, InvoiceValidationException, Exception {
        invoiceDetail.setCreditMemo(invoiceCreditMemo);
        invoiceDetail.setTransaction(invoiceCreditMemo);
        invoiceDetail.setButton("refund");

        when(sapAppPropertyUtil.isSAPBusinessUnitEnabledForObject(invoiceCreditMemo)).thenReturn(true);
        when(contractor.getBalance()).thenReturn(new BigDecimal(-2000));
        when(invoiceCreditMemo.getCreditLeft()).thenReturn(BigDecimal.ZERO);

        when(permissions.getAccountId()).thenReturn(6987);
        when(permissions.hasPermission(OpPerms.AllContractors)).thenReturn(true);

        invoiceDetail.execute();

        verify(contractorAccountDAO, never()).save(contractor);
        verify(invoiceDAO, never()).save(any(RefundAppliedToCreditMemo.class));
    }

    @Test
    public void testExecute_RefundCreditMemo_Refund() throws IOException, InvoiceValidationException, Exception {
        invoiceDetail.setCreditMemo(invoiceCreditMemo);
        invoiceDetail.setTransaction(invoiceCreditMemo);
        invoiceDetail.setButton("refund");

        when(sapAppPropertyUtil.isSAPBusinessUnitEnabledForObject(invoiceCreditMemo)).thenReturn(true);
        when(contractor.getBalance()).thenReturn(new BigDecimal(-2000));
        when(invoiceCreditMemo.getCreditLeft()).thenReturn(new BigDecimal(1000));

        when(permissions.getAccountId()).thenReturn(6987);
        when(permissions.hasPermission(OpPerms.AllContractors)).thenReturn(true);

        invoiceDetail.execute();

        verify(billingService, times(1)).syncBalance(contractor);
        verify(contractorAccountDAO, times(1)).save(contractor);
        verify(invoiceDAO, times(1)).save(any(RefundAppliedToCreditMemo.class));
    }

    @Test
    public void testCreateRefundForCreditMemo() throws Exception {
        RefundAppliedToCreditMemo refundAppliedToCreditMemo = Whitebox.invokeMethod(invoiceDetail, "createRefundForCreditMemo", BigDecimal.TEN.negate());
        assertEquals(BigDecimal.TEN, refundAppliedToCreditMemo.getAmount());
        assertEquals(BigDecimal.TEN, refundAppliedToCreditMemo.getRefund().getAmountApplied());
    }

    @Test
    public void testReturnItem() throws Exception {
        InvoiceItem invoiceItem = mock(InvoiceItem.class);
        when(invoiceItem.getRevenueFinishDate()).thenReturn(new Date());
        when(invoiceItem.getAmount()).thenReturn(BigDecimal.TEN);
        ReturnItem item = new ReturnItem(invoiceItem);
        assertEquals(item.getRevenueStartDate(), item.getRevenueFinishDate());
    }

    private void commonVerificationForExecuteTest(String expectedActionResult, String actualActionResult)
			throws Exception {
		assertEquals(expectedActionResult, actualActionResult);
		verify(contractor, never()).setStatus(AccountStatus.Deactivated);
	}

}
