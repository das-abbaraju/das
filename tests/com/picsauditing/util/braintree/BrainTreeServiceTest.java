package com.picsauditing.util.braintree;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.util.reflection.Whitebox.*;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;

////
import com.braintreegateway.exceptions.BraintreeException;
import com.picsauditing.util.braintree.BrainTreeResponse.BrainTreeRequest;
import com.picsauditing.util.braintree.BrainTreeService;
import com.picsauditing.util.braintree.CreditCard;
////

import com.picsauditing.PICS.NoBrainTreeServiceResponseException;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.util.StreamContentProvider;
import com.picsauditing.util.Strings;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class BrainTreeServiceTest {

	BrainTreeService upperclassUnderTest;
	BrainTreeRequest lowerclassUnderTest;

	@Mock AppPropertyDAO mockDao;
	@Mock StreamContentProvider mockContentProvider;
	Payment testPayment;
	Invoice testInvoice;

	@Before
	public void setup() throws Exception {
		testPayment = new Payment();
		Account testAccount = new Account();
		testAccount.setId(TESTING_ID);
		testPayment.setAccount(testAccount);
		testPayment.setTotalAmount(TESTING_TRANSACTION_AMOUNT);
		testInvoice = new Invoice();
		testInvoice.setId(TESTING_ID);

		upperclassUnderTest = new BrainTreeService();
		lowerclassUnderTest = new BrainTreeRequest();

		MockitoAnnotations.initMocks(this);
		setInternalState(lowerclassUnderTest, "dao", mockDao);
		setInternalState(lowerclassUnderTest, "contentProvider", mockContentProvider);
		setInternalState(upperclassUnderTest, "brainTree", lowerclassUnderTest);

		AppProperty fakeBTuserName = new AppProperty(), fakeBTpassword = new AppProperty();
		fakeBTuserName.setValue("foo");
		fakeBTpassword.setValue("bar");
		when(mockDao.find("brainTree.username")).thenReturn(fakeBTuserName);
		when(mockDao.find("brainTree.password")).thenReturn(fakeBTpassword);
	}

	@Test
	public void getCreditCard_emptyResponse () throws Exception {
		ByteArrayInputStream testStream = spy(new ByteArrayInputStream(EMPTY_XML_RESPONSE.getBytes()));
		when(mockContentProvider.openResponseFrom(anyString())).thenReturn(testStream);

		CreditCard resultingCard = upperclassUnderTest.getCreditCard(TESTING_ID);

		assertEquals(resultingCard, null);
		verify(mockContentProvider).openResponseFrom(CREDIT_CARD_REQUEST_URL);
		verify(testStream, atLeastOnce()).close();
	}

	@Test
	public void getCreditCard_successfulResponse () throws Exception {
		ByteArrayInputStream testStream = spy(new ByteArrayInputStream(CREDIT_CARD_XML_RESPONSE.getBytes()));
		when(mockContentProvider.openResponseFrom(anyString())).thenReturn(testStream);

		CreditCard resultingCard = upperclassUnderTest.getCreditCard(TESTING_ID);

		assertEquals(resultingCard.getSimpleExpirationDate(), TESTING_CARD_EXPIRATION);
		assertEquals(resultingCard.getCardNumber(), TESTING_CARD_NUMBER);
		verify(mockContentProvider).openResponseFrom(CREDIT_CARD_REQUEST_URL);
		verify(testStream, atLeastOnce()).close();
	}

	@Test
	public void deleteCreditCard () throws Exception {
		ByteArrayInputStream testStream = spy(new ByteArrayInputStream(CREDIT_CARD_XML_RESPONSE.getBytes()));
		when(mockContentProvider.openResponseFrom(anyString())).thenReturn(testStream);

		upperclassUnderTest.deleteCreditCard(TESTING_ID);

		verify(mockContentProvider).openResponseFrom(CREDIT_CARD_DELETE_URL);
		verify(testStream, atLeastOnce()).close();
	}

	@Test
	public void getTransactionCondition () throws Exception {
		ByteArrayInputStream testStream = spy(new ByteArrayInputStream(TRANSACTION_XML_RESPONSE.getBytes()));
		when(mockContentProvider.openResponseFrom(anyString())).thenReturn(testStream);

		String response = upperclassUnderTest.getTransactionCondition(TESTING_TRANSACTION_ID);

		assertEquals(response, TESTING_TRANSACTION_CONDITION);
		verify(mockContentProvider).openResponseFrom(TRANSACTION_CONDITION_URL);
		verify(testStream, atLeastOnce()).close();
	}

	@Test
	public void processRefund () throws Exception {
		when(mockContentProvider.getResponseFrom(anyString())).thenReturn(POSITIVE_RESPONSE_TEXT);

		assertTrue(upperclassUnderTest.processRefund(TESTING_TRANSACTION_ID, TESTING_TRANSACTION_AMOUNT));

		verify(mockContentProvider).getResponseFrom(REFUND_URL);
	}

	@Test(expected=Exception.class)
	public void processRefund_failure () throws Exception {
		when(mockContentProvider.getResponseFrom(anyString())).thenReturn(NEGATIVE_RESPONSE);

		try {
			assertTrue(upperclassUnderTest.processRefund(TESTING_TRANSACTION_ID, TESTING_TRANSACTION_AMOUNT));
		} catch (Exception e) {
			verify(mockContentProvider).getResponseFrom(REFUND_URL);
			assertEquals(e.getMessage(), NEGATIVE_RESPONSE_TEXT);
			throw e;
		}
		fail();
	}

	@Test
	public void processCancellation () throws Exception {
		when(mockContentProvider.getResponseFrom(anyString())).thenReturn(POSITIVE_RESPONSE_TEXT);

		assertTrue(upperclassUnderTest.processCancellation(TESTING_TRANSACTION_ID));

		verify(mockContentProvider).getResponseFrom(CANCELLATION_URL);
	}

	@Test(expected=Exception.class)
	public void processCancellation_failure () throws Exception {
		when(mockContentProvider.getResponseFrom(anyString())).thenReturn(NEGATIVE_RESPONSE);

		try {
			assertTrue(upperclassUnderTest.processCancellation(TESTING_TRANSACTION_ID));
		} catch (Exception e) {
			verify(mockContentProvider).getResponseFrom(CANCELLATION_URL);
			assertEquals(e.getMessage(), NEGATIVE_RESPONSE_TEXT);
			throw e;
		}
		fail();
	}

	@Test
	public void voidTransaction () throws Exception {
		when(mockContentProvider.getResponseFrom(anyString())).thenReturn(POSITIVE_RESPONSE_TEXT);

		assertTrue(upperclassUnderTest.voidTransaction(TESTING_TRANSACTION_ID));

		verify(mockContentProvider).getResponseFrom(VOID_URL);
	}

	@Test(expected=BraintreeException.class)
	public void voidTransaction_failure () throws Exception {
		when(mockContentProvider.getResponseFrom(anyString())).thenReturn(NEGATIVE_RESPONSE);

		try {
			assertTrue(upperclassUnderTest.voidTransaction(TESTING_TRANSACTION_ID));
		} catch (Exception e) {
			verify(mockContentProvider).getResponseFrom(VOID_URL);
			assertEquals(e.getMessage(), NEGATIVE_RESPONSE_TEXT);
			throw e;
		}
		fail();
	}

	@Test
	public void processPayment_canadian_withInvoice () throws Exception {
		setUpDAOForProcessors();
		testPayment.setCurrency(Currency.CAD);
		when(mockContentProvider.getResponseFrom(anyString())).thenReturn(POSITIVE_RESPONSE_TEXT);

		assertTrue(upperclassUnderTest.processPayment(testPayment, testInvoice));

		verify(mockContentProvider).getResponseFrom(CANADIAN_TEST_STRING_WITH_INVOICE);
		assertEquals(testPayment.getTransactionID(), TESTING_TRANSACTION_ID);
	}

	@Test
	public void processPayment_norwegian_withInvoice () throws Exception {
		setUpDAOForProcessors();
		testPayment.setCurrency(Currency.NOK);
		when(mockContentProvider.getResponseFrom(anyString())).thenReturn(POSITIVE_RESPONSE_TEXT);
		
		assertTrue(upperclassUnderTest.processPayment(testPayment, testInvoice));
		
		verify(mockContentProvider).getResponseFrom(NORWEGIAN_TEST_STRING_WITH_INVOICE);
		assertEquals(testPayment.getTransactionID(), TESTING_TRANSACTION_ID);
	}

	@Test
	public void processPayment_danish_withInvoice () throws Exception {
		setUpDAOForProcessors();
		testPayment.setCurrency(Currency.DKK);
		when(mockContentProvider.getResponseFrom(anyString())).thenReturn(POSITIVE_RESPONSE_TEXT);
		
		assertTrue(upperclassUnderTest.processPayment(testPayment, testInvoice));
		
		verify(mockContentProvider).getResponseFrom(DANISH_TEST_STRING_WITH_INVOICE);
		assertEquals(testPayment.getTransactionID(), TESTING_TRANSACTION_ID);
	}

	@Test
	public void processPayment_southAfrican_withInvoice () throws Exception {
		setUpDAOForProcessors();
		testPayment.setCurrency(Currency.ZAR);
		when(mockContentProvider.getResponseFrom(anyString())).thenReturn(POSITIVE_RESPONSE_TEXT);
		
		assertTrue(upperclassUnderTest.processPayment(testPayment, testInvoice));
		
		verify(mockContentProvider).getResponseFrom(SOUTHAFRICAN_TEST_STRING_WITH_INVOICE);
		assertEquals(testPayment.getTransactionID(), TESTING_TRANSACTION_ID);
	}

	@Test
	public void processPayment_swedish_withInvoice () throws Exception {
		setUpDAOForProcessors();
		testPayment.setCurrency(Currency.SEK);
		when(mockContentProvider.getResponseFrom(anyString())).thenReturn(POSITIVE_RESPONSE_TEXT);
		
		assertTrue(upperclassUnderTest.processPayment(testPayment, testInvoice));
		
		verify(mockContentProvider).getResponseFrom(SWEDISH_TEST_STRING_WITH_INVOICE);
		assertEquals(testPayment.getTransactionID(), TESTING_TRANSACTION_ID);
	}



	@Test(expected=BrainTreeCardDeclinedException.class)
	public void processPayment_us_noInvoice_failure () throws Exception {
		setUpDAOForProcessors();
		testPayment.setCurrency(Currency.USD);
		when(mockContentProvider.getResponseFrom(anyString())).thenReturn(NEGATIVE_RESPONSE);

		try {
			assertTrue(upperclassUnderTest.processPayment(testPayment, null));
		} catch (Exception e) {
			verify(mockContentProvider).getResponseFrom(US_TEST_STRING_WITHOUT_INVOICE);
			assertEquals(COMPLEX_FAILURE_RESPONSE_TEXT, e.getMessage());
			throw e;
		}
		fail();
	}

	@Test(expected=NoBrainTreeServiceResponseException.class)
	public void processPayment_euro_withInvoice_emptyResponse () throws Exception {
		setUpDAOForProcessors();
		testPayment.setCurrency(Currency.EUR);
		when(mockContentProvider.getResponseFrom(anyString())).thenReturn(Strings.EMPTY_STRING);

		try {
			assertTrue(upperclassUnderTest.processPayment(testPayment, testInvoice));
		} catch (Exception e) {
			verify(mockContentProvider).getResponseFrom(EURO_TEST_STRING_WITH_INVOICE);
			assertEquals("No response from BrainTree service recieved. Unable to verify procedure success.(No response.)", e.getMessage().trim());
			throw e;
		}
		fail();
	}

	@Test(expected=BrainTreeServiceErrorResponseException.class)
	public void processPayment_gbp_withoutInvoice_oddballResponse () throws Exception {
		setUpDAOForProcessors();
		testPayment.setCurrency(Currency.GBP);
		when(mockContentProvider.getResponseFrom(anyString())).thenReturn(OTHER_RESPONSE);

		try {
			assertTrue(upperclassUnderTest.processPayment(testPayment, null));
		} catch (Exception e) {
			verify(mockContentProvider).getResponseFrom(GBP_TEST_STRING_WITHOUT_INVOICE);
			assertEquals(OTHER_RESPONSE_TEXT, e.getMessage());
			throw e;
		}
		fail();
	}


	private void setUpDAOForProcessors() {
		AppProperty property1 = new AppProperty(),
					property2 = new AppProperty(),
					property3 = new AppProperty(),
					property4 = new AppProperty(),
					property5 = new AppProperty(),
					property6 = new AppProperty(),
					property7 = new AppProperty(),
					property8 = new AppProperty();
		property1.setValue("canadaprocessor");
		property2.setValue("britainprocessor");
		property3.setValue("europrocessor");
		property4.setValue("usprocessor");
		property5.setValue("dkkprocessor");
		property6.setValue("nokprocessor");
		property7.setValue("sekprocessor");
		property8.setValue("zarprocessor");
		when(mockDao.find("brainTree.processor_id.canada")).thenReturn(property1);
		when(mockDao.find("brainTree.processor_id.gbp")).thenReturn(property2);
		when(mockDao.find("brainTree.processor_id.eur")).thenReturn(property3);
		when(mockDao.find("brainTree.processor_id.us")).thenReturn(property4);
		when(mockDao.find("brainTree.processor_id.dkk")).thenReturn(property5);
		when(mockDao.find("brainTree.processor_id.nok")).thenReturn(property6);
		when(mockDao.find("brainTree.processor_id.sek")).thenReturn(property7);
		when(mockDao.find("brainTree.processor_id.zar")).thenReturn(property8);
	}


	private static final int TESTING_ID = 5;
	private static final String TESTING_CARD_NUMBER = "9240123456789";
	private static final String TESTING_CARD_EXPIRATION = "1209";
	private static final String TESTING_TRANSACTION_ID = "523";
	private static final String TESTING_TRANSACTION_CONDITION = "bjorked";
	private static final BigDecimal TESTING_TRANSACTION_AMOUNT = BigDecimal.valueOf(12.09);
	private static final String CREDIT_CARD_REQUEST_URL =
			"https://secure.braintreepaymentgateway.com/api/query.php?report_type=customer_vault&customer_vault_id=5&username=foo&password=bar";
	private static final String EMPTY_XML_RESPONSE =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<nm_response>\n</nm_response>";
	private static final String CREDIT_CARD_XML_RESPONSE =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<nm_response>\n\t<cc_number>9240123456789</cc_number>\n\t<cc_exp>1209</cc_exp>\n</nm_response>";
	private static final String CREDIT_CARD_DELETE_URL =
			"https://secure.braintreepaymentgateway.com/api/transact.php?customer_vault=delete_customer&customer_vault_id=5&username=foo&password=bar";
	private static final String TRANSACTION_CONDITION_URL =
			"https://secure.braintreepaymentgateway.com/api/query.php?transaction_id=523&username=foo&password=bar";
	private static final String TRANSACTION_XML_RESPONSE =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<nm_response>\n\t<condition>bjorked</condition>\n</nm_response>";
	private static final String POSITIVE_RESPONSE_TEXT = "response=1&responsetext=success&transactionid=523";
	private static final String NEGATIVE_RESPONSE = "response=2&responsetext=DENIED!!&response_code=999";
	private static final String NEGATIVE_RESPONSE_TEXT = "DENIED!!";
	private static final String OTHER_RESPONSE = "response=3&responsetext=FAIL!!&response_code=999";
	private static final String OTHER_RESPONSE_TEXT = "FAIL!! Unknown code 999";
	private static final String COMPLEX_FAILURE_RESPONSE_TEXT = "DENIED!! Unknown code 999";
	private static final String REFUND_URL =
			"https://secure.braintreepaymentgateway.com/api/transact.php?type=refund&transactionid=523&amount=12.09&username=foo&password=bar";
	private static final String CANCELLATION_URL =
			"https://secure.braintreepaymentgateway.com/api/transact.php?type=refund&transactionid=523&username=foo&password=bar";
	private static final String VOID_URL =
			"https://secure.braintreepaymentgateway.com/api/transact.php?type=void&transactionid=523&username=foo&password=bar";
	private static final String CANADIAN_TEST_STRING_WITH_INVOICE =
			"https://secure.braintreepaymentgateway.com/api/transact.php?type=sale&customer_vault_id=5&amount=12.09&currency=CAD&processor_id=canadaprocessor&order_id=5&username=foo&password=bar";
	private static final String US_TEST_STRING_WITHOUT_INVOICE =
			"https://secure.braintreepaymentgateway.com/api/transact.php?type=sale&customer_vault_id=5&amount=12.09&currency=USD&processor_id=usprocessor&username=foo&password=bar";
	private static final String EURO_TEST_STRING_WITH_INVOICE =
			"https://secure.braintreepaymentgateway.com/api/transact.php?type=sale&customer_vault_id=5&amount=12.09&currency=EUR&processor_id=europrocessor&order_id=5&username=foo&password=bar";
	private static final String GBP_TEST_STRING_WITHOUT_INVOICE =
			"https://secure.braintreepaymentgateway.com/api/transact.php?type=sale&customer_vault_id=5&amount=12.09&currency=GBP&processor_id=britainprocessor&username=foo&password=bar";
	private static final String DANISH_TEST_STRING_WITH_INVOICE = 
			"https://secure.braintreepaymentgateway.com/api/transact.php?type=sale&customer_vault_id=5&amount=12.09&currency=DKK&processor_id=dkkprocessor&order_id=5&username=foo&password=bar";
	private static final String SOUTHAFRICAN_TEST_STRING_WITH_INVOICE = 
			"https://secure.braintreepaymentgateway.com/api/transact.php?type=sale&customer_vault_id=5&amount=12.09&currency=ZAR&processor_id=zarprocessor&order_id=5&username=foo&password=bar";
	private static final String NORWEGIAN_TEST_STRING_WITH_INVOICE = 
			"https://secure.braintreepaymentgateway.com/api/transact.php?type=sale&customer_vault_id=5&amount=12.09&currency=NOK&processor_id=nokprocessor&order_id=5&username=foo&password=bar";
	private static final String SWEDISH_TEST_STRING_WITH_INVOICE =
			"https://secure.braintreepaymentgateway.com/api/transact.php?type=sale&customer_vault_id=5&amount=12.09&currency=SEK&processor_id=sekprocessor&order_id=5&username=foo&password=bar";
}
