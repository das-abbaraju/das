package com.picsauditing.report;

import static org.junit.Assert.assertEquals;

import org.json.simple.JSONObject;
import org.junit.Test;

import com.picsauditing.report.fields.QueryMethod;

@SuppressWarnings("unchecked")
public class ColumnTest {

	private Column column = new Column();
	private JSONObject jsonObj = new JSONObject();

	@Test
	public void testEmpty() {
		column.fromJSON(jsonObj);

		String expected = "{\"name\":null}";
		assertEquals(expected, column.toJSON(true).toJSONString());
	}

	@Test
	public void testNameOnly() {
		jsonObj.put("name", "AccountName");
		column.fromJSON(jsonObj);
		assertEquals("AccountName", column.getFieldName());

		String expected = "{\"name\":\"AccountName\"}";
		assertEquals(expected, column.toJSON(true).toJSONString());
	}

	@Test
	public void testMethod() {
		jsonObj.put("name", "AccountNameUpperCase");
		jsonObj.put("method", "UpperCase");
		column.fromJSON(jsonObj);
		assertEquals(QueryMethod.UpperCase, column.getMethod());

		String expected = "{\"name\":\"AccountNameUpperCase\",\"method\":\"UpperCase\"}";
		assertEquals(expected, column.toJSON(true).toJSONString());
	}

	@Test
	public void testGetFieldName() {
		column.setFieldName("AccountName");
		assertEquals("AccountName", column.getFieldName());
	}
}
