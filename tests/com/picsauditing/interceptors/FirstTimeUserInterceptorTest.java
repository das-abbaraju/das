package com.picsauditing.interceptors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.access.Permissions;

public class FirstTimeUserInterceptorTest {

	FirstTimeUserInterceptor firstTimeUserInterceptor;

	@Mock
	private Permissions permissions;
	@Mock
	private ActionContext actionContext;
	@Mock
	private Map<String, Object> session;
	@Mock
	private ActionInvocation invocation;
	@Mock
	private ValueStack valueStack;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		firstTimeUserInterceptor = new FirstTimeUserInterceptor();
		when(actionContext.getSession()).thenReturn(session);
		when(invocation.invoke()).thenReturn("next");
		when(invocation.getStack()).thenReturn(valueStack);

		ActionContext.setContext(actionContext);
	}

	@Test
	public void testIntercept_NullPermissions() throws Exception {
		when(session.get("permissions")).thenReturn(null);
		String result = firstTimeUserInterceptor.intercept(invocation);
		assertEquals("next", result);
	}

	@Test
	public void testIntercept_RedirectForFirstTimeUser() throws Exception {
		when(permissions.isLoggedIn()).thenReturn(true);
		when(permissions.isUsingDynamicReports()).thenReturn(true);
		when(permissions.getUsingDynamicReportsDate()).thenReturn(null);

		when(session.get("permissions")).thenReturn(permissions);

		String result = firstTimeUserInterceptor.intercept(invocation);

		assertEquals("redirect", result);
	}

	@Test
	public void testRedirectUserToTutorial_NullPermissions() throws Exception {
		Boolean result = Whitebox.invokeMethod(firstTimeUserInterceptor, "redirectUserToTutorial",
				(Permissions) null);
		assertFalse(result);
	}

	@Test
	public void testRedirectUserToTutorial_AlreadyUsedDynamicReports() throws Exception {
		when(permissions.isLoggedIn()).thenReturn(true);
		when(permissions.isUsingDynamicReports()).thenReturn(true);
		when(permissions.getUsingDynamicReportsDate()).thenReturn(new Date());

		Boolean result = Whitebox.invokeMethod(firstTimeUserInterceptor, "redirectUserToTutorial",
				permissions);

		assertFalse(result);
	}

	@Test
	public void testRedirectUserToTutorial_FirstTimeUser() throws Exception {
		when(permissions.isLoggedIn()).thenReturn(true);
		when(permissions.isUsingDynamicReports()).thenReturn(true);
		when(permissions.getUsingDynamicReportsDate()).thenReturn(null);

		Boolean result = Whitebox.invokeMethod(firstTimeUserInterceptor, "redirectUserToTutorial",
				permissions);

		assertTrue(result);
	}

}
