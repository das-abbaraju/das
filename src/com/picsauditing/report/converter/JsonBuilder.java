package com.picsauditing.report.converter;

import static com.picsauditing.report.ReportJson.*;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.jpa.entities.ReportElement;
import com.picsauditing.jpa.entities.Sort;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class JsonBuilder {

	public static JSONObject fromReport(Report report) {
		JSONObject json = new JSONObject();

		addReportLevelData(json, report);

		addColumns(json, report.getColumns());
		addFilters(json, report.getFilters());
		addSorts(json, report.getSorts());

		return json;
	}

	private static void addReportLevelData(JSONObject json, Report report) {
		json.put(REPORT_ID, report.getId());
		json.put(REPORT_MODEL_TYPE, report.getModelType().toString());
		json.put(REPORT_NAME, report.getName());
		json.put(REPORT_DESCRIPTION, report.getDescription());
		json.put(REPORT_FILTER_EXPRESSION, report.getFilterExpression());
		json.put(REPORT_EDITABLE, report.isEditable());
		json.put(REPORT_FAVORITE, report.isFavorite());
	}

	private static void addColumns(JSONObject json, List<Column> columns) {
		JSONArray jsonArray = new JSONArray();

		for (Column column : columns) {
			jsonArray.add(columnToJson(column));
		}

		json.put(REPORT_COLUMNS, jsonArray);
	}

	private static void addFilters(JSONObject json, List<Filter> filters) {
		JSONArray jsonArray = new JSONArray();

		for (Filter filter : filters) {
			jsonArray.add(filterToJson(filter));
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

	private static JSONObject columnToJson(Column column) {
		JSONObject json = elementToCommonJson(column);

		json.put(COLUMN_TYPE, column.getField().getColumnType());
		json.put(COLUMN_URL, column.getField().getUrl());
		json.put(COLUMN_SQL_FUNCTION, Strings.toStringPreserveNull(column.getSqlFunction()));
		json.put(COLUMN_WIDTH, column.getWidth());
		json.put(COLUMN_SORTABLE, column.getField().isSortable());

		return json;
	}

	private static JSONObject filterToJson(Filter filter) {
		JSONObject json = elementToCommonJson(filter);

		json.put(FILTER_TYPE, filter.getField().getFilterType());
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

		json.put(REPORT_ELEMENT_FIELD_ID, sort.getName());
		json.put(SORT_DIRECTION, sort.isAscending() ? Sort.ASCENDING : Sort.DESCENDING);

		return json;
	}

	private static JSONObject elementToCommonJson(ReportElement element) {
		JSONObject json = new JSONObject();

		// TODO sort out these member variable names
		json.put(REPORT_ELEMENT_FIELD_ID, element.getName());
		json.put(REPORT_ELEMENT_CATEGORY, element.getField().getCategoryTranslation());
		json.put(REPORT_ELEMENT_NAME, element.getField().getText());
		json.put(REPORT_ELEMENT_DESCRIPTION, element.getField().getHelp());

		return json;
	}

}
