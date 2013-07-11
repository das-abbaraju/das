package com.picsauditing.actions;

import com.picsauditing.PICS.MainPage;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.*;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.hierarchy.HierarchyBuilder;
import org.json.simple.JSONObject;
import org.junit.*;
import org.mockito.*;
import org.powermock.reflect.Whitebox;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class PicsActionSupportTest extends PicsActionTest {

    private PicsActionSupport picsActionSupport;

    @Mock
    private AppPropertyDAO propertyDAO;
    @Mock
    private FeatureToggle featureToggleChecker;
    @Mock
    private HierarchyBuilder hierarchyBuilder;
    @Mock
    private BufferedReader bufferedReader;
    @Mock
    protected UserDAO userDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        picsActionSupport = new PicsActionSupport();
        super.setUp(picsActionSupport);

        PicsTestUtil.autowireDAOsFromDeclaredMocks(picsActionSupport, this);
        Whitebox.setInternalState(picsActionSupport, "featureToggleChecker", featureToggleChecker);
    }

    @After
    public void tearDown() throws Exception {
        System.setProperty("pics.env", "");
        Whitebox.setInternalState(PicsActionSupport.class, "CONFIG", (Object) null);
    }

    @Test
    public void testLoadPermissionsReturnsSameInstanceIfSet() throws Exception {
        Permissions permissions = new Permissions();

        picsActionSupport.permissions = permissions;
        picsActionSupport.loadPermissions();
        Permissions permissions2 = picsActionSupport.getPermissions();
		/*
		 * this may not be the best test if the impl changes to return an
		 * equivalent Permission, but ATM, this is comparing instances to verify
		 * that we got what we put in and it wasn't reloaded from the session or
		 * reinstantiated
		 */
        assertTrue(permissions == permissions2);
    }

    @Test
    public void testLoadPermissionsReturnsFromSessionIfSet() throws Exception {
        picsActionSupport.loadPermissions();
    }

    @Test
    public void testIsConfigEnvironment_False() throws Exception {
        when(propertyDAO.getProperty("PICS.config")).thenReturn("0").thenReturn("1");

        assertFalse("Config should be false", picsActionSupport.isConfigEnvironment());
        assertFalse("Config should still be false despite second thenReturn (static variable)",
                picsActionSupport.isConfigEnvironment());
    }

    @Test
    public void testIsConfigEnvironment_True() throws Exception {
        when(propertyDAO.getProperty("PICS.config")).thenReturn("1");

        assertTrue(picsActionSupport.isConfigEnvironment());
    }

    @Test
    public void testGetPicsEnvironment_AlphaPerEnvironmentVariable() throws Exception {
        // Note: PICS-7936 clarified that we should take the word of whoever
        // sets the -Dpicvs.env string on the name of the environment and not
        // try to match it up to a known list. So, this test was changed from
        // considering that -Dpics.env=Alphabet was the same as
        // -Dpics.env=alpha, because "Alphabet" starts with alpha.
        System.setProperty("pics.env", "Alphabet");
        when(request.getServerName()).thenReturn(new String("www.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertEquals("Equivalent of -Dpics.env=Alphabet", "alphabet", picsActionSupport.getPicsEnvironment());
    }

    @Test
    public void testGetPicsEnvironment_BetaPerUrl_withEmptyEnvironmentVariable() throws Exception {
        System.setProperty("pics.env", "    ");
        when(request.getServerName()).thenReturn(new String("beta.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertEquals("Equivalent of -Dpics.env=", "beta", picsActionSupport.getPicsEnvironment());
    }

    @Test
    public void testIsAlphaEnvironment() throws Exception {
        when(request.getServerName()).thenReturn(new String("alpha.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertTrue("url starts with alpha", picsActionSupport.isAlphaEnvironment());
    }

    @Test
    public void testIsAlphaEnvironment_false() throws Exception {
        when(request.getServerName()).thenReturn(new String("www.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertFalse("url does not start with alpha", picsActionSupport.isAlphaEnvironment());
    }

    @Test
    public void testIsBetaEnvironment_SystemPropertySaysBeta() throws Exception {
        System.setProperty("pics.env", " BETA ");
        when(request.getServerName()).thenReturn(new String("www.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertTrue("Equivalent of -Dpics.env=beta", picsActionSupport.isBetaEnvironment());
    }

    @Test
    public void testIsBetaEnvironment_SystemPropertySaysAlpha() throws Exception {
        System.setProperty("pics.env", "alpha");
        when(request.getServerName()).thenReturn(new String("www.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertFalse("Equivalent of -Dpics.env=alpha", picsActionSupport.isBetaEnvironment());
    }

    @Test
    public void testIsBetaEnvironment_UrlExplicitlyStartsWithBeta() throws Exception {
        when(request.getServerName()).thenReturn(new String("beta.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertTrue("url starts with beta", picsActionSupport.isBetaEnvironment());
    }

    @Test
    public void testIsBetaEnvironment_UrlExplicitlyStartsWithAlpha() throws Exception {
        when(request.getServerName()).thenReturn(new String("alpha.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertFalse(picsActionSupport.isBetaEnvironment());
    }

    @Test
    public void testIsBetaEnvironment_UrlExplicitlyStartsWithLocalhost() throws Exception {
        when(request.getServerName()).thenReturn(new String("localhost:8080"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertFalse(picsActionSupport.isBetaEnvironment());
    }

    @Test
    public void testIsBetaEnvironment_UrlExplicitlyStartsWithQaBeta() throws Exception {
        when(request.getServerName()).thenReturn(new String("qa-beta.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));
        // Confirming intention: "qa-beta" is NOT considered "beta" (as far as
        // the environment bar being colorized light blue for beta is
        // concerned).
        assertFalse(picsActionSupport.isBetaEnvironment());
    }

    @Test
    public void testIsBetaEnvironment_comparedToLowVersionNumberInAppProperties() throws Exception {
        when(request.getServerName()).thenReturn(new String("www.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));
        // as if app_properties thinks that version 1.0 is running live
        when(propertyDAO.getProperty("VERSION.major")).thenReturn("1");
        when(propertyDAO.getProperty("VERSION.minor")).thenReturn("0");

        assertTrue("URL is not explicitly beta, but version number is higher than app_properties",
                picsActionSupport.isBetaEnvironment());
    }

    @Test
    public void testIsBetaEnvironment_comparedToHighVersionNumberInAppProperties() throws Exception {
        when(request.getServerName()).thenReturn(new String("www.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String(""));
        // as if app_properties thinks that version 200000.0 is running live
        when(propertyDAO.getProperty("VERSION.major")).thenReturn("200000");
        when(propertyDAO.getProperty("VERSION.minor")).thenReturn("0");

        assertFalse("URL is not explicitly beta, but version number is lower than app_properties",
                picsActionSupport.isBetaEnvironment());
    }

    @Test
    public void testIsLiveEnvironment_UrlExplicitlyStartsWithStable() throws Exception {
        when(request.getServerName()).thenReturn(new String("stable.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertTrue(picsActionSupport.isLiveEnvironment());
    }

    @Test
    public void testIsLiveEnvironment_UrlExplicitlyStartsWithAlpha() throws Exception {
        when(request.getServerName()).thenReturn(new String("alpha.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertFalse(picsActionSupport.isLiveEnvironment());
    }

    @Test
    public void testIsLiveEnvironment_UrlExplicitlyStartsWithConfig() throws Exception {
        when(request.getServerName()).thenReturn(new String("config.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertFalse(picsActionSupport.isLiveEnvironment());
    }

    @Test
    public void testIsLiveEnvironment_UrlExplicitlyStartsWithQaBeta() throws Exception {
        when(request.getServerName()).thenReturn(new String("qa-beta.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertFalse(picsActionSupport.isLiveEnvironment());
    }

    @Test
    public void testIsLiveEnvironment_UrlExplicitlyStartsWithQaStable() throws Exception {
        when(request.getServerName()).thenReturn(new String("qa-stable.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertFalse(picsActionSupport.isLiveEnvironment());
    }

    @Test
    public void testIsLiveEnvironment_UrlExplicitlyStartsWithLocalhost() throws Exception {
        when(request.getServerName()).thenReturn(new String("localhost:8080"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertFalse(picsActionSupport.isLiveEnvironment());
    }

    @Test
    public void testIsLiveEnvironment_comparedToLowVersionNumberInAppProperties() throws Exception {
        when(request.getServerName()).thenReturn(new String("www.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));
        // as if app_properties thinks that version 1.0 is running live
        when(propertyDAO.getProperty("VERSION.major")).thenReturn("1");
        when(propertyDAO.getProperty("VERSION.minor")).thenReturn("0");

        assertFalse("URL is not explicitly stable, but version number is higher than app_properties",
                picsActionSupport.isLiveEnvironment());
    }

    @Test
    public void testIsLiveEnvironment_comparedToHighVersionNumberInAppProperties() throws Exception {
        when(request.getServerName()).thenReturn(new String("www.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));
        // as if app_properties thinks that version 200000.0 is running live
        when(propertyDAO.getProperty("VERSION.major")).thenReturn("200000");
        when(propertyDAO.getProperty("VERSION.minor")).thenReturn("0");

        assertTrue("URL is not explicitly stable, but version number is lower than app_properties",
                picsActionSupport.isLiveEnvironment());
    }

    @Test
    public void testIsQaEnvironment_UrlExplicitlyStartsWithQaStable() throws Exception {
        when(request.getServerName()).thenReturn(new String("qa-stable.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertTrue(picsActionSupport.isQaEnvironment());
    }

    @Test
    public void testIsQaEnvironment_UrlExplicitlyStartsWithQaBeta() throws Exception {
        when(request.getServerName()).thenReturn(new String("qa-beta.picsorganizer.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertTrue(picsActionSupport.isQaEnvironment());
    }

    @Test
    public void testIsLocalhostEnvironment_perEnvironmentVar() throws Exception {
        System.setProperty("pics.env", "localhost");
        when(request.getServerName()).thenReturn(new String("example.com"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertTrue(picsActionSupport.isLocalhostEnvironment());
    }

    @Test
    public void testIsLocalhostEnvironment_noPort() throws Exception {
        when(request.getServerName()).thenReturn(new String("localhost"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertTrue(picsActionSupport.isLocalhostEnvironment());
    }

    @Test
    public void testIsLocalhostEnvironment_8080() throws Exception {
        when(request.getServerName()).thenReturn(new String("localhost:8080"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertTrue(picsActionSupport.isLocalhostEnvironment());
    }

    @Test
    public void testIsLocalhostEnvironment_DotLocal_noPort() throws Exception {
        when(request.getServerName()).thenReturn(new String("foo.local"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertTrue(picsActionSupport.isLocalhostEnvironment());
    }

    @Test
    public void testIsLocalhostEnvironment_DotLocal_8080() throws Exception {
        when(request.getServerName()).thenReturn(new String("foo.local:8080"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));

        assertTrue(picsActionSupport.isLocalhostEnvironment());
    }

    @Test
    public void testIsLocalhostEnvironment_AppPropertyVersionIsLowButLocalTakesPriority() throws Exception {
        when(request.getServerName()).thenReturn(new String("localhost:8080"));
        when(request.getRequestURI()).thenReturn(new String("/index.html"));
        when(propertyDAO.getProperty("VERSION.major")).thenReturn("1");
        when(propertyDAO.getProperty("VERSION.minor")).thenReturn("0");

        assertTrue(picsActionSupport.isLocalhostEnvironment());
    }

    @Test
    public void testGetJsonFromRequestPayload_NullReaderReturnsEmptyJSON() throws Exception {
        when(request.getReader()).thenReturn(null);

        JSONObject result = Whitebox.invokeMethod(picsActionSupport, "getJsonFromRequestPayload");

        verify(bufferedReader, never()).close();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetJsonFromRequestPayload_NullBufferedReaderReturnsEmptyJSON() throws Exception {
        when(bufferedReader.readLine()).thenReturn(null);
        when(request.getReader()).thenReturn(bufferedReader);

        JSONObject result = Whitebox.invokeMethod(picsActionSupport, "getJsonFromRequestPayload");

        verify(bufferedReader, times(1)).close();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetJsonFromRequestPayload_ParseJsonInRequest() throws Exception {
        String json = "{\"test\":\"yay it works\"}";
        BufferedReader spy = Mockito.spy(new BufferedReader(new StringReader(json)));

        when(request.getReader()).thenReturn(spy);

        JSONObject actual = Whitebox.invokeMethod(picsActionSupport, "getJsonFromRequestPayload");

        verify(spy, times(1)).close();
        assertEquals(json, actual.toJSONString());
    }

    @Test
    public void testGetLocalizedPhoneNumberForUser_FeatureToggleNotEnabled() {
		/*
		 * if (featureToggleChecker.isFeatureEnabled(FeatureToggle.
		 * TOGGLE_COUNTRY_PHONE_NUMBER) && user.getPhone().length() <= 4) {
		 * String format = "%s x%s";
		 * 
		 * if (country != null) { return String.format(format,
		 * country.getPhone(), user.getPhone()); } else { return
		 * String.format(format, getPicsPhoneNumber(), user.getPhone()); } }
		 * 
		 * return user.getPhone();
		 */
        User user = mock(User.class);
        when(featureToggleChecker.isFeatureEnabled(anyString())).thenReturn(false);
        when(user.getPhone()).thenReturn("Phone");

        assertEquals("Phone", picsActionSupport.getLocalizedPhoneNumberForUser(user));
    }

    @Test
    public void testGetLocalizedPhoneNumberForUser_FeatureToggleEnabledAndUserPhoneLengthGreaterThan4() {
        User user = mock(User.class);
        when(featureToggleChecker.isFeatureEnabled(anyString())).thenReturn(true);
        when(user.getPhone()).thenReturn("Phone");

        assertEquals("Phone", picsActionSupport.getLocalizedPhoneNumberForUser(user));
    }

    @Test
    public void testGetLocalizedPhoneNumberForUser_FeatureToggleEnabledAndUserPhoneLengthEqualTo4() {
        CountryDAO countryDAO = mock(CountryDAO.class);
        User user = mock(User.class);

        when(featureToggleChecker.isFeatureEnabled(anyString())).thenReturn(true);
        when(user.getPhone()).thenReturn("1234");

        Whitebox.setInternalState(picsActionSupport, "countryDAO", countryDAO);

        assertEquals(MainPage.PICS_PHONE_NUMBER + " x1234", picsActionSupport.getLocalizedPhoneNumberForUser(user));
    }

    @Test
    public void testGetLocalizedPhoneNumberForUser_FeatureToggleEnabledAndUserPhoneLengthEqualTo4AndCountryProvided() {
        Country country = mock(Country.class);
        User user = mock(User.class);

        when(country.getPhone()).thenReturn("Country Phone");
        when(featureToggleChecker.isFeatureEnabled(anyString())).thenReturn(true);
        when(user.getPhone()).thenReturn("1234");

        assertEquals("Country Phone x1234", picsActionSupport.getLocalizedPhoneNumberForUser(user, country));
    }

    @Test
    public void testGetSafetyList_UserIsAdminAndNotCorporate_FindsBySafetyGroup() throws Exception {
        when(permissions.isAdmin()).thenReturn(true);
        when(permissions.isOperatorCorporate()).thenReturn(false);

        picsActionSupport.getSafetyList();

        verify(userDAO).findByGroup(User.GROUP_SAFETY);
    }

    @Test
    public void testGetSafetyList_UserIsNotAdminAndNotCorporate_FindsCorpAuditorsByAccountId() throws Exception {
        when(permissions.isAdmin()).thenReturn(false);
        when(permissions.isOperatorCorporate()).thenReturn(false);
        when(permissions.getAccountId()).thenReturn(12345);

        picsActionSupport.getSafetyList();

        verify(userDAO).findCorporateAuditors(12345);
    }

    @Test
    public void testGetSafetyList_UserIsNotAdminAndIsCorporate_FindsCorpAuditorsByAccountId() throws Exception {
        when(permissions.isAdmin()).thenReturn(false);
        when(permissions.isOperatorCorporate()).thenReturn(true);
        when(permissions.getAccountId()).thenReturn(12345);

        picsActionSupport.getSafetyList();

        verify(userDAO).findCorporateAuditors(12345);
    }
}
