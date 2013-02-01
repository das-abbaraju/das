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

		for (ReportElement obj : getReportChildren(report, type)) {
			switch (type) {
			case Columns:
				jsonArray.add(toJSON((Column) obj));
				break;

			case Filters:
				jsonArray.add(toJSON((Filter) obj));
				break;

			case Sorts:
				jsonArray.add(toJSON((Sort) obj));
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

	private static JSONObject toJSON(Column obj) {
		JSONObject json = elementToCommonJson(obj);

		json.put(COLUMN_TYPE, obj.getField().getColumnType());
		json.put(COLUMN_URL, obj.getField().getUrl());
		json.put(COLUMN_SQL_FUNCTION, Strings.toStringPreserveNull(obj.getSqlFunction()));
		json.put(COLUMN_WIDTH, obj.getWidth());
		json.put(COLUMN_SORTABLE, obj.getField().isSortable());

		return json;
	}

	private static JSONObject toJSON(Filter obj) {
		JSONObject json = elementToCommonJson(obj);
		json.put(FILTER_TYPE, obj.getField().getFilterType());
		json.put(FILTER_OPERATOR, obj.getOperator().toString());

		setFilterValue(obj, json);

		if (Strings.isNotEmpty(obj.getColumnCompare())) {
			json.put(Filter.FIELD_COMPARE, obj.getColumnCompare());
		}

		return json;
	}

	private static void setFilterValue(Filter obj, JSONObject json) {
		if (obj.getOperator().isValueCurrentlySupported()) {
			JSONArray valueArray = new JSONArray();
			valueArray.addAll(obj.getValues());

			// TODO Make sure the value handshake is correct
			// http://intranet.picsauditing.com/display/it/Handshake
			if (obj.getValues().size() == 1) {
				json.put(FILTER_VALUE, obj.getValues().get(0));
			} else {
				json.put(FILTER_VALUE, StringUtils.join(obj.getValues(), ", "));
			}
		}
	}

	private static JSONObject toJSON(Sort obj) {
		JSONObject json = new JSONObject();
		json.put(REPORT_ELEMENT_FIELD_ID, obj.getName());
		json.put(SORT_DIRECTION, obj.isAscending() ? Sort.ASCENDING : Sort.DESCENDING);
		return json;
	}

	private static JSONObject elementToCommonJson(ReportElement obj) {
		JSONObject json = new JSONObject();
		json.put(REPORT_ELEMENT_FIELD_ID, obj.getName());
		json.put(REPORT_ELEMENT_CATEGORY, obj.getField().getCategoryTranslation());
		json.put(REPORT_ELEMENT_NAME, obj.getField().getText());
		json.put(REPORT_ELEMENT_DESCRIPTION, obj.getField().getHelp());
		return json;
	}

}
