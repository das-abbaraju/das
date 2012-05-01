package com.picsauditing.report.fields;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class SortTest {
	private Sort sort = new Sort();
	private JSONObject jsonObj = new JSONObject();

	@Test
	public void testEmpty() {
		sort.fromJSON(jsonObj);

		String expected = "{\"column\":null}";
		assertEquals(expected, sort.toJSON(true).toJSONString());
	}

	@Test
	public void testNameOnly() {
		jsonObj.put("column", "AccountName");
		sort.fromJSON(jsonObj);
		assertEquals("AccountName", sort.getName());

		String expected = "{\"column\":\"AccountName\"}";
		assertEquals(expected, sort.toJSON(true).toJSONString());
	}
}
