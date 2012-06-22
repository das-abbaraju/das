package com.picsauditing.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.util.SpringUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ActionContext.class, SpringUtils.class, ServletActionContext.class })
@PowerMockIgnore({ "javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*" })
public class PicsActionSupportTest {
	PicsActionSupport picsActionSupport;

	@Mock
	private EntityManager em;
	@Mock
	HttpServletRequest request;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		Map<String, Object> session = new HashMap<String, Object>();
		session.put("permissions", new Permissions());

		ActionContext actionContext = mock(ActionContext.class);
		when(actionContext.getSession()).thenReturn(session);

		PowerMockito.mockStatic(ActionContext.class);
		when(ActionContext.getContext()).thenReturn(actionContext);

		PowerMockito.mockStatic(ServletActionContext.class);
		when(ServletActionContext.getRequest()).thenReturn(request);

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
		
		assertFalse("url does not have beta", picsActionSupport.isBetaEnvironment());
	}

	@Test
	public void testIsBetaEnvironment_UrlContainsWithNoBetaCheckCookieExist() throws Exception {
		picsActionSupport = PowerMockito.spy(new PicsActionSupport());
		PowerMockito.doReturn("www.picsorganizer.com").when(picsActionSupport, "getRequestHost");
		when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("USE_BETA", "true") });
		assertTrue("url has beta", picsActionSupport.isBetaEnvironment());
	}

	@Test
	public void testIsBetaEnvironment_UrlContainsWithNoBetaCheckCookieNotExist() throws Exception {
		picsActionSupport = PowerMockito.spy(new PicsActionSupport());
		PowerMockito.doReturn("www.picsorganizer.com").when(picsActionSupport, "getRequestHost");
		when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("Nothing", "true") });

		assertFalse("url does not have beta", picsActionSupport.isBetaEnvironment());
	}
}
