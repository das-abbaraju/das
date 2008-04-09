package com.picsauditing.util;
//Todo: figure out where the SelectSQL and SQLUpdate classes should go

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Similar to the SelectSQL utility class that lets us create UPDATE statements for MySQL
 * 
 * @author Trevor
 */
public class SQLUpdate {
	private boolean log = true;
	private Type updateType = Type.UPDATE;
	private String tableName;
	private boolean isLowPriority = false;
	private boolean ignoreErrors = false;
	private int limit = 0;
	private ArrayList<String> whereClause = new ArrayList<String>();
	private HashMap<String, String> fields = new HashMap<String, String>();
	
	public static enum Type {UPDATE, REPLACE, INSERT}
	
	public SQLUpdate(String tableName) {
		super();
		this.tableName = tableName;
	}

	public String toString() {
		if (fields.size() == 0)
			return "";
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(this.updateType).append(" ");
		if (this.isLowPriority) {
			sql.append("LOW_PRIORITY ");
		}
		if (this.ignoreErrors) {
			sql.append("IGNORE ");
		}
		sql.append(tableName);
		sql.append(" SET ");
		boolean started = false;
		for(String key: fields.keySet()) {
			if (started) sql.append(", ");
			started = true;
			
			sql.append(key);
			sql.append(" = ");
			sql.append(fields.get(key));
		}
		
		if (whereClause.size() > 0) {
			sql.append("\nWHERE 1");
			for(String whereSQL: this.whereClause) {
				sql.append("\n AND (");
				sql.append(whereSQL);
				sql.append(") ");
			}
		}
		
		if (this.limit >= 0) {
			sql.append("\nLIMIT ");
			sql.append(this.limit);
		}
		if (log)
			System.out.println(sql.toString());
		
		return sql.toString();
	}

	//////// Basic Setters ///////////
	
	public void setLog(boolean log) {
		this.log = log;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setLowPriority(boolean isLowPriority) {
		this.isLowPriority = isLowPriority;
	}

	public void setIgnoreErrors(boolean ignoreErrors) {
		this.ignoreErrors = ignoreErrors;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void addWhereClause(String where) {
		this.whereClause.add(where);
	}

	public void addFields(String field, String value) {
		this.fields.put(field, value);
	}

	public void addFields(String field, Integer value) {
		this.fields.put(field, value.toString());
	}

	public Type getUpdateType() {
		return updateType;
	}

	public void setUpdateType(Type updateType) {
		this.updateType = updateType;
	}
}
