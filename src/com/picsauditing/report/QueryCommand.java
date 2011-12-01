package com.picsauditing.report;

import java.util.ArrayList;
import java.util.List;

public class QueryCommand {
	private List<String> columns = new ArrayList<String>();
	private List<SortableField> orderBy = new ArrayList<SortableField>();
	private List<SortableField> groupBy = new ArrayList<SortableField>();

	// 1 = Name LIKE 'Trevor%'
	// 2 =
	private List<String> filters;
	/**
	 * (0 OR 1) AND 2 AND (3 OR 4)
	 */
	private String filterExpression = "0";
	private int page = 1;
	private int rowsPerPage = 100;

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

	public List<String> getFilters() {
		return filters;
	}

	public void setFilters(List<String> filters) {
		this.filters = filters;
	}

	public String getFilterExpression() {
		return filterExpression;
	}

	public void setFilterExpression(String filterExpression) {
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
		this.rowsPerPage = rowsPerPage;
	}

}
