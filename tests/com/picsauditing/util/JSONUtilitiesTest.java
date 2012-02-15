package com.picsauditing.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class JSONUtilitiesTest {

	@Test
	public void testMayBeJSON() {
		String s = "[\"something\"]";
		assertTrue(JSONUtilities.mayBeJSON(s));
		
		s = "{\"id\":\"1\", \"value\":\"something\"}";
		assertTrue(JSONUtilities.mayBeJSON(s));
		
		s = "This { is a ] test }";
		assertFalse(JSONUtilities.mayBeJSON(s));
	}

}
