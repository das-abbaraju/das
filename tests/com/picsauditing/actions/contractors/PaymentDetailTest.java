package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.PicsActionTest;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.PaymentDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.PaymentApplied;
import com.picsauditing.jpa.entities.PaymentAppliedToInvoice;
import com.picsauditing.jpa.entities.PaymentAppliedToRefund;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.jpa.entities.TransactionStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.billing.BillingNoteModel;
import com.picsauditing.util.Strings;
import com.picsauditing.util.braintree.BrainTreeService;
import com.picsauditing.util.braintree.CreditCard;

public class PaymentDetailTest extends PicsActionTest {

	private static final String FIND_CREDIT_CARD_BUTTON = "findcc";

	private static final int CONTRACTOR_ID = 4578;

	PaymentDetail paymentDetail;

	@Mock
	private ContractorAccount contractor;
	@Mock
	private Payment payment;
	@Mock
	private PaymentDAO paymentDAO;
	@Mock
	private BrainTreeService brainTreeService;
	@Mock
	private ContractorAccountDAO contractorAccountDAO;
	@Mock
	private BillingNoteModel billingNoteModel;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		paymentDetail = new PaymentDetail();
		super.setUp(paymentDetail);

		Whitebox.setInternalState(paymentDetail, "paymentDAO", paymentDAO);
		Whitebox.setInternalState(paymentDetail, "paymentService", brainTreeService);
		Whitebox.setInternalState(paymentDetail, "contractorAccountDao", contractorAccountDAO);
		Whitebox.setInternalState(paymentDetail, "billingNoteModel", billingNoteModel);
	}

	@Test
	public void testExecute_FindCreditCardButton() throws Exception {
		paymentDetail.setId(CONTRACTOR_ID);
		paymentDetail.setButton(FIND_CREDIT_CARD_BUTTON);
		when(brainTreeService.getCreditCard(CONTRACTOR_ID)).thenReturn(new CreditCard(Strings.EMPTY_STRING));

		String result = paymentDetail.execute();

		assertEquals(ActionSupport.SUCCESS, result);
		assertEquals(PaymentMethod.CreditCard, paymentDetail.getMethod());
		assertNotNull(paymentDetail.getCreditCard());
	}

	@Test
	public void testExecute_FindContractor() throws Exception {
		paymentDetail.setContractor(null);
		Whitebox.setInternalState(paymentDetail, "id", CONTRACTOR_ID);
		Whitebox.setInternalState(paymentDetail, "limitedView", true);
		when(contractorAccountDAO.find(CONTRACTOR_ID)).thenReturn(contractor);

		String result = paymentDetail.execute();

		assertEquals(ActionSupport.SUCCESS, result);
	}

	@Test
	public void testGetOverallInvoiceCurrency_AllInvoicesHaveSameCurrenct() throws Exception {
		List<Invoice> mockInvoices = buildMockInvoiceList();

		Currency result = Whitebox.invokeMethod(paymentDetail, "getOverallInvoiceCurrency", contractor, mockInvoices);

		assertEquals(result, Currency.USD);
	}

	@Test
	public void testGetOverallInvoiceCurrency_InvoicesHaveDifferenceCurrencies() throws Exception {
		when(contractor.getCountry()).thenReturn(new Country("US"));
		List<Invoice> mockInvoices = buildMockInvoiceListNotSameCurrency();

		Currency result = Whitebox.invokeMethod(paymentDetail, "getOverallInvoiceCurrency", contractor, mockInvoices);

		assertEquals(result, Currency.USD);
	}

	@Test
	public void testGetAppliedInvoices() throws Exception {
		List<Invoice> mockInvoices = buildMockInvoiceList();
		when(contractor.getInvoices()).thenReturn(mockInvoices);

		@SuppressWarnings("serial")
		Map<Integer, BigDecimal> mapPaymentsToInvoices = new HashMap<Integer, BigDecimal>() {
			{
				put(Integer.valueOf(123), BigDecimal.valueOf(450));
				put(Integer.valueOf(456), BigDecimal.valueOf(250));
			}
		};

		List<Invoice> results = Whitebox.invokeMethod(paymentDetail, "getAppliedInvoices", contractor,
				mapPaymentsToInvoices);

		assertNotNull(results);
		assertEquals(2, results.size());
	}

	@Test
	public void testGetAppliedInvoices_NoInvoicesToApplyPayment() throws Exception {
		Map<Integer, BigDecimal> mapPaymentsToInvoices = null;
		List<Invoice> results = Whitebox.invokeMethod(paymentDetail, "getAppliedInvoices", contractor,
				mapPaymentsToInvoices);

		assertNotNull(results);
		assertTrue(results.isEmpty());
	}

	@Ignore("I believe that this method is broken")
	@Test
	public void testUnapplyAll() throws Exception {
		Payment payment = new Payment();
		List<PaymentApplied> paymentsApplied = new ArrayList<PaymentApplied>();
		paymentsApplied.add(new PaymentAppliedToInvoice());
		paymentsApplied.add(new PaymentAppliedToRefund());
		payment.setApplied(paymentsApplied);
		paymentDetail.setPayment(payment);

		Whitebox.invokeMethod(paymentDetail, "unapplyAll");

		verify(paymentDAO, times(1)).removePaymentInvoice(any(PaymentAppliedToInvoice.class), any(User.class));
		verify(paymentDAO, times(1)).removePaymentRefund(any(PaymentAppliedToRefund.class), any(User.class));

		assertTrue(paymentDetail.getPayment().getInvoices().isEmpty());
		assertTrue(paymentDetail.getPayment().getRefunds().isEmpty());
	}

	@Test
	public void testIsHasUnpaidInvoices_PaymentHasBeenPaid() {
		when(payment.getStatus()).thenReturn(TransactionStatus.Paid);
		paymentDetail.setPayment(payment);

		assertFalse(paymentDetail.isHasUnpaidInvoices());
	}

	@Test
	public void testIsHasUnpaidInvoices_NoInvoices() {
		when(contractor.getInvoices()).thenReturn(new ArrayList<Invoice>());
		when(payment.getStatus()).thenReturn(TransactionStatus.Unpaid);
		paymentDetail.setPayment(payment);
		paymentDetail.setContractor(contractor);

		assertFalse(paymentDetail.isHasUnpaidInvoices());
	}

	@Test
	public void testIsHasUnpaidInvoices() {
		List<Invoice> mockInvoices = buildMockInvoiceList();
		when(contractor.getInvoices()).thenReturn(mockInvoices);
		when(payment.getStatus()).thenReturn(TransactionStatus.Unpaid);
		paymentDetail.setPayment(payment);
		paymentDetail.setContractor(contractor);

		assertTrue(paymentDetail.isHasUnpaidInvoices());
	}

	private List<Invoice> buildMockInvoiceListNotSameCurrency() {
		List<Invoice> mockInvoices = new ArrayList<Invoice>();
		mockInvoices.add(buildMockInvoice(123, BigDecimal.valueOf(450), TransactionStatus.Paid, Currency.EUR));
		mockInvoices.add(buildMockInvoice(456, BigDecimal.valueOf(250), TransactionStatus.Unpaid, Currency.CAD));

		return mockInvoices;
	}

	private List<Invoice> buildMockInvoiceList() {
		List<Invoice> mockInvoices = new ArrayList<Invoice>();
		mockInvoices.add(buildMockInvoice(123, BigDecimal.valueOf(450), TransactionStatus.Paid, Currency.USD));
		mockInvoices.add(buildMockInvoice(456, BigDecimal.valueOf(250), TransactionStatus.Unpaid, Currency.USD));

		return mockInvoices;
	}

	private Invoice buildMockInvoice(int id, BigDecimal totalAmount, TransactionStatus status, Currency currency) {
		Invoice mockInvoice = Mockito.mock(Invoice.class);
		when(mockInvoice.getId()).thenReturn(id);
		when(mockInvoice.getTotalAmount()).thenReturn(totalAmount);
		when(mockInvoice.getStatus()).thenReturn(status);
		when(mockInvoice.getCurrency()).thenReturn(currency);

		return mockInvoice;
	}

}
