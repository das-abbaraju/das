package com.picsauditing.report.converter;

import static com.picsauditing.report.ReportJson.*;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportElement;
import com.picsauditing.jpa.entities.Sort;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class JsonReportBuilder {

	private static final Logger logger = LoggerFactory.getLogger(JsonReportBuilder.class);

	public static JSONObject buildReportJson(Report report, int userId) {
		JSONObject json = new JSONObject();

		addReportLevelData(json, report, userId);

		addColumns(json, report.getColumns());
		addFilters(json, report.getFilters());
		addSorts(json, report.getSorts());

		return json;
	}

	private static void addReportLevelData(JSONObject json, Report report, int userId) {
		json.put(REPORT_ID, report.getId());
		json.put(REPORT_MODEL_TYPE, report.getModelType().toString());
		json.put(REPORT_NAME, report.getName());
		json.put(REPORT_DESCRIPTION, report.getDescription());
		json.put(REPORT_FILTER_EXPRESSION, report.getFilterExpression());
		json.put(REPORT_EDITABLE, report.isEditableBy(userId));
		json.put(REPORT_FAVORITE, report.isFavoritedBy(userId));
	}

	private static void addColumns(JSONObject json, List<Column> columns) {
		JSONArray jsonArray = new JSONArray();

		for (Column column : columns) {
			try {
				jsonArray.add(columnToJson(column));
			} catch (ReportValidationException rve) {
				logger.error(rve.getMessage());
			}
		}

		json.put(REPORT_COLUMNS, jsonArray);
	}

	private static void addFilters(JSONObject json, List<Filter> filters) {
		JSONArray jsonArray = new JSONArray();

		for (Filter filter : filters) {
			try {
				jsonArray.add(filterToJson(filter));
			} catch (ReportValidationException rve) {
				logger.error(rve.getMessage());
			}
		}

		json.put(REPORT_FILTERS, jsonArray);
	}

	private static void addSorts(JSONObject json, List<Sort> sorts) {
		JSONArray jsonArray = new JSONArray();

		for (Sort sort: sorts) {
			jsonArray.add(sortToJson(sort));
		}

		json.put(REPORT_SORTS, jsonArray);
	}

	private static JSONObject columnToJson(Column column) throws ReportValidationException {
		JSONObject json = elementToCommonJson(column);

		json.put(COLUMN_TYPE, column.getField().getDisplayType().name());
		json.put(COLUMN_URL, column.getField().getUrl());
		json.put(COLUMN_SQL_FUNCTION, Strings.toStringPreserveNull(column.getSqlFunction()));
		json.put(COLUMN_WIDTH, column.getWidth());
		json.put(COLUMN_SORTABLE, column.getField().isSortable());
		json.put(COLUMN_SORT, column.getSortIndex());

		return json;
	}

	private static JSONObject filterToJson(Filter filter) throws ReportValidationException {
		JSONObject json = elementToCommonJson(filter);

		json.put(FILTER_TYPE, filter.getField().getFilterType().name());
		json.put(FILTER_OPERATOR, filter.getOperator().toString());
		String filterValue = makeFilterValue(filter);
		json.put(FILTER_VALUE, filterValue);
		json.put(FILTER_COLUMN_COMPARE, filter.getColumnCompare());

		return json;
	}

	private static String makeFilterValue(Filter filter) {
		String filterValue = "";

		if (filter.getOperator().isValueCurrentlySupported()) {
			if (filter.getValues().size() == 1) {
				filterValue = filter.getValues().get(0);
			} else {
				filterValue = StringUtils.join(filter.getValues(), ", ");
			}
		}

		return filterValue;
	}

	private static JSONObject sortToJson(Sort sort) {
		JSONObject json = new JSONObject();

		json.put(REPORT_ID, sort.getId());
		json.put(REPORT_ELEMENT_FIELD_ID, sort.getName());
		json.put(SORT_DIRECTION, sort.isAscending() ? Sort.ASCENDING : Sort.DESCENDING);

		return json;
	}

	private static JSONObject elementToCommonJson(ReportElement element) throws ReportValidationException {
		JSONObject json = new JSONObject();

		if (element.getField() == null) {
			throw new ReportValidationException("Field with name '" + element.getName() + "' was not loaded correctly. The field name in the database (report_column, etc.) probably hasn't been updated to match.");
		}

		// TODO sort out these member variable names
		json.put(REPORT_ID, element.getId());
		json.put(REPORT_ELEMENT_FIELD_ID, element.getName());
		json.put(REPORT_ELEMENT_CATEGORY, element.getField().getCategoryTranslation());
		json.put(REPORT_ELEMENT_NAME, element.getField().getText());
		json.put(REPORT_ELEMENT_DESCRIPTION, element.getField().getHelp());

		return json;
	}

}