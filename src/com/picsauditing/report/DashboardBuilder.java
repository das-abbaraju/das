package com.picsauditing.report;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.jpa.entities.Dashboard;
import com.picsauditing.jpa.entities.DashboardWidget;
import com.picsauditing.jpa.entities.WidgetType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.util.Strings;

/*
 * panels: [ {
 type: 'html',
 name: 'Test Html',
 url: 'dashboard_test.html'
 } ]
 */
@SuppressWarnings("unchecked")
public class DashboardBuilder {

	public static JSONArray build(Dashboard dashboard) {
		JSONArray result = new JSONArray();
		Map<Integer, JSONArray> panels = new HashMap<Integer, JSONArray>();
		for (DashboardWidget widget : dashboard.getWidgets()) {
			JSONArray column = getColumn(panels, widget.getColumn());
			column.add(toJson(widget));
		}
		for (Integer columnNumber : panels.keySet()) {
			JSONObject columnPanel = new JSONObject();
			JSONArray column = panels.get(columnNumber);
			columnPanel.put("panels", column);
			result.add(columnPanel);
		}
		return result;
	}

	private static JSONArray getColumn(Map<Integer, JSONArray> panels, int column) {
		if (!panels.containsKey(column))
			panels.put(column, new JSONArray());
		return panels.get(column);
	}

	private static JSONObject toJson(DashboardWidget widget) {
		JSONObject json = new JSONObject();
		json.put("type", widget.getWidgetType().toString());
		if (widget.getWidgetType().equals(WidgetType.Html)) {
			json.put("url", widget.getUrl());
		} else if (widget.getReport() != null) {
			json.put("id", widget.getReport().getId());
			json.put("name", widget.getReport().getName());
			
			SqlBuilder builder = new SqlBuilder();
			builder.setBaseModelFromReport(widget.getReport());
			
			JSONArray fields = new JSONArray();

//			for (QueryField field : builder.getAvailableFields().values()) {
//				if (isCanSeeQueryField(field)) {
//					JSONObject obj = field.toJSONObject();
//					obj.put("category", translateCategory(field.getCategory().toString()));
//					obj.put("text", translateLabel(field));
//					String help = getText("Report." + field.getName() + ".help");
//					if (help != null)
//						obj.put("help", help);
//					fields.add(obj);
//				}
//			}
			
//            fields: [ 'contractorName' ],
//            columns: [ {
//                header: 'Contractor Name',
//                flex: 1,
//                dataIndex: 'contractorName'
//            }, {
//                header: 'Phone',
//                dataIndex: 'phone'
//            } ]

		}
		return json;
	}
}
