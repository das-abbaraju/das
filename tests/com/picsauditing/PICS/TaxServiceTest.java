package com.picsauditing.PICS;

import com.picsauditing.dao.InvoiceFeeCountryDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.billing.AccountingSystemSynchronization;
import com.picsauditing.util.SapAppPropertyUtil;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class TaxServiceTest {

	private TaxService taxService = new TaxService();

    @Mock
    private InvoiceFeeCountryDAO invoiceFeeCountryDAO;
	@Mock
	private Invoice invoice;
	@Mock
	private Account account;
	@Mock
	private Country country;
	@Mock
	private BusinessUnit businessUnit;
	@Mock
	private SapAppPropertyUtil sapAppPropertyUtil;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(taxService, "invoiceFeeCountryDAO", invoiceFeeCountryDAO);
        setupInvoiceFeeCountries();
		setupMocks();
		AccountingSystemSynchronization.setSapAppPropertyUtil(sapAppPropertyUtil);
		when(invoice.getAccount()).thenReturn(account);
		when(account.getCountry()).thenReturn(country);
		when(country.getBusinessUnit()).thenReturn(businessUnit);
		when(businessUnit.getId()).thenReturn(2);
	}

    @After
    public void tearDown() throws Exception {
        resetNowTime();
    }

    private void setupInvoiceFeeCountries() {
        InvoiceFee invoiceFee = createTaxInvoiceFee(FeeClass.CanadianTax, 5, 7.5);

        List<InvoiceFeeCountry> invoiceFeeCountriesForAlberta = new ArrayList<InvoiceFeeCountry>();
        invoiceFeeCountriesForAlberta.add(createInvoiceFeeCountry("CA-AB", 5, "2012-04-01", invoiceFee));
        invoiceFeeCountriesForAlberta.add(createInvoiceFeeCountry("CA-AB", 7.5, "2013-04-01", invoiceFee));

        List<InvoiceFeeCountry> invoiceFeeCountries = new ArrayList<InvoiceFeeCountry>();
        invoiceFeeCountries.add(createInvoiceFeeCountry("CA-BC", 7.5, "2012-04-01", invoiceFee));
        invoiceFeeCountries.add(createInvoiceFeeCountry("CA-SK", 7.5, "2012-04-01", invoiceFee));
        invoiceFeeCountries.addAll(invoiceFeeCountriesForAlberta);

        invoiceFee.setInvoiceFeeCountries(invoiceFeeCountries);

        CountrySubdivision alberta = new CountrySubdivision("CA-AB");
        when(invoiceFeeCountryDAO.findAllInvoiceFeeCountrySubdivision(eq(FeeClass.CanadianTax), eq(alberta))).thenReturn(
                invoiceFeeCountriesForAlberta);

    }

    private InvoiceFeeCountry createInvoiceFeeCountry(String isoCode, double subdivisionRate, String effectiveDate,
                                                      InvoiceFee invoiceFee) {
        InvoiceFeeCountry invoiceFeeCountry = new InvoiceFeeCountry();
        invoiceFeeCountry.setSubdivision(new CountrySubdivision(isoCode));
        invoiceFeeCountry.setRatePercent(new BigDecimal(subdivisionRate));
        invoiceFeeCountry.setEffectiveDate(DateBean.parseDate(effectiveDate));
        invoiceFeeCountry.setInvoiceFee(invoiceFee);
        return invoiceFeeCountry;
    }

    @Test
	public void testFindTaxInvoiceFee_WhenInvoiceHasTax_ReturnsThatTax() {
		final InvoiceFee existingTaxInvoiceFee = createTaxInvoiceFee(FeeClass.CanadianTax, 5, 9.975);
		ArrayList<InvoiceItem> invoiceItems = buildInvoiceItemsWithTax(existingTaxInvoiceFee);
		when(invoice.getItems()).thenReturn(invoiceItems);

        InvoiceItem foundInvoiceItem = getTaxItem(invoice);

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

		InvoiceItem foundInvoiceItem = getTaxItem(invoice);

		assertEquals(null, foundInvoiceItem);
	}

	@Test
	public void testApplyTax_whenInvoiceHasExistingNewCanadianTax_DontChangeTheTax() throws Exception {
		final InvoiceFee existingTaxInvoiceFee = createTaxInvoiceFeeWithoutSubdivisionRate(FeeClass.CanadianTax, 5);
		InvoiceFeeCountry provinceTaxFee = new InvoiceFeeCountry();
		provinceTaxFee.setRatePercent(new BigDecimal("9.975"));
		ArrayList<InvoiceItem> invoiceItems = buildInvoiceItemsWithTax(existingTaxInvoiceFee);
		Invoice invoice = buildInvoice(invoiceItems, Currency.CAD);

		taxService.applyTax(invoice);

		InvoiceItem newTaxInvoiceItem = getTaxItem(invoice);
		assertEquals(existingTaxInvoiceFee, newTaxInvoiceItem.getInvoiceFee());
	}

	@Test
	public void testApplyTax_whenInvoiceHasExistingGST_DontChangeTheTax() throws Exception {
		final InvoiceFee existingTaxInvoiceFee = createTaxInvoiceFee(FeeClass.GST);
		ArrayList<InvoiceItem> invoiceItems = buildInvoiceItemsWithTax(existingTaxInvoiceFee);
		Invoice invoice = buildInvoice(invoiceItems, Currency.CAD);

		taxService.applyTax(invoice);

		InvoiceItem newTaxInvoiceItem = getTaxItem(invoice);
		assertEquals(existingTaxInvoiceFee, newTaxInvoiceItem.getInvoiceFee());
	}

    @Test
    public void testApplyTax_whenInvoiceHasExistingVat_DontChangeTheTax() throws Exception {
        final InvoiceFee existingTaxInvoiceFee = createTaxInvoiceFee(FeeClass.VAT);
        ArrayList<InvoiceItem> invoiceItems = buildInvoiceItemsWithTax(existingTaxInvoiceFee);
        Invoice invoice = buildInvoice(invoiceItems, Currency.GBP);

        taxService.applyTax(invoice);

        InvoiceItem newTaxInvoiceItem = getTaxItem(invoice);
        assertEquals(existingTaxInvoiceFee, newTaxInvoiceItem.getInvoiceFee());
    }

    @Test
	public void testApplyTax_whenInvoiceHasNoTax_ApplyNewCanadianTax() throws Exception {
		ArrayList<InvoiceItem> invoiceItems = buildInvoiceItemsWithoutTax();
		Invoice invoice = buildInvoice(invoiceItems, Currency.CAD);
		int beforeInvoiceItemCount = invoice.getItems().size();

        InvoiceFee existingTaxInvoiceFee = createTaxInvoiceFee(FeeClass.CanadianTax);
        List<InvoiceFeeCountry> subFees = new ArrayList<InvoiceFeeCountry>();
        InvoiceFeeCountry subdivisionFee = new InvoiceFeeCountry();
        subdivisionFee.setInvoiceFee(existingTaxInvoiceFee);
        subdivisionFee.setRatePercent(new BigDecimal("5"));
        subFees.add(subdivisionFee);
        when(invoiceFeeCountryDAO.findAllInvoiceFeeCountrySubdivision(any(FeeClass.class),any(CountrySubdivision.class))).thenReturn(subFees);

		taxService.applyTax(invoice);

		assertEquals(beforeInvoiceItemCount + 1, invoiceItems.size());
		InvoiceItem taxInvoiceItem = getTaxItem(invoice);
		assertEquals(FeeClass.CanadianTax, taxInvoiceItem.getInvoiceFee().getFeeClass());
		assertEquals(new BigDecimal("15.00"), taxInvoiceItem.getAmount());
		assertEquals(new BigDecimal("315.00"), invoice.getTotalAmount());
	}

	@Test
	public void testApplyTax_whenInvoiceHasNoTaxAndCurrencyIsGbp_ApplyVat() throws Exception {
        InvoiceFee existingTaxInvoiceFee = createTaxInvoiceFee(FeeClass.VAT);
        List<InvoiceFeeCountry> subFees = new ArrayList<InvoiceFeeCountry>();
        InvoiceFeeCountry subdivisionFee = new InvoiceFeeCountry();
        subdivisionFee.setInvoiceFee(existingTaxInvoiceFee);
        subdivisionFee.setRatePercent(new BigDecimal("20"));

        subFees.add(subdivisionFee);

		ArrayList<InvoiceItem> invoiceItems = buildInvoiceItemsWithoutTax();
		Invoice invoice = buildInvoice(invoiceItems, Currency.GBP);
		int beforeInvoiceItemCount = invoice.getItems().size();

        when(invoiceFeeCountryDAO.findAllInvoiceFeeCountrySubdivision(any(FeeClass.class),any(CountrySubdivision.class))).thenReturn(subFees);

		taxService.applyTax(invoice);

		assertEquals(beforeInvoiceItemCount + 1, invoiceItems.size());
		InvoiceItem taxInvoiceItem = getTaxItem(invoice);
		assertEquals(FeeClass.VAT, taxInvoiceItem.getInvoiceFee().getFeeClass());
		assertEquals(new BigDecimal("60.00"), taxInvoiceItem.getAmount());
		assertEquals(new BigDecimal("360.00"), invoice.getTotalAmount());
	}

    @Test
    public void testApplyTax_whenInvoiceHasNoTaxAndCurrencyIsGbpButCountryIsNotUK_DontApplyVat() throws Exception {
        InvoiceFee existingTaxInvoiceFee = createTaxInvoiceFee(FeeClass.VAT);

        List<InvoiceFeeCountry> subFees = new ArrayList<InvoiceFeeCountry>();
        InvoiceFeeCountry subdivisionFee = new InvoiceFeeCountry();
        subdivisionFee.setRatePercent(new BigDecimal("20"));
        subdivisionFee.setInvoiceFee(existingTaxInvoiceFee);
        subFees.add(subdivisionFee);

        ArrayList<InvoiceItem> invoiceItems = buildInvoiceItemsWithoutTax();
        Invoice invoice = buildInvoice(invoiceItems, Currency.GBP);
        invoice.getAccount().setCountry(new Country("JE"));
        invoice.updateTotalAmount();
        int beforeInvoiceItemCount = invoice.getItems().size();

        when(invoiceFeeCountryDAO.findAllInvoiceFeeCountrySubdivision(any(FeeClass.class),any(CountrySubdivision.class))).thenReturn(null);
        when(invoiceFeeCountryDAO.findAllInvoiceFeeCountry(any(FeeClass.class),any(Country.class))).thenReturn(subFees);

        taxService.applyTax(invoice);

        assertEquals(beforeInvoiceItemCount, invoiceItems.size());
        InvoiceItem taxInvoiceItem = getTaxItem(invoice);
        assertNull(taxInvoiceItem);
        assertEquals(new BigDecimal("300.00"), invoice.getTotalAmount());
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
        if (currency.isCAD()) {
            account.setCountry(new Country("CA"));
        }
        else if (currency.isGBP()) {
            account.setCountry(new Country("GB"));
        }
        else if (currency.isEUR()) {
            account.setCountry(new Country("FR"));
        }
		invoice.setAccount(account);
		account.getCountry().setBusinessUnit(businessUnit);
		when(businessUnit.getId()).thenReturn(2);
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
		invoiceFee.setRegionalFee(invoiceFeeCountry);

		return invoiceFee;
	}

	private InvoiceFee createTaxInvoiceFeeWithoutSubdivisionRate(FeeClass feeClass, double invoiceFeeRate) {
		InvoiceFee invoiceFee = new InvoiceFee();

		invoiceFee.setFeeClass(feeClass);
		invoiceFee.setRatePercent(new BigDecimal(invoiceFeeRate));

		invoiceFee.setQbFullName("qb" + feeClass);
		invoiceFee.setRegionalFee(null);

		return invoiceFee;
	}

	private InvoiceFee createTaxInvoiceFee(FeeClass feeClass) {
		InvoiceFee invoiceFee = new InvoiceFee();
		invoiceFee.setFeeClass(feeClass);
		return invoiceFee;
	}

    private InvoiceItem getTaxItem(Invoice invoice) {
        for (InvoiceItem item : invoice.getItems()) {
            if (TaxService.TAX_FEE_CLASSES.contains(item.getInvoiceFee().getFeeClass())) {
                return item;
            }
        }
        return null;
    }

    @Test
    public void testGetCanadianTaxInvoiceFeeForProvince_oldScheduleShouldBeInEffect() throws Exception {
        setNowTime("2012-12-1");

        CountrySubdivision countrySubdivision = new CountrySubdivision("CA-AB");
        Country country = new Country("CA");

        InvoiceFee taxInvoiceFee = taxService.getTaxInvoiceFee(FeeClass.CanadianTax,country,countrySubdivision);

        assertEquals(new BigDecimal(5), taxInvoiceFee.getRatePercent());
        assertEquals(new BigDecimal(5), taxInvoiceFee.getRegionalFee().getRatePercent());
        assertEquals(FeeClass.CanadianTax, taxInvoiceFee.getFeeClass());
        assertEquals("qbCanadianTax", taxInvoiceFee.getQbFullName());
        assertEquals(DateBean.parseDate("2012-04-01"), taxInvoiceFee.getRegionalFee().getEffectiveDate());
    }

    @Test
    public void testGetCanadianTaxInvoiceFeeForProvince_newScheduleShouldBeInEffect() throws Exception {
        setNowTime("2013-04-02");
        CountrySubdivision countrySubdivision = new CountrySubdivision("CA-AB");
        Country country = new Country("CA");

        InvoiceFee taxInvoiceFee = taxService.getTaxInvoiceFee(FeeClass.CanadianTax,country,countrySubdivision);

        assertEquals(new BigDecimal(5), taxInvoiceFee.getRatePercent());
        assertEquals(new BigDecimal(7.5), taxInvoiceFee.getRegionalFee().getRatePercent());
        assertEquals(FeeClass.CanadianTax, taxInvoiceFee.getFeeClass());
        assertEquals("qbCanadianTax", taxInvoiceFee.getQbFullName());
        assertEquals(DateBean.parseDate("2013-04-01"), taxInvoiceFee.getRegionalFee().getEffectiveDate());
    }

    @Test
    public void testGetCanadianTaxInvoiceFeeForProvince() throws Exception {
        resetNowTime();
        CountrySubdivision countrySubdivision = new CountrySubdivision("CA-AB");
        Country country = new Country("CA");

        when(invoiceFeeCountryDAO.findAllInvoiceFeeCountrySubdivision(eq(FeeClass.CanadianTax), eq(countrySubdivision))).thenReturn(null);
        when(invoiceFeeCountryDAO.findAllInvoiceFeeCountry(eq(FeeClass.CanadianTax), eq(country))).thenReturn(null);

        assertNull(taxService.getTaxInvoiceFee(FeeClass.CanadianTax, country, countrySubdivision));
    }

    private void setNowTime(String dateString) {
        Date date = DateBean.parseDate(dateString);
        DateTimeUtils.setCurrentMillisFixed(date.getTime());
    }

    private void resetNowTime() {
        DateTimeUtils.setCurrentMillisSystem();
    }

}
