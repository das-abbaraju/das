package com.picsauditing.report.converter;

import static com.picsauditing.report.ReportJson.*;
import static org.junit.Assert.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.report.models.ModelType;

public class ReportBuilderTest {

	@Test(expected = IllegalArgumentException.class)
	public void testBadMethodName() throws Exception {
		String BAD_JSON = "{\"type\":\"Accounts\""
				+ "\"columns\":[{\"name\":\"ContractorPayingFacilities__BadSqlFunction\",\"sql_function\":\"BadSqlFunction\"}]}";
		JSONObject jsonReport = (JSONObject) JSONValue.parse(BAD_JSON);
		ReportBuilder.fromJson(jsonReport);
	}

	@Test
	public void testFromJson_WhenJsonReportParametersArePassedIn_ThenMembersAreSetOnReport() throws ReportValidationException {
		String name = "Report Name";
		String description = "This is a report";
		ModelType modelType = ModelType.Accounts;
		String filterExpression = "{1} OR {2}";

		JSONObject reportJson = makeReportJson(name, description, modelType, filterExpression);

		Report report = ReportBuilder.fromJson(reportJson);

		assertEquals(name, report.getName());
		assertEquals(description, report.getDescription());
		assertEquals(modelType.toString(), report.getModelType().toString());
		assertEquals(filterExpression, report.getFilterExpression());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFromJson_WhenJsonColumnParametersArePassedIn_ThenMembersAreSetOnColumn() throws ReportValidationException {
		int id = 123;
		String name = "Column Name";
		SqlFunction sqlFunction = SqlFunction.Max;
		int width = 150;

		JSONObject columnJson = new JSONObject();
		columnJson.put(REPORT_ID, id);
		columnJson.put(REPORT_ELEMENT_FIELD_ID, name);
		columnJson.put(COLUMN_SQL_FUNCTION, sqlFunction.toString());
		columnJson.put(COLUMN_WIDTH, width);

		JSONArray columnsJson = new JSONArray();
		columnsJson.add(columnJson);

		JSONObject reportJson = makeMinimalReportJson();
		reportJson.put(REPORT_COLUMNS, columnsJson);

		Report report = ReportBuilder.fromJson(reportJson);
		assertEquals(1, report.getColumns().size());

		Column column = report.getColumns().get(0);
		assertEquals(id, column.getId());
		assertEquals(name, column.getName());
		assertEquals(sqlFunction, column.getSqlFunction());
		assertEquals(width, column.getWidth());
	}

	private JSONObject makeMinimalReportJson() {
		return makeReportJson("", "", ModelType.Accounts, "");
	}

	@SuppressWarnings("unchecked")
	private JSONObject makeReportJson(String name, String description, ModelType modelType,
			String filterExpression) {
		JSONObject reportJson = new JSONObject();

		reportJson.put(REPORT_NAME, name);
		reportJson.put(REPORT_DESCRIPTION, description);
		reportJson.put(REPORT_MODEL_TYPE, modelType.toString());
		reportJson.put(REPORT_FILTER_EXPRESSION, filterExpression);

		return reportJson;
	}
}
