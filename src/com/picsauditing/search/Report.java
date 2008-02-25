package com.picsauditing.search;

import static java.lang.Math.ceil;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import org.apache.commons.beanutils.BasicDynaBean;

public class Report {
	private SelectSQL sql;
	private int limit = 100;
	private int currentPage = 1;
	private int returnedRows = 0;
	private int allRows = 0;
	private ArrayList<SelectFilter> filters = new ArrayList<SelectFilter>();
	private String orderBy;
	
	public List<BasicDynaBean> getPage() throws SQLException {
		sql.addOrderBy(this.orderBy);
		for(SelectFilter filter: filters) {
			sql.addWhere(filter.getWhereClause());
		}
		sql.setLimit(this.limit);
		sql.setStartRow((this.currentPage - 1) * this.limit);
		
		Database db = new Database();
		List<BasicDynaBean> pageData = db.select(sql.toString(), true);
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
	
	private int getPages() {
		double pages = (double)this.allRows / (double)this.limit;
		return (int)ceil(pages);
	}

	public int getCurrentPage() {
		return this.currentPage;
	}
	
	public void setPageByResult(HttpServletRequest request) {
		String showPage = request.getParameter("showPage");
		if (showPage != null) {
			this.setCurrentPage(Integer.valueOf(showPage));
		}
	}
	
	public void setCurrentPage(int value) {
		this.currentPage = value;
	}

	public void setLimit(int value) {
		this.limit = value;
	}

	public void setOrderBy(String value, String defaultValue) {
		if (value != null && value.length() > 0)
			this.orderBy = value;
		else
			this.orderBy = defaultValue;
	}
	
	/// Various HTML Utilities for displaying search results on an HTML Page
	// This may need to go into a SearchHTML class, but I'm not sure
	
	public String getStartsWithLinks() {
		return getStartsWithLinks("");
	}
	public String getStartsWithLinks(String additionalParams) {
		String html = "<span class=\"blueMain\">Starts with: ";
		html += "<a href=\"?"+additionalParams+"\" class=blueMain title=\"Show All\">*</a> ";
		if (additionalParams != null && additionalParams.length() >= 3)
			additionalParams = "&"+additionalParams;
		else
			additionalParams = "";
		for (char c = 'A';c<='Z';c++)
			html += "<a href=?startsWith="+c+additionalParams+" class=blueMain>"+c+"</a> ";
		html += " </span>";
		return html;
	}
	public String getPageLinks() {
		return getPageLinks("");
	}
	public String getPageLinks(String additionalParams) {
		String temp = "<span class=\"redMain\">";
		temp+="Found <b>"+this.allRows+"</b> results";
		if (this.returnedRows == this.allRows) {
			// We're showing all the results, just return
			temp += "</span>";
			return temp;
		}
		temp += ": Page ";
		
		int SHOW_PAGES = 2;
		// If all Rows = 1000 and limit = 100, then lastPage = 999/101
		
		// if currentPage = 10, print 1...
		if (this.currentPage - SHOW_PAGES > 1) {
			temp += addPageLink(1, additionalParams);
			if (this.currentPage - SHOW_PAGES > 2) {
				temp += " ... ";
			}
		}
		
		// if currentPage = 5, and SHOW_PAGES=2, print pages 3 4 5 6 7
		for(int i=(this.currentPage-SHOW_PAGES); i <= this.currentPage+SHOW_PAGES; i++) {
			temp += addPageLink(i, additionalParams);
		}

		// if currentPage = 10 and pageCount = 19, print ...19
		if ((this.currentPage + SHOW_PAGES) < this.getPages() ) {
			if ((this.currentPage + SHOW_PAGES) < (this.getPages()-1) ) {
				temp += " ... ";
			}
			temp += addPageLink(this.getPages(), additionalParams);
		}

		temp+="</span>";
		return temp;
	}
	
	private String addPageLink(int page, String additionalParams) {
		if (page == this.currentPage) {
			return "<strong>"+this.currentPage+"</strong>";
		}
		if (page < 1) return "";
		if (page > this.getPages()) return "";
		
		String orderBy = this.sql.getOrderBy();
		if (orderBy.length() > 0)
			orderBy = "orderBy=" + orderBy + "&";
		
		return " <a href=\"?"+orderBy+"showPage="+page+additionalParams+"\">"+page+"</a> ";
	}
}
