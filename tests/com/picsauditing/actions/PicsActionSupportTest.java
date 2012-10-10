package com.picsauditing.actions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.util.PicsOrganizerVersion;

public class PicsActionSupportTest extends PicsActionTest {
	private PicsActionSupport picsActionSupport;

	@Mock
	private AppPropertyDAO propertyDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		picsActionSupport = new PicsActionSupport();
		super.setUp(picsActionSupport);

		Whitebox.setInternalState(picsActionSupport, "propertyDAO", propertyDAO);
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
	public void testIsConfigEnvironmentFalse() throws Exception {
		// return false then true and make sure it stays false
		when(propertyDAO.getProperty("PICS.config")).thenReturn("0").thenReturn("1");

		assertFalse("Config should be false", picsActionSupport.isConfigEnvironment());
		assertFalse("Config should still be false (static variable)", picsActionSupport.isConfigEnvironment());
	}

	@Test
	public void testIsBetaEnvironment_UrlContainsBeta() throws Exception {
		when(request.getRequestURL()).thenReturn(new StringBuffer("beta.picsorganizer.com"));
		when(request.getRequestURI()).thenReturn(new String("/index.html"));

		assertTrue("url has beta", picsActionSupport.isBetaEnvironment());
	}

	@Test
	public void testIsBetaEnvironment_UrlContainsWithNoBeta() throws Exception {
		when(request.getRequestURL()).thenReturn(new StringBuffer("www.picsorganizer.com"));
		when(request.getRequestURI()).thenReturn(new String("/index.html"));
		when(propertyDAO.getProperty("VERSION.major")).thenReturn("1");
		when(propertyDAO.getProperty("VERSION.minor")).thenReturn("0");

		assertTrue("url does not have beta", picsActionSupport.isBetaEnvironment());
	}

	@Test
	public void testIsBetaEnvironment_UrlContainsWithNoBetaCheckVersionYes() throws Exception {
		when(request.getRequestURL()).thenReturn(new StringBuffer("www.picsorganizer.com"));
		when(request.getRequestURI()).thenReturn(new String(""));
		when(propertyDAO.getProperty("VERSION.major")).thenReturn("1");
		when(propertyDAO.getProperty("VERSION.minor")).thenReturn("0");

		assertTrue("url has beta", picsActionSupport.isBetaEnvironment());
	}

	@Test
	public void testIsBetaEnvironment_UrlContainsWithNoBetaCheckVersionNo() throws Exception {
		when(request.getRequestURL()).thenReturn(new StringBuffer("www.picsorganizer.com"));
		when(request.getRequestURI()).thenReturn(new String(""));
		when(propertyDAO.getProperty("VERSION.major")).thenReturn("200000");
		when(propertyDAO.getProperty("VERSION.minor")).thenReturn("0");

		assertFalse("url does not have beta", picsActionSupport.isBetaEnvironment());
	}

	@Test
	public void testIsLiveEnvironment_UrlContainsStable() throws Exception {
		when(request.getRequestURL()).thenReturn(new StringBuffer("stable.picsorganizer.com"));
		when(request.getRequestURI()).thenReturn(new String("/index.html"));

		assertTrue("url has stable", picsActionSupport.isLiveEnvironment());
	}

	@Test
	public void testIsLiveEnvironment_UrlContainsWithNoStable() throws Exception {
		when(request.getRequestURL()).thenReturn(new StringBuffer("www.picsorganizer.com"));
		when(request.getRequestURI()).thenReturn(new String("/index.html"));
		when(propertyDAO.getProperty("VERSION.major")).thenReturn("1");
		when(propertyDAO.getProperty("VERSION.minor")).thenReturn("0");

		assertFalse("url does not have stable", picsActionSupport.isLiveEnvironment());
	}

	@Test
	public void testIsLiveEnvironment_UrlContainsWithNoStableCheckVersionNo() throws Exception {
		when(request.getRequestURL()).thenReturn(new StringBuffer("www.picsorganizer.com"));
		when(request.getRequestURI()).thenReturn(new String(""));
		when(propertyDAO.getProperty("VERSION.major")).thenReturn("1");
		when(propertyDAO.getProperty("VERSION.minor")).thenReturn("0");

		assertFalse(picsActionSupport.isLiveEnvironment());
	}

	@Test
	public void testIsLiveEnvironment_UrlContainsWithNoStableCheckVersionYes() throws Exception {
		when(request.getRequestURL()).thenReturn(new StringBuffer("www.picsorganizer.com"));
		when(request.getRequestURI()).thenReturn(new String(""));
		when(propertyDAO.getProperty("VERSION.major")).thenReturn("200000");
		when(propertyDAO.getProperty("VERSION.minor")).thenReturn("0");

		assertTrue("has no beta cookie, its stable", picsActionSupport.isLiveEnvironment());
	}
}
