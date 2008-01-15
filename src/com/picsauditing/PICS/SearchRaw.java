package com.picsauditing.PICS;

import java.sql.*;
//import java.util.ArrayList;
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

	public SearchRaw() {
		sql = new SQLBuilder();

		// Set default options for searching
		sql.setSQL_CALC_FOUND_ROWS(true);
		sql.setLimit(this.limit);
	}

	public SimpleResultSet doSearch() throws Exception {
		SimpleResultSet dataSet = new SimpleResultSet();
		Connection Conn = DBBean.getDBConnection();
		Statement SQLStatement = Conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		
		int startCount = 0;
		startCount = (currentPage-1)*limit;
		this.sql.setStartRow(startCount);
		
		ResultSet SQLResult = SQLStatement.executeQuery(this.sql.toString());
		ResultSetMetaData colTypes = SQLResult.getMetaData();
		
		while (SQLResult.next()) {
			// Create the row
			SimpleResultRow rowData = new SimpleResultRow();
			// Load the row with data from the ResultSet
			for(int i=1; i <= colTypes.getColumnCount(); i++) {
				rowData.put(colTypes.getColumnName(i), SQLResult.getString(colTypes.getColumnName(i)));
				//System.out.println(colTypes.getColumnName(i)+": "+SQLResult.getString(colTypes.getColumnName(i)));
			}
			// Add the row to the set
			dataSet.add(rowData);
		}
		this.returnedRows = dataSet.size();
		
		if (this.sql.isSQL_CALC_FOUND_ROWS()) {
			ResultSet tempRS = SQLStatement.executeQuery("SELECT FOUND_ROWS()");
			tempRS.next();
			this.allRows = tempRS.getInt(1);
			tempRS.close();
		}
		
		// Finish
		
		SQLResult.close();
		SQLStatement.close();
		Conn.close();
		return dataSet;
	}// doSearch

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

	public double getPages() {
		double pages = this.allRows / this.limit;
		return ceil(pages);
	}

	public int getCurrentPage() {
		return this.currentPage;
	}
}// SearchBean
