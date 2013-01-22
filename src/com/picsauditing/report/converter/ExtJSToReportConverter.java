package com.picsauditing.report.converter;

import static com.picsauditing.report.ReportJson.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportElement;
import com.picsauditing.jpa.entities.Sort;
import com.picsauditing.report.FilterExpression;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.util.Strings;

public class ExtJSToReportConverter {

	private static final Logger logger = LoggerFactory.getLogger(ExtJSToReportConverter.class);

	public static Report convertToReport(JSONObject reportJson) throws ReportValidationException {
		Report report = new Report();

		report.setName((String) reportJson.get(REPORT_NAME));
		report.setDescription((String) reportJson.get(REPORT_DESCRIPTION));
		report.setModelType(parseModelType(reportJson));

		report.setFilterExpression(parseFilterExpression(reportJson));

		addColumns(reportJson, report);
		addFilters(reportJson, report);
		addSorts(reportJson, report);

		return report;
	}

	private static ModelType parseModelType(JSONObject json) throws ReportValidationException {
		String modelTypeString = (String) json.get(REPORT_MODEL_TYPE);

		if (modelTypeString == null) {
			throw new ReportValidationException("Report does not have a valid model type defined");
		}

		return ModelType.valueOf(modelTypeString);
	}

	private static String parseFilterExpression(JSONObject json) {
		String filterExpressionFromJson = (String) json.get(REPORT_FILTER_EXPRESSION);
		if (FilterExpression.isValid(filterExpressionFromJson)) {
			return filterExpressionFromJson;
		}
		return null;
	}

	private static void addColumns(JSONObject json, Report report) {
		JSONArray jsonArray = (JSONArray) json.get(REPORT_COLUMNS);
		if (jsonArray == null) {
			return;
		}

		for (Object object : jsonArray) {
			if (object == null) {
				continue;
			}

			JSONObject jsonObject = (JSONObject)object;
			Column column = toColumn(jsonObject);
			report.addColumn(column);
		}
	}

	private static void addFilters(JSONObject json, Report report) {
		JSONArray jsonArray = (JSONArray) json.get(REPORT_FILTERS);
		if (jsonArray == null) {
			return;
		}

		for (Object object : jsonArray) {
			if (object != null) {
				report.getFilters().add(toFilter((JSONObject) object));
			}
		}
	}

	private static void addSorts(JSONObject json, Report report) {
		JSONArray jsonArray = (JSONArray) json.get(REPORT_SORTS);
		if (jsonArray == null) {
			return;
		}

		for (Object object : jsonArray) {
			if (object != null) {
				report.getSorts().add(toSort((JSONObject) object));
			}
		}
	}

	private static Column toColumn(JSONObject json) {

		Column column = new Column();
		toElementFromJSON(json, column);

		return column;
	}

	private static Sort toSort(JSONObject json) {
		if (json == null) {
			return null;
		}

		Sort sort = new Sort();
		toElementFromJSON(json, sort);
		sort.setAscending(isAscending(json));

		return sort;
	}


	private static boolean isAscending(JSONObject json) {
		String direction = (String) json.get(SORT_DIRECTION);
		if (direction != null && direction.equals(Sort.DESCENDING)) {
			return false;
		}

		return true;
	}

	private static Filter toFilter(JSONObject json) {
		if (json == null) {
			return null;
		}

		Filter filter = new Filter();
		toElementFromJSON(json, filter);
		filter.setOperator(parseOperator(json));
		filter.getValues().addAll(parseValues(json));
		parseAdvancedFilter(json);
		return filter;
	}


	private static void toElementFromJSON(JSONObject json, ReportElement element) {
		// TODO make sure this didn't break everything
		element.setName((String) json.get(REPORT_ELEMENT_FIELD_ID));

		String sqlFunction = (String) json.get(COLUMN_SQL_FUNCTION);
		if (sqlFunction != null) {
			element.setSqlFunction(SqlFunction.valueOf(sqlFunction));
		}
	}

	private static QueryFilterOperator parseOperator(JSONObject json) {
		String object = (String) json.get(FILTER_OPERATOR);
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

	private static Field parseAdvancedFilter(JSONObject json) {
		String advancedFilterOption = (String) json.get(Filter.FIELD_COMPARE);
		if (Strings.isEmpty(advancedFilterOption) || advancedFilterOption.equals("false")) {
			return null;
		}

		return new Field(advancedFilterOption.toString());
	}
}
