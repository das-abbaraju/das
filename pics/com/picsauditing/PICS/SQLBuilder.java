package com.picsauditing.PICS;

import java.util.*;

/**
 * SQLBuilder
 * 
 * @author Trevor Allred
 * @created 11/16/2007
 */
public class SQLBuilder {
	private String fromTable;
	private ArrayList<String> whereClause = new ArrayList<String>();
	private ArrayList<String> groupByFields = new ArrayList<String>();
	private String havingClause;
	private ArrayList<String> fields = new ArrayList<String>();
	private ArrayList<String> joinClause = new ArrayList<String>();
	private ArrayList<String> orderBys = new ArrayList<String>();
	private int startRow = 0;
	private int limit = -1;
	private boolean SQL_CALC_FOUND_ROWS = false;

	/**
	 * fullClause allows developers to use the SQLBuilder class 
	 * as a sql string instead and explicitly describe the SQL 
	 * statement in full.
	 */
	String fullClause = "";

	/**
	 * Return the sql clause in this format:
	 * 
	 * SELECT {fields<String>}
	 * FROM {fromTable}
	 * [{joinClause<String>}]
	 * [WHERE {whereClause}]
	 * [GROUP BY {groupByFields<String>]
	 * [HAVING {havingClause}]
	 * [ORDER BY {orderBys<String>}
	 * [LIMIT {limit}|LIMIT {startRow}, {limit}]
	 */
	public String toString() {
		if (fullClause.length() > 0) return fullClause;
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT ");
		if (this.SQL_CALC_FOUND_ROWS) {
			sql.append("SQL_CALC_FOUND_ROWS ");
		}
		sql.append(combineArray(fields));
		sql.append(" FROM ");
		sql.append(fromTable);
		for(String joinSQL: this.joinClause) {
			sql.append("\n");
			sql.append(joinSQL);
		}
		
		if (whereClause.size() > 0) {
			sql.append("\nWHERE 1");
			for(String whereSQL: this.whereClause) {
				sql.append("\n AND (");
				sql.append(whereSQL);
				sql.append(") ");
			}
		}
		if (this.groupByFields.size() > 0) {
			sql.append("\nGROUP BY ");
			sql.append(this.combineArray(this.groupByFields));
			if (havingClause.length() > 0) {
				sql.append("\nHAVING ");
				sql.append(this.havingClause);
			}
		}
		if (this.orderBys.size() > 0) {
			sql.append("\nORDER BY ");
			sql.append(this.combineArray(this.orderBys));
		}
		
		if (this.limit >= 0) {
			sql.append("\nLIMIT ");
			if (this.startRow > 0){
				sql.append(this.startRow);
				sql.append(", ");
			}
			sql.append(this.limit);
		}
		System.out.println(sql.toString());
		
		return sql.toString();
	}

	public SQLBuilder() {
	}
	
	public SQLBuilder(String from) {
		this.fromTable = from;
	}
	
	public SQLBuilder(String from, String where) {
		this.fromTable = from;
		this.whereClause.add(where);
	}
	
	public String combineArray(ArrayList<String> list) {
		StringBuilder sql = new StringBuilder();
		Boolean start = true;
		for(String value: list) {
			if (!start) sql.append(", ");
			sql.append(value);
			start = false;
		}
		return sql.toString();
	}
	
	public void setFromTable(String fromTable) {
		this.fromTable = fromTable;
	}

	public void addWhere(String whereClause) {
		this.whereClause.add(whereClause);
	}

	public void addGroupBy(String groupBy) {
		this.groupByFields.add(groupBy);
	}

	public void setHavingClause(String havingClause) {
		this.havingClause = havingClause;
	}
	
	public void addJoin(String join) {
		this.joinClause.add(join);
	}
	
	public void addField(String field) {
		this.fields.add(field);
	}

	public void addOrderBy(String field) {
		this.orderBys.add(field);
	}

	public void setFullClause(String fullClause) {
		this.fullClause = fullClause;
	}

	public int getStartRow() {
		return startRow;
	}
	
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public boolean isSQL_CALC_FOUND_ROWS() {
		return SQL_CALC_FOUND_ROWS;
	}

	public void setSQL_CALC_FOUND_ROWS(boolean sql_calc_found_rows) {
		SQL_CALC_FOUND_ROWS = sql_calc_found_rows;
	}
}