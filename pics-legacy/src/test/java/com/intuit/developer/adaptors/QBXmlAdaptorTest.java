package com.intuit.developer.adaptors;

import com.intuit.developer.QBSession;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.quickbooks.qbxml.CurrencyRef;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QBXmlAdaptorTest {

    @Test
    public void testGetQBListID_CHF() throws Exception {
        QBXmlAdaptor qBXmlAdaptor = new QBXmlAdaptor();
        String qbListId = (String) Whitebox.invokeMethod(qBXmlAdaptor, "getQBListID", Currency.CHF);
        assertEquals("qbListCHID", qbListId);
    }

    @Test
    public void testGetQBListID_CAD() throws Exception {
        QBXmlAdaptor qBXmlAdaptor = new QBXmlAdaptor();
        String qbListId = (String) Whitebox.invokeMethod(qBXmlAdaptor, "getQBListID", Currency.CAD);
        assertEquals("qbListCAID", qbListId);
    }

    @Test
    public void testGetQBListID_GBP() throws Exception {
        QBXmlAdaptor qBXmlAdaptor = new QBXmlAdaptor();
        String qbListId = (String) Whitebox.invokeMethod(qBXmlAdaptor, "getQBListID", Currency.GBP);
        assertEquals("qbListUKID", qbListId);
    }

    @Test
    public void testGetQBListID_EUR() throws Exception {
        QBXmlAdaptor qBXmlAdaptor = new QBXmlAdaptor();
        String qbListId = (String) Whitebox.invokeMethod(qBXmlAdaptor, "getQBListID", Currency.EUR);
        assertEquals("qbListEUID", qbListId);
    }

    @Test
    public void testGetQBListID_default() throws Exception {
        QBXmlAdaptor qBXmlAdaptor = new QBXmlAdaptor();
        String qbListId = (String) Whitebox.invokeMethod(qBXmlAdaptor, "getQBListID", Currency.USD);
        assertEquals("qbListID", qbListId);
    }

    @Test
      public void testGetAccountsReceivableAccountRef_EUR() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.EUR.name());
        QBXmlAdaptor qBXmlAdaptor = new QBXmlAdaptor();

        String accountsReceivableAccountRef = qBXmlAdaptor.getAccountsReceivableAccountRef(qbSession);

        assertEquals(accountsReceivableAccountRef, QBXmlAdaptor.ACCOUNTS_RECEIVABLE_EURO);
    }

    @Test
    public void testGetAccountsReceivableAccountRef_CHF() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.CHF.name());
        QBXmlAdaptor qBXmlAdaptor = new QBXmlAdaptor();

        String accountsReceivableAccountRef = qBXmlAdaptor.getAccountsReceivableAccountRef(qbSession);

        assertEquals(accountsReceivableAccountRef, QBXmlAdaptor.ACCOUNTS_RECEIVABLE_CHF);
    }

    @Test
    public void testGetAccountsReceivableAccountRef_PLN() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.PLN.name());
        QBXmlAdaptor qBXmlAdaptor = new QBXmlAdaptor();

        String accountsReceivableAccountRef = qBXmlAdaptor.getAccountsReceivableAccountRef(qbSession);

        assertEquals(accountsReceivableAccountRef, QBXmlAdaptor.ACCOUNTS_RECEIVABLE_PLN);
    }
    @Test
    public void testGetAccountsReceivableAccountRef_Others() throws Exception {
        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.USD.name());
        QBXmlAdaptor qBXmlAdaptor = new QBXmlAdaptor();

        String accountsReceivableAccountRef = qBXmlAdaptor.getAccountsReceivableAccountRef(qbSession);

        assertEquals(accountsReceivableAccountRef, QBXmlAdaptor.ACCOUNTS_RECEIVABLE);
    }

    @Test
    public void testGetCurrencyRefFullName_EUR() throws Exception {
        ContractorAccount contractorAccount =  mock(ContractorAccount.class);
        Country country = mock(Country.class);

        when(contractorAccount.getCountry()).thenReturn(country);
        when(country.getCurrency()).thenReturn(Currency.EUR);

        String currencyRefOutput = QBXmlAdaptor.getCurrencyRefFullName(contractorAccount);

        assertEquals(currencyRefOutput, QBXmlAdaptor.EURO_FULL_NAME);
    }

    @Test
    public void testGetCurrencyRefFullName_CHF() throws Exception {
        ContractorAccount contractorAccount =  mock(ContractorAccount.class);
        Country country = mock(Country.class);

        when(contractorAccount.getCountry()).thenReturn(country);
        when(country.getCurrency()).thenReturn(Currency.CHF);

        String currencyRefOutput = QBXmlAdaptor.getCurrencyRefFullName(contractorAccount);

        assertEquals(currencyRefOutput, QBXmlAdaptor.CHF_FULL_NAME);
    }

    @Test
    public void testGetCurrencyRefFullName_PLN() throws Exception {
        ContractorAccount contractorAccount =  mock(ContractorAccount.class);
        Country country = mock(Country.class);

        when(contractorAccount.getCountry()).thenReturn(country);
        when(country.getCurrency()).thenReturn(Currency.PLN);

        String currencyRefOutput = QBXmlAdaptor.getCurrencyRefFullName(contractorAccount);

        assertEquals(currencyRefOutput, QBXmlAdaptor.PLN_FULL_NAME);
    }
    @Test
    public void testGetCurrencyRefFullName_GBP() throws Exception {
        ContractorAccount contractorAccount =  mock(ContractorAccount.class);
        Country country = mock(Country.class);

        when(contractorAccount.getCountry()).thenReturn(country);
        when(country.getCurrency()).thenReturn(Currency.GBP);

        String currencyRefOutput = QBXmlAdaptor.getCurrencyRefFullName(contractorAccount);

        assertEquals(currencyRefOutput, QBXmlAdaptor.GBP_FULL_NAME);
    }

    @Test
    public void testGetCurrencyRefFullName_Others() throws Exception {
        ContractorAccount contractorAccount =  mock(ContractorAccount.class);
        Country country = mock(Country.class);

        when(contractorAccount.getCountry()).thenReturn(country);
        when(country.getCurrency()).thenReturn(Currency.USD);

        String currencyRefOutput = QBXmlAdaptor.getCurrencyRefFullName(contractorAccount);

        assertEquals(currencyRefOutput, null);
    }
}
