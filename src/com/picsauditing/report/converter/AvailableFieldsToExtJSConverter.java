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
public class AvailableFieldsToExtJSConverter {

	public static JSONArray getColumns(AbstractModel model, Permissions permissions) {
		JSONArray list = new JSONArray();
		Collection<Field> fields = model.getAvailableFields().values();
		for (Field field : fields) {
			if (field.isVisible() && field.canUserSeeQueryField(permissions)) {
				ReportUtil.translateField(field, permissions.getLocale());
				list.add(fieldToColumnJson(field));
			}
		}

		return list;
	}

	public static Object getFilters(AbstractModel model, Permissions permissions) {
		JSONArray list = new JSONArray();
		Collection<Field> fields = model.getAvailableFields().values();
		for (Field field : fields) {
			if (field.isFilterable() && field.canUserSeeQueryField(permissions)) {
				ReportUtil.translateField(field, permissions.getLocale());
				list.add(fieldToFilterJson(field));
			}
		}

		return list;
	}

	private static JSONObject fieldToColumnJson(Field field) {
		JSONObject obj = fieldToCommonJson(field);

		obj.put(COLUMN_TYPE, field.getColumnType());
		obj.put(COLUMN_URL, field.getUrl());
		obj.put(COLUMN_WIDTH, field.getWidth());
		obj.put(COLUMN_SORTABLE, field.isSortable());
		obj.put(COLUMN_SQL_FUNCTION, null);

		return obj;
	}

	private static Object fieldToFilterJson(Field field) {
		JSONObject obj = fieldToCommonJson(field);

		obj.put(FILTER_TYPE, field.getFilterType());
		obj.put(FILTER_OPERATOR, null);
		obj.put(FILTER_VALUE, null);
		obj.put(FILTER_COLUMN_COMPARE, null);

		return obj;
	}

	public static JSONObject fieldToCommonJson(Field field) {
		JSONObject obj = new JSONObject();

		obj.put(REPORT_ELEMENT_FIELD_ID, field.getName());
		obj.put(REPORT_ELEMENT_CATEGORY, field.getCategoryTranslation());
		obj.put(REPORT_ELEMENT_NAME, field.getText());
		obj.put(REPORT_ELEMENT_DESCRIPTION, field.getHelp());

		return obj;
	}

}
