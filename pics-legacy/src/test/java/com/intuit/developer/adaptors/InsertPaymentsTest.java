package com.intuit.developer.adaptors;

import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.quickbooks.model.CreditCard;
import com.picsauditing.quickbooks.model.CreditCardAccount;
import com.picsauditing.quickbooks.model.UnDepositedFundsAccount;
import com.picsauditing.quickbooks.qbxml.DepositToAccountRef;
import com.picsauditing.quickbooks.qbxml.PaymentMethodRef;
import com.picsauditing.quickbooks.qbxml.ReceivePaymentAdd;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InsertPaymentsTest {

    InsertPayments insertPayments;

    @Before
    public void setUp() throws Exception {
        insertPayments = new InsertPayments();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test(expected=InvalidQBCreditCardException.class)
    public void testProcessCreditCardPayment_CHF_Amex_isNotAllowed() throws Exception {
        Payment paymentJPA = new Payment();
        ReceivePaymentAdd receivePaymentsAdd = mock(ReceivePaymentAdd.class);
        when(receivePaymentsAdd.getPaymentMethodRef()).thenReturn(new PaymentMethodRef());

        insertPayments.processCreditCardPayment(Currency.CHF, paymentJPA, receivePaymentsAdd, CreditCard.AMEX);
    }

    @Test(expected=InvalidQBCreditCardException.class)
    public void testProcessCreditCardPayment_PLN_Amex_isNotAllowed() throws Exception {
        Payment paymentJPA = new Payment();
        paymentJPA.setTransactionID("abc123");
        ReceivePaymentAdd receivePaymentsAdd = mock(ReceivePaymentAdd.class);
        when(receivePaymentsAdd.getPaymentMethodRef()).thenReturn(new PaymentMethodRef());

        insertPayments.processCreditCardPayment(Currency.PLN, paymentJPA, receivePaymentsAdd, CreditCard.AMEX);
    }

    @Test
    public void testProcessCreditCardPayment_USD_Amex() throws Exception {
        Payment paymentJPA = createPayment("hello123");
        ReceivePaymentAdd receivePaymentsAdd = createReceivePaymentAdd();

        insertPayments.processCreditCardPayment(Currency.USD, paymentJPA, receivePaymentsAdd, CreditCard.AMEX);

        assertEquals("Braintree AMEX", receivePaymentsAdd.getPaymentMethodRef().getFullName());
        assertEquals(CreditCardAccount.AMEX_MERCHANT_ACCOUNT.getAccountName(), receivePaymentsAdd.getDepositToAccountRef().getFullName());
        assertEquals(paymentJPA.getTransactionID(), receivePaymentsAdd.getRefNumber());
    }

    @Test
    public void testProcessCreditCardPayment_CAD_Amex() throws Exception {
        Payment paymentJPA = createPayment("hello123");
        ReceivePaymentAdd receivePaymentsAdd = createReceivePaymentAdd();

        insertPayments.processCreditCardPayment(Currency.CAD, paymentJPA, receivePaymentsAdd, CreditCard.AMEX);

        assertEquals("Braintree AMEX", receivePaymentsAdd.getPaymentMethodRef().getFullName());
        assertEquals(CreditCardAccount.AMEX_MERCHANT_ACCOUNT.getAccountName(), receivePaymentsAdd.getDepositToAccountRef().getFullName());
        assertEquals("hello123", receivePaymentsAdd.getRefNumber());
    }

    @Test
    public void testProcessCreditCardPayment_GBP_Amex() throws Exception {
        Payment paymentJPA = createPayment("hello123");
        ReceivePaymentAdd receivePaymentsAdd = createReceivePaymentAdd();

        insertPayments.processCreditCardPayment(Currency.GBP, paymentJPA, receivePaymentsAdd, CreditCard.AMEX);

        assertEquals("Braintree AMEX", receivePaymentsAdd.getPaymentMethodRef().getFullName());
        assertEquals(CreditCardAccount.AMEX_MERCHANT_ACCOUNT.getAccountName(), receivePaymentsAdd.getDepositToAccountRef().getFullName());
        assertEquals("hello123", receivePaymentsAdd.getRefNumber());
    }

    @Test
    public void testProcessCreditCardPayment_EUR_Amex() throws Exception {
        Payment paymentJPA = createPayment("hello123");
        ReceivePaymentAdd receivePaymentsAdd = createReceivePaymentAdd();

        insertPayments.processCreditCardPayment(Currency.EUR, paymentJPA, receivePaymentsAdd, CreditCard.AMEX);

        assertEquals("Braintree AMEX", receivePaymentsAdd.getPaymentMethodRef().getFullName());
        assertEquals(CreditCardAccount.AMEX_MERCHANT_ACCOUNT_EURO.getAccountName(), receivePaymentsAdd.getDepositToAccountRef().getFullName());
        assertEquals("hello123", receivePaymentsAdd.getRefNumber());
    }

    // todo: These will need to be converted with more refactoring like what we did for processCreditCardPayment()
    @Test
    public void testGetUndepositedFundsAccountName_CHF() throws Exception {
        String result = (String) Whitebox.invokeMethod(insertPayments, "getUnDepositedFundsAccountName", Currency.CHF);
        assertEquals(UnDepositedFundsAccount.UNDEPOSITED_FUNDS_CHF.getAccountName(), result);
    }

    @Test
    public void testGetUndepositedFundsAccountName_USD() throws Exception {
        String result = (String) Whitebox.invokeMethod(insertPayments, "getUnDepositedFundsAccountName", Currency.USD);
        assertEquals(UnDepositedFundsAccount.UNDEPOSITED_FUNDS.getAccountName(), result);
    }

    @Ignore
    public void testGetUndepositedFundsAccountName_GBP() throws Exception {
        String result = (String) Whitebox.invokeMethod(insertPayments, "getUnDepositedFundsAccountName", Currency.GBP);
        assertEquals(UnDepositedFundsAccount.UNDEPOSITED_FUNDS.getAccountName(), result);
    }

    @Test
    public void testGetUndepositedFundsAccountName_EUR() throws Exception {
        String result = (String) Whitebox.invokeMethod(insertPayments, "getUnDepositedFundsAccountName", Currency.EUR);
        assertEquals(UnDepositedFundsAccount.UNDEPOSITED_FUNDS_EURO.getAccountName(), result);
    }

    @Test
    public void testGetUndepositedFundsAccountName_PLN() throws Exception {
        String result = (String) Whitebox.invokeMethod(insertPayments, "getUnDepositedFundsAccountName", Currency.PLN);
        assertEquals(UnDepositedFundsAccount.UNDEPOSITED_FUNDS_PLN.getAccountName(), result);
    }

    private ReceivePaymentAdd createReceivePaymentAdd() {
        ReceivePaymentAdd receivePaymentsAdd = new ReceivePaymentAdd();
        receivePaymentsAdd.setPaymentMethodRef(new PaymentMethodRef());
        receivePaymentsAdd.setDepositToAccountRef(new DepositToAccountRef());

        return receivePaymentsAdd;
    }

    private Payment createPayment(String transactionID) {
        Payment payment = new Payment();
        payment.setTransactionID(transactionID);
        return payment;
    }
}
