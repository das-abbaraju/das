package com.picsauditing.report.converter;

import static com.picsauditing.report.ReportJson.*;

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

public class ReportBuilder {

	private static final Logger logger = LoggerFactory.getLogger(ReportBuilder.class);

	public static Report fromJson(JSONObject reportJson) throws ReportValidationException {
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
			if (object != null) {
				Column column = toColumn((JSONObject) object);
				report.addColumn(column);
			}
		}
	}

	private static void addFilters(JSONObject json, Report report) {
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

	private static void addSorts(JSONObject json, Report report) {
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

	private static Column toColumn(JSONObject json) {
		Column column = new Column();
		toElementFromJSON(json, column);

		try {
			String widthString = json.get(COLUMN_WIDTH).toString();
			int width = Integer.parseInt(widthString);

			if (width < Column.MIN_WIDTH) {
				width = Column.MIN_WIDTH;
			}

			column.setWidth(width);
		} catch (Exception e) {
			logger.warn("Couldn't parse width from Column '" + json.get(REPORT_ID));
		}

		try {
			String sortIndexString = json.get(COLUMN_SORT).toString();
			int sortIndex = Integer.parseInt(sortIndexString);

			if (sortIndex < 1) {
				sortIndex = Column.DEFAULT_SORT_INDEX;
			}

			column.setSortIndex(sortIndex);
		} catch (Exception e) {
			logger.warn("Couldn't parse sort from Column '" + json.get(REPORT_ID));
			column.setSortIndex(Column.DEFAULT_SORT_INDEX);
		}

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
		filter.setValue(Strings.toString(json.get(FILTER_VALUE)));
		filter.setFieldForComparison(parseAdvancedFilter(json));

		return filter;
	}

	private static void toElementFromJSON(JSONObject json, ReportElement element) {
		try {
			String idString = json.get(REPORT_ID).toString();
			int id = Integer.parseInt(idString);
			element.setId(id);
		} catch (Exception e) {
			logger.warn("Couldn't parse id from ReportElement '" + json.get(REPORT_ID) + "' ReportElement's id will default to 0.");
		}

		element.setName((String) json.get(REPORT_ELEMENT_FIELD_ID));

		String sqlFunction = (String) json.get(COLUMN_SQL_FUNCTION);
		if (sqlFunction != null) {
			element.setSqlFunction(SqlFunction.valueOf(sqlFunction));
		}
	}

	private static QueryFilterOperator parseOperator(JSONObject json) {
		String operatorString = (String) json.get(FILTER_OPERATOR);
		if (Strings.isEmpty(operatorString)) {
			return QueryFilterOperator.Equals;
		}

		return QueryFilterOperator.valueOf(operatorString);
	}

	private static Field parseAdvancedFilter(JSONObject json) {
		String columnCompare = (String) json.get(FILTER_COLUMN_COMPARE);
		if (Strings.isEmpty(columnCompare) || columnCompare.equals("false")) {
			return null;
		}

		return new Field(columnCompare);
	}
}
