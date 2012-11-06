package com.picsauditing.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.util.hierarchy.HierarchyBuilder;

public class PicsActionSupportTest extends PicsActionTest {

	private PicsActionSupport picsActionSupport;

	@Mock
	private AppPropertyDAO propertyDAO;
	@Mock
	private HierarchyBuilder hierarchyBuilder;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		picsActionSupport = new PicsActionSupport();
		super.setUp(picsActionSupport);

		Whitebox.setInternalState(picsActionSupport, "propertyDAO", propertyDAO);
	}

	@After
	public void tearDown() throws Exception {
		System.setProperty("pics.env", "");
	}

	@Test
	public void testLoadPermissionsReturnsSameInstanceIfSet() throws Exception {
		Permissions permissions = new Permissions();
		permissions.setHierarchyBuilder(hierarchyBuilder);

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
		assertFalse("Config should still be false despite second thenReturn (static variable)", picsActionSupport.isConfigEnvironment());
	}

	@Test
	public void testIsConfigEnvironment_True() throws Exception {
		when(propertyDAO.getProperty("PICS.config")).thenReturn("1");

		assertFalse(picsActionSupport.isConfigEnvironment());
	}

	@Test
	public void testGetPicsEnvironment_AlphaPerEnvironmentVariable() throws Exception {
		System.setProperty("pics.env", "Alphabet");
		when(request.getServerName()).thenReturn(new String("www.picsorganizer.com"));
		when(request.getRequestURI()).thenReturn(new String("/index.html"));

		assertEquals("Equivalent of -Dpics.env=Alphabet", "alpha", picsActionSupport.getPicsEnvironment());
	}

	@Test
	public void testGetPicsEnvironment_BetaPerUrl_withNonsenseEnvironmentVariable() throws Exception {
		System.setProperty("pics.env", "nonsense");
		when(request.getServerName()).thenReturn(new String("beta.picsorganizer.com"));
		when(request.getRequestURI()).thenReturn(new String("/index.html"));

		assertEquals("Equivalent of -Dpics.env=nonsense", "beta", picsActionSupport.getPicsEnvironment());
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
		// Confirming intention: "qa-beta" is NOT considered "beta" (as far as the environment bar being colorized light blue for beta is concerned).
		assertFalse(picsActionSupport.isBetaEnvironment());
	}

	@Test
	public void testIsBetaEnvironment_comparedToLowVersionNumberInAppProperties() throws Exception {
		when(request.getServerName()).thenReturn(new String("www.picsorganizer.com"));
		when(request.getRequestURI()).thenReturn(new String("/index.html"));
		// as if app_properties thinks that version 1.0 is running live
		when(propertyDAO.getProperty("VERSION.major")).thenReturn("1");
		when(propertyDAO.getProperty("VERSION.minor")).thenReturn("0");

		assertTrue("URL is not explicitly beta, but version number is higher than app_properties", picsActionSupport.isBetaEnvironment());
	}

	@Test
	public void testIsBetaEnvironment_comparedToHighVersionNumberInAppProperties() throws Exception {
		when(request.getServerName()).thenReturn(new String("www.picsorganizer.com"));
		when(request.getRequestURI()).thenReturn(new String(""));
		// as if app_properties thinks that version 200000.0 is running live
		when(propertyDAO.getProperty("VERSION.major")).thenReturn("200000");
		when(propertyDAO.getProperty("VERSION.minor")).thenReturn("0");

		assertFalse("URL is not explicitly beta, but version number is lower than app_properties", picsActionSupport.isBetaEnvironment());
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

		assertFalse("URL is not explicitly stable, but version number is higher than app_properties", picsActionSupport.isLiveEnvironment());
	}

	@Test
	public void testIsLiveEnvironment_comparedToHighVersionNumberInAppProperties() throws Exception {
		when(request.getServerName()).thenReturn(new String("www.picsorganizer.com"));
		when(request.getRequestURI()).thenReturn(new String("/index.html"));
		// as if app_properties thinks that version 200000.0 is running live
		when(propertyDAO.getProperty("VERSION.major")).thenReturn("200000");
		when(propertyDAO.getProperty("VERSION.minor")).thenReturn("0");

		assertTrue("URL is not explicitly stable, but version number is lower than app_properties", picsActionSupport.isLiveEnvironment());
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
	public void testIsLocalhostEnvironment() throws Exception {
		when(request.getServerName()).thenReturn(new String("localhost:8080"));
		when(request.getRequestURI()).thenReturn(new String("/index.html"));

		assertTrue(picsActionSupport.isLocalhostEnvironment());
	}

	@Test
	public void testGetChatUrl() throws Exception {
		when(request.getScheme()).thenReturn("http");

		picsActionSupport.getChatUrl();

		verify(request).getScheme();
	}

}
