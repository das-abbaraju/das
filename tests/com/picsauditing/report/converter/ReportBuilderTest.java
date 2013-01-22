package com.picsauditing.report.converter;


import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

public class ReportBuilderTest {

	@Test(expected = IllegalArgumentException.class)
	public void testBadMethodName() throws Exception {
		String BAD_JSON = "{\"type\":\"Accounts\""
				+ "\"columns\":[{\"name\":\"ContractorPayingFacilities__BadSqlFunction\",\"sql_function\":\"BadSqlFunction\"}]}";
		JSONObject jsonReport = (JSONObject) JSONValue.parse(BAD_JSON);
		ReportBuilder.fromJson(jsonReport);
	}
}
