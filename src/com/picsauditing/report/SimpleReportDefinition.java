package com.picsauditing.report;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.util.JSONUtilities;

public class SimpleReportDefinition implements JSONable {
	private List<SimpleReportField> columns = new ArrayList<SimpleReportField>();
	private List<SimpleReportField> groupBy = new ArrayList<SimpleReportField>();
	private List<SimpleReportField> orderBy = new ArrayList<SimpleReportField>();
	private List<SimpleReportField> having = new ArrayList<SimpleReportField>();

	// 1 = Name LIKE 'Trevor%'
	// 2 =
	private List<SimpleReportFilter> filters = new ArrayList<SimpleReportFilter>();
	/**
	 * ({0} OR {1}) AND {2} AND ({3} OR {4})
	 */
	private String filterExpression;
	private int page = 1;
	private int rowsPerPage = 100;

	public SimpleReportDefinition() {
	}

	public SimpleReportDefinition(String json) {
		JSONObject obj = (JSONObject) JSONValue.parse(json);
		fromJSON(obj);
	}

	public List<SimpleReportField> getColumns() {
		return columns;
	}

	public void setColumns(List<SimpleReportField> columns) {
		this.columns = columns;
	}

	public List<SimpleReportField> getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(List<SimpleReportField> orderBy) {
		this.orderBy = orderBy;
	}

	public List<SimpleReportField> getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(List<SimpleReportField> groupBy) {
		this.groupBy = groupBy;
	}

	public List<SimpleReportField> getHaving() {
		return having;
	}

	public void setHaving(List<SimpleReportField> having) {
		this.having = having;
	}

	public List<SimpleReportFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<SimpleReportFilter> filters) {
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
		if (page > 1)
			json.put("page", page);
		if (filterExpression != null)
			json.put("filterExpression", filterExpression);
		if (filters.size() > 0)
			json.put("filters", JSONUtilities.convertFromList(filters));
		if (columns.size() > 0)
			json.put("columns", JSONUtilities.convertFromList(columns));
		if (orderBy.size() > 0)
			json.put("orderBy", JSONUtilities.convertFromList(orderBy));
		if (groupBy.size() > 0)
			json.put("groupBy", JSONUtilities.convertFromList(groupBy));
		if (having.size() > 0)
			json.put("having", JSONUtilities.convertFromList(having));

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
		this.having = parseSortableFieldList(json.get("having"));
	}

	private List<SimpleReportFilter> parseQueryFilterList(Object obj) {
		List<SimpleReportFilter> filters = new ArrayList<SimpleReportFilter>();

		if (obj == null)
			return filters;

		JSONArray filterArray = (JSONArray) obj;
		for (Object filterObj : filterArray) {
			SimpleReportFilter filter = new SimpleReportFilter();
			if (filterObj instanceof JSONObject) {
				filter.fromJSON((JSONObject) filterObj);
			}
			filters.add(filter);
		}

		return filters;
	}

	private List<SimpleReportField> parseSortableFieldList(Object obj) {
		List<SimpleReportField> fields = new ArrayList<SimpleReportField>();

		if (obj == null)
			return fields;

		JSONArray fieldArray = (JSONArray) obj;
		for (Object fieldObj : fieldArray) {
			SimpleReportField field = new SimpleReportField();
			if (fieldObj instanceof JSONObject) {
				field.fromJSON((JSONObject) fieldObj);
			} else {
				field.setField((String) fieldObj);
			}
			fields.add(field);
		}

		return fields;
	}
}
