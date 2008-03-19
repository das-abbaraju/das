package com.picsauditing.util;

import junit.framework.TestCase;

public class StringsTest extends TestCase {

	public StringsTest(String name) {
		super(name);
	}
	public void testInsertSpaceNull() {
		assertEquals(null, Strings.insertSpaces(null));
	}
	public void testInsertSpace1() {
		assertEquals("a", Strings.insertSpaces("a"));
	}
	public void testInsertSpace3() {
		assertEquals("a b c", Strings.insertSpaces("abc"));
	}
}
