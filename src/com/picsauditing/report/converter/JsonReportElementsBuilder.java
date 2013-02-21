package com.picsauditing.report.converter;

import static com.picsauditing.report.ReportJson.*;

import java.util.Collection;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.ReportUtil;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.report.models.AbstractModel;

@SuppressWarnings("unchecked")
public class JsonReportElementsBuilder {

	public static JSONArray buildColumns(AbstractModel model, Permissions permissions) {
		JSONArray jsonArray = new JSONArray();
		Collection<Field> fields = model.getAvailableFields().values();

		for (Field field : fields) {
			if (field.isVisible() && field.canUserSeeQueryField(permissions)) {
				ReportUtil.translateField(field, permissions.getLocale());
				JSONObject columnJson = fieldToColumnJson(field, permissions.getLocale());
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

	private static JSONObject fieldToColumnJson(Field field, Locale locale) {
		JSONObject json = fieldToCommonJson(field);

		json.put(COLUMN_TYPE, field.getDisplayType().name());
		json.put(COLUMN_URL, field.getUrl());
		
		// TODO: This does not match column-functions.json.
		JSONArray sqlFunctionArray = new JSONArray();

		for (SqlFunction sqlFunction : field.getType().getSqlFunctions()) {

			JSONObject sqlFunctionKeyValue = new JSONObject();
			sqlFunctionKeyValue.put("key", sqlFunction.name());
			sqlFunctionKeyValue.put("value",
					ReportUtil.getText(ReportUtil.REPORT_FUNCTION_KEY_PREFIX + sqlFunction.name(), locale));
			sqlFunctionArray.add(sqlFunctionKeyValue);
		}

		json.put(COLUMN_SQL_FUNCTION, sqlFunctionArray);
		json.put(COLUMN_WIDTH, field.getWidth());
		json.put(COLUMN_SORTABLE, field.isSortable());

		return json;
	}

	private static JSONObject fieldToFilterJson(Field field) {
		JSONObject json = fieldToCommonJson(field);

		json.put(FILTER_TYPE, field.getFilterType().name());
		json.put(FILTER_OPERATOR, field.getFilterType().defaultOperator.toString());
		json.put(FILTER_VALUE, "");
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
