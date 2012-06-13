package com.picsauditing.search;

import static java.lang.Math.ceil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.util.LinkBuilder;

@SuppressWarnings("serial")
public class Report extends TranslationActionSupport {
	private SelectSQL sql;
	private List<SelectSQL> unionSql = new ArrayList<SelectSQL>();

	private int limit = 100;
	private int currentPage = 1;
	private int returnedRows = 0;
	private int allRows = 0;
	private HashMap<String, SelectFilter> filters = new HashMap<String, SelectFilter>();
	private String orderBy;

	public List<BasicDynaBean> getPage() throws SQLException {
		sql.addOrderBy(this.orderBy);
		for (String filter : filters.keySet()) {
			sql.addWhere(filters.get(filter).getWhere());
		}
		sql.setLimit(this.limit);
		sql.setStartRow((this.currentPage - 1) * this.limit);

		for (SelectSQL union : unionSql) {
			for (String filter : filters.keySet()) {
				union.addWhere(filters.get(filter).getWhere());
			}
		}
		Database db = new Database();
		String sqlText = sql.toString(unionSql);
		
		List<BasicDynaBean> pageData = db.select(sqlText, true);
		returnedRows = pageData.size();
		allRows = db.getAllRows();
		return pageData;
	}

	public SelectSQL getSql() {
		return sql;
	}

	public void setSql(SelectSQL sql) {
		this.sql = sql;
		this.sql.setSQL_CALC_FOUND_ROWS(true);
	}

	public List<SelectSQL> getUnionSql() {
		return unionSql;
	}

	public void setUnionSql(List<SelectSQL> unionSql) {
		this.unionSql = unionSql;
	}

	private int getPages() {
		double pages = (double) this.allRows / (double) this.limit;
		return (int) ceil(pages);
	}

	public int getCurrentPage() {
		return this.currentPage;
	}

	public int getFirstRowNumber() {
		return ((currentPage - 1) * limit) + 1;
	}

	public void setPageByResult(String page) {
		String showPage = page;
		if (showPage != null) {
			this.setCurrentPage(Integer.valueOf(showPage));
		}
	}

	public String getFilterParams() {
		StringBuilder params = new StringBuilder();
		for (String name : this.filters.keySet()) {
			if (filters.get(name).isSet()) {
				params.append("&");
				params.append(filters.get(name).getName());
				params.append("=");
				params.append(filters.get(name).getValue());
			}
		}
		return params.toString();
	}

	public String getFilterValue(String name) {
		SelectFilter selectFilter = this.filters.get(name);
		if (selectFilter == null)
			return "";
		return selectFilter.getValue() == null ? "" : selectFilter.getValue();
	}

	public void addFilter(SelectFilter filter) {
		this.filters.put(filter.getName(), filter);
	}

	public HashMap<String, SelectFilter> getFilters() {
		return filters;
	}

	public void setFilters(HashMap<String, SelectFilter> filters) {
		this.filters = filters;
	}

	public void setCurrentPage(int value) {
		this.currentPage = value;
	}

	public void setLimit(int value) {
		this.limit = value;
	}

	public String getOrderBy() {
		return this.orderBy;
	}

	public void setOrderBy(String value, String defaultValue) {
		if (value != null && value.length() > 0)
			this.orderBy = value;
		else
			this.orderBy = defaultValue;
	}

	// / Various HTML Utilities for displaying search results on an HTML Page
	// This may need to go into a SearchHTML class, but I'm not sure

	public String getStartsWithLinks() {
		return getStartsWithLinks("");
	}

	public String getStartsWithLinks(String additionalParams) {
		String filterParams = getFilterParams();
		if (additionalParams != null && additionalParams.length() >= 3)
			additionalParams = "&" + additionalParams;
		else
			additionalParams = "";
		additionalParams += filterParams;

		StringBuilder searchAZ = new StringBuilder("");
		for (char c = 'A'; c <= 'Z'; c++)
			searchAZ.append(String.format("<li><a href=\"?startsWith=%1$c%2$s\" class=blueMain>%1$c</a></li>", c,
					additionalParams));

		String html = String.format("<ul class=\"paging\">%s: <li><a href=\"?%s\" title=\"%s\">*</a></li>%s</ul>",
				getText("Filters.paging.StartsWith"), additionalParams, getText("Filters.paging.ShowAll"),
				searchAZ.toString());

		return html;
	}

	public String getStartsWithLinksWithDynamicForm() {
		return LinkBuilder.getStartsWithLinks();
	}

	public String getPageLinks() {
		return getPageLinks("");
	}

	public String getPageLinks(String additionalParams) {
		String filterParams = getFilterParams();
		additionalParams += filterParams;

		StringBuilder temp = new StringBuilder(String.format("<ul class=\"paging\">%s",
				getText("Filters.paging.FoundResults", new Object[] { (Integer) this.allRows })));
		if (this.returnedRows == this.allRows) {
			// We're showing all the results, just return
			temp.append("</ul>");
			return temp.toString();
		}
		temp.append(String.format(": %s ", getText("Filters.paging.Page")));

		int SHOW_PAGES = 2;
		// If all Rows = 1000 and limit = 100, then lastPage = 999/101

		// if currentPage = 10, print 1...
		if (this.currentPage - SHOW_PAGES > 1) {
			temp.append(addPageLink(1, additionalParams));
			if (this.currentPage - SHOW_PAGES > 2) {
				temp.append(" ... ");
			}
		}

		// if currentPage = 5, and SHOW_PAGES=2, print pages 3 4 5 6 7
		for (int i = (this.currentPage - SHOW_PAGES); i <= this.currentPage + SHOW_PAGES; i++) {
			temp.append(addPageLink(i, additionalParams));
		}

		// if currentPage = 10 and pageCount = 19, print ...19
		if ((this.currentPage + SHOW_PAGES) < this.getPages()) {
			if ((this.currentPage + SHOW_PAGES) < (this.getPages() - 1)) {
				temp.append(" ... ");
			}
			temp.append(addPageLink(this.getPages(), additionalParams));
		}

		temp.append("</ul>");
		return temp.toString();
	}

	public String getPageLinksWithDynamicForm() {
		return LinkBuilder.getPageNOfXLinks(this.allRows, this.limit, this.limit * (this.currentPage - 1) + 1,
				(this.limit) * (this.currentPage) > this.allRows ? this.allRows : (this.limit) * (this.currentPage),
				this.currentPage);
	}

	private String addPageLink(int page, String additionalParams) {
		if (page < 1)
			return "";
		if (page > this.getPages())
			return "";

		String orderBy = this.sql.getOrderBy();
		if (orderBy.length() > 0)
			orderBy = String.format("orderBy=%s&", orderBy);

		return String.format("<li><a href=\"?%1$sshowPage=%2$s%3$s\">%2$s</a></li>", orderBy, page, additionalParams);
	}

	public int getAllRows() {
		return allRows;
	}

}
