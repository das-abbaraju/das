package com.picsauditing.jpa.entities;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

    @Test
    public void testComparators() {
        assertFalse(LowMedHigh.Low.isLessThan(LowMedHigh.Low));
        assertTrue(LowMedHigh.Low.isLessThan(LowMedHigh.Med));
        assertTrue(LowMedHigh.Low.isLessThan(LowMedHigh.High));

        assertTrue(LowMedHigh.Low.isLessThanOrEqualTo(LowMedHigh.Low));
        assertTrue(LowMedHigh.Low.isLessThan(LowMedHigh.Med));
        assertTrue(LowMedHigh.Low.isLessThan(LowMedHigh.High));

        assertFalse(LowMedHigh.Low.isGreaterThan(LowMedHigh.Low));
        assertFalse(LowMedHigh.Low.isGreaterThan(LowMedHigh.Med));
        assertFalse(LowMedHigh.Low.isGreaterThan(LowMedHigh.High));

        assertTrue(LowMedHigh.Low.isGreaterThanOrEqualTo(LowMedHigh.Low));
        assertFalse(LowMedHigh.Low.isGreaterThanOrEqualTo(LowMedHigh.Med));
        assertFalse(LowMedHigh.Low.isGreaterThanOrEqualTo(LowMedHigh.High));

        assertFalse(LowMedHigh.Med.isLessThan(LowMedHigh.Low));
        assertFalse(LowMedHigh.Med.isLessThan(LowMedHigh.Med));
        assertTrue(LowMedHigh.Med.isLessThan(LowMedHigh.High));

        assertFalse(LowMedHigh.Med.isLessThanOrEqualTo(LowMedHigh.Low));
        assertTrue(LowMedHigh.Med.isLessThanOrEqualTo(LowMedHigh.Med));
        assertTrue(LowMedHigh.Med.isLessThanOrEqualTo(LowMedHigh.High));

        assertTrue(LowMedHigh.Med.isGreaterThan(LowMedHigh.Low));
        assertFalse(LowMedHigh.Med.isGreaterThan(LowMedHigh.Med));
        assertFalse(LowMedHigh.Med.isGreaterThan(LowMedHigh.High));

        assertTrue(LowMedHigh.Med.isGreaterThanOrEqualTo(LowMedHigh.Low));
        assertTrue(LowMedHigh.Med.isGreaterThanOrEqualTo(LowMedHigh.Med));
        assertFalse(LowMedHigh.Med.isGreaterThanOrEqualTo(LowMedHigh.High));

        assertFalse(LowMedHigh.High.isLessThan(LowMedHigh.Low));
        assertFalse(LowMedHigh.High.isLessThan(LowMedHigh.Med));
        assertFalse(LowMedHigh.High.isLessThan(LowMedHigh.High));

        assertFalse(LowMedHigh.High.isLessThanOrEqualTo(LowMedHigh.Low));
        assertFalse(LowMedHigh.High.isLessThanOrEqualTo(LowMedHigh.Med));
        assertTrue(LowMedHigh.High.isLessThanOrEqualTo(LowMedHigh.High));

        assertTrue(LowMedHigh.High.isGreaterThan(LowMedHigh.Low));
        assertTrue(LowMedHigh.High.isGreaterThan(LowMedHigh.Med));
        assertFalse(LowMedHigh.High.isGreaterThan(LowMedHigh.High));

        assertTrue(LowMedHigh.High.isGreaterThanOrEqualTo(LowMedHigh.Low));
        assertTrue(LowMedHigh.High.isGreaterThanOrEqualTo(LowMedHigh.Med));
        assertTrue(LowMedHigh.High.isGreaterThanOrEqualTo(LowMedHigh.High));
    }
}
