package com.picsauditing.report;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.assertEquals;

import org.json.simple.JSONObject;
import org.junit.Test;

import com.picsauditing.report.fields.QueryMethod;

@SuppressWarnings("unchecked")
public class ColumnTest {

	private Column column = new Column();
	private JSONObject jsonObj = new JSONObject();

	@Test
	public void testNameOnly() {
		jsonObj.put("name", "AccountName");
		column = new Column(jsonObj);
		assertEquals("AccountName", column.getFieldName());

		String expected = "\"name\":\"AccountName\"";
		assertContains(expected, column.toJSON(true).toJSONString());
	}

	@Test
	public void testMethod() {
		jsonObj.put("name", "AccountName__UpperCase");
		column = new Column(jsonObj);
		assertEquals(QueryMethod.UpperCase, column.getMethod());

		String expected = "\"method\":\"UpperCase\"";
		assertContains(expected, column.toJSON(true).toJSONString());
	}

	@Test
	public void testBadMethodName() throws Exception {
		try {
			jsonObj.put("name", "AccountName__BadMethodThatDoesNotExist");
			column = new Column(jsonObj);
		} catch (Exception weWantToThrowException) {
			return;
		}
		throw new Exception("There is no QueryMethod called BadMethodThatDoesNotExist");
	}

	@Test
	public void testGetFieldName() {
		column.setFieldName("AccountName");
		assertEquals("AccountName", column.getFieldName());
	}

	@Test
	public void testGetFieldNameWithoutMethod() {
		column.setFieldName("FacilityCount__Count");
		assertEquals("FacilityCount__Count", column.getFieldName());
		assertEquals("FacilityCount", column.getFieldNameWithoutMethod());
		assertEquals(QueryMethod.Count, column.getMethod());
	}

	@Test
	public void testToJsonWithNonAutoMethod() {
		column.setFieldName("FacilityCount__Count");
		JSONObject json = column.toJSON(true);
		Column column2 = new Column(json);
		assertEquals(column.getFieldName(), column2.getFieldName());
	}

}
