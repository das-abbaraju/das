package com.picsauditing.PICS;

import com.picsauditing.dao.InvoiceDAO;
import com.picsauditing.dao.InvoiceFeeCountryDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.RecordNotFoundException;

import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class InvoiceServiceTest {

	private InvoiceService invoiceService = new InvoiceService();
	@Mock private TaxService taxService = new TaxService();
	@Mock private InvoiceDAO invoiceDAO = new InvoiceDAO();
	@Mock private InvoiceFeeCountryDAO invoiceFeeCountryDAO;
	@Mock private Invoice invoice;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(invoiceService, "taxService", taxService);
		Whitebox.setInternalState(invoiceService, "invoiceDAO", invoiceDAO);
		Whitebox.setInternalState(invoiceService, "invoiceFeeCountryDAO", invoiceFeeCountryDAO);
		setupInvoiceFeeCountries();
	}

	@After
	public void tearDown() throws Exception {
		resetNowTime();
	}

	@Test
	public void testSaveInvoice() throws Exception {
		invoiceService.saveInvoice(invoice);

		verify(taxService).applyTax(invoice);
		verify(invoiceDAO).save(invoice);
	}

	@Test
	public void testSaveInvoice_whenInvoiceHasDuplicateTax_throwExceptionAndDontSave() throws Exception {
		InvoiceFee invoiceFee1 = createTaxInvoiceFee("Foo", 5, FeeClass.GST);
		InvoiceItem invoiceItem1 = new InvoiceItem(invoiceFee1);
		InvoiceFee invoiceFee2 = createTaxInvoiceFee("Bar", 10, FeeClass.CanadianTax);
		InvoiceItem invoiceItem2 = new InvoiceItem(invoiceFee2);
		List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
		invoiceItems.add(invoiceItem1);
		invoiceItems.add(invoiceItem2);
		when(invoice.getItems()).thenReturn(invoiceItems);

		try {
			invoiceService.saveInvoice(invoice);
			fail();
		} catch (InvoiceValidationException ive) {
		} catch (Exception e) {
			fail();
		}

		verify(invoiceDAO, never()).save(invoice);
	}

	@Test
	public void testSaveInvoice_whenInvoiceHasOneTax_SaveNormally() throws Exception {
		InvoiceFee invoiceFee1 = createTaxInvoiceFee("Foo", 5, FeeClass.CanadianTax);
		InvoiceItem invoiceItem1 = new InvoiceItem(invoiceFee1);
		List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
		invoiceItems.add(invoiceItem1);
		when(invoice.getItems()).thenReturn(invoiceItems);

		invoiceService.saveInvoice(invoice);

		verify(invoiceDAO).save(invoice);
	}

	@Test
	public void testGetCanadianTaxInvoiceFeeForProvince_oldScheduleShouldBeInEffect() throws Exception {
		setNowTime("2012-12-1");
		CountrySubdivision countrySubdivision = new CountrySubdivision("CA-AB");

		InvoiceFee taxInvoiceFee = invoiceService.getCanadianTaxInvoiceFeeForProvince(countrySubdivision);

		assertEquals("Foo", taxInvoiceFee.getFee().getKey());
		assertEquals(new BigDecimal(5), taxInvoiceFee.getRatePercent());
		assertEquals(new BigDecimal(5), taxInvoiceFee.getSubdivisionFee().getRatePercent());
		assertEquals(FeeClass.CanadianTax, taxInvoiceFee.getFeeClass());
		assertEquals("qbFoo", taxInvoiceFee.getQbFullName());
		assertEquals(DateBean.parseDate("2012-04-01"), taxInvoiceFee.getSubdivisionFee().getEffectiveDate());
	}

	@Test
	public void testGetCanadianTaxInvoiceFeeForProvince_newScheduleShouldBeInEffect() throws Exception {
		setNowTime("2013-04-02");
		CountrySubdivision countrySubdivision = new CountrySubdivision("CA-AB");

		InvoiceFee taxInvoiceFee = invoiceService.getCanadianTaxInvoiceFeeForProvince(countrySubdivision);

		assertEquals("Foo", taxInvoiceFee.getFee().getKey());
		assertEquals(new BigDecimal(5), taxInvoiceFee.getRatePercent());
		assertEquals(new BigDecimal(7.5), taxInvoiceFee.getSubdivisionFee().getRatePercent());
		assertEquals(FeeClass.CanadianTax, taxInvoiceFee.getFeeClass());
		assertEquals("qbFoo", taxInvoiceFee.getQbFullName());
		assertEquals(DateBean.parseDate("2013-04-01"), taxInvoiceFee.getSubdivisionFee().getEffectiveDate());
	}


	@Test(expected = RecordNotFoundException.class)
	public void testGetCanadianTaxInvoiceFeeForProvince() throws Exception {
		resetNowTime();
		CountrySubdivision alberta = new CountrySubdivision("CA-AB");
		when(invoiceFeeCountryDAO.findAllInvoiceFeeCountry(eq(FeeClass.CanadianTax), eq(alberta))).thenReturn(null);

		invoiceService.getCanadianTaxInvoiceFeeForProvince(alberta);
	}

	private void setupInvoiceFeeCountries() {
		InvoiceFee invoiceFee = createTaxInvoiceFee("Foo", 5, FeeClass.CanadianTax);

		List<InvoiceFeeCountry> invoiceFeeCountriesForAlberta = new ArrayList<InvoiceFeeCountry>();
		invoiceFeeCountriesForAlberta.add(createInvoiceFeeCountry("CA-AB", 5, "2012-04-01", invoiceFee));
		invoiceFeeCountriesForAlberta.add(createInvoiceFeeCountry("CA-AB", 7.5, "2013-04-01", invoiceFee));

		List<InvoiceFeeCountry> invoiceFeeCountries = new ArrayList<InvoiceFeeCountry>();
		invoiceFeeCountries.add(createInvoiceFeeCountry("CA-BC", 7.5, "2012-04-01", invoiceFee));
		invoiceFeeCountries.add(createInvoiceFeeCountry("CA-SK", 7.5, "2012-04-01", invoiceFee));
		invoiceFeeCountries.addAll(invoiceFeeCountriesForAlberta);

		invoiceFee.setInvoiceFeeCountries(invoiceFeeCountries);

		CountrySubdivision alberta = new CountrySubdivision("CA-AB");
		when(invoiceFeeCountryDAO.findAllInvoiceFeeCountry(eq(FeeClass.CanadianTax), eq(alberta))).thenReturn(invoiceFeeCountriesForAlberta);

	}

	private InvoiceFeeCountry createInvoiceFeeCountry(String isoCode, double subdivisionRate, String effectiveDate, InvoiceFee invoiceFee) {
		InvoiceFeeCountry invoiceFeeCountry = new InvoiceFeeCountry();
		invoiceFeeCountry.setSubdivision(new CountrySubdivision(isoCode));
		invoiceFeeCountry.setRatePercent(new BigDecimal(subdivisionRate));
		invoiceFeeCountry.setEffectiveDate(DateBean.parseDate(effectiveDate));
		invoiceFeeCountry.setInvoiceFee(invoiceFee);
		return invoiceFeeCountry;
	}

	private InvoiceFee createTaxInvoiceFee(String feeName, double invoiceFeeRate, FeeClass feeClass) {
		InvoiceFee invoiceFee = new InvoiceFee();
		TranslatableString fee = new TranslatableString();
		fee.setKey(feeName);
		invoiceFee.setFee(fee);
		invoiceFee.setRatePercent(new BigDecimal(invoiceFeeRate));
		invoiceFee.setFeeClass(feeClass);
		invoiceFee.setQbFullName("qb" + feeName);
		return invoiceFee;
	}

	private void setNowTime(String dateString) {
		Date date = DateBean.parseDate(dateString);
		DateTimeUtils.setCurrentMillisFixed(date.getTime());
	}

	private void resetNowTime() {
		DateTimeUtils.setCurrentMillisSystem();
	}

}
