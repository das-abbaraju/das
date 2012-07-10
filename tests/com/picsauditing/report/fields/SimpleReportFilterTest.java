package com.picsauditing.report.fields;

import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class SimpleReportFilterTest {
	private SimpleReportFilter filter = new SimpleReportFilter();
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
	public void testNot() {
		jsonObj.put("column", "accountID");
		jsonObj.put("not", true);
		
		filter.fromJSON(jsonObj);

		String expected = "{\"not\":true,\"column\":\"accountID\",\"operator\":\"Equals\"}";
		assertEquals(expected, filter.toJSON(true).toJSONString());
		assertTrue(filter.isNot());
	}

	@Test
	public void testValue() {
		jsonObj.put("column", "AccountName");
		jsonObj.put("operator", "BeginsWith");
		jsonObj.put("value", "Trevor's");
		filter.fromJSON(jsonObj);
		assertEquals("AccountName", filter.getColumn());
		assertEquals(QueryFilterOperator.BeginsWith, filter.getOperator());
		assertEquals("Trevor's", filter.getValue());

		String expected = "{\"value\":\"Trevor's\",\"column\":\"AccountName\",\"operator\":\"BeginsWith\"}";
		assertEquals(expected, filter.toJSON(true).toJSONString());
	}

	@Test
	public void testField2() {
		jsonObj.put("column", "ContractorRegistrationDate");
		jsonObj.put("operator", "GreaterThan");
		jsonObj.put("column2", "ContractorCCExpirationDate");
		filter.fromJSON(jsonObj);
		assertEquals("ContractorRegistrationDate", filter.getColumn());
		assertEquals(QueryFilterOperator.GreaterThan, filter.getOperator());
		assertEquals("ContractorCCExpirationDate", filter.getColumn2());

		String expected = "{\"column\":\"ContractorRegistrationDate\",\"column2\":\"ContractorCCExpirationDate\",\"operator\":\"GreaterThan\"}";
		assertEquals(expected, filter.toJSON(true).toJSONString());
	}
}
