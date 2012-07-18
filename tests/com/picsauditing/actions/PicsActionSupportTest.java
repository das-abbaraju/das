package com.picsauditing.actions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.util.PicsOrganizerVersion;
import com.picsauditing.util.SpringUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ActionContext.class, SpringUtils.class, ServletActionContext.class, I18nCache.class,
		PicsOrganizerVersion.class })
@PowerMockIgnore({ "javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*" })
public class PicsActionSupportTest {
	PicsActionSupport picsActionSupport;

	@Mock
	private EntityManager em;
	@Mock
	HttpServletRequest request;
	@Mock
	PicsOrganizerVersion picOrgVersion;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(I18nCache.class);

		Map<String, Object> session = new HashMap<String, Object>();
		session.put("permissions", new Permissions());

		ActionContext actionContext = mock(ActionContext.class);
		when(actionContext.getSession()).thenReturn(session);

		PowerMockito.mockStatic(ActionContext.class);
		when(ActionContext.getContext()).thenReturn(actionContext);

		PowerMockito.mockStatic(ServletActionContext.class);
		when(ServletActionContext.getRequest()).thenReturn(request);

		PowerMockito.mockStatic(PicsOrganizerVersion.class);

		picsActionSupport = new PicsActionSupport();

		AppPropertyDAO propertyDAO = new AppPropertyDAO();
		propertyDAO.setEntityManager(em);
		picsActionSupport.propertyDAO = propertyDAO;
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
		AppProperty appPropertyFalse = new AppProperty();
		appPropertyFalse.setProperty("PICS.config");
		appPropertyFalse.setValue("0");
		AppProperty appPropertyTrue = new AppProperty();
		appPropertyTrue.setProperty("PICS.config");
		appPropertyTrue.setValue("1");
		when(em.find(AppProperty.class, "PICS.config")).thenReturn(appPropertyFalse).thenReturn(appPropertyTrue);
		assertFalse("Config should be false", picsActionSupport.isConfigEnvironment());
		assertFalse("Config should still be false (static variable)", picsActionSupport.isConfigEnvironment());
		verify(em, times(1)).find(AppProperty.class, "PICS.config");
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
		when(picsActionSupport.isBetaVersion()).thenReturn(true);

		assertTrue("url does not have beta", picsActionSupport.isBetaEnvironment());
	}

	@Test
	public void testIsBetaEnvironment_UrlContainsWithNoBetaCheckVersionYes() throws Exception {
		when(request.getRequestURL()).thenReturn(new StringBuffer("www.picsorganizer.com"));
		when(request.getRequestURI()).thenReturn(new String(""));
		when(picsActionSupport.isBetaVersion()).thenReturn(true);

		assertTrue("url has beta", picsActionSupport.isBetaEnvironment());
	}

	@Test
	public void testIsBetaEnvironment_UrlContainsWithNoBetaCheckVersionNo() throws Exception {
		when(request.getRequestURL()).thenReturn(new StringBuffer("www.picsorganizer.com"));
		when(request.getRequestURI()).thenReturn(new String(""));
		when(picsActionSupport.isBetaVersion()).thenReturn(false);

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
		when(picsActionSupport.isBetaVersion()).thenReturn(true);

		assertFalse("url does not have stable", picsActionSupport.isLiveEnvironment());
	}

	@Test
	public void testIsLiveEnvironment_UrlContainsWithNoStableCheckVersionNo() throws Exception {
		when(request.getRequestURL()).thenReturn(new StringBuffer("www.picsorganizer.com"));
		when(request.getRequestURI()).thenReturn(new String(""));
		when(picsActionSupport.isBetaVersion()).thenReturn(true);

		assertFalse("has beta cookie, its not stable", picsActionSupport.isLiveEnvironment());
	}

	@Test
	public void testIsLiveEnvironment_UrlContainsWithNoStableCheckVersionYes() throws Exception {
		when(request.getRequestURL()).thenReturn(new StringBuffer("www.picsorganizer.com"));
		when(request.getRequestURI()).thenReturn(new String(""));
		when(picsActionSupport.isBetaVersion()).thenReturn(false);

		assertTrue("has no beta cookie, its stable", picsActionSupport.isLiveEnvironment());
	}
}
