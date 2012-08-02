package com.picsauditing.report.fields;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class QueryMethodTest {
	@Test
	public void testIsAggregate() {
		assertTrue(QueryMethod.Average.isAggregate());
		assertFalse(QueryMethod.LowerCase.isAggregate());
	}

	@Test
	public void testIsNeedsParameter() {
		assertFalse(QueryMethod.Count.isNeedsParameter());
		assertTrue(QueryMethod.Round.isNeedsParameter());
		assertTrue(QueryMethod.Left.isNeedsParameter());
	}

}
