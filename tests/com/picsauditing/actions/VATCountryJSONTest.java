package com.picsauditing.actions;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Country;
import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServletActionContext.class, I18nCache.class})
public class VATCountryJSONTest {

    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private CountryDAO mockDao;
    @Mock
    private Country mockCountry;
    @Mock
    private I18nCache mockCache;

    private VATCountryJSON classUnderTest;

    private static final String REQEST_PARAMETER = "iso_code";
    private static final String TESTING_ISO = "XX";
    private static final String UK_ISO = "GB";
    private static final String TRUE_JSON = "{\"vat_required\":true}";
    private static final String FALSE_JSON = "{\"vat_required\":false}";
    private static final String RESPONSE = "json";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(ServletActionContext.class);
        PowerMockito.mockStatic(I18nCache.class);
        when(I18nCache.getInstance()).thenReturn(mockCache);
        when(ServletActionContext.getRequest()).thenReturn(mockRequest);
        classUnderTest = new VATCountryJSON();
        Whitebox.setInternalState(classUnderTest, "dao", mockDao);
    }

    @Test
    public void testExecute_true() {
        when(mockRequest.getParameter(REQEST_PARAMETER)).thenReturn(TESTING_ISO);
        when(mockDao.findbyISO(TESTING_ISO)).thenReturn(mockCountry);
        when(mockCountry.isEuropeanUnion()).thenReturn(true);
        assertEquals(RESPONSE, classUnderTest.execute());
        assertEquals(TRUE_JSON, classUnderTest.getJson().toJSONString());
    }

    @Test
    public void testExecute_false() {
        when(mockRequest.getParameter(REQEST_PARAMETER)).thenReturn(TESTING_ISO);
        when(mockDao.findbyISO(TESTING_ISO)).thenReturn(mockCountry);
        when(mockCountry.isEuropeanUnion()).thenReturn(false);
        assertEquals(RESPONSE, classUnderTest.execute());
        assertEquals(FALSE_JSON, classUnderTest.getJson().toJSONString());
    }

    @Test
    public void testExecute_UKisFalse () {
        when(mockRequest.getParameter(REQEST_PARAMETER)).thenReturn(UK_ISO);
        when(mockDao.findbyISO(UK_ISO)).thenReturn(mockCountry);
        when(mockCountry.isEuropeanUnion()).thenReturn(true);
        assertEquals(RESPONSE, classUnderTest.execute());
        assertEquals(FALSE_JSON, classUnderTest.getJson().toJSONString());
    }

}
