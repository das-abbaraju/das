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
import com.picsauditing.report.ReportJson.ReportListType;
import com.picsauditing.util.PicsDateFormat;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class JsonBuilder {

	public static JSONObject fromReport(Report report) {
		JSONObject json = new JSONObject();

		convertReportLevelData(report, json);
		convertListToJson(report, json, ReportListType.Columns);
		convertListToJson(report, json, ReportListType.Filters);
		convertListToJson(report, json, ReportListType.Sorts);

		return json;
	}

	private static void convertReportLevelData(Report report, JSONObject json) {
		json.put(REPORT_ID, report.getId());
		json.put(REPORT_MODEL_TYPE, report.getModelType().toString());
		json.put(REPORT_NAME, report.getName());
		json.put(REPORT_DESCRIPTION, report.getDescription());
		json.put(REPORT_FILTER_EXPRESSION, report.getFilterExpression());
		json.put(REPORT_EDITABLE, report.isEditable());
		json.put(REPORT_FAVORITE, report.isFavorite());

		setJsonForAuditColumns(report, json);
	}

	private static void setJsonForAuditColumns(Report report, JSONObject json) {
		if (report.getCreatedBy() != null && report.getCreationDate() != null) {
			json.put(BASE_CREATION_DATE, PicsDateFormat.formatDateIsoOrBlank(report.getCreationDate()));
			json.put(BASE_CREATED_BY, report.getCreatedBy().getName());
		}

		if (report.getUpdatedBy() != null && report.getUpdateDate() != null) {
			json.put(BASE_UPDATE_DATE,  PicsDateFormat.formatDateIsoOrBlank(report.getUpdateDate()));
			json.put(BASE_UPDATED_BY, report.getUpdatedBy().getName());
		}
	}

	private static void convertListToJson(Report report, JSONObject json, ReportListType type) {
		JSONArray jsonArray = new JSONArray();
		json.put(type.getKey(), jsonArray);

		for (ReportElement element : getReportChildren(report, type)) {
			switch (type) {
			case Columns:
				jsonArray.add(toJSON((Column) element));
				break;

			case Filters:
				jsonArray.add(toJSON((Filter) element));
				break;

			case Sorts:
				jsonArray.add(toJSON((Sort) element));
				break;
			}
		}
	}

	private static List<? extends ReportElement> getReportChildren(Report report, ReportListType type) {
		List<? extends ReportElement> reportChild = null;

		if (type == ReportListType.Columns) {
			reportChild = report.getColumns();
		} else if (type == ReportListType.Filters) {
			reportChild = report.getFilters();
		} else if (type == ReportListType.Sorts) {
			reportChild = report.getSorts();
		} else {
			// TODO log this error
		}

		return reportChild;
	}

	private static JSONObject toJSON(Column column) {
		JSONObject json = elementToCommonJson(column);

		json.put(COLUMN_TYPE, column.getField().getColumnType());
		json.put(COLUMN_URL, column.getField().getUrl());
		json.put(COLUMN_SQL_FUNCTION, Strings.toStringPreserveNull(column.getSqlFunction()));
		json.put(COLUMN_WIDTH, column.getWidth());
		json.put(COLUMN_SORTABLE, column.getField().isSortable());

		return json;
	}

	private static JSONObject toJSON(Filter filter) {
		JSONObject json = elementToCommonJson(filter);
		json.put(FILTER_TYPE, filter.getField().getFilterType());
		json.put(FILTER_OPERATOR, filter.getOperator().toString());

		setFilterValue(filter, json);

		json.put(FILTER_COLUMN_COMPARE, filter.getColumnCompare());

		return json;
	}

	private static void setFilterValue(Filter filter, JSONObject json) {
		if (filter.getOperator().isValueCurrentlySupported()) {
			JSONArray valueArray = new JSONArray();
			valueArray.addAll(filter.getValues());

			// TODO Make sure the value handshake is correct
			// http://intranet.picsauditing.com/display/it/Handshake
			if (filter.getValues().size() == 1) {
				json.put(FILTER_VALUE, filter.getValues().get(0));
			} else {
				json.put(FILTER_VALUE, StringUtils.join(filter.getValues(), ", "));
			}
		}
	}

	private static JSONObject toJSON(Sort sort) {
		JSONObject json = new JSONObject();
		json.put(REPORT_ELEMENT_FIELD_ID, sort.getName());
		json.put(SORT_DIRECTION, sort.isAscending() ? Sort.ASCENDING : Sort.DESCENDING);
		return json;
	}

	private static JSONObject elementToCommonJson(ReportElement element) {
		JSONObject json = new JSONObject();
		json.put(REPORT_ELEMENT_FIELD_ID, element.getName());
		json.put(REPORT_ELEMENT_CATEGORY, element.getField().getCategoryTranslation());
		json.put(REPORT_ELEMENT_NAME, element.getField().getText());
		json.put(REPORT_ELEMENT_DESCRIPTION, element.getField().getHelp());
		return json;
	}

}
