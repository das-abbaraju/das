package com.picsauditing.jpa.entities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CurrencyTest {

    @Test
    public void testNOK() {
        assertEquals(com.picsauditing.currency.Currency.NOK, Currency.NOK.toNewCurrency());
    }

    @Test
    public void testUSD() {
        assertEquals(com.picsauditing.currency.Currency.USD, Currency.USD.toNewCurrency());
    }


}
