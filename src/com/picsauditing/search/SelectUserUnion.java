package com.picsauditing.search;

import com.picsauditing.PICS.Utilities;

public class SelectUserUnion extends SelectSQL {
	public SelectUserUnion() {
		super();
	}
	
	/**
	 * Return the sql clause in this format:
	 * 
	 * SELECT {fields<String>}
	 * FROM users
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
		if (fields.size() > 0)
			sql.append(combineArray(fields));
		else 
			sql.append("*");
		sql.append("\nFROM (\n");
		
		String innerUnionSQL = "SELECT 'U' as tableType, id, username, password, email, name, isActive, dateCreated, lastLogin, accountID, null as phone " +
				"FROM users where isGroup ='No' " +
				"UNION " +
				"SELECT 'A' as tableType, id, username, password, email, contact, case active when 'Y' THEN 'Yes' ELSE 'No' end, dateCreated, lastLogin, id, phone " +
				"FROM accounts where type = 'Contractor'";
		sql.append(innerUnionSQL);
		
		sql.append("\n) u");
		
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
			if (havingClause != null && havingClause.length() > 0) {
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
}
