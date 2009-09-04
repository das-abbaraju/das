package com.picsauditing.util;

import junit.framework.TestCase;

public class LuhnTest extends TestCase {

	public LuhnTest(String name) {
		super(name);
	}

	public final void testIsValidNumber2() {
		assertTrue(Luhn.isValidNumber("19463"));
	}

	public final void testIsValidNumber() {
		assertTrue(Luhn.isValidNumber("11114"));
	}

	public final void testGetCheckDigit() {
		assertEquals("4".charAt(0), Luhn.getCheckDigit("1111"));
	}
}
