package com.picsauditing.model.report;

import static com.picsauditing.report.access.ReportUtil.COLUMNS;
import static com.picsauditing.report.access.ReportUtil.FILTERS;
import static com.picsauditing.report.access.ReportUtil.FILTER_EXPRESSION;
import static com.picsauditing.report.access.ReportUtil.SORTS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.Column;
import com.picsauditing.report.Filter;
import com.picsauditing.report.FilterExpression;
import com.picsauditing.report.ReportElement;
import com.picsauditing.report.Sort;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.util.Strings;

// TODO Remove this method after the next release
@SuppressWarnings("unchecked")
public class ReportParameterConverter {

	private static final Logger logger = LoggerFactory.getLogger(ReportParameterConverter.class);

	// From Report to JSON
	public static JSONObject toJSON(Report report) {
		JSONObject json = new JSONObject();
		// String VERSION = "6.29";
		// json.put("version", VERSION);
		
		convertReportLevelData(report, json);
		convertColumnsToJson(report, json);
		convertFiltersToJson(report, json);
		convertSortsToJson(report, json);

		return json;
	}

	private static void convertReportLevelData(Report report, JSONObject json) {
		// json.put("id", report.getId());
		json.put("name", report.getName());
		if (report.getModelType() != null)
			json.put("modelType", report.getModelType().toString());
		json.put("description", report.getDescription());

		if (!Strings.isEmpty(report.getFilterExpression()))
			json.put(FILTER_EXPRESSION, report.getFilterExpression());
	}

	private static void convertColumnsToJson(Report report, JSONObject json) {
		JSONArray jsonArray = new JSONArray();
		json.put(COLUMNS, jsonArray);
		for (Column obj : report.getColumns()) {
			jsonArray.add(toJSON(obj));
		}
	}

	private static void convertFiltersToJson(Report report, JSONObject json) {
		JSONArray jsonArray = new JSONArray();
		json.put(FILTERS, jsonArray);
		for (Filter obj : report.getFilters()) {
			jsonArray.add(toJSON(obj));
		}
	}

	private static void convertSortsToJson(Report report, JSONObject json) {
		JSONArray jsonArray = new JSONArray();
		json.put(SORTS, jsonArray);
		for (Sort obj : report.getSorts()) {
			jsonArray.add(toJSON(obj));
		}
	}

	private static JSONObject toJSONBase(ReportElement obj) {
		JSONObject json = new JSONObject();
		json.put("name", obj.getName());
		// This is a legacy feature only used on the old ExtJS
		// Removing for now since it makes testing a lot easier
		// json.put("field", toJSON(obj.getField()));
		return json;
	}

	public static JSONObject toJSON(Column obj) {
		JSONObject json = toJSONBase(obj);
		if (obj.getSqlFunction() != null)
			json.put("method", obj.getSqlFunction().toString());
		return json;
	}

	public static JSONObject toJSON(Field obj) {
		if (obj == null) {
			obj = new Field("Missing", "", FieldType.String);
		}

		// TODO Move this to SimpleColumn.js toGridColumn
		JSONObject json = new JSONObject();
		json.put("name", obj.getName());
		json.put("text", obj.getText());
		json.put("help", obj.getHelp());

		if (obj.getWidth() > 0)
			json.put("width", obj.getWidth());

		if (!Strings.isEmpty(obj.getUrl()))
			json.put("url", obj.getUrl());

		json.put("fieldType", obj.getType().toString());
		json.put("filterType", obj.getType().getFilterType().toString());
		json.put("displayType", obj.getType().toString().toLowerCase());
		json.put("type", obj.getType().toString().toLowerCase());

		JSONArray functionsArray = new JSONArray();
		for (String key : obj.getFunctions().keySet()) {
			JSONObject translatedFunction = new JSONObject();
			translatedFunction.put("key", key);
			translatedFunction.put("value", obj.getFunctions().get(key));
			functionsArray.add(translatedFunction);
		}

		json.put("functions", functionsArray);

		return json;
	}

