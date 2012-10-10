package com.picsauditing.interceptors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.PicsActionTest;
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
