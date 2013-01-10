package com.picsauditing.PICS;

import com.picsauditing.jpa.entities.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

public class TaxServiceTest {

	private TaxService taxService = new TaxService();
	@Mock private InvoiceService invoiceService;
	@Mock private Invoice invoice;
	@Mock private Account account;
	private ArrayList<InvoiceItem> invoiceItems;
	private InvoiceFee taxInvoiceFee;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(taxService, "invoiceService", invoiceService);
		setupMocks();
	}

	@Test
	public void testApplyTax() throws Exception {

		taxInvoiceFee = createTaxInvoiceFee("TaxFee", 5, 5, "2012-04-01");
		when(invoiceService.getCanadianTaxInvoiceFeeForProvince(any(CountrySubdivision.class))).thenReturn(taxInvoiceFee);

		invoiceItems = new ArrayList<InvoiceItem>(){{
			add(createInvoiceItem(1, createInvoiceFee(1, "Fee1"), new BigDecimal("100.00")));
			add(createInvoiceItem(2, createInvoiceFee(2, "Fee2"), new BigDecimal("200.00")));
			add(createInvoiceItem(3, createInvoiceFee(3, "Fee3"), new BigDecimal("300.00")));
			add(createInvoiceItem(4, createInvoiceFee(4, "Fee4"), new BigDecimal("400.00")));
		}};
		when(invoice.getItems()).thenReturn(invoiceItems);
		assertEquals(4, invoice.getItems().size());

		taxService.applyTax(invoice);

		assertEquals(5, invoice.getItems().size());
		InvoiceItem taxItem = invoice.getItems().get(4);
		assertEquals(invoice, taxItem.getInvoice());
		assertEquals(taxInvoiceFee, taxItem.getInvoiceFee());
		assertEquals(new BigDecimal("100.00"), taxItem.getAmount());
		verify(invoice).updateAmount();
	}

	@Test
	public void testApplyTax_updateExistingTaxItemIfFound() throws Exception {

		taxInvoiceFee = createTaxInvoiceFee("TaxFee", 5, 9.975, "2012-04-01");
		when(invoiceService.getCanadianTaxInvoiceFeeForProvince(any(CountrySubdivision.class))).thenReturn(taxInvoiceFee);
		invoiceItems = new ArrayList<InvoiceItem>(){{
			add(createInvoiceItem(1, createInvoiceFee(1, "Fee1"), new BigDecimal("100.00")));
			add(createInvoiceItem(2, createInvoiceFee(2, "Fee2"), new BigDecimal("200.00")));
			add(createInvoiceItem(3, createInvoiceFee(3, "Fee3"), new BigDecimal("300.00")));
			add(createInvoiceItem(4, createInvoiceFee(4, "Fee4"), new BigDecimal("400.00")));
			add(createInvoiceItem(5, taxInvoiceFee, new BigDecimal("50.00")));
			add(createInvoiceItem(6, createInvoiceFee(6, "SomeNewFeeJustAdded"), new BigDecimal("49.99")));
		}};
		when(invoice.getItems()).thenReturn(invoiceItems);
		assertEquals(6, invoice.getItems().size());

		taxService.applyTax(invoice);

		assertEquals(6, invoice.getItems().size());
		InvoiceItem taxItem = invoice.getItems().get(4);
		assertEquals(taxInvoiceFee, taxItem.getInvoiceFee());
		assertEquals(new BigDecimal("157.24"), taxItem.getAmount());
		verify(invoice).updateAmount();
	}

	private void setupMocks() throws Exception {
		when(account.getCountrySubdivision()).thenReturn(new CountrySubdivision("foo"));
		when(invoice.getCurrency()).thenReturn(Currency.CAD);
		when(invoice.getAccount()).thenReturn(account);
	}

	private InvoiceItem createInvoiceItem(int id, InvoiceFee invoiceFee, BigDecimal amount) {
		InvoiceItem invoiceItem = new InvoiceItem();
		invoiceItem.setId(id);
		invoiceItem.setInvoiceFee(invoiceFee);
		invoiceItem.setAmount(amount);
		return invoiceItem;
	}

	private InvoiceFee createInvoiceFee(int id, String feeName) {
		InvoiceFee invoiceFee = new InvoiceFee();
		invoiceFee.setId(id);
		TranslatableString fee = new TranslatableString();
		fee.setKey(feeName);
		invoiceFee.setFee(fee);
		return invoiceFee;
	}

	private InvoiceFee createTaxInvoiceFee(String feeName, double invoiceFeeRate, double subdivisionRate, String effectiveDate) {
		InvoiceFee invoiceFee = new InvoiceFee();
		TranslatableString fee = new TranslatableString();
		fee.setKey(feeName);
		invoiceFee.setFee(fee);
		invoiceFee.setRatePercent(new BigDecimal(invoiceFeeRate));
		invoiceFee.setFeeClass(FeeClass.CanadianTax);
		invoiceFee.setQbFullName("qb" + feeName);

		InvoiceFeeCountry invoiceFeeCountry = new InvoiceFeeCountry();
		invoiceFeeCountry.setRatePercent(new BigDecimal(subdivisionRate));
		invoiceFeeCountry.setEffectiveDate(DateBean.parseDate(effectiveDate));
		invoiceFee.setSubdivisionFee(invoiceFeeCountry);
		return invoiceFee;
	}

}
