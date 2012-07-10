package com.picsauditing.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.picsauditing.jpa.entities.JSONable;
import com.picsauditing.report.fields.SimpleReportColumn;
import com.picsauditing.report.fields.SimpleReportFilter;
import com.picsauditing.report.fields.SimpleReportSort;
import com.picsauditing.util.JSONUtilities;
import com.picsauditing.util.Strings;

public class SimpleReportDefinition implements JSONable {
	private List<SimpleReportColumn> columns = new ArrayList<SimpleReportColumn>();
	private List<SimpleReportFilter> filters = new ArrayList<SimpleReportFilter>();
	private List<SimpleReportSort> orderBy = new ArrayList<SimpleReportSort>();
	
	/**
	 * ({0} OR {1}) AND {2} AND ({3} OR {4})
	 */
	private String filterExpression;
	private int rowsPerPage = 100;

	public SimpleReportDefinition() {
	}

	public SimpleReportDefinition(String json) {
		if (StringUtils.isEmpty(json)) {
			return;
		}
		JSONObject obj = (JSONObject) JSONValue.parse(json);
		fromJSON(obj);
	}

	public List<SimpleReportColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<SimpleReportColumn> columns) {
		this.columns = columns;
	}

	public List<SimpleReportSort> getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(List<SimpleReportSort> orderBy) {
		this.orderBy = orderBy;
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
		if (filterExpression != null)
			json.put("filterExpression", filterExpression);
		if (columns.size() > 0)
			json.put("columns", JSONUtilities.convertFromList(columns));
		if (filters.size() > 0)
			json.put("filters", JSONUtilities.convertFromList(filters));
		if (orderBy.size() > 0)
			json.put("sorts", JSONUtilities.convertFromList(orderBy));

		return json;
	}

	public void fromJSON(JSONObject json) {
		if (json == null)
			return;
		setRowsPerPage(JSONUtilities.convertToInteger(json, "rowsPerPage"));

		this.filterExpression = (String) json.get("filterExpression");

		this.filters = parseQueryFilterList(json.get("filters"));
		this.columns = parseColumnList(json.get("columns"));
		this.orderBy = parseSortList(json.get("sorts"));
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
				filters.add(filter);
			}
		}

		return filters;
	}

	private List<SimpleReportColumn> parseColumnList(Object obj) {
		List<SimpleReportColumn> fields = new ArrayList<SimpleReportColumn>();

		if (obj == null)
			return fields;

		JSONArray fieldArray = (JSONArray) obj;
		for (Object fieldObj : fieldArray) {
			SimpleReportColumn field = new SimpleReportColumn();
			if (fieldObj instanceof JSONObject) {
				field.fromJSON((JSONObject) fieldObj);
				fields.add(field);
			}
		}

		return fields;
	}

	private List<SimpleReportSort> parseSortList(Object obj) {
		List<SimpleReportSort> fields = new ArrayList<SimpleReportSort>();

		if (obj == null)
			return fields;

		JSONArray fieldArray = (JSONArray) obj;
		for (Object fieldObj : fieldArray) {
			SimpleReportSort field = new SimpleReportSort();
			if (fieldObj instanceof JSONObject) {
				field.fromJSON((JSONObject) fieldObj);
				fields.add(field);
			}
		}

		return fields;
	}

	public SimpleReportDefinition merge(SimpleReportDefinition definition) {
		if (definition != null) {
			columns.addAll(definition.getColumns());
			orderBy.addAll(definition.getOrderBy());
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
