package com.picsauditing.util;

import junit.framework.TestCase;

public class GeoTest extends TestCase {

	public void testDistance() {
		assertEquals(0.0, Geo.distance(0, 0, 0, 0));
		assertEquals(1568.5205567985759, Geo.distance(10, 10, 0, 0));
		assertEquals(1568.5205567985759, Geo.distance(-10, -10, 0, 0));
		assertEquals(1568.5205567985759, Geo.distance(-10, 10, 0, 0));
		assertEquals(1568.5205567985759, Geo.distance(10, -10, 0, 0));
		assertEquals(3137.041113597152, Geo.distance(10, 10, -10, -10));
	}
}
