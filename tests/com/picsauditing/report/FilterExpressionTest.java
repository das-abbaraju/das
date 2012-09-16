package com.picsauditing.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

public class FilterExpressionTest {

	@Test
	public void testDefault1() throws Exception {
		assertEquals("{1}", FilterExpression.getDefault(1));
	}

	@Test
	public void testDefault2() throws Exception {
		assertEquals("{1} AND {2}", FilterExpression.getDefault(2));
	}

	@Test
	public void testDefault4() throws Exception {
		assertEquals("{1} AND {2} AND {3} AND {4}", FilterExpression.getDefault(4));
	}

	@Test
	public void testSimple() throws Exception {
		assertTrue(FilterExpression.isValid("{1}"));
	}

	@Test
	public void testNull() throws Exception {
		assertTrue(FilterExpression.isValid(null));
	}

	@Test
	public void testEmpty() throws Exception {
		assertTrue(FilterExpression.isValid("  "));
	}

	@Test
	public void testAdvanced() throws Exception {
		assertTrue(FilterExpression.isValid("({1} AND {2}) OR ({3} AND {4}) AND {13}"));
	}

	@Ignore
	@Test
	public void testBad() {
		assertFalse(FilterExpression.isValid("A AND B"));
	}
}
