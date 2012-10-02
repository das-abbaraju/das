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
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.PicsActionTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.report.ReportActionSupport;

public class ReportUsageInterceptorTest extends PicsActionTest {
	private ReportUsageInterceptor classUnderTest;
	
	@Mock
	private ActionInvocation invocation;
	@Mock
	private ReportActionSupport goodAction;
	@Mock
	private ActionSupport badAction;
	@Mock
	private Logger logger;

	@After
	public void tearDown() throws Exception {
		Whitebox.setInternalState(ReportUsageInterceptor.class, "logger", (Logger) null);
	}
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ReportUsageInterceptor();
		setupMocks();
		Whitebox.setInternalState(ReportUsageInterceptor.class, "logger", logger);
		when(permissions.getUserId()).thenReturn(1);
		when(invocation.invoke()).thenReturn("SUCCESS");
		when(invocation.getInvocationContext()).thenReturn(actionContext);
	}
	
	@Test
	public void testIntercept_goodAction() throws Exception {
		when(invocation.getAction()).thenReturn(goodAction);
		when(goodAction.toString()).thenReturn("foo.bar.baz@something");
		parameters.put("foo", "bar");
		assertEquals("SUCCESS", classUnderTest.intercept(invocation));
		
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
