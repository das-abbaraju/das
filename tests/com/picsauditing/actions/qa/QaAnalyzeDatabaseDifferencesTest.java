package com.picsauditing.actions.qa;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsActionTest;
import com.picsauditing.access.NoRightsException;

public class QaAnalyzeDatabaseDifferencesTest extends PicsActionTest {
	private QaAnalyzeDatabaseDifferences analyzeQaDiffLive;
	
	@Mock
	private FlagAnalyzer flagAnalyzer;
	@Mock
	private AuditAnalyzer auditAnalyzer;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		analyzeQaDiffLive = new QaAnalyzeDatabaseDifferences(flagAnalyzer, auditAnalyzer);
		super.setUp(analyzeQaDiffLive);
		
		when(permissions.isAdmin()).thenReturn(true);
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
