package com.picsauditing.report;

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

	public static final String REPORT_FILTER_EXPRESSION = "filterExpression";
	public static final String REPORT_FAVORITE_COUNT = "num_times_favorited";
	public static final String REPORT_EDITABLE = "is_editable";
	public static final String REPORT_FAVORITE = "is_favorite";
	public static final String BASE_CREATION_DATE = "creation_date";
	public static final String BASE_CREATED_BY = "created_by";
	public static final String BASE_UPDATE_DATE = "update_date";
	public static final String BASE_UPDATED_BY = "updated_by";

	public static final String REPORT_ELEMENT_ID = "id";
	public static final String REPORT_ELEMENT_CATEGORY = "category";
	public static final String REPORT_ELEMENT_NAME = "name";
	public static final String REPORT_ELEMENT_DESCRIPTION = "description";

	public static final String REPORT_COLUMNS = "columns";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_URL = "url";
	public static final String COLUMN_SQL_FUNCTION = "sql_function";
	public static final String COLUMN_WIDTH = "width";
	public static final String COLUMN_SORTABLE = "is_sortable";

	public static final String REPORT_FILTERS = "filters";
	public static final String FILTER_TYPE = "type";
	public static final String FILTER_OPERATOR = "operator";
	public static final String FILTER_VALUE = "value";
	public static final String FILTER_COMPARE_COLUMN = "compare_column_id";
	public static final String FILTER_SQL_FUNCTION = "sql_function";

	public static final String REPORT_SORTS = "sorts";
	public static final String SORT_DIRECTION = "direction";

	public static final String RESULTS_TOTAL = "total";

	public enum ReportListType {
		Columns(REPORT_COLUMNS), Filters(REPORT_FILTERS), Sorts(REPORT_SORTS);
		private String key;

		private ReportListType(String json) {
			this.key = json;
		}

		public String getKey() {
			return key;
		}
	}
}
