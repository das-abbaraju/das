package com.picsauditing.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.util.JSONUtilities;
import com.picsauditing.util.Strings;

public class Definition implements JSONable {
	private List<Column> columns = new ArrayList<Column>();
	private List<Filter> filters = new ArrayList<Filter>();
	private List<Sort> sorts = new ArrayList<Sort>();

	private String filterExpression;

	public Definition() {
	}

	public Definition(String json) {
		if (StringUtils.isEmpty(json)) {
			return;
		}
		JSONObject obj = (JSONObject) JSONValue.parse(json);
		fromJSON(obj);
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Sort> getSorts() {
		return sorts;
	}

	public void setSorts(List<Sort> sorts) {
		this.sorts = sorts;
	}

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public String getFilterExpression() {
		return filterExpression;
	}

	public void setFilterExpression(String filterExpression) {
		// Check this is valid
		// Should only contain 0-9,(,),AND,OR,SPACE
		this.filterExpression = filterExpression;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();
		if (filterExpression != null)
			json.put("filterExpression", filterExpression);
		if (columns.size() > 0)
			json.put("columns", JSONUtilities.convertFromList(columns));
		if (filters.size() > 0)
			json.put("filters", JSONUtilities.convertFromList(filters));
		if (sorts.size() > 0)
			json.put("sorts", JSONUtilities.convertFromList(sorts));

		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;
		this.filterExpression = (String) json.get("filterExpression");

		this.filters = parseQueryFilterList(json.get("filters"));
		this.columns = parseColumnList(json.get("columns"));
		this.sorts = parseSortList(json.get("sorts"));
	}

	private List<Filter> parseQueryFilterList(Object obj) {
		List<Filter> filters = new ArrayList<Filter>();

		if (obj == null)
			return filters;

		JSONArray filterArray = (JSONArray) obj;
		for (Object filterObj : filterArray) {
			Filter filter = new Filter();
			if (filterObj instanceof JSONObject) {
				filter.fromJSON((JSONObject) filterObj);
				filters.add(filter);
			}
		}

		return filters;
	}

	private List<Column> parseColumnList(Object obj) {
		List<Column> fields = new ArrayList<Column>();

		if (obj == null)
			return fields;

		JSONArray fieldArray = (JSONArray) obj;
		for (Object fieldObj : fieldArray) {
			Column field = new Column();
			if (fieldObj instanceof JSONObject) {
				field.fromJSON((JSONObject) fieldObj);
				fields.add(field);
			}
		}

		return fields;
	}

	private List<Sort> parseSortList(Object obj) {
		List<Sort> fields = new ArrayList<Sort>();

		if (obj == null)
			return fields;

		JSONArray fieldArray = (JSONArray) obj;
		for (Object fieldObj : fieldArray) {
			Sort field = new Sort();
			if (fieldObj instanceof JSONObject) {
				field.fromJSON((JSONObject) fieldObj);
				fields.add(field);
			}
		}

		return fields;
	}

	public Definition merge(Definition definition) {
		if (definition != null) {
			columns.addAll(definition.getColumns());
			sorts.addAll(definition.getSorts());
			filters.addAll(definition.getFilters());
			if (!Strings.isEmpty(definition.getFilterExpression())) {
				if (Strings.isEmpty(filterExpression))
					filterExpression = definition.getFilterExpression();
				else
					filterExpression = filterExpression + " AND " + definition.getFilterExpression();
			}
		}

		return definition;
	}
}
