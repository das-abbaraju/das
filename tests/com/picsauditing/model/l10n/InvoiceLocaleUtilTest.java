package com.picsauditing.model.l10n;

import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.when;

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
	}

	@Test
	public void testInvoiceIsToBeEmailedViaBPROCS_ReceiveNullContractor() throws Exception {
		setupSingleLocaleForEmail();

		boolean result = InvoiceLocaleUtil.invoiceIsToBeEmailedViaBPROCS(null);

		assertFalse(result);
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

	private void setupSingleLocaleForEmail() {
		when(appProperty.getValue()).thenReturn("en_UK");
		when(appPropertyDAO.find(FeatureToggle.TOGGLE_INVOICE_LOCALES_TO_EMAIL_VIA_BPROCS)).thenReturn(appProperty);
	}

}
