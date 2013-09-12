package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;

public class LowMedHighTest {
	@Test
	public void testGetMap() {
		Map<Integer, LowMedHigh> map = LowMedHigh.getMap();

		assertEquals(LowMedHigh.None, map.get(0));
		assertEquals(LowMedHigh.Low, map.get(1));
		assertEquals(LowMedHigh.Med, map.get(2));
		assertEquals(LowMedHigh.High, map.get(3));
		assertNull(map.get(4));
	}

	@Test
	public void testGetName() {
		assertEquals("None", LowMedHigh.getName(0));
		assertEquals("Low", LowMedHigh.getName(1));
		assertEquals("Med", LowMedHigh.getName(2));
		assertEquals("High", LowMedHigh.getName(3));
		assertEquals("", LowMedHigh.getName(4));
	}

	@Test
	public void testParseLowMedHigh_Parseable() {
		assertEquals(LowMedHigh.None, LowMedHigh.parseLowMedHigh("None"));
		assertEquals(LowMedHigh.Low, LowMedHigh.parseLowMedHigh("Low"));
		assertEquals(LowMedHigh.Med, LowMedHigh.parseLowMedHigh("Medium"));
		assertEquals(LowMedHigh.High, LowMedHigh.parseLowMedHigh("High"));
		assertNull(LowMedHigh.parseLowMedHigh(""));
		assertNull(LowMedHigh.parseLowMedHigh(" "));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseLowMedHigh_NotParseable() {
		assertNull(LowMedHigh.parseLowMedHigh("Other Value"));
	}
}
