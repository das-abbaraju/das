package com.picsauditing.PICS;

import java.sql.*;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;

import static java.lang.Math.*;

/**
 * Basic Search class used for various searches and lists
 * 
 * @author Trevor Allred
 * 
 * public abstract class SearchRaw {
 */
public class SearchRaw {
	public SQLBuilder sql;
	protected int limit = 100;
	protected int returnedRows = 0;
	protected int allRows = 0;
	protected int currentPage = 1;
	private int counter = 0;

	public SearchRaw() {
		sql = new SQLBuilder();

		// Set default options for searching
		sql.setSQL_CALC_FOUND_ROWS(true);
	}

	public List<BasicDynaBean> doSearch() throws Exception {
		Connection Conn = DBBean.getDBConnection();
		Statement stmt = Conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		
		int startCount = 0;
		startCount = (currentPage-1)*limit;
		this.sql.setStartRow(startCount);
		this.sql.setLimit(this.limit);
		
		ResultSet rs = stmt.executeQuery(this.sql.toString());		
	    RowSetDynaClass rsdc = new RowSetDynaClass(rs, false);
	    rs.close();
	    
		if (this.sql.isSQL_CALC_FOUND_ROWS()) {
			ResultSet tempRS = stmt.executeQuery("SELECT FOUND_ROWS()");
			tempRS.next();
			this.allRows = tempRS.getInt(1);
			tempRS.close();
		}
		stmt.close();
	    Conn.close();
	    this.returnedRows = rsdc.getRows().size();
	    return rsdc.getRows();
	}
	
	public HashMap<String, BasicDynaBean> doSearch(String keyName) throws Exception {
		HashMap<String, BasicDynaBean> map = new HashMap<String, BasicDynaBean>();
		List<BasicDynaBean> result = this.doSearch();
		for (BasicDynaBean row : result) {
			map.put(row.get(keyName).toString(), row);
		}
		return map;
	}

	public int getAllRows() {
		return this.allRows;
	}

	public int getReturnedRows() {
		return this.returnedRows;
	}

	public int getStartRow() {
		if (this.returnedRows == 0) return 0;
		return 1 + ((this.currentPage - 1) * this.limit);
	}

	public int getEndRow() {
		return this.getStartRow() + (this.returnedRows - 1);
	}

	public int getPages() {
		double pages = (double)this.allRows / (double)this.limit;
		return (int)ceil(pages);
	}

	public int getCurrentPage() {
		return this.currentPage;
	}
	
	public void setCurrentPage(int value) {
		this.currentPage = value;
	}

	public void setLimit(int value) {
		this.limit = value;
	}

	/// Various HTML Utilities for displaying search results on an HTML Page
	// This may need to go into a SearchHTML class, but I'm not sure
	
	public String getStartsWithLinks() {
		String html = "<span class=\"blueMain\">Starts with: ";
		html += "<a href=\"?\" class=blueMain title=\"Show All\">*</a> ";
		for (char c = 'A';c<='Z';c++)
			html += "<a href=?startsWith="+c+" class=blueMain>"+c+"</a> ";
		html += " </span>";
		return html;
	}
	public String getPageLinks() {
		String temp = "<span class=\"redMain\">";
		temp+="Found <b>"+this.allRows+"</b> results";
		if (this.returnedRows == this.allRows) {
			// We're showing all the results, just return
			temp += "</span>";
			return temp;
		}
		temp += ": Page ";
		
		if (!this.sql.isSQL_CALC_FOUND_ROWS()) {
			// We didn't calculate the total row count so just return
			// TODO: add at least a next button
			temp += addPageLink(this.currentPage-1);
			temp += addPageLink(this.currentPage);
			temp += addPageLink(this.currentPage+1);
			temp += "</span>";
			return temp;
		}
		/*
		this.allRows;
		this.currentPage;
		this.limit;
		this.returnedRows;
		*/
		
		int SHOW_PAGES = 2;
		// If all Rows = 1000 and limit = 100, then lastPage = 999/101
		
		// if currentPage = 10, print 1...
		if (this.currentPage - SHOW_PAGES > 1) {
			temp += addPageLink(1);
			if (this.currentPage - SHOW_PAGES > 2) {
				temp += " ... ";
			}
		}
		
		// if currentPage = 5, and SHOW_PAGES=2, print pages 3 4 5 6 7
		for(int i=(this.currentPage-SHOW_PAGES); i <= this.currentPage+SHOW_PAGES; i++) {
			temp += addPageLink(i);
		}

		// if currentPage = 10 and pageCount = 19, print ...19
		if ((this.currentPage + SHOW_PAGES) < this.getPages() ) {
			if ((this.currentPage + SHOW_PAGES) < (this.getPages()-1) ) {
				temp += " ... ";
			}
			temp += addPageLink(this.getPages());
		}

		temp+="</span>";
		return temp;
	}
	
	private String addPageLink(int page) {
		if (page == this.currentPage) {
			return "<strong>"+this.currentPage+"</strong>";
		}
		if (page < 1) return "";
		if (page > this.getPages()) return "";
		
		String orderBy = this.sql.getOrderBy();
		if (orderBy.length() > 0)
			orderBy = "orderBy=" + orderBy + "&";
		
		return " <a href=\"?"+orderBy+"&showPage="+page+"\">"+page+"</a> ";
	}
	
	public void setPageByResult(HttpServletRequest request) {
		String showPage = request.getParameter("showPage");
		if (showPage != null) {
			this.setCurrentPage(Integer.valueOf(showPage));
		}
	}
	/**
	 * Return white if even numbers
	 * @return bgcolor="#FFFFFF"
	 */
	public String getBGColor() {
		this.counter++;
		if ((this.counter % 2) == 1)
			return " bgcolor=\"#FFFFFF\"";
		else
			return "";
	}
}// SearchBean
