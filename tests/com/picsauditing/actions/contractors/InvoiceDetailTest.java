package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;

import com.picsauditing.model.account.AccountStatusChanges;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.InvoiceService;
import com.picsauditing.PICS.InvoiceValidationException;
import com.picsauditing.PICS.data.DataObservable;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.BillingStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.TransactionStatus;

public class InvoiceDetailTest extends PicsActionTest {

	InvoiceDetail invoiceDetail;

	@Mock
	private Account account;
	@Mock
	private Invoice invoice;
	@Mock
	private Country country;
	@Mock
	private ContractorAccount contractor;
	@Mock
	private InvoiceService invoiceService;
	@Mock
	private BillingCalculatorSingle billingService;
	@Mock
	private ContractorAccountDAO contractorAccountDAO;
	@Mock
	private DataObservable salesCommissionDataObservable;
	@Mock
	private NoteDAO noteDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		invoiceDetail = new InvoiceDetail();
		invoiceDetail.setContractor(contractor);

		super.setUp(invoiceDetail);

		Whitebox.setInternalState(invoiceDetail, "invoiceService", invoiceService);
		Whitebox.setInternalState(invoiceDetail, "billingService", billingService);
		Whitebox.setInternalState(invoiceDetail, "contractorAccountDao", contractorAccountDAO);
		Whitebox.setInternalState(invoiceDetail, "salesCommissionDataObservable", salesCommissionDataObservable);
		Whitebox.setInternalState(invoiceDetail, "noteDAO", noteDAO);
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
		when(permissions.getAccountId()).thenReturn(6987);
		when(account.getCountry()).thenReturn(country);
		when(invoice.getStatus()).thenReturn(TransactionStatus.Unpaid);
		when(country.getCurrency()).thenReturn(Currency.USD);
		when(account.getCountry()).thenReturn(country);
		when(contractor.getCountry()).thenReturn(country);
		when(invoice.getTotalAmount()).thenReturn(BigDecimal.valueOf(199.00));
		when(contractor.getStatus()).thenReturn(AccountStatus.Active);
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.Reactivation);
		when(contractor.getAccountLevel()).thenReturn(AccountLevel.BidOnly);
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

	private void commonVerificationForExecuteTest(String expectedActionResult, String actualActionResult) throws Exception {
		assertEquals(expectedActionResult, actualActionResult);
		verify(contractor, never()).setStatus(AccountStatus.Deactivated);
		verify(invoiceService, times(1)).saveInvoice(invoice);
		verify(billingService, times(1)).calculateContractorInvoiceFees(contractor);
		verify(contractor, times(1)).syncBalance();
		verify(contractor, times(1)).setAuditColumns(permissions);
		verify(contractorAccountDAO, times(1)).save(contractor);
	}

}
