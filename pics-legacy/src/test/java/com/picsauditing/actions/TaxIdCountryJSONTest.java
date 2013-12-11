package com.picsauditing.actions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Country;

public class TaxIdCountryJSONTest extends PicsActionTest {
	private TaxIdCountryJSON taxIdCountryJSON;

    @Mock
    private CountryDAO mockDao;
    @Mock
    private Country mockCountry;

    private static final String REQUEST_PARAMETER = "iso_code";
    private static final String TESTING_ISO = "XX";
    private static final String UK_ISO = "GB";
    private static final String TRUE_JSON = "{\"tax_id_required\":true}";
    private static final String FALSE_JSON = "{\"tax_id_required\":false}";
    private static final String RESPONSE = "json";

    @Before
	public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
		taxIdCountryJSON = new TaxIdCountryJSON();
		super.setUp(taxIdCountryJSON);
		Whitebox.setInternalState(taxIdCountryJSON, "dao", mockDao);
    }

    @Test
    public void testExecute_true() {
		when(request.getParameter(REQUEST_PARAMETER)).thenReturn(TESTING_ISO);
        when(mockDao.findbyISO(TESTING_ISO)).thenReturn(mockCountry);
        when(mockCountry.isEuropeanUnion()).thenReturn(true);
		assertEquals(RESPONSE, taxIdCountryJSON.execute());
		assertEquals(TRUE_JSON, taxIdCountryJSON.getJson().toJSONString());
    }

    @Test
    public void testExecute_false() {
		when(request.getParameter(REQUEST_PARAMETER)).thenReturn(TESTING_ISO);
        when(mockDao.findbyISO(TESTING_ISO)).thenReturn(mockCountry);
        when(mockCountry.isEuropeanUnion()).thenReturn(false);
		assertEquals(RESPONSE, taxIdCountryJSON.execute());
		assertEquals(FALSE_JSON, taxIdCountryJSON.getJson().toJSONString());
    }

    @Test
    public void testExecute_UKisFalse () {
		when(request.getParameter(REQUEST_PARAMETER)).thenReturn(UK_ISO);
        when(mockDao.findbyISO(UK_ISO)).thenReturn(mockCountry);
        when(mockCountry.isEuropeanUnion()).thenReturn(true);
		assertEquals(RESPONSE, taxIdCountryJSON.execute());
		assertEquals(FALSE_JSON, taxIdCountryJSON.getJson().toJSONString());
    }

    @Test
    public void testExecute_BR() {
        when(request.getParameter(REQUEST_PARAMETER)).thenReturn("BR");
        when(mockDao.findbyISO("BR")).thenReturn(mockCountry);
        when(mockCountry.isEuropeanUnion()).thenReturn(false);
        when(mockCountry.isBrazil()).thenReturn(true);
        assertEquals(RESPONSE, taxIdCountryJSON.execute());
        assertEquals(TRUE_JSON, taxIdCountryJSON.getJson().toJSONString());
    }

}
