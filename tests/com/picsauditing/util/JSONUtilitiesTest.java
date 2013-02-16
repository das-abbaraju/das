package com.picsauditing.util;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
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

		assertFalse(JSONUtilities.mayBeJSON(null));
		assertFalse(JSONUtilities.mayBeJSON(""));
		assertFalse(JSONUtilities.mayBeJSON("  "));
	}

	@Test
	public void testIsEmpty_NullReturnsTrue() {
		boolean result = JSONUtilities.isEmpty(null);

		assertTrue(result);
	}

	@Test
	public void testIsEmpty_NewObjectReturnsTrue() {
		JSONObject json = new JSONObject();

		boolean result = JSONUtilities.isEmpty(json);

		assertTrue(result);
	}

	@Test
	public void testIsEmpty_AnythingInObjectReturnsFalse() {
		JSONObject json = new JSONObject();
		json.put("foo", "bar");

		boolean result = JSONUtilities.isEmpty(json);

		assertFalse(result);
	}

}
