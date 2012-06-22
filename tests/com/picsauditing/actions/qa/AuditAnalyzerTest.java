package com.picsauditing.actions.qa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.picsauditing.search.SelectSQL;

@RunWith(PowerMockRunner.class)
@PrepareForTest(QueryRunnerFactory.class)
@PowerMockIgnore({"javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*"})
public class AuditAnalyzerTest {
	private AuditAnalyzer auditAnalyzer;
	
	@Mock private QueryRunner queryRunner;
	@Mock private TabularModel velocityData;
	@Mock private TabularModel auditDiffData;
	@Mock private TabularModel caoDiffData;
	@Mock private TabularModel auditForDiffData;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		PowerMockito.mockStatic(QueryRunnerFactory.class);
		when(QueryRunnerFactory.instance((SelectSQL) anyObject())).thenReturn(queryRunner);
		
		auditAnalyzer = new AuditAnalyzer();
	}

	@Test
	public void testRun() throws Exception {
		when(queryRunner.run())
		.thenReturn(velocityData)
		.thenReturn(auditDiffData)
		.thenReturn(caoDiffData)
		.thenReturn(auditForDiffData);
		
		auditAnalyzer.run();
		
		assertEquals(velocityData, auditAnalyzer.getVelocityData());
		assertEquals(auditDiffData, auditAnalyzer.getAuditDiffData());
		assertEquals(caoDiffData, auditAnalyzer.getCaoDiffData());
		assertEquals(auditForDiffData, auditAnalyzer.getAuditForDiffData());
	}
	
	@Test
	public void testRun_SQLException() throws Exception {
		when(queryRunner.run()).thenThrow(new SQLException());
		try{
			auditAnalyzer.run();
			fail("expected exception not thrown");
		} catch (SQLException e) {
			assertTrue("expected exception thrown", true);
		}
	}
}
