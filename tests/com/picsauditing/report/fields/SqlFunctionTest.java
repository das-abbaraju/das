package com.picsauditing.report.fields;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SqlFunctionTest {
	@Test
	public void testIsAggregate() {
		assertTrue(SqlFunction.Average.isAggregate());
		assertFalse(SqlFunction.LowerCase.isAggregate());
	}

	@Test
	public void testIsNeedsParameter() {
		assertFalse(SqlFunction.Count.isNeedsParameter());
		assertTrue(SqlFunction.Round.isNeedsParameter());
		assertTrue(SqlFunction.Left.isNeedsParameter());
	}

}