	public static JSONObject toJSON(Filter obj) {
		JSONObject json = new JSONObject();
		json.put("name", obj.getName());
		json.put("operator", obj.getOperator().toString());

		if (obj.getOperator().isValueUsed()) {
			JSONArray valueArray = new JSONArray();
			valueArray.addAll(obj.getValues());
			// json.put("values", valueArray);

			// Until we phase out the old code, we need this for backwards
			// compatibility
			if (obj.getValues().size() == 1) {
				json.put("value", obj.getValues().get(0));
			} else {
				json.put("value", StringUtils.join(obj.getValues(), ", "));
			}
		}

		if (obj.getFieldForComparison() != null)
			json.put(Filter.FIELD_COMPARE, obj.getFieldForComparison().getName());

		return json;
	}

	public static JSONObject toJSON(Sort obj) {
		JSONObject json = new JSONObject();
		json.put("name", obj.getName());
		if (!obj.isAscending())
			json.put("direction", "DESC");

		return json;
	}

	// From JSON to Report

	public static void fillParameters(Report dto) {
		JSONObject json = (JSONObject) JSONValue.parse(dto.getParameters());
		
		dto.setFilterExpression(parseFilterExpression(json));

		addColumns(json, dto);
		addFilters(json, dto);
		addSorts(json, dto);
	}

	private static String parseFilterExpression(JSONObject json) {
		String filterExpressionFromJson = (String) json.get(FILTER_EXPRESSION);
		if (FilterExpression.isValid(filterExpressionFromJson))
			return filterExpressionFromJson;
		return null;
	}

	private static void addColumns(JSONObject json, Report dto) {
		JSONArray jsonArray = (JSONArray) json.get(COLUMNS);
		if (jsonArray == null)
			return;

		for (Object object : jsonArray) {
			if (object != null) {
				dto.getColumns().add(toColumn((JSONObject) object));
			}
		}
	}

	private static void addFilters(JSONObject json, Report dto) {
		JSONArray jsonArray = (JSONArray) json.get(FILTERS);
		if (jsonArray == null)
			return;

		for (Object object : jsonArray) {
			if (object != null) {
				dto.getFilters().add(toFilter((JSONObject) object));
			}
		}
	}

	private static void addSorts(JSONObject json, Report dto) {
		JSONArray jsonArray = (JSONArray) json.get(SORTS);
		if (jsonArray == null)
			return;

		for (Object object : jsonArray) {
			if (object != null) {
				dto.getSorts().add(toSort((JSONObject) object));
			}
		}
	}

	public static Column toColumn(JSONObject json) {

		Column column = new Column();
		toElementFromJSON(json, column);

		return column;
	}

	public static Sort toSort(JSONObject json) {
		if (json == null)
			return null;

		Sort sort = new Sort();
		toElementFromJSON(json, sort);
		sort.setAscending(isAscending(json));

		return sort;
	}

	private static boolean isAscending(JSONObject json) {
		String direction = (String) json.get("direction");
		if (direction != null && direction.equals("DESC"))
			return false;
		return true;
	}

	public static Filter toFilter(JSONObject json) {
		if (json == null)
			return null;

		Filter filter = new Filter();
		toElementFromJSON(json, filter);
		filter.setOperator(parseOperator(json));
		filter.getValues().addAll(parseValues(json));
		parseAdvancedFilter(json);
		return filter;
	}

	private static void toElementFromJSON(JSONObject json, ReportElement obj) {
		obj.setName((String) json.get("name"));
	}

	private static QueryFilterOperator parseOperator(JSONObject json) {
		String object = (String) json.get("operator");
		if (Strings.isEmpty(object)) {
			return QueryFilterOperator.Equals;
		}

		return QueryFilterOperator.valueOf(object.toString());
	}

	private static List<String> parseValues(JSONObject json) {
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

			if (Strings.isEmpty(value))
				return values;

			if (value.contains(",")) {
				logger.warn("Old style filter value found with commas separating multiple values. "
						+ "Until we phase out the old code, we need this for backwards compatibility");

				String[] valueSplit = value.split(", ");
				if (valueSplit.length == 1 && value.contains(","))
					valueSplit = value.split(",");
				values.addAll(Arrays.asList(valueSplit));
			} else {
				values.add(value);
			}
		}

		return values;
	}

	private static Field parseAdvancedFilter(JSONObject json) {
		String advancedFilterOption = (String) json.get(Filter.FIELD_COMPARE);
		if (Strings.isEmpty(advancedFilterOption) || advancedFilterOption.equals("false"))
			return null;

		return new Field(advancedFilterOption.toString());
	}

	// END FROM JSON to Filters ///
}
