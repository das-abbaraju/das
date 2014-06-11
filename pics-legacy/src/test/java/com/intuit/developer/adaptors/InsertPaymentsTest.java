package com.intuit.developer.adaptors;

import com.intuit.developer.QBSession;
import com.picsauditing.jpa.entities.Currency;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;

public class InsertPaymentsTest {

    InsertPayments insertPayments;

    @Before
    public void setUp() throws Exception {
        insertPayments = new InsertPayments();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetAmexAccountName_CHF() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.CHF.name());
        String result = (String) Whitebox.invokeMethod(insertPayments, "getAmexCreditCardAccountName", qbSession);
        assertEquals(null,result);
    }

    @Test
    public void testGetAmexAccountName_USD() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.USD.name());
        String result = (String) Whitebox.invokeMethod(insertPayments, "getAmexCreditCardAccountName", qbSession);
        assertEquals(InsertPayments.AMEX_MERCHANT_ACCOUNT,result);
    }

    @Test
    public void testGetAmexAccountName_GBP() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.GBP.name());
        String result = (String) Whitebox.invokeMethod(insertPayments, "getAmexCreditCardAccountName", qbSession);
        assertEquals(InsertPayments.AMEX_MERCHANT_ACCOUNT,result);
    }

    @Test
    public void testGetAmexAccountName_EUR() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.EUR.name());
        String result = (String) Whitebox.invokeMethod(insertPayments, "getAmexCreditCardAccountName", qbSession);
        assertEquals(InsertPayments.AMEX_MERCHANT_ACCOUNT_EURO,result);
    }

    @Test
    public void testGetVisaMCDiscAccountName_CHF() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.CHF.name());
        String result = (String) Whitebox.invokeMethod(insertPayments, "getVisaMCDiscCreditCardAccountName", qbSession);
        assertEquals(InsertPayments.VISA_CHF,result);
    }

    @Test
    public void testGetVisaMCDiscAccountName_USD() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.USD.name());
        String result = (String) Whitebox.invokeMethod(insertPayments, "getVisaMCDiscCreditCardAccountName", qbSession);
        assertEquals(InsertPayments.VISA_MC_DISC_MERCHANT_ACCOUNT,result);
    }

    @Test
    public void testGetVisaMCDiscAccountName_GBP() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.GBP.name());
        String result = (String) Whitebox.invokeMethod(insertPayments, "getVisaMCDiscCreditCardAccountName", qbSession);
        assertEquals(InsertPayments.VISA_MC_DISC_MERCHANT_ACCOUNT,result);
    }

    @Test
    public void testGetVisaMCDiscAccountName_EUR() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.EUR.name());
        String result = (String) Whitebox.invokeMethod(insertPayments, "getVisaMCDiscCreditCardAccountName", qbSession);
        assertEquals(InsertPayments.VISA_MC_DISC_MERCHANT_ACCT_EURO,result);
    }

    @Test
    public void testGetUndepositedFundsAccountName_CHF() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.CHF.name());
        String result = (String) Whitebox.invokeMethod(insertPayments, "getUnDepositedFundsAccountName", qbSession);
        assertEquals(InsertPayments.UNDEPOSITED_FUNDS_CHF,result);
    }

    @Test
    public void testGetUndepositedFundsAccountName_USD() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.USD.name());
        String result = (String) Whitebox.invokeMethod(insertPayments, "getUnDepositedFundsAccountName", qbSession);
        assertEquals(InsertPayments.UNDEPOSITED_FUNDS,result);
    }

    @Test
    public void testGetUndepositedFundsAccountName_GBP() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.GBP.name());
        String result = (String) Whitebox.invokeMethod(insertPayments, "getUnDepositedFundsAccountName", qbSession);
        assertEquals(InsertPayments.UNDEPOSITED_FUNDS,result);
    }

    @Test
    public void testGetUndepositedFundsAccountName_EUR() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.EUR.name());
        String result = (String) Whitebox.invokeMethod(insertPayments, "getUnDepositedFundsAccountName", qbSession);
        assertEquals(InsertPayments.UNDEPOSITED_FUNDS_EURO,result);
    }
}