package com.picsauditing.report.converter;

import static com.picsauditing.util.Assert.*;
import static com.picsauditing.report.ReportJson.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

	@Mock
	private Report report;

	private static final int USER_ID = 123;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testBuildReportJson_WhenReportFieldsAreSet_ThenTheyreWrittenToJson() {
		int reportId = 123;
		String reportName = "Test Report";
		ModelType modelType = ModelType.Contractors;
		String description = "This is a test report";
		String filterExpression = "{1} AND {2}";
		boolean editable = true;
		boolean favorite = true;

		when(report.getId()).thenReturn(reportId);
		when(report.getName()).thenReturn(reportName);
		when(report.getModelType()).thenReturn(modelType);
		when(report.getDescription()).thenReturn(description);
		when(report.getFilterExpression()).thenReturn(filterExpression);
		when(report.isEditableBy(USER_ID)).thenReturn(editable);
		when(report.isFavoritedBy(USER_ID)).thenReturn(favorite);

		JSONObject json = JsonReportBuilder.buildReportJson(report, USER_ID);
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
		mockMinimalReport();

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
		List<Column> columns = new ArrayList<Column>();
		columns.add(column);
		when(report.getColumns()).thenReturn(columns);

		JSONObject json = JsonReportBuilder.buildReportJson(report, USER_ID);
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
		mockMinimalReport();

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

		List<Filter> filters = new ArrayList<Filter>();
		filters.add(filter);
		when(report.getFilters()).thenReturn(filters);

		JSONObject json = JsonReportBuilder.buildReportJson(report, USER_ID);
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
		mockMinimalReport();

		// ReportElement common properties
		int id = 321;
		String fieldName = "Field Name";

		// Sort specific properties
		boolean ascending = true;

		Sort sort = new Sort();
		sort.setId(id);
		sort.setName(fieldName);
		sort.setAscending(ascending);

		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(sort);
		when(report.getSorts()).thenReturn(sorts);

		JSONObject json = JsonReportBuilder.buildReportJson(report, USER_ID);
		String jsonString = json.toString();

		assertJsonNoQuotes(REPORT_ID, id, jsonString);
		assertContains(REPORT_SORTS, jsonString);
		assertJson(REPORT_ELEMENT_FIELD_ID, fieldName, jsonString);
		assertJson(SORT_DIRECTION, Sort.ASCENDING, jsonString);
	}

	@Test
	public void testBuildReportJson_WhenColumnsFieldIsNull_ThenNoExceptionIsThrown() {
		mockMinimalReport();

		Column column = new Column();
		column.setField(null);

		List<Column> columns = new ArrayList<Column>();
		columns.add(column);
		when(report.getColumns()).thenReturn(columns);

		JsonReportBuilder.buildReportJson(report, USER_ID);
	}

	@Test
	public void testBuildReportJson_WhenFiltersFieldIsNull_ThenNoExceptionIsThrown() {
		mockMinimalReport();

		Filter filter = new Filter();
		filter.setField(null);

		List<Filter> filters = new ArrayList<Filter>();
		filters.add(filter);
		when(report.getFilters()).thenReturn(filters);

		JsonReportBuilder.buildReportJson(report, USER_ID);
	}

	public void mockMinimalReport() {
		when(report.getModelType()).thenReturn(ModelType.Accounts);
	}

}