package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class CurrencyTest {
	@Test
	public void testGetTaxFee_TestEuro() throws Exception {			
		assertNull(Currency.EUR.getTaxFee());
	}

	@Test
	public void testGetTaxFee_TestGBP() throws Exception {
		assertEquals(FeeClass.VAT, Currency.GBP.getTaxFee().getFeeClass());
	}

	@Test
	public void testGetTaxFee_TestCAD() throws Exception {
		assertEquals(FeeClass.GST, Currency.CAD.getTaxFee().getFeeClass());
	}

	@Test
	public void testGetTaxFee_TestUSD() throws Exception {
		assertNull(Currency.USD.getTaxFee());
	}
}
