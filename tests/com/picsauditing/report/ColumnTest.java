package com.picsauditing.report;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import com.picsauditing.report.fields.QueryFunction;

import com.picsauditing.report.Column;

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
	public void testFunction() {
		jsonObj.put("name", "AccountNameUpperCase");
		jsonObj.put("function", "UpperCase");
		column.fromJSON(jsonObj);
		assertEquals(QueryFunction.UpperCase, column.getFunction());

		String expected = "{\"name\":\"AccountNameUpperCase\",\"function\":\"UpperCase\"}";
		assertEquals(expected, column.toJSON(true).toJSONString());
	}

	@Test
	public void testGetFieldName() {
		column.setFieldName("AccountName");
		assertEquals("AccountName", column.getFieldName());
	}

	@Test
	public void testGetFieldNameWithoutFunction() {
		column.setFieldName("AccountNameUpperCase");
		column.setFunction(QueryFunction.UpperCase);
		assertEquals("AccountName", column.getFieldNameWithoutFunction());
	}

	@Test
	public void testGetFieldNameWithDuplicate() {
		column.setFieldName("FirstYearDateYear");
		column.setFunction(QueryFunction.Year);
		assertEquals("FirstYearDate", column.getFieldNameWithoutFunction());
	}
}
