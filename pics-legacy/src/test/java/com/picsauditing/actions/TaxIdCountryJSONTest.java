package com.picsauditing.actions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.picsauditing.i18n.service.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Country;

import java.util.Locale;

public class TaxIdCountryJSONTest extends PicsActionTest {
	private TaxIdCountryJSON taxIdCountryJSON;

    @Mock
    private CountryDAO mockDao;
    @Mock
    private Country mockCountry;
    @Mock
    private TranslationService mockTranslationService;

    private static final String REQUEST_PARAMETER = "iso_code";
    private static final String TESTING_ISO = "XX";
    private static final String UK_ISO = "GB";
    private static final String TRUE_VAT_JSON = "{\"label\":\"VAT\",\"tax_id_required\":true}";
    private static final String TRUE_CNPJ_JSON = "{\"label\":\"CNPJ\",\"tax_id_required\":true}";
    private static final String FALSE_JSON = "{\"label\":\"\",\"tax_id_required\":false}";
    private static final String RESPONSE = "json";

    @Before
	public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
		taxIdCountryJSON = new TaxIdCountryJSON();
		super.setUp(taxIdCountryJSON);
		Whitebox.setInternalState(taxIdCountryJSON, "dao", mockDao);

        when(translationService.hasKey("FeeClass.CNPJ", Locale.ENGLISH)).thenReturn(true);
        when(translationService.getText("FeeClass.CNPJ", Locale.ENGLISH, null)).thenReturn("CNPJ");
        when(translationService.hasKey("FeeClass.VAT", Locale.ENGLISH)).thenReturn(true);
        when(translationService.getText("FeeClass.VAT", Locale.ENGLISH, null)).thenReturn("VAT");
    }

    @Test
    public void testExecute_true() {
		when(request.getParameter(REQUEST_PARAMETER)).thenReturn(TESTING_ISO);
        when(mockDao.findbyISO(TESTING_ISO)).thenReturn(mockCountry);
        when(mockCountry.isEuropeanUnion()).thenReturn(true);
        when(mockCountry.isUK()).thenReturn(false);
		assertEquals(RESPONSE, taxIdCountryJSON.execute());
		assertEquals(TRUE_VAT_JSON, taxIdCountryJSON.getJson().toJSONString());
    }

    @Test
    public void testExecute_false() {
		when(request.getParameter(REQUEST_PARAMETER)).thenReturn(TESTING_ISO);
        when(mockDao.findbyISO(TESTING_ISO)).thenReturn(mockCountry);
        when(mockCountry.isEuropeanUnion()).thenReturn(false);
        when(mockCountry.isUK()).thenReturn(false);
		assertEquals(RESPONSE, taxIdCountryJSON.execute());
		assertEquals(FALSE_JSON, taxIdCountryJSON.getJson().toJSONString());
    }

    @Test
    public void testExecute_UKisFalse () {
		when(request.getParameter(REQUEST_PARAMETER)).thenReturn(UK_ISO);
        when(mockDao.findbyISO(UK_ISO)).thenReturn(mockCountry);
        when(mockCountry.isEuropeanUnion()).thenReturn(true);
        when(mockCountry.isUK()).thenReturn(true);
		assertEquals(RESPONSE, taxIdCountryJSON.execute());
		assertEquals(FALSE_JSON, taxIdCountryJSON.getJson().toJSONString());
    }

    @Test
    public void testExecute_BR() {
        when(request.getParameter(REQUEST_PARAMETER)).thenReturn("BR");
        when(mockDao.findbyISO("BR")).thenReturn(mockCountry);
        when(mockCountry.isEuropeanUnion()).thenReturn(false);
        when(mockCountry.isBrazil()).thenReturn(true);
        when(mockCountry.isUK()).thenReturn(false);
        assertEquals(RESPONSE, taxIdCountryJSON.execute());
        assertEquals(TRUE_CNPJ_JSON, taxIdCountryJSON.getJson().toJSONString());
    }

}
