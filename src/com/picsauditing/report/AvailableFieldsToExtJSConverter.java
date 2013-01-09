package com.picsauditing.report;

import java.util.Collection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.access.ReportUtil;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.QueryFilterOperator;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.util.Strings;

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
		obj.put("type", field.getColumnType());
		if (!Strings.isEmpty(field.getUrl()))
			obj.put("url", field.getUrl());
		obj.put("width", field.getWidth());
		obj.put("is_sortable", field.isSortable());
		return obj;
	}

	private static Object fieldToFilterJson(Field field) {
		JSONObject obj = fieldToCommonJson(field);
		obj.put("type", field.getFilterType());
		// obj.put("operator", QueryFilterOperator.Equals);
		return obj;
	}
	
	private static JSONObject fieldToCommonJson(Field field) {
		JSONObject obj = new JSONObject();
		obj.put("id", field.getName());
		obj.put("category", field.getCategoryTranslation());
		obj.put("name", field.getText());
		obj.put("description", field.getHelp());
		return obj;
	}

}
