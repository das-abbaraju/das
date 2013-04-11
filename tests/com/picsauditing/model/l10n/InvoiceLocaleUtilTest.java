package com.picsauditing.model.l10n;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;

import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"InvoiceLocaleUtil-context.xml"})
public class InvoiceLocaleUtilTest {

	@Mock
	private AppPropertyDAO propertyDAO;

	@Mock
	private CountryDAO countryDAO;

	@Mock
	private ContractorAccount contractorAccount;

	@BeforeClass
	public static void classSetup() {
		Whitebox.setInternalState(SpringUtils.class, "applicationContext", Mockito.mock(ApplicationContext.class));

		AppPropertyDAO appPropertyDAO = Mockito.mock(AppPropertyDAO.class);
		when(appPropertyDAO.find(anyString())).thenReturn(new AppProperty(FeatureToggle.TOGGLE_INVOICE_LOCALES_TO_EMAIL_VIA_BPROCS, "ca_FR"));
		Whitebox.setInternalState(InvoiceLocaleUtil.class, "propertyDAO", Mockito.mock(AppPropertyDAO.class));

		Whitebox.setInternalState(InvoiceLocaleUtil.class, "countryDAO", Mockito.mock(CountryDAO.class));
	}

	@Before
	public void setUp() throws Exception {

	}

	@AfterClass
	public static void classTeardown() {
		Whitebox.setInternalState(InvoiceLocaleUtil.class, "propertyDAO", (AppPropertyDAO) null);
		Whitebox.setInternalState(InvoiceLocaleUtil.class, "countryDAO", (CountryDAO) null);
	}

	@Test
	public void testInvoiceIsToBeEmailedViaBPROCS_ReceiveNullContractor() throws Exception {
		InvoiceLocaleUtil invoiceLocaleUtil = InvoiceLocaleUtil.getInstance();
		Boolean result = invoiceLocaleUtil.invoiceIsToBeEmailedViaBPROCS(null);
		assertFalse(result);
	}


}
