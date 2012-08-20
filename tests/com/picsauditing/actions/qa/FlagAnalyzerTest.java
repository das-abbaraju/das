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

public class FlagAnalyzerTest extends AnalyzerTest {
	private FlagAnalyzer flagAnalyzer;

	@Mock
	private TabularModel flagDiffData;
	@Mock
	private TabularModel flagDiffCaoStatus;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		super.setUp();

		flagAnalyzer = new FlagAnalyzer();
	}

	@Test
	public void testRun() throws Exception {
		when(queryRunner.run()).thenReturn(flagDiffData).thenReturn(flagDiffCaoStatus);

		flagAnalyzer.run();

		assertEquals(flagDiffData, flagAnalyzer.getFlagDiffData());
		assertEquals(flagDiffCaoStatus, flagAnalyzer.getFlagDiffCaoStatus());
	}

	@Test
	public void testRun_SQLException() throws Exception {
		when(queryRunner.run()).thenThrow(new SQLException());
		try {
			flagAnalyzer.run();
			fail("expected exception not thrown");
		} catch (SQLException e) {
			assertTrue("expected exception thrown", true);
		}
	}
}
