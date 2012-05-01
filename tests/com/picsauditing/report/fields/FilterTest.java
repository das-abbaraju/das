package com.picsauditing.report.fields;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class FilterTest {
	private Filter filter = new Filter();
	private JSONObject jsonObj = new JSONObject();

	@Test
	public void testEmpty() {
		filter.fromJSON(jsonObj);

		String expected = "{\"column\":null}";
		assertEquals(expected, filter.toJSON(true).toJSONString());
	}

	@Test
	public void testBlankOperator() {
		jsonObj.put("column", "accountID");
		jsonObj.put("operator", "");
		
		filter.fromJSON(jsonObj);

		String expected = "{\"column\":\"accountID\",\"operator\":\"Equals\"}";
		assertEquals(expected, filter.toJSON(true).toJSONString());
	}

	@Test
	public void testValue() {
		jsonObj.put("column", "AccountName");
		jsonObj.put("operator", "BeginsWith");
		jsonObj.put("value", "Trevor's");
		filter.fromJSON(jsonObj);
		assertEquals("AccountName", filter.getName());
		assertEquals(QueryFilterOperator.BeginsWith, filter.getOperator());
		assertEquals("Trevor's", filter.getValue());

		String expected = "{\"value\":\"Trevor's\",\"column\":\"AccountName\",\"operator\":\"BeginsWith\"}";
		assertEquals(expected, filter.toJSON(true).toJSONString());
	}
}
