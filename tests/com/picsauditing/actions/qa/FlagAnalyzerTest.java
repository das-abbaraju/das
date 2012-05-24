package com.picsauditing.actions.qa;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.picsauditing.search.SelectSQL;

@RunWith(PowerMockRunner.class)
@PrepareForTest(QueryRunnerFactory.class)
public class FlagAnalyzerTest {
	private FlagAnalyzer flagAnalyzer;
	
	@Mock private QueryRunner queryRunner;
	@Mock private TabularModel velocityData;
	@Mock private TabularModel flagDiffData;
	@Mock private TabularModel flagDataDiffDetails;
	@Mock private TabularModel flagDiffCaoStatus;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		PowerMockito.mockStatic(QueryRunnerFactory.class);
		when(QueryRunnerFactory.instance((SelectSQL) anyObject())).thenReturn(queryRunner);
		
		flagAnalyzer = new FlagAnalyzer();
	}

	@Test
	public void testRun() throws Exception {
		when(queryRunner.run())
		.thenReturn(velocityData)
		.thenReturn(flagDiffData)
		.thenReturn(flagDataDiffDetails)
		.thenReturn(flagDiffCaoStatus);
		
		flagAnalyzer.run();
		
		assertEquals(velocityData, flagAnalyzer.getVelocityData());
		assertEquals(flagDiffData, flagAnalyzer.getFlagDiffData());
		assertEquals(flagDataDiffDetails, flagAnalyzer.getFlagDataDiffDetails());
		assertEquals(flagDiffCaoStatus, flagAnalyzer.getFlagDiffCaoStatus());
	}
	
	@Test
	public void testRun_SQLException() throws Exception {
		when(queryRunner.run()).thenThrow(new SQLException());
		try{
			flagAnalyzer.run();
			fail("expected exception not thrown");
		} catch (SQLException e) {
			assertTrue("expected exception thrown", true);
		}
	}

}
