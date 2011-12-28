package com.picsauditing.search;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.Permissions;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

public class SelectUserUnion extends SelectSQL {

	private String userWhere = "";

	public SelectUserUnion() {
		super();
	}

	/**
	 * Return the sql clause in this format:
	 * 
	 * SELECT {fields<String>} FROM users [WHERE {whereClause}] [GROUP BY
	 * {groupByFields<String>] [HAVING {havingClause}] [ORDER BY
	 * {orderBys<String>} [LIMIT {limit}|LIMIT {startRow}, {limit}]
	 */
	@Override
	public String toString() {
		return toString(new ArrayList<SelectSQL>());
	}

	@Override
	public String toString(List<SelectSQL> unionSql) {
		PicsLogger.start("SelectSQL");
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
				+ "FROM users where isGroup ='No' " + userWhere;

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
		}

		if (havingClause.size() > 0) {
			sql.append("\nHAVING ");
			boolean needAnd = false;
			for (String havingSQL : this.havingClause) {
				if (needAnd)
					sql.append("\n AND ");
				sql.append("(").append(havingSQL).append(") ");
				needAnd = true;
			}
		}
		
		// do the same as above for the union
		if (unionSql.size() > 0) {
			for (SelectSQL union : unionSql) {
				sql.append("\nUNION\n");
				sql.append(union.toString());
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
		PicsLogger.log(sql.toString());
		PicsLogger.stop();

		return sql.toString();
	}

	/**
	 * Limit contractor search to the accounts I can see based on my perms If
	 * I'm an operator join to gc.flag and gc.workStatus too
	 * 
	 * @param permissions
	 */
	public void setPermissions(Permissions permissions) {
		if(permissions.isOperator()) {
			userWhere = "AND accountID = " + permissions.getAccountIdString();
		}
		if(permissions.isCorporate()) {
			userWhere = "AND accountID IN (" + Strings.implode(permissions.getVisibleAccounts())+")";
		}
	}
}
