package com.picsauditing.jpa.entities;

import static org.junit.Assert.*;

import org.junit.Test;

public class YesNoTest {

	@Test
	public void testToBoolean_NullReturnsFalse() {
		boolean result = YesNo.toBoolean(null);

		assertFalse(result);
	}

	@Test
	public void testToBoolean_NoReturnsFalse() {
		boolean result = YesNo.toBoolean(YesNo.No);

		assertFalse(result);
	}

	@Test
	public void testToBoolean_YesReturnsTrue() {
		boolean result = YesNo.toBoolean(YesNo.Yes);

		assertTrue(result);
	}
}
