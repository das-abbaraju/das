package com.picsauditing.actions.qa;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FlagChangeVelocityAnalyzerTest extends AnalyzerTest {
	private FlagChangeVelocityAnalyzer flagChangeVelocityAnalyzer;

	@Mock
	private TabularModel velocityData;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		super.setUp();

		flagChangeVelocityAnalyzer = new FlagChangeVelocityAnalyzer();
	}

	@Test
	public void testRun() throws Exception {
		when(queryRunner.run()).thenReturn(velocityData);

		flagChangeVelocityAnalyzer.run();

		assertEquals(velocityData, flagChangeVelocityAnalyzer.getVelocityData());
	}

}
