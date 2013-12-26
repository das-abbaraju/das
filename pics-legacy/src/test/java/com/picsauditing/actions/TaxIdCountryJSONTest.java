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
    public static final String BR_ISO = "BR";
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
    private static final String REQUEST_PARAMETER_LOCALE = "locale";
    private static final String TESTING_LOCALE_STRING = "en-us";
    public static final Locale TESTING_LOCALE = new Locale(TESTING_LOCALE_STRING);
    private static final String CNPJ_TRANSLATION_KEY = "FeeClass.CNPJ";
    private static final String VAT_TRANSLATION_KEY = "FeeClass.VAT";
    private static final String CNPJ_TRANSLATION_VALUE = "CNPJ";
    private static final String VAT_TRANSLATION_VALUE = "VAT";

    @Before
	public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
		taxIdCountryJSON = new TaxIdCountryJSON();
		super.setUp(taxIdCountryJSON);
		Whitebox.setInternalState(taxIdCountryJSON, "dao", mockDao);

        when(translationService.hasKey(CNPJ_TRANSLATION_KEY, TESTING_LOCALE)).thenReturn(true);
        when(translationService.getText(CNPJ_TRANSLATION_KEY, TESTING_LOCALE, null)).thenReturn(CNPJ_TRANSLATION_VALUE);
        when(translationService.hasKey(VAT_TRANSLATION_KEY, TESTING_LOCALE)).thenReturn(true);
        when(translationService.getText(VAT_TRANSLATION_KEY, TESTING_LOCALE, null)).thenReturn(VAT_TRANSLATION_VALUE);
        when(request.getParameter(REQUEST_PARAMETER_LOCALE)).thenReturn(TESTING_LOCALE_STRING);
    }

    @Test
    public void testExecute_true() {
		when(request.getParameter(REQUEST_PARAMETER)).thenReturn(TESTING_ISO);
        when(request.getParameter(REQUEST_PARAMETER_LOCALE)).thenReturn(TESTING_LOCALE_STRING);
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
        when(request.getParameter(REQUEST_PARAMETER)).thenReturn(BR_ISO);
        when(mockDao.findbyISO(BR_ISO)).thenReturn(mockCountry);
        when(mockCountry.isEuropeanUnion()).thenReturn(false);
        when(mockCountry.isBrazil()).thenReturn(true);
        when(mockCountry.isUK()).thenReturn(false);
        assertEquals(RESPONSE, taxIdCountryJSON.execute());
        assertEquals(TRUE_CNPJ_JSON, taxIdCountryJSON.getJson().toJSONString());
    }

}
