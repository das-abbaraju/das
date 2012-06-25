package com.picsauditing.interceptors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.report.ReportActionSupport;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ActionContext.class, LoggerFactory.class})
public class ReportUsageInterceptorTest {
	
	@Mock ActionContext actionContext;
	@Mock ActionInvocation invocation;
	@Mock ReportActionSupport goodAction;
	@Mock ActionSupport badAction;
	@Mock Permissions permissions;
	@Mock Logger logger;
	Map<String, Object> fauxSession;
	ReportUsageInterceptor classUnderTest;
	
	@SuppressWarnings("rawtypes")
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(ActionContext.class);
		PowerMockito.mockStatic(LoggerFactory.class);
		PowerMockito.when(ActionContext.getContext()).thenReturn(actionContext);
		PowerMockito.when(LoggerFactory.getLogger((Class) any())).thenReturn(logger);
		
		classUnderTest = new ReportUsageInterceptor();
		fauxSession = new HashMap<String, Object>();
			fauxSession.put("permissions", permissions);
		when(actionContext.getSession()).thenReturn(fauxSession);
		when(permissions.getUserId()).thenReturn(1);
		
		when(invocation.invoke()).thenReturn("SUCCESS");
		when(invocation.getInvocationContext()).thenReturn(actionContext);
		when(actionContext.getParameters()).thenReturn(fauxSession);
	}
	
	@Test
	public void testIntercept_goodAction() throws Exception {
		when(invocation.getAction()).thenReturn(goodAction);
		when(goodAction.toString()).thenReturn("foo.bar.baz@something");
		
		assertEquals("SUCCESS", classUnderTest.intercept(invocation));
		
		//verify(logger).info((String) any(), (Object[]) any());
		verify(logger).info("{},{},{}", new Object[] {1, "baz", 1} );
	}
	
	@Test
	public void testIntercept_badAction() throws Exception {
		when(invocation.getAction()).thenReturn(badAction);
		when(badAction.toString()).thenReturn("foo.bar.baz@something");
		
		assertEquals("SUCCESS", classUnderTest.intercept(invocation));
		
		verify(logger, never()).info((String) any(), (Object[]) any());
	}
	
	@Test
	public void testIntercept_noMatch() throws Exception {
		when(invocation.getAction()).thenReturn(badAction);
		when(badAction.toString()).thenReturn("noMatch");
		
		assertEquals("SUCCESS", classUnderTest.intercept(invocation));
		
		verify(logger, never()).info((String) any(), (Object[]) any());
	}

}
