package com.picsauditing.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class AppVersionTest {

	private AppVersion version1_0 = new AppVersion(1, 0);
	private AppVersion version1_3 = new AppVersion(1, 2);
	private AppVersion version2_0 = new AppVersion(2, 0);
	private AppVersion version2_3 = new AppVersion(2, 2);

	@Test
	public void testGreaterThan_majorGreater() throws Exception {
		assertTrue(version2_0.greaterThan(version1_3));
		assertTrue(version2_3.greaterThan(version1_0));
		assertFalse(version2_0.greaterThan(version2_0));
		assertFalse(version2_0.greaterThan(version2_3));
	}

	@Test
	public void testGreaterThan_minorGreater() throws Exception {
		assertTrue(version1_3.greaterThan(version1_0));
	}

	@Test
	public void testGreaterThan_bothEqual() throws Exception {
		assertTrue(version1_0.equals(new AppVersion(1, 0)));
	}
	
	@Test
	public void testVersionString_2() throws Exception {
		AppVersion version = new AppVersion("2");
		assertEquals(2, version.getMajor());
		assertEquals(0, version.getMinor());
	}
	
	@Test
	public void testVersionString_2_0() throws Exception {
		AppVersion version = new AppVersion("2.0");
		assertEquals(2, version.getMajor());
		assertEquals(0, version.getMinor());
	}
	
}
