package com.picsauditing.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;



public class GeoTest  {

	@Test
	public void testDistance() {
		assertEquals(0.0, Geo.distance(0, 0, 0, 0), 0.01);
		assertEquals(1568.5205567985759, Geo.distance(10, 10, 0, 0), 0.01);
		assertEquals(1568.5205567985759, Geo.distance(-10, -10, 0, 0), 0.01);
		assertEquals(1568.5205567985759, Geo.distance(-10, 10, 0, 0), 0.01);
		assertEquals(1568.5205567985759, Geo.distance(10, -10, 0, 0), 0.01);
		assertEquals(3137.041113597152, Geo.distance(10, 10, -10, -10), 0.01);
	}
}
