package com.picsauditing.report;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.junit.Test;

import com.picsauditing.report.Sort;

@SuppressWarnings("unchecked")
public class SortTest {
	private Sort sort = new Sort();
	private JSONObject jsonObj = new JSONObject();

	@Test
	public void testEmpty() {
		sort.fromJSON(jsonObj);

		String expected = "{\"name\":null}";
		assertEquals(expected, sort.toJSON(true).toJSONString());
	}

	@Test
	public void testNameOnly() {
		jsonObj.put("name", "AccountName");
		sort.fromJSON(jsonObj);
		assertEquals("AccountName", sort.getFieldName());

		String expected = "{\"name\":\"AccountName\"}";
		assertEquals(expected, sort.toJSON(true).toJSONString());
	}
}
