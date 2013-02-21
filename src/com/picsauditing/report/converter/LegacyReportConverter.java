package com.picsauditing.report.converter;

import static com.picsauditing.report.ReportJson.LEGACY_METHOD;
import static com.picsauditing.report.ReportJson.LEGACY_MODEL_TYPE;
import static com.picsauditing.report.ReportJson.LEGACY_REPORT_FILTER_EXPRESSION;
import static com.picsauditing.report.ReportJson.REPORT_COLUMNS;
import static com.picsauditing.report.ReportJson.REPORT_DESCRIPTION;
import static com.picsauditing.report.ReportJson.REPORT_FILTERS;
import static com.picsauditing.report.ReportJson.REPORT_NAME;
import static com.picsauditing.report.ReportJson.REPORT_SORTS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportElement;
import com.picsauditing.jpa.entities.Sort;
import com.picsauditing.report.FilterExpression;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.util.Strings;

// TODO Remove this method after the next release
@SuppressWarnings("unchecked")
public class LegacyReportConverter {

	private static final Logger logger = LoggerFactory.getLogger(LegacyReportConverter.class);

	// From Report to JSON
	@Deprecated
	public JSONObject toJSON(Report report) {
		JSONObject json = new JSONObject();

		convertReportLevelData(report, json);
		convertColumnsToJson(report, json);
		convertFiltersToJson(report, json);
		convertSortsToJson(report, json);

		return json;
	}

	private void convertReportLevelData(Report report, JSONObject json) {
		// json.put("id", report.getId());
		json.put("name", report.getName());
		if (report.getModelType() != null) {
			json.put("modelType", report.getModelType().toString());
		}

		json.put("description", report.getDescription());

		if (Strings.isNotEmpty(report.getFilterExpression())) {
			json.put(LEGACY_REPORT_FILTER_EXPRESSION, report.getFilterExpression());
		}
	}

	private void convertColumnsToJson(Report report, JSONObject json) {
		JSONArray jsonArray = new JSONArray();
		json.put(REPORT_COLUMNS, jsonArray);
		for (Column obj : report.getColumns()) {
			jsonArray.add(toJSON(obj));
		}
	}

	private void convertFiltersToJson(Report report, JSONObject json) {
		JSONArray jsonArray = new JSONArray();
		json.put(REPORT_FILTERS, jsonArray);
		for (Filter obj : report.getFilters()) {
			jsonArray.add(toJSON(obj));
		}
	}

	private void convertSortsToJson(Report report, JSONObject json) {
		JSONArray jsonArray = new JSONArray();
		json.put(REPORT_SORTS, jsonArray);
		for (Sort obj : report.getSorts()) {
			jsonArray.add(toJSON(obj));
		}
	}

	private JSONObject toJSONBase(ReportElement element) {
		JSONObject json = new JSONObject();
		json.put("name", element.getName());
		// This is a legacy feature only used on the old ExtJS
		// Removing for now since it makes testing a lot easier
		// json.put("field", toJSON(obj.getField()));
		return json;
	}

	private JSONObject toJSON(Column column) {
		JSONObject json = toJSONBase(column);
		if (column.getSqlFunction() != null) {
			json.put("method", column.getSqlFunction().toString());
		}

		return json;
	}

	public JSONObject toJSON(Field field) {
		if (field == null) {
			field = new Field("Missing", "", FieldType.String);
		}

		// TODO Move this to SimpleColumn.js toGridColumn
		JSONObject json = new JSONObject();
		json.put("name", field.getName());
		json.put("text", field.getText());
		json.put("help", field.getHelp());

		if (field.getWidth() > 0) {
			json.put("width", field.getWidth());
		}

		if (!Strings.isEmpty(field.getUrl())) {
			json.put("url", field.getUrl());
		}

		json.put("fieldType", field.getType().toString());
		json.put("filterType", field.getType().getFilterType().toString());
		json.put("displayType", field.getType().toString().toLowerCase());
		json.put("type", field.getType().toString().toLowerCase());
		return json;
	}

	private JSONObject toJSON(Filter filter) {
		JSONObject json = new JSONObject();
		json.put("name", filter.getName());
		json.put("operator", filter.getOperator().toString());

		if (filter.getOperator().isValueCurrentlySupported()) {
			JSONArray valueArray = new JSONArray();
			valueArray.addAll(filter.getValues());
			// json.put("values", valueArray);

			// Until we phase out the old code, we need this for backwards
			// compatibility
			if (filter.getValues().size() == 1) {
				json.put("value", filter.getValues().get(0));
			} else {
				json.put("value", StringUtils.join(filter.getValues(), ", "));
			}
		}

		if (filter.getFieldForComparison() != null) {
			json.put(Filter.FIELD_COMPARE, filter.getFieldForComparison().getName());
		}

		return json;
	}

	private JSONObject toJSON(Sort sort) {
		JSONObject json = new JSONObject();
		json.put("name", sort.getName());
		if (!sort.isAscending()) {
			json.put("direction", Sort.DESCENDING);
		}

		return json;
	}

