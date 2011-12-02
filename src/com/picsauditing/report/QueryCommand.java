package com.picsauditing.report;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.util.JSONUtilities;

public class QueryCommand implements JSONable {
	private List<String> columns = new ArrayList<String>();
	private List<SortableField> orderBy = new ArrayList<SortableField>();
	private List<SortableField> groupBy = new ArrayList<SortableField>();

	// 1 = Name LIKE 'Trevor%'
	// 2 =
	private List<QueryFilter> filters = new ArrayList<QueryFilter>();
	/**
	 * (0 OR 1) AND 2 AND (3 OR 4)
	 */
	private String filterExpression = "0";
	private int page = 1;
	private int rowsPerPage = 100;

	public QueryCommand() {
	}

	public QueryCommand(String json) {
		JSONObject obj = (JSONObject) JSONValue.parse(json);
		fromJSON(obj);
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
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
		if (rowsPerPage == 0)
			this.rowsPerPage = 100;
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
		json.put("filters", filters);
		json.put("columns", columns);
		json.put("orderBy", JSONUtilities.convertFromList(orderBy));
		return json;
	}

	@SuppressWarnings("unchecked")
	public void fromJSON(JSONObject json) {
		if (json == null)
			return;
		setRowsPerPage(JSONUtilities.convertToInteger(json, "rowsPerPage"));
		this.page = JSONUtilities.convertToInteger(json, "page");

		this.filterExpression = (String) json.get("filterExpression");
		JSONArray filterListObjs = (JSONArray) json.get("filters");
		this.filters.clear();
		if (filterListObjs != null) {
			for (Object filterListObj : filterListObjs) {
				QueryFilter filter = new QueryFilter();
				filter.fromJSON((JSONObject) filterListObj);
				filters.add(filter);
			}
		}

		JSONArray columnsObj = (JSONArray) json.get("columns");
		this.columns.clear();
		if (columnsObj != null)
			this.columns.addAll(columnsObj);

		JSONArray orderByObjs = (JSONArray) json.get("orderBy");
		this.orderBy.clear();
		if (orderByObjs != null) {
			for (Object orderByObj : orderByObjs) {
				SortableField field = new SortableField();
				field.fromJSON((JSONObject) orderByObj);
				orderBy.add(field);
			}
		}

		JSONArray groupByObjs = (JSONArray) json.get("groupBy");
		this.groupBy.clear();
		if (groupByObjs != null) {
			for (Object groupByObj : groupByObjs) {
				SortableField field = new SortableField();
				field.fromJSON((JSONObject) groupByObj);
				groupBy.add(field);
			}
		}

	}

}
