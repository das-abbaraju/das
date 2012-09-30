package com.picsauditing.actions.qa;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.picsauditing.PicsTest;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.Permissions;
import com.picsauditing.util.SpringUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ActionContext.class, SpringUtils.class, ServletActionContext.class })
@PowerMockIgnore({"javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*"})
public class QaAnalyzeDatabaseDifferencesTest extends PicsTest {
	private QaAnalyzeDatabaseDifferences analyzeQaDiffLive;
	
	@Mock
	private Permissions permissions;
	@Mock
	private FlagAnalyzer flagAnalyzer;
	@Mock
	private AuditAnalyzer auditAnalyzer;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		MockitoAnnotations.initMocks(this);
		
		when(permissions.getLocale()).thenReturn(Locale.ENGLISH);
		when(permissions.isAdmin()).thenReturn(true);
		Map<String, Object> session = new HashMap<String, Object>();
		session.put("permissions", permissions);
		
		ActionContext actionContext = mock(ActionContext.class);
		when(actionContext.getSession()).thenReturn(session);
		PowerMockito.mockStatic(ServletActionContext.class);
		when(ServletActionContext.getRequest()).thenReturn(request);
		when(ServletActionContext.getResponse()).thenReturn(response);
		
		PowerMockito.mockStatic(ActionContext.class);
		when(ActionContext.getContext()).thenReturn(actionContext);
		
		analyzeQaDiffLive = new QaAnalyzeDatabaseDifferences(flagAnalyzer, auditAnalyzer); 
	}		
	
	@Test
	public void testExecuteFlags_FlagAnalyzerRun() throws Exception {
		analyzeQaDiffLive.executeFlags();
		
		verify(flagAnalyzer).run();
		verify(auditAnalyzer, never()).run();
	}

	@Test
	public void testExecuteFlags_AuditAnalyzerRun() throws Exception {
		analyzeQaDiffLive.executeAudits();
		
		verify(flagAnalyzer, never()).run();
		verify(auditAnalyzer).run();
	}
	
	@Test
	public void testExecuteFlags_ReportNameSet() throws Exception {
		analyzeQaDiffLive.executeFlags();
		
		assertTrue("Flag Analyzer".equals(analyzeQaDiffLive.getReportName()));
	}

	@Test
	public void testExecuteAudits_ReportNameSet() throws Exception {
		analyzeQaDiffLive.executeAudits();
		
		assertTrue("Audit Analyzer".equals(analyzeQaDiffLive.getReportName()));
	}

	@Test
	public void testExecuteFlags_NoPermissionsNotAdmit() throws Exception {
		when(permissions.isAdmin()).thenReturn(false);
		
		try {
			analyzeQaDiffLive.executeFlags();
			fail("should have thrown NoRightsException");
		} catch (NoRightsException e) {
			assertTrue("threw expected exception", true);
		}
	}
	
	@Test
	public void testExecuteAudits_NoPermissionsNotAdmit() throws Exception {
		when(permissions.isAdmin()).thenReturn(false);
		
		try {
			analyzeQaDiffLive.executeAudits();
			fail("should have thrown NoRightsException");
		} catch (NoRightsException e) {
			assertTrue("threw expected exception", true);
		}
	}
}
