package com.picsauditing.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class AppVersionTest {

	private AppVersion version1_0 = new AppVersion(1, 0);
	private AppVersion version1_3 = new AppVersion(1, 3);
	private AppVersion version2_0 = new AppVersion(2, 0);
	private AppVersion version2_3 = new AppVersion(2, 3);
	private AppVersion version2_3_1 = new AppVersion(2, 3, 1);

    private AppVersion version7_0 = new AppVersion(7, 0);
    private AppVersion version8_0 = new AppVersion(8, 0);
    private AppVersion version7_0_2 = new AppVersion(7, 0, 2);
    private AppVersion version7_0_str = new AppVersion("7", "0");
    private AppVersion version8_0_str = new AppVersion("8", "0");
    private AppVersion version7_0_2_str = new AppVersion("7", "0", "2");


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

    @Test
    public void testVersionString_WithPatchLevel_MajorMinorComparedToMajorMinorPatch_GreaterThan() throws Exception {
        assertFalse(version7_0.greaterThan(version7_0_2));
        assertTrue(version7_0_2.greaterThan(version7_0));
    }

    @Test
    public void testVersionString_WithPatchLevel_MajorMinorComparedToMajorMinorPatch_LessThan() throws Exception {
        assertFalse(version7_0_2.greaterThan(version8_0));
        assertTrue(version8_0.greaterThan(version7_0_2));
    }

    @Test
    public void testVersionString_WithPatchLevel_MajorMinorComparedToMajorMinorPatchWithInts_GreaterThan() throws Exception {
        assertFalse(version7_0.greaterThan(7, 0, 2));
        assertTrue(version7_0_2.greaterThan(7, 0));
    }

    @Test
    public void testVersionString_WithPatchLevel_MajorMinorComparedToMajorMinorPatchWithInts_LessThan() throws Exception {
        assertFalse(version7_0_2.greaterThan(8, 0));
        assertTrue(version8_0.greaterThan(7, 0, 2));
    }

    @Test
    public void testVersionString_WithPatchLevel_MajorMinorComparedToMajorMinorPatchWithStrings_GreaterThan() throws Exception {
        assertFalse(version7_0_str.greaterThan(version7_0_2_str));
        assertTrue(version7_0_2_str.greaterThan(version7_0_str));
    }

    @Test
    public void testVersionString_WithPatchLevel_MajorMinorComparedToMajorMinorPatchWithStrings_LessThan() throws Exception {
        assertFalse(version7_0_2_str.greaterThan(version8_0_str));
        assertTrue(version8_0_str.greaterThan(version7_0_2_str));
    }

    @Test
    public void testGetVersion() throws Exception {
        assertTrue("2.3".equals(version2_3.getVersion()));
        assertTrue("2.3.1".equals(version2_3_1.getVersion()));
    }

    @Test
    public void testGreaterThanOrEqualTo() throws Exception {
        assertTrue(version7_0_2.greaterThanOrEqualTo(7, 0));
        assertTrue(version7_0.greaterThanOrEqualTo(7, 0));
    }
}
