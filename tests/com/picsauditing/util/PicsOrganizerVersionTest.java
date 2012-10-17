package com.picsauditing.util;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PicsOrganizerVersionTest {

	@Before
	public void setUp() throws Exception {
	}
	@Test
	public void testGreaterThan_majorGreater() throws Exception {
		assertTrue("expected", PicsOrganizerVersion.greaterThan(PicsOrganizerVersion.major-1, PicsOrganizerVersion.minor));
	}
	@Test
	public void testGreaterThan_minorGreater() throws Exception {
		assertTrue("expected", PicsOrganizerVersion.greaterThan(PicsOrganizerVersion.major, PicsOrganizerVersion.minor-1));
	}
	@Test
	public void testGreaterThan_bothEqual() throws Exception {
		assertFalse("expected", PicsOrganizerVersion.greaterThan(PicsOrganizerVersion.major, PicsOrganizerVersion.minor));
	}
}
