package com.picsauditing.jpa.entities;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class InvoiceFeeTest {

	InvoiceFee invoiceFee = new InvoiceFee();
	InvoiceFeeCountry invoiceFeeCountry = new InvoiceFeeCountry();

	@Test
	public void testGetTax_CanadianTaxFeeClass() throws Exception {

		BigDecimal gstRatePercent = new BigDecimal("5");
		invoiceFee.setRatePercent(gstRatePercent);
		invoiceFee.setFeeClass(FeeClass.CanadianTax);
		BigDecimal provinceRatePercent = new BigDecimal("9.75");
		invoiceFeeCountry.setRatePercent(provinceRatePercent);
		invoiceFee.setSubdivisionFee(invoiceFeeCountry);
		BigDecimal amountToTax = new BigDecimal("125.79");

		BigDecimal taxAmount = invoiceFee.getTax(amountToTax);

		assertEquals(new BigDecimal("18.56"), taxAmount);
	}
}
