package com.picsauditing.util;

import junit.framework.TestCase;

public class LuhnTest extends TestCase {

	public LuhnTest(String name) {
		super(name);
	}

	public final void testIsValidNumber() {
		assertTrue(Luhn.isValidNumber("11116"));
	}

	public final void testGetCheckDigit() {
		assertEquals("6".charAt(0), Luhn.getCheckDigit("1111"));
	}
}
