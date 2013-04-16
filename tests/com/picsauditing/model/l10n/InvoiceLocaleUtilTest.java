package com.picsauditing.model.l10n;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Locale;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;

public class InvoiceLocaleUtilTest {

	@Mock
	private ContractorAccount contractorAccount;
	@Mock
	private AppProperty appProperty;
	@Mock
	private Country country;

	private static ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
	private static AppPropertyDAO appPropertyDAO = Mockito.mock(AppPropertyDAO.class);
	private static CountryDAO countryDAO = Mockito.mock(CountryDAO.class);

	@BeforeClass
	public static void classSetup() {
		when(applicationContext.getBean(SpringUtils.APP_PROPERTY_DAO)).thenReturn(appPropertyDAO);
		when(applicationContext.getBean(SpringUtils.COUNTRY_DAO)).thenReturn(countryDAO);
		Whitebox.setInternalState(SpringUtils.class, "applicationContext", applicationContext);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		Mockito.reset(appPropertyDAO, countryDAO);
	}

	@AfterClass
	public static void classTeardown() {
		Whitebox.setInternalState(SpringUtils.class, "applicationContext", (ApplicationContext) null);
		Whitebox.setInternalState(InvoiceLocaleUtil.class, "localesToEmailInvoicesInBPROCS", (List<Locale>) null);
	}

	private void setupNullLocaleSpecifiedForBPROCSEmail() {
		when(appProperty.getValue()).thenReturn(null);
		when(appPropertyDAO.find(FeatureToggle.TOGGLE_INVOICE_LOCALES_TO_EMAIL_VIA_BPROCS)).thenReturn(appProperty);
	}

	@Test
	public void testInvoiceIsToBeEmailedViaBPROCS_NoLocalesSpecified() {
		Locale localeToTest = Locale.GERMANY;
		String isoCodeToTest = Country.GERMANY_ISO_CODE;

		setupNullLocaleSpecifiedForBPROCSEmail();
		setupCountryDAOToReturnLocaleWhenGivenIsoCode(isoCodeToTest, localeToTest);
		boolean result = InvoiceLocaleUtil.invoiceIsToBeEmailedViaBPROCS(contractorAccount);

		assertFalse(result);
	}

	private void setupSingleLocaleForEmail() {
		when(appProperty.getValue()).thenReturn("en_UK");
		when(appPropertyDAO.find(FeatureToggle.TOGGLE_INVOICE_LOCALES_TO_EMAIL_VIA_BPROCS)).thenReturn(appProperty);
	}

	@Test
	public void testInvoiceIsToBeEmailedViaBPROCS_ReceiveNullContractor() throws Exception {
		setupSingleLocaleForEmail();

		boolean result = InvoiceLocaleUtil.invoiceIsToBeEmailedViaBPROCS(null);

		assertFalse(result);
	}

	@Test
	public void testNoAppPropertyFound_DoesNotThrowException() {
		when(appPropertyDAO.find(anyString())).thenReturn(null);

		InvoiceLocaleUtil.invoiceIsToBeEmailedViaBPROCS(null);
	}

	@Test
	public void testInvoiceIsToBeEmailedViaBPROCS_ReceiveContractorWithNullBillingCountry() throws Exception {
		setupSingleLocaleForEmail();
		when(contractorAccount.getBillingCountry()).thenReturn(null);

		boolean result = InvoiceLocaleUtil.invoiceIsToBeEmailedViaBPROCS(contractorAccount);

		assertFalse(result);
	}

	@Test
	public void testInvoiceIsToBeEmailedViaBPROCS_ReceiveContractorWithNullBillingCountryIsoCode() throws Exception {
		setupSingleLocaleForEmail();
		when(country.getIsoCode()).thenReturn(null);
		when(contractorAccount.getBillingCountry()).thenReturn(country);

		boolean result = InvoiceLocaleUtil.invoiceIsToBeEmailedViaBPROCS(contractorAccount);

		assertFalse(result);
	}

	private void setupInvoiceLocaleUtilEmailListContainsLocaleTest() {
		Locale localeToTest = Locale.FRANCE;
		String isoCodeToTest = Country.FRANCE_ISO_CODE;

		setupSingleLocaleForEmail();
		setupContractorAccountBillingCountryTo(isoCodeToTest);
		setupCountryDAOToReturnLocaleWhenGivenIsoCode(isoCodeToTest, localeToTest);
		addLocaleToEmailList(localeToTest);
	}

	@Test
	public void testInvoiceIsToBeEmailedViaBPROCS_LocaleToEmailContainsCountry() throws Exception {
		setupInvoiceLocaleUtilEmailListContainsLocaleTest();

		boolean result = InvoiceLocaleUtil.invoiceIsToBeEmailedViaBPROCS(contractorAccount);

		assertTrue(result);
	}

	private void setupInvoiceLocaleUtilEmailListDoesNotContainsLocaleTest() {
		Locale localeToTest = Locale.GERMANY;
		String isoCodeToTest = Country.GERMANY_ISO_CODE;

		setupSingleLocaleForEmail();
		setupContractorAccountBillingCountryTo(isoCodeToTest);
		setupCountryDAOToReturnLocaleWhenGivenIsoCode(isoCodeToTest, localeToTest);
		removeLocaleFromEmailList(localeToTest);
	}

	@Test
	public void testInvoiceIsToBeEmailedViaBPROCS_LocaleToEmailDoesNotContainCountry() throws Exception {
		setupInvoiceLocaleUtilEmailListDoesNotContainsLocaleTest();

		boolean result = InvoiceLocaleUtil.invoiceIsToBeEmailedViaBPROCS(contractorAccount);

		assertFalse(result);
	}

	private void setupContractorAccountBillingCountryTo(String isoCode) {
		when(country.getIsoCode()).thenReturn(isoCode);
		when(contractorAccount.getBillingCountry()).thenReturn(country);
	}

	private void setupCountryDAOToReturnLocaleWhenGivenIsoCode(String isoCode, Locale locale) {
		when(countryDAO.findLocaleByCountryISO(isoCode)).thenReturn(locale);
	}

	private void addLocaleToEmailList(Locale locale) {
		getLocaleList().add(locale);
	}

	private void removeLocaleFromEmailList(Locale locale) {
		getLocaleList().remove(locale);
	}

	private List<Locale> getLocaleList() {
		return Whitebox.getInternalState(InvoiceLocaleUtil.class, "localesToEmailInvoicesInBPROCS");
	}

}
