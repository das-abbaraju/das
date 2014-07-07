package com.picsauditing.quickbooks.model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CreditCardTest {

    @Test
    public void testCreditCardFromName_Visa() throws Exception {
        CreditCard creditCard = CreditCard.fromName("Visa");

        assertTrue(creditCard == CreditCard.VISA);
    }

    @Test
    public void testCreditCardFromName_Mastercard() throws Exception {
        CreditCard creditCard = CreditCard.fromName("Mastercard");

        assertTrue(creditCard == CreditCard.MASTERCARD);
    }

    @Test
    public void testCreditCardFromName_Discover() throws Exception {
        CreditCard creditCard = CreditCard.fromName("Discover");

        assertTrue(creditCard == CreditCard.DISCOVER);
    }

    @Test
    public void testCreditCardFromName_American_Express() throws Exception {
        CreditCard creditCard = CreditCard.fromName("American Express");

        assertTrue(creditCard == CreditCard.AMEX);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreditCardFromName_Others() throws Exception {
        CreditCard creditCard = CreditCard.fromName("Amazon");
    }
}
