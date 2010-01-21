package com.picsauditing.search;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;

public class SelectUserUnion extends SelectSQL {
	private String userWhere = "";

	public SelectUserUnion() {
		super();
	}

	/**
	 * Return the sql clause in this format:
	 * 
	 * SELECT {fields<String>} FROM users [WHERE {whereClause}] [GROUP BY
	 * {groupByFields<String>] [HAVING {havingClause}] [ORDER BY {orderBys<String>}
	 * [LIMIT {limit}|LIMIT {startRow}, {limit}]
	 */
	public String toString() {
		if (fullClause.length() > 0)
			return fullClause;

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

		String innerUnionSQL = "SELECT id, username, password, email, name, isActive, creationDate, lastLogin, accountID, phoneIndex as phone "
				+ "FROM users where isGroup ='No' "
				+ userWhere;
		
		sql.append(innerUnionSQL);

		sql.append("\n) u");

		for (String joinSQL : this.joinClause) {
			sql.append("\n");
			sql.append(joinSQL);
		}

		if (whereClause.size() > 0) {
			sql.append("\nWHERE 1");
			for (String whereSQL : this.whereClause) {
		
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
			if (this.startRow > 0) {
				sql.append(this.startRow);
				sql.append(", ");
			}
			sql.append(this.limit);
		}
		System.out.println(sql.toString());

		return sql.toString();
	}

	/**
	 * Limit contractor search to the accounts I can see based on my perms If
	 * I'm an operator join to flags.flag and gc.workStatus too
	 * 
	 * @param permissions
	 */
	public void setPermissions(Permissions permissions) {
		if (!permissions.hasPermission(OpPerms.AllOperators))
			userWhere = "AND accountID = " + permissions.getAccountIdString();
	}
}
