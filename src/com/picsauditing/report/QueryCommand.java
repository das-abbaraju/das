package com.picsauditing.report;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.util.JSONUtilities;

public class QueryCommand implements JSONable {
	private List<SortableField> columns = new ArrayList<SortableField>();
	private List<SortableField> groupBy = new ArrayList<SortableField>();
	private List<SortableField> orderBy = new ArrayList<SortableField>();

	// 1 = Name LIKE 'Trevor%'
	// 2 =
	private List<QueryFilter> filters = new ArrayList<QueryFilter>();
	/**
	 * ({0} OR {1}) AND {2} AND ({3} OR {4})
	 */
	private String filterExpression;
	private int page = 1;
	private int rowsPerPage = 100;

	public QueryCommand() {
	}

	public QueryCommand(String json) {
		JSONObject obj = (JSONObject) JSONValue.parse(json);
		fromJSON(obj);
	}

	public List<SortableField> getColumns() {
		return columns;
	}

	public void setColumns(List<SortableField> columns) {
		this.columns = columns;
	}

	public List<SortableField> getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(List<SortableField> orderBy) {
		this.orderBy = orderBy;
	}

	public List<SortableField> getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(List<SortableField> groupBy) {
		this.groupBy = groupBy;
	}

	public List<QueryFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<QueryFilter> filters) {
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

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		if (rowsPerPage <= 0)
			// TODO Set to 10 while we're testing...before release, bump it to 100
			this.rowsPerPage = 10;
		else
			this.rowsPerPage = rowsPerPage;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSON(boolean full) {
		JSONObject json = new JSONObject();
		if (rowsPerPage > 0)
			json.put("rowsPerPage", rowsPerPage);
		json.put("page", page);
		json.put("filterExpression", filterExpression);
		json.put("filters", JSONUtilities.convertFromList(filters));
		json.put("columns", JSONUtilities.convertFromList(columns));
		json.put("orderBy", JSONUtilities.convertFromList(orderBy));
		json.put("groupBy", JSONUtilities.convertFromList(groupBy));
		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;
		setRowsPerPage(JSONUtilities.convertToInteger(json, "rowsPerPage"));
		this.page = JSONUtilities.convertToInteger(json, "page");

		this.filterExpression = (String) json.get("filterExpression");

		this.filters = parseQueryFilterList(json.get("filters"));
		this.columns = parseSortableFieldList(json.get("columns"));
		this.orderBy = parseSortableFieldList(json.get("orderBy"));
		this.groupBy = parseSortableFieldList(json.get("groupBy"));
	}

	private List<QueryFilter> parseQueryFilterList(Object obj) {
		List<QueryFilter> filters = new ArrayList<QueryFilter>();

		if (obj == null)
			return filters;

		JSONArray filterArray = (JSONArray) obj;
		for (Object filterObj : filterArray) {
			QueryFilter filter = new QueryFilter();
			if (filterObj instanceof JSONObject) {
				filter.fromJSON((JSONObject) filterObj);
			}
			filters.add(filter);
		}

		return filters;
	}

	private List<SortableField> parseSortableFieldList(Object obj) {
		List<SortableField> fields = new ArrayList<SortableField>();

		if (obj == null)
			return fields;

		JSONArray fieldArray = (JSONArray) obj;
		for (Object fieldObj : fieldArray) {
			SortableField field = new SortableField();
			if (fieldObj instanceof JSONObject) {
				field.fromJSON((JSONObject) fieldObj);
			} else {
				field.field = (String) fieldObj;
			}
			fields.add(field);
		}

		return fields;
	}
}
