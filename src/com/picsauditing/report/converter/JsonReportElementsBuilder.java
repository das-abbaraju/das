package com.picsauditing.report.converter;

import static com.picsauditing.report.ReportJson.*;

import java.util.Collection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.ReportUtil;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.models.AbstractModel;

@SuppressWarnings("unchecked")
public class JsonReportElementsBuilder {

	public static JSONArray buildColumns(AbstractModel model, Permissions permissions) {
		JSONArray jsonArray = new JSONArray();
		Collection<Field> fields = model.getAvailableFields().values();

		for (Field field : fields) {
			if (field.isVisible() && field.canUserSeeQueryField(permissions)) {
				ReportUtil.translateField(field, permissions.getLocale());
				JSONObject columnJson = fieldToColumnJson(field);
				jsonArray.add(columnJson);
			}
		}

		return jsonArray;
	}

	public static JSONArray buildFilters(AbstractModel model, Permissions permissions) {
		JSONArray jsonArray = new JSONArray();
		Collection<Field> fields = model.getAvailableFields().values();

		for (Field field : fields) {
			if (field.isFilterable() && field.canUserSeeQueryField(permissions)) {
				ReportUtil.translateField(field, permissions.getLocale());
				JSONObject filterJson = fieldToFilterJson(field);
				jsonArray.add(filterJson);
			}
		}

		return jsonArray;
	}

	private static JSONObject fieldToColumnJson(Field field) {
		JSONObject json = fieldToCommonJson(field);

		json.put(COLUMN_TYPE, field.getDisplayType().name());
		json.put(COLUMN_URL, field.getUrl());
		json.put(COLUMN_SQL_FUNCTION, null);
		json.put(COLUMN_WIDTH, field.getWidth());
		json.put(COLUMN_SORTABLE, field.isSortable());

		return json;
	}

	private static JSONObject fieldToFilterJson(Field field) {
		JSONObject json = fieldToCommonJson(field);

		json.put(FILTER_TYPE, field.getFilterType().name());
		json.put(FILTER_OPERATOR, null);
		json.put(FILTER_VALUE, null);
		json.put(FILTER_COLUMN_COMPARE, null);

		return json;
	}

	private static JSONObject fieldToCommonJson(Field field) {
		JSONObject json = new JSONObject();

		json.put(REPORT_ELEMENT_FIELD_ID, field.getName());
		json.put(REPORT_ELEMENT_CATEGORY, field.getCategoryTranslation());
		json.put(REPORT_ELEMENT_NAME, field.getText());
		json.put(REPORT_ELEMENT_DESCRIPTION, field.getHelp());

		return json;
	}

}
