package com.intuit.developer.adaptors;

import com.intuit.developer.QBSession;
import com.picsauditing.jpa.entities.Currency;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CustomerAdaptorTest {

    @Test
    public void testGetCurrencyCodeSuffixForQB_CHF() throws Exception {
        CustomerAdaptor customerAdaptor = new CustomerAdaptor();

        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.CHF.name());
        assertEquals(qbSession.getCurrencyCode(),customerAdaptor.getCurrencyCodeSuffixForQB(qbSession));
    }

    @Test
    public void testGetCurrencyCodeSuffixForQB_EUR() throws Exception {
        CustomerAdaptor customerAdaptor = new CustomerAdaptor();

        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.EUR.name());
        assertEquals(CustomerAdaptor.EUR_CURRENCY_SUFFIX,customerAdaptor.getCurrencyCodeSuffixForQB(qbSession));
    }

    @Test
    public void testGetCurrencyCodeSuffixForQB_GBP() throws Exception {
        CustomerAdaptor customerAdaptor = new CustomerAdaptor();

        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.GBP.name());
        assertEquals("",customerAdaptor.getCurrencyCodeSuffixForQB(qbSession));
    }

    @Test
    public void testGetCurrencyCodeSuffixForQB_USD() throws Exception {
        CustomerAdaptor customerAdaptor = new CustomerAdaptor();

        QBSession qbSession = new QBSession();
        qbSession.setCurrencyCode(Currency.USD.name());
        assertEquals("",customerAdaptor.getCurrencyCodeSuffixForQB(qbSession));
    }
}