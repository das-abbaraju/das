package com.picsauditing.actions.qa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AuditAnalyzerTest extends AnalyzerTest {
	private AuditAnalyzer auditAnalyzer;

	@Mock
	private TabularModel auditDiffData;
	@Mock
	private TabularModel caoDiffData;
	@Mock
	private TabularModel auditForDiffData;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		super.setUp();

		auditAnalyzer = new AuditAnalyzer();
	}

	@Test
	public void testRun() throws Exception {
		when(queryRunner.run()).thenReturn(auditDiffData).thenReturn(caoDiffData).thenReturn(auditForDiffData);

		auditAnalyzer.run();

		assertEquals(auditDiffData, auditAnalyzer.getAuditDiffData());
		assertEquals(caoDiffData, auditAnalyzer.getCaoDiffData());
		assertEquals(auditForDiffData, auditAnalyzer.getAuditForDiffData());
	}

	@Test
	public void testRun_SQLException() throws Exception {
		when(queryRunner.run()).thenThrow(new SQLException());
		try {
			auditAnalyzer.run();
			fail("expected exception not thrown");
		} catch (SQLException e) {
			assertTrue("expected exception thrown", true);
		}
	}
}
