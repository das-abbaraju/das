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
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.util.SapAppPropertyUtil;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.account.AccountStatusChanges;
import com.picsauditing.model.billing.AccountingSystemSynchronization;
import com.picsauditing.model.billing.BillingNoteModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class InvoiceDetailTest extends PicsActionTest {

	InvoiceDetail invoiceDetail;

	@Mock
	private Account account;
	@Mock
	private Invoice invoice;
	@Mock
	private Country country;
    @Mock
    private CountryContact countryContact;
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
		when(contractor.getBillingStatus()).thenReturn(BillingStatus.Reactivation);
		when(contractor.getAccountLevel()).thenReturn(AccountLevel.BidOnly);
        when(country.getCountryContact()).thenReturn(countryContact);
		when(countryContact.getBusinessUnit()).thenReturn(businessUnit);
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

	private void commonVerificationForExecuteTest(String expectedActionResult, String actualActionResult)
			throws Exception {
		assertEquals(expectedActionResult, actualActionResult);
		verify(contractor, never()).setStatus(AccountStatus.Deactivated);
		verify(billingService, times(1)).saveInvoice(invoice);
		verify(feeService, times(1)).calculateContractorInvoiceFees(contractor);
		verify(billingService, times(1)).syncBalance(contractor);
		verify(contractor, times(1)).setAuditColumns(permissions);
		verify(contractorAccountDAO, times(1)).save(contractor);
	}

}