	public void setReportPropertiesFromJsonParameters(Report report) {
		if (report.getParameters() == null) {
			return;
		}

		JSONObject json = (JSONObject) JSONValue.parse(report.getParameters());

		report.setFilterExpression(parseFilterExpression(json));
		setReportModelType(report, json);   // todo: call a method and use setter here
		report.setName((String) json.get(REPORT_NAME));
		report.setDescription((String) json.get(REPORT_DESCRIPTION));

		addColumns(json, report);
		addFilters(json, report);
		addSorts(json, report);
	}

	private void setReportModelType(Report report, JSONObject json) {
		String modelTypeValue = (String) json.get(LEGACY_MODEL_TYPE);
		if (Strings.isNotEmpty(modelTypeValue)) {
			report.setModelType(ModelType.valueOf(modelTypeValue));
		}
	}

	private String parseFilterExpression(JSONObject json) {
		String filterExpressionFromJson = (String) json.get(LEGACY_REPORT_FILTER_EXPRESSION);
		if (FilterExpression.isValid(filterExpressionFromJson)) {
			return filterExpressionFromJson;
		}

		return null;
	}

	private void addColumns(JSONObject json, Report report) {
		JSONArray jsonArray = (JSONArray) json.get(REPORT_COLUMNS);
		if (jsonArray == null) {
			return;
		}

		for (Object object : jsonArray) {
			if (object != null) {
				Column column = toColumn((JSONObject) object);
				report.addColumn(column);
			}
		}
	}

	private void addFilters(JSONObject json, Report report) {
		JSONArray jsonArray = (JSONArray) json.get(REPORT_FILTERS);
		if (jsonArray == null) {
			return;
		}

		for (Object object : jsonArray) {
			if (object != null) {
				Filter filter = toFilter((JSONObject) object);
				report.addFilter(filter);
			}
		}
	}

	private void addSorts(JSONObject json, Report report) {
		JSONArray jsonArray = (JSONArray) json.get(REPORT_SORTS);
		if (jsonArray == null) {
			return;
		}

		for (Object object : jsonArray) {
			if (object != null) {
				Sort sort = toSort((JSONObject) object);
				report.addSort(sort);
			}
		}
	}

	public Column toColumn(JSONObject json) {
		Column column = new Column();
		toElementFromJSON(json, column);
		return column;
	}

	public Sort toSort(JSONObject json) {
		if (json == null) {
			return null;
		}

		Sort sort = new Sort();
		toElementFromJSON(json, sort);
		sort.setAscending(isAscending(json));

		return sort;
	}

	private boolean isAscending(JSONObject json) {
		String direction = (String) json.get("direction");
		if (direction != null && direction.equals("DESC")) {
			return false;
		}

		return true;
	}

	public Filter toFilter(JSONObject json) {
		if (json == null) {
			return null;
		}

		Filter filter = new Filter();
		toElementFromJSON(json, filter);
		filter.setOperator(parseOperator(json));
		filter.setValues(parseValues(json));
		filter.setFieldForComparison(parseAdvancedFilter(json));

		return filter;
	}

	private void toElementFromJSON(JSONObject json, ReportElement reportElement) {
		reportElement.setName((String) json.get("name"));

		String methodName = (String) json.get(LEGACY_METHOD);
		if (Strings.isNotEmpty(methodName)) {
			reportElement.setSqlFunction(SqlFunction.valueOf(methodName));
		}
	}

	private QueryFilterOperator parseOperator(JSONObject json) {
		String object = (String) json.get("operator");
		if (Strings.isEmpty(object)) {
			return QueryFilterOperator.Equals;
		}

		return QueryFilterOperator.valueOf(object.toString());
	}

	private List<String> parseValues(JSONObject json) {
		JSONArray valuesJsonArray = null;
		List<String> values = new ArrayList<String>();

		try {
			valuesJsonArray = (JSONArray) json.get("values");
		} catch (ClassCastException cce) {
			logger.warn("A filter's values field is not a JSONArray", cce);
		} catch (Exception e) {
			logger.warn("Old format report that doesn't have 'values' in filter");
		}

		if (valuesJsonArray != null && valuesJsonArray.size() > 0) {
			for (Object value : valuesJsonArray) {
				values.add(value.toString().trim());
			}
		} else {
			String value = (String) json.get("value");

			if (Strings.isEmpty(value)) {
				return values;
			}

			if (value.contains(",")) {
				logger.warn("Old style filter value found with commas separating multiple values. "
						+ "Until we phase out the old code, we need this for backwards compatibility");

				String[] valueSplit = value.split(", ");
				if (valueSplit.length == 1 && value.contains(",")) {
					valueSplit = value.split(",");
				}

				values.addAll(Arrays.asList(valueSplit));
			} else {
				values.add(value);
			}
		}

		return values;
	}

	private Field parseAdvancedFilter(JSONObject json) {
		String advancedFilterOption = (String) json.get(Filter.FIELD_COMPARE);
		if (Strings.isEmpty(advancedFilterOption) || advancedFilterOption.equals("false")) {
			return null;
		}

		return new Field(advancedFilterOption);
	}
}
