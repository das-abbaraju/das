package com.picsauditing.search;

import junit.framework.TestCase;

public class SelectFilterTest extends TestCase {

	public SelectFilterTest(String name) {
		super(name);
	}
	
	public void testString() {
		SelectFilter filter = new SelectFilter("foo", "fooColumn = '?'", "bar");
		assertEquals(filter.getWhere(), "fooColumn = 'bar'");
		assertEquals(filter.getValue(), "bar");
		assertTrue(filter.isSet());
	}
	
	public void testStringEmpty() {
		SelectFilter filter = new SelectFilter("foo", "fooColumn = '?'", "");
		assertEquals(filter.getWhere(), "");
		assertEquals(filter.getValue(), "");
		assertFalse(filter.isSet());
	}
	
	public void testStringPrompt() {
		SelectFilter filter = new SelectFilter("foo", "fooColumn = '?'", "bar", "- Pick -", "- Pick -");
		assertEquals(filter.getWhere(), "fooColumn = 'bar'");
		assertEquals(filter.getValue(), "bar");
		assertTrue(filter.isSet());
	}
	
	public void testStringPromptEmpty() {
		SelectFilter filter = new SelectFilter("foo", "fooColumn = '?'", "- Pick -", "- Pick -", "- Pick -");
		assertEquals(filter.getWhere(), "");
		assertEquals(filter.getValue(), "- Pick -");
		assertFalse(filter.isSet());
	}
	
	public void testStringDefaultSame() {
		SelectFilter filter = new SelectFilter("foo", "fooColumn = '?'", "bar", "bar", "");
		assertEquals(filter.getWhere(), "fooColumn = 'bar'");
		assertEquals(filter.getValue(), "bar");
		assertTrue(filter.isSet());
	}
	
	public void testStringDefaultEmpty() {
		SelectFilter filter = new SelectFilter("foo", "fooColumn = '?'", "", "bar", "");
		assertEquals(filter.getWhere(), "");
		assertEquals(filter.getValue(), "bar");
		assertFalse(filter.isSet());
	}

	public void testStringDefaultDifferent() {
		SelectFilter filter = new SelectFilter("foo", "fooColumn = '?'", "foo", "bar", "");
		assertEquals(filter.getWhere(), "fooColumn = 'foo'");
		assertEquals(filter.getValue(), "foo");
		assertTrue(filter.isSet());
	}
}
