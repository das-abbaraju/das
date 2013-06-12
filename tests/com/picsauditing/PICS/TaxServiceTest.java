package com.picsauditing.PICS;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceFeeCountry;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.toggle.FeatureToggle;

public class TaxServiceTest {

	private TaxService taxService = new TaxService();

	@Mock
	private InvoiceService invoiceService;
	@Mock
	private Invoice invoice;
	@Mock
	private Account account;
	@Mock
	private FeatureToggle featureToggle;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(taxService, "invoiceService", invoiceService);
		Whitebox.setInternalState(taxService, "featureToggle", featureToggle);
		setupMocks();
	}

	@Test
	public void testFindTaxInvoiceFee_WhenInvoiceHasTax_ReturnsThatTax() {
		final InvoiceFee existingTaxInvoiceFee = createTaxInvoiceFee(FeeClass.CanadianTax, 5, 9.975);
		ArrayList<InvoiceItem> invoiceItems = buildInvoiceItemsWithTax(existingTaxInvoiceFee);
		when(invoice.getItems()).thenReturn(invoiceItems);

		InvoiceItem foundInvoiceItem = taxService.findTaxInvoiceItem(invoice);

		assertEquals(existingTaxInvoiceFee, foundInvoiceItem.getInvoiceFee());
	}

	@Test
	public void testFindTaxInvoiceFee_WhenNoTaxInInvoice_ReturnsNull() {
		ArrayList<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>() {
			{
				add(createInvoiceItem(1, createInvoiceFee(1, "Fee1"), new BigDecimal("100.00")));
				add(createInvoiceItem(2, createInvoiceFee(2, "Fee2"), new BigDecimal("200.00")));
			}
		};
		when(invoice.getItems()).thenReturn(invoiceItems);

		InvoiceItem foundInvoiceItem = taxService.findTaxInvoiceItem(invoice);

		assertEquals(null, foundInvoiceItem);
	}

	@Test
	public void testApplyTax_whenInvoiceHasExistingNewCanadianTaxAndToggleIsOn_DontChangeTheTax() throws Exception {
		final InvoiceFee existingTaxInvoiceFee = createTaxInvoiceFeeWithoutSubdivisionRate(FeeClass.CanadianTax, 5);
		InvoiceFeeCountry provinceTaxFee = new InvoiceFeeCountry();
		provinceTaxFee.setRatePercent(new BigDecimal("9.975"));
		when(invoiceService.getProvinceTaxFee(any(CountrySubdivision.class))).thenReturn(provinceTaxFee);
		ArrayList<InvoiceItem> invoiceItems = buildInvoiceItemsWithTax(existingTaxInvoiceFee);
		Invoice invoice = buildInvoice(invoiceItems, Currency.CAD);
		when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_CANADIAN_TAX)).thenReturn(true);

		taxService.applyTax(invoice);

		InvoiceItem newTaxInvoiceItem = taxService.findTaxInvoiceItem(invoice);
		assertEquals(existingTaxInvoiceFee, newTaxInvoiceItem.getInvoiceFee());
	}

	@Test
	public void testApplyTax_whenInvoiceHasExistingNewCanadianTaxAndToggleIsOff_DontChangeTheTax() throws Exception {
		final InvoiceFee existingTaxInvoiceFee = createTaxInvoiceFeeWithoutSubdivisionRate(FeeClass.CanadianTax, 5);
		InvoiceFeeCountry provinceTaxFee = new InvoiceFeeCountry();
		provinceTaxFee.setRatePercent(new BigDecimal("9.975"));
		when(invoiceService.getProvinceTaxFee(any(CountrySubdivision.class))).thenReturn(provinceTaxFee);
		ArrayList<InvoiceItem> invoiceItems = buildInvoiceItemsWithTax(existingTaxInvoiceFee);
		Invoice invoice = new Invoice();
		invoice.setItems(invoiceItems);
		when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_CANADIAN_TAX)).thenReturn(false);

		taxService.applyTax(invoice);

		InvoiceItem newTaxInvoiceItem = taxService.findTaxInvoiceItem(invoice);
		assertEquals(existingTaxInvoiceFee, newTaxInvoiceItem.getInvoiceFee());
	}

	@Test
	public void testApplyTax_whenInvoiceHasExistingLegacyGstAndToggleIsOn_DontChangeTheTax() throws Exception {
		final InvoiceFee existingTaxInvoiceFee = createTaxInvoiceFee(FeeClass.GST);
		ArrayList<InvoiceItem> invoiceItems = buildInvoiceItemsWithTax(existingTaxInvoiceFee);
		Invoice invoice = buildInvoice(invoiceItems, Currency.CAD);
		when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_CANADIAN_TAX)).thenReturn(true);

		taxService.applyTax(invoice);

		InvoiceItem newTaxInvoiceItem = taxService.findTaxInvoiceItem(invoice);
		assertEquals(existingTaxInvoiceFee, newTaxInvoiceItem.getInvoiceFee());
	}

	@Test
	public void testApplyTax_whenInvoiceHasExistingLegacyGstAndToggleIsOff_DontChangeTheTax() throws Exception {
		final InvoiceFee existingTaxInvoiceFee = createTaxInvoiceFee(FeeClass.GST);
		ArrayList<InvoiceItem> invoiceItems = buildInvoiceItemsWithTax(existingTaxInvoiceFee);
		Invoice invoice = buildInvoice(invoiceItems, Currency.CAD);
		when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_CANADIAN_TAX)).thenReturn(false);

		taxService.applyTax(invoice);

		InvoiceItem newTaxInvoiceItem = taxService.findTaxInvoiceItem(invoice);
		assertEquals(existingTaxInvoiceFee, newTaxInvoiceItem.getInvoiceFee());
	}

	@Test
	public void testApplyTax_whenInvoiceHasExistingVatAndToggleIsOn_DontChangeTheTax() throws Exception {
		final InvoiceFee existingTaxInvoiceFee = createTaxInvoiceFee(FeeClass.VAT);
		ArrayList<InvoiceItem> invoiceItems = buildInvoiceItemsWithTax(existingTaxInvoiceFee);
		Invoice invoice = buildInvoice(invoiceItems, Currency.EUR);

		when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_CANADIAN_TAX)).thenReturn(true);

		taxService.applyTax(invoice);

		InvoiceItem newTaxInvoiceItem = taxService.findTaxInvoiceItem(invoice);
		assertEquals(existingTaxInvoiceFee, newTaxInvoiceItem.getInvoiceFee());
	}

	@Test
	public void testApplyTax_whenInvoiceHasExistingVatAndToggleIsOff_DontChangeTheTax() throws Exception {
		final InvoiceFee existingTaxInvoiceFee = createTaxInvoiceFee(FeeClass.VAT);
		ArrayList<InvoiceItem> invoiceItems = buildInvoiceItemsWithTax(existingTaxInvoiceFee);
		Invoice invoice = buildInvoice(invoiceItems, Currency.EUR);
		when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_CANADIAN_TAX)).thenReturn(false);

		taxService.applyTax(invoice);

		InvoiceItem newTaxInvoiceItem = taxService.findTaxInvoiceItem(invoice);
		assertEquals(existingTaxInvoiceFee, newTaxInvoiceItem.getInvoiceFee());
	}

	@Test
	public void testApplyTax_whenInvoiceHasNoTaxAndToggleIsOn_ApplyNewCanadianTax() throws Exception {
		InvoiceFee taxInvoiceFee = createTaxInvoiceFee(FeeClass.CanadianTax, 5, 9.975);
		when(invoiceService.getCanadianTaxInvoiceFeeForProvince(any(CountrySubdivision.class))).thenReturn(
				taxInvoiceFee);
		ArrayList<InvoiceItem> invoiceItems = buildInvoiceItemsWithoutTax();
		Invoice invoice = buildInvoice(invoiceItems, Currency.CAD);
		int beforeInvoiceItemCount = invoice.getItems().size();
		when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_CANADIAN_TAX)).thenReturn(true);

		taxService.applyTax(invoice);

		assertEquals(beforeInvoiceItemCount + 1, invoiceItems.size());
		InvoiceItem taxInvoiceItem = taxService.findTaxInvoiceItem(invoice);
		assertEquals(FeeClass.CanadianTax, taxInvoiceItem.getInvoiceFee().getFeeClass());
		assertEquals(new BigDecimal("44.93"), taxInvoiceItem.getAmount());
		assertEquals(new BigDecimal("344.93"), invoice.getTotalAmount());
	}

	@Test
	public void testApplyTax_whenInvoiceHasNoTaxAndToggleIsOff_ApplyLegacyGst() throws Exception {
		ArrayList<InvoiceItem> invoiceItems = buildInvoiceItemsWithoutTax();
		Invoice invoice = buildInvoice(invoiceItems, Currency.CAD);
		int beforeInvoiceItemCount = invoice.getItems().size();
		when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_USE_NEW_CANADIAN_TAX)).thenReturn(false);

		taxService.applyTax(invoice);

		assertEquals(beforeInvoiceItemCount + 1, invoiceItems.size());
		InvoiceItem taxInvoiceItem = taxService.findTaxInvoiceItem(invoice);
		assertEquals(FeeClass.GST, taxInvoiceItem.getInvoiceFee().getFeeClass());
		assertEquals(new BigDecimal("15.00"), taxInvoiceItem.getAmount());
		assertEquals(new BigDecimal("315.00"), invoice.getTotalAmount());
	}

	@Test
	public void testApplyTax_whenInvoiceHasNoTaxAndCurrencyIsGbp_ApplyVat() throws Exception {
		ArrayList<InvoiceItem> invoiceItems = buildInvoiceItemsWithoutTax();
		Invoice invoice = buildInvoice(invoiceItems, Currency.GBP);
		int beforeInvoiceItemCount = invoice.getItems().size();

		taxService.applyTax(invoice);

		assertEquals(beforeInvoiceItemCount + 1, invoiceItems.size());
		InvoiceItem taxInvoiceItem = taxService.findTaxInvoiceItem(invoice);
		assertEquals(FeeClass.VAT, taxInvoiceItem.getInvoiceFee().getFeeClass());
		assertEquals(new BigDecimal("60.00"), taxInvoiceItem.getAmount());
		assertEquals(new BigDecimal("360.00"), invoice.getTotalAmount());
	}

	private ArrayList<InvoiceItem> buildInvoiceItemsWithTax(final InvoiceFee taxInvoiceFee) {
		ArrayList<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>() {
			{
				add(createInvoiceItem(1, createInvoiceFee(1, "Fee1"), new BigDecimal("100.00")));
				add(createInvoiceItem(2, createInvoiceFee(2, "Fee2"), new BigDecimal("200.00")));
				add(createInvoiceItem(3, taxInvoiceFee, new BigDecimal("15.00")));
			}
		};

		return invoiceItems;
	}

	private ArrayList<InvoiceItem> buildInvoiceItemsWithoutTax() {
		ArrayList<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>() {
			{
				add(createInvoiceItem(1, createInvoiceFee(1, "Fee1"), new BigDecimal("100.00")));
				add(createInvoiceItem(2, createInvoiceFee(2, "Fee2"), new BigDecimal("200.00")));
			}
		};

		return invoiceItems;
	}

	public Invoice buildInvoice(ArrayList<InvoiceItem> invoiceItems, Currency currency) {
		Invoice invoice = new Invoice();
		invoice.setItems(invoiceItems);
		invoice.setCurrency(currency);
		Account account = new Account();
		account.setCountrySubdivision(new CountrySubdivision("foo"));
		invoice.setAccount(account);
		return invoice;
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
		invoiceFee.setFee(feeName);
		return invoiceFee;
	}

	private InvoiceFee createTaxInvoiceFee(FeeClass feeClass, double invoiceFeeRate, double subdivisionRate) {
		InvoiceFee invoiceFee = new InvoiceFee();

		invoiceFee.setFeeClass(feeClass);
		invoiceFee.setRatePercent(new BigDecimal(invoiceFeeRate));

		InvoiceFeeCountry invoiceFeeCountry = new InvoiceFeeCountry();
		invoiceFeeCountry.setRatePercent(new BigDecimal(subdivisionRate));

		invoiceFee.setQbFullName("qb" + feeClass);
		invoiceFee.setSubdivisionFee(invoiceFeeCountry);

		return invoiceFee;
	}

	private InvoiceFee createTaxInvoiceFeeWithoutSubdivisionRate(FeeClass feeClass, double invoiceFeeRate) {
		InvoiceFee invoiceFee = new InvoiceFee();

		invoiceFee.setFeeClass(feeClass);
		invoiceFee.setRatePercent(new BigDecimal(invoiceFeeRate));

		invoiceFee.setQbFullName("qb" + feeClass);
		invoiceFee.setSubdivisionFee(null);

		return invoiceFee;
	}

	private InvoiceFee createTaxInvoiceFee(FeeClass feeClass) {
		InvoiceFee invoiceFee = new InvoiceFee();
		invoiceFee.setFeeClass(feeClass);
		return invoiceFee;
	}

}
