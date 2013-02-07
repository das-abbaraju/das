package com.picsauditing.report.converter;

import static com.picsauditing.util.Assert.*;
import static com.picsauditing.report.ReportJson.*;

import org.json.simple.JSONObject;
import org.junit.Test;

import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.Sort;
import com.picsauditing.report.converter.JsonReportBuilder;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.report.models.ModelType;

public class JsonReportBuilderTest {

	@Test
	public void testBuildReportJson_WhenReportFieldsAreSet_ThenTheyreWrittenToJson() {
		Report report = new Report();
		int reportId = 123;
		String reportName = "Test Report";
		ModelType modelType = ModelType.Contractors;
		String description = "This is a test report";
		String filterExpression = "{1} AND {2}";
		boolean editable = true;
		boolean favorite = true;

		report.setId(reportId);
		report.setName(reportName);
		report.setModelType(modelType);
		report.setDescription(description);
		report.setFilterExpression(filterExpression);
		report.setEditable(editable);
		report.setFavorite(favorite);

		JSONObject json = JsonReportBuilder.buildReportJson(report);
		String jsonString = json.toString();

		assertJsonNoQuotes(REPORT_ID, reportId, jsonString);
		assertJson(REPORT_NAME, reportName, jsonString);
		assertJson(REPORT_MODEL_TYPE, modelType, jsonString);
		assertJson(REPORT_DESCRIPTION, description, jsonString);
		assertJson(REPORT_FILTER_EXPRESSION, filterExpression, jsonString);
		assertJsonNoQuotes(REPORT_EDITABLE, editable, jsonString);
		assertJsonNoQuotes(REPORT_FAVORITE, favorite, jsonString);
	}

	@Test
	public void testBuildReportJson_WhenColumnsAreSet_ThenTheyreWrittenToJson() {
		Report report = buildMinimalReport();

		// ReportElement common properties
		int id = 321;
		String fieldName = "Field Name";
		String fieldText = "Field Text";
		String categoryTranslation = "Account Information";
		String description = "This is a column";

		// TODO why does this require an argument?
		Field field = new Field("");
		field.setName(fieldName);
		field.setText(fieldText);
		field.setCategoryTranslation(categoryTranslation);
		field.setHelp(description);

		// Column specific properties
		String url = "www.picsauditing.com";
		SqlFunction sqlFunction = SqlFunction.Max;
		int width = 123;
		boolean sortable = true;
		String columnType = "String";

		Column column = new Column();
		column.setId(id);
		column.setName(fieldName);
		field.setUrl(url);
		column.setSqlFunction(sqlFunction);
		column.setWidth(width);
		field.setSortable(sortable);

		column.setField(field);
		report.addColumn(column);

		JSONObject json = JsonReportBuilder.buildReportJson(report);
		String jsonString = json.toString();

		assertJsonNoQuotes(REPORT_ID, id, jsonString);
		assertJson(REPORT_ELEMENT_FIELD_ID, fieldName, jsonString);
		assertJson(REPORT_ELEMENT_CATEGORY, categoryTranslation, jsonString);
		assertJson(REPORT_ELEMENT_NAME, fieldText, jsonString);
		assertJson(REPORT_ELEMENT_DESCRIPTION, description, jsonString);

		assertContains(REPORT_COLUMNS, jsonString);
		assertJson(COLUMN_TYPE, columnType, jsonString);
		assertJson(COLUMN_URL, url, jsonString);
		assertJson(COLUMN_SQL_FUNCTION, sqlFunction, jsonString);
		assertJsonNoQuotes(COLUMN_WIDTH, width, jsonString);
		assertJsonNoQuotes(COLUMN_SORTABLE, sortable, jsonString);
	}

	@Test
	public void testBuildReportJson_WhenFiltersAreSet_ThenTheyreWrittenToJson() {
		Report report = buildMinimalReport();

		// ReportElement common properties
		int id = 321;
		String fieldName = "Field Name";
		String fieldText = "Field Text";
		String categoryTranslation = "Account Information";
		String description = "This is a column";

		// TODO why does this require an argument?
		Field field = new Field("");
		field.setName(fieldName);
		field.setText(fieldText);
		field.setCategoryTranslation(categoryTranslation);
		field.setHelp(description);

		// Filter specific properties
		FieldType fieldType = FieldType.String;
		QueryFilterOperator operator = QueryFilterOperator.BeginsWith;
		String value = "value";
		String columnCompareId = "column compare";

		Filter filter = new Filter();
		filter.setId(id);
		filter.setName(fieldName);
		field.setType(fieldType);
		filter.setOperator(operator);
		filter.setValue(value);
		filter.setColumnCompare(columnCompareId);
		filter.setField(field);
		report.addFilter(filter);

		JSONObject json = JsonReportBuilder.buildReportJson(report);
		String jsonString = json.toString();

		assertJsonNoQuotes(REPORT_ID, id, jsonString);
		assertJson(REPORT_ELEMENT_FIELD_ID, fieldName, jsonString);
		assertJson(REPORT_ELEMENT_CATEGORY, categoryTranslation, jsonString);
		assertJson(REPORT_ELEMENT_NAME, fieldText, jsonString);
		assertJson(REPORT_ELEMENT_DESCRIPTION, description, jsonString);

		assertContains(REPORT_FILTERS, jsonString);
		assertJson(FILTER_TYPE, fieldType.getFilterType(), jsonString);
		assertJson(FILTER_OPERATOR, operator, jsonString);
		assertJson(FILTER_VALUE, value, jsonString);
		assertJson(FILTER_COLUMN_COMPARE, columnCompareId, jsonString);
	}

	@Test
	public void testBuildReportJson_WhenSortsAreSet_ThenTheyreWrittenToJson() {
		Report report = buildMinimalReport();

		// ReportElement common properties
		int id = 321;
		String fieldName = "Field Name";

		// Sort specific properties
		boolean ascending = true;

		Sort sort = new Sort();
		sort.setId(id);
		sort.setName(fieldName);
		sort.setAscending(ascending);
		report.addSort(sort);

		JSONObject json = JsonReportBuilder.buildReportJson(report);
		String jsonString = json.toString();

		assertJsonNoQuotes(REPORT_ID, id, jsonString);
		assertContains(REPORT_SORTS, jsonString);
		assertJson(REPORT_ELEMENT_FIELD_ID, fieldName, jsonString);
		assertJson(SORT_DIRECTION, Sort.ASCENDING, jsonString);
	}

	private Report buildMinimalReport() {
		Report report = new Report();
		report.setModelType(ModelType.Accounts);
		return report;
	}

}
