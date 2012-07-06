package com.picsauditing.access;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

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

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.util.SpringUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ActionContext.class, SpringUtils.class, ServletActionContext.class })
public class LoginControllerTest {
	LoginController controller;

	@Mock
	private HttpServletRequest request;
	@Mock
	private I18nCache i18nCache;

	private Map<String, Object> session;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

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
}
