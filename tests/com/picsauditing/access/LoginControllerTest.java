package com.picsauditing.access;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Database;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.SpringUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ActionContext.class, SpringUtils.class, ServletActionContext.class, AjaxUtils.class })
public class LoginControllerTest {
	LoginController controller;

	@Mock
	private HttpServletRequest request;
	@Mock
	private I18nCache i18nCache;
	@Mock 
	private Database databaseForTesting;
	@Mock
	private PicsActionSupport picsActionSupport;
	@Mock
	private Permissions permissions;
	@Mock
	private JSONObject json;
	private Map<String, Object> session;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);
		
		when(permissions.isLoggedIn()).thenReturn(true);
		
		controller = new LoginController();

		session = new HashMap<String, Object>();
		session.put("permissions", new Permissions());

		setupMocks();

		Whitebox.setInternalState(controller, "i18nCache", i18nCache);
		when(i18nCache.hasKey(anyString(), any(Locale.class))).thenReturn(false);
	}

	private void setupMocks() {
		ActionContext actionContext = mock(ActionContext.class);
		when(actionContext.getSession()).thenReturn(session);
		
		PowerMockito.mockStatic(ActionContext.class);
		when(ActionContext.getContext()).thenReturn(actionContext);

		PowerMockito.mockStatic(ServletActionContext.class);
		when(ServletActionContext.getRequest()).thenReturn(request);
		when(request.getCookies()).thenReturn(new Cookie[] {});

		PowerMockito.mockStatic(AjaxUtils.class);
	}

	@Test
	public void testDefaultAction() throws Exception {
		assertEquals(LoginController.SUCCESS, controller.execute());
		assertEquals(0, controller.getActionMessages().size());
	}

	@Test
	public void testCookiesAreDisabled() throws Exception {
		controller.setButton("logout");
		when(request.getCookies()).thenReturn(null);
		assertEquals(LoginController.SUCCESS, controller.execute());
		assertEquals(1, controller.getActionMessages().size());
	}

	@Test
	public void testLogout() throws Exception {
		controller.setButton("logout");
		assertEquals(LoginController.SUCCESS, controller.execute());
		assertEquals(0, controller.getActionMessages().size());
	}
	
	@Test
	public void testAjax_nullAjax() throws Exception{
		PowerMockito.when(AjaxUtils.isAjax(request)).thenReturn(false);

		assertEquals("blank", controller.ajax());		
	}
	
	@Test
	public void testAjax_withAjax() throws Exception{
		PowerMockito.when(AjaxUtils.isAjax(request)).thenReturn(true);
		String result = "success";
		
		//json.put("loggedIn", permissions.isLoggedIn());
		//assertEquals("json", controller.ajax());
	}
}
