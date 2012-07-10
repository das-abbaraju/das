package com.picsauditing.report.fields;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class SimpleReportColumnTest {
	private SimpleReportColumn column = new SimpleReportColumn();
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
		assertEquals("AccountName", column.getName());

		String expected = "{\"name\":\"AccountName\"}";
		assertEquals(expected, column.toJSON(true).toJSONString());
	}

	@Ignore("Not ready to run yet.")
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
	public void testGetAvailableFieldNameSimple() {
		column.setName("AccountName");
		assertEquals("AccountName", column.getAvailableFieldName());
	}
	
	@Test
	public void testGetAvailableFieldNameWithFunction() {
		column.setName("AccountNameUpperCase");
		column.setFunction(QueryFunction.UpperCase);
		assertEquals("AccountName", column.getAvailableFieldName());
	}
	
	@Test
	public void testGetAvailableFieldNameWithDuplicate() {
		column.setName("FirstYearDateYear");
		column.setFunction(QueryFunction.Year);
		assertEquals("FirstYearDate", column.getAvailableFieldName());
	}
}
