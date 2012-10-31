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

public class VATCountryJSONTest extends PicsActionTest {
	private VATCountryJSON vATCountryJSON;

    @Mock
    private CountryDAO mockDao;
    @Mock
    private Country mockCountry;

    private static final String REQEST_PARAMETER = "iso_code";
    private static final String TESTING_ISO = "XX";
    private static final String UK_ISO = "GB";
    private static final String TRUE_JSON = "{\"vat_required\":true}";
    private static final String FALSE_JSON = "{\"vat_required\":false}";
    private static final String RESPONSE = "json";

    @Before
	public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
		vATCountryJSON = new VATCountryJSON();
		super.setUp(vATCountryJSON);
		Whitebox.setInternalState(vATCountryJSON, "dao", mockDao);
    }

    @Test
    public void testExecute_true() {
		when(request.getParameter(REQEST_PARAMETER)).thenReturn(TESTING_ISO);
        when(mockDao.findbyISO(TESTING_ISO)).thenReturn(mockCountry);
        when(mockCountry.isEuropeanUnion()).thenReturn(true);
		assertEquals(RESPONSE, vATCountryJSON.execute());
		assertEquals(TRUE_JSON, vATCountryJSON.getJson().toJSONString());
    }

    @Test
    public void testExecute_false() {
		when(request.getParameter(REQEST_PARAMETER)).thenReturn(TESTING_ISO);
        when(mockDao.findbyISO(TESTING_ISO)).thenReturn(mockCountry);
        when(mockCountry.isEuropeanUnion()).thenReturn(false);
		assertEquals(RESPONSE, vATCountryJSON.execute());
		assertEquals(FALSE_JSON, vATCountryJSON.getJson().toJSONString());
    }

    @Test
    public void testExecute_UKisFalse () {
		when(request.getParameter(REQEST_PARAMETER)).thenReturn(UK_ISO);
        when(mockDao.findbyISO(UK_ISO)).thenReturn(mockCountry);
        when(mockCountry.isEuropeanUnion()).thenReturn(true);
		assertEquals(RESPONSE, vATCountryJSON.execute());
		assertEquals(FALSE_JSON, vATCountryJSON.getJson().toJSONString());
    }

}
