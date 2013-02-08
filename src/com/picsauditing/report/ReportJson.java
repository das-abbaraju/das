package com.picsauditing.report;

import org.json.simple.JSONObject;

/**
 * @see http://intranet.picsauditing.com/display/it/Handshake
 */
public class ReportJson {

	public static final String LEVEL_REPORT = "report";
	public static final String LEVEL_COLUMNS = "columns";
	public static final String LEVEL_FILTERS = "filters";
	public static final String LEVEL_RESULTS = "results";
	public static final String LEVEL_DATA = "data";

	public static final String REPORT_ID = "id";
	public static final String REPORT_MODEL_TYPE = "type";
	public static final String REPORT_NAME = "name";
	public static final String REPORT_DESCRIPTION = "description";

	public static final String REPORT_FILTER_EXPRESSION = "filter_expression";
	public static final String REPORT_EDITABLE = "is_editable";
	public static final String REPORT_FAVORITE = "is_favorite";
	public static final String BASE_CREATION_DATE = "creation_date";
	public static final String BASE_CREATED_BY = "created_by";
	public static final String BASE_UPDATE_DATE = "update_date";
	public static final String BASE_UPDATED_BY = "updated_by";

	public static final String REPORT_ELEMENT_FIELD_ID = "field_id";
	public static final String REPORT_ELEMENT_CATEGORY = "category";
	public static final String REPORT_ELEMENT_NAME = "name";
	public static final String REPORT_ELEMENT_DESCRIPTION = "description";

	public static final String REPORT_COLUMNS = "columns";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_URL = "url";
	public static final String COLUMN_SQL_FUNCTION = "sql_function";
	public static final String COLUMN_WIDTH = "width";
	public static final String COLUMN_SORTABLE = "is_sortable";
	public static final String COLUMN_SORT = "sort";

	public static final String REPORT_FILTERS = "filters";
	public static final String FILTER_TYPE = "type";
	public static final String FILTER_OPERATOR = "operator";
	public static final String FILTER_VALUE = "value";
	public static final String FILTER_COLUMN_COMPARE = "column_compare_id";
	public static final String FILTER_SQL_FUNCTION = "sql_function";

	public static final String REPORT_SORTS = "sorts";
	public static final String SORT_DIRECTION = "direction";

	public static final String RESULTS_TOTAL = "total";

	public static final String EXT_JS_SUCCESS = "success";
	public static final String EXT_JS_MESSAGE = "message";

	public static final String DEBUG_SQL = "sql";

	@Deprecated
	public static final String LEGACY_MODEL_TYPE = "modelType";
	@Deprecated
	public static final String LEGACY_METHOD = "method";
	@Deprecated
	public static final String LEGACY_REPORT_FILTER_EXPRESSION = "filterExpression";

	@SuppressWarnings("unchecked")
	public static void writeJsonSuccess(JSONObject json) {
		json.put(ReportJson.EXT_JS_SUCCESS, true);
	}

	public static void writeJsonException(JSONObject json, Exception e) {
		String message = e.getMessage();
		if (message == null) {
			message = e.toString();
		}

		writeJsonErrorMessage(json, message);
	}

	@SuppressWarnings("unchecked")
	public static void writeJsonErrorMessage(JSONObject json, String message) {
		json.put(ReportJson.EXT_JS_SUCCESS, false);
		json.put(ReportJson.EXT_JS_MESSAGE, message);
	}

}
