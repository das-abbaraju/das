package com.picsauditing.report.converter;

import static com.picsauditing.report.ReportJson.*;
import static org.junit.Assert.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.models.ModelType;

public class ReportBuilderTest {

	@Test(expected = IllegalArgumentException.class)
	public void testBadMethodName() throws Exception {
		String BAD_JSON = "{\"type\":\"Accounts\""
				+ "\"columns\":[{\"name\":\"ContractorPayingFacilities__BadSqlFunction\",\"sql_function\":\"BadSqlFunction\"}]}";
		JSONObject jsonReport = (JSONObject) JSONValue.parse(BAD_JSON);
		ReportBuilder.fromJson(jsonReport);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFromJson_WhenJsonReportParametersArePassedIn_ThenParametersAreSetOnReport() throws ReportValidationException {
		String name = "Report Name";
		String description = "This is a report";
		ModelType modelType = ModelType.Accounts;
		String filterExpression = "{1} OR {2}";

		JSONObject reportJson = new JSONObject();
		reportJson.put(REPORT_NAME, name);
		reportJson.put(REPORT_DESCRIPTION, description);
		reportJson.put(REPORT_MODEL_TYPE, modelType.toString());
		reportJson.put(REPORT_FILTER_EXPRESSION, filterExpression);

		Report report = ReportBuilder.fromJson(reportJson);

		assertEquals(name, report.getName());
		assertEquals(description, report.getDescription());
		assertEquals(modelType.toString(), report.getModelType().toString());
		assertEquals(filterExpression, report.getFilterExpression());
	}
}
