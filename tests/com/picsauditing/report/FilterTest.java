package com.picsauditing.report;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.junit.Test;

import com.picsauditing.report.fields.QueryFilterOperator;

import com.picsauditing.report.Filter;

@SuppressWarnings("unchecked")
public class FilterTest {
	private Filter filter = new Filter();
	private JSONObject jsonObj = new JSONObject();

	@Test
	public void testEmpty() {
		filter.fromJSON(jsonObj);

		String expected = "{\"name\":null}";
		assertEquals(expected, filter.toJSON(true).toJSONString());
	}

	@Test
	public void testBlankOperator() {
		jsonObj.put("name", "accountID");
		jsonObj.put("operator", "");
		
		filter.fromJSON(jsonObj);

		String expected = "{\"name\":\"accountID\",\"operator\":\"Equals\"}";
		assertEquals(expected, filter.toJSON(true).toJSONString());
	}

	@Test
	public void testValue() {
		jsonObj.put("name", "AccountName");
		jsonObj.put("operator", "BeginsWith");
		jsonObj.put("value", "Trevor's");
		filter.fromJSON(jsonObj);
		assertEquals("AccountName", filter.getFieldName());
		assertEquals(QueryFilterOperator.BeginsWith, filter.getOperator());
		assertEquals("Trevor's", filter.getValue());

		String expected = "{\"name\":\"AccountName\",\"value\":\"Trevor's\",\"operator\":\"BeginsWith\"}";
		assertEquals(expected, filter.toJSON(true).toJSONString());
	}
}
