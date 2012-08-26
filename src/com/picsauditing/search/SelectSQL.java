package com.picsauditing.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.picsauditing.util.Strings;

public class SelectSQL {
	protected String fromTable;
	protected ArrayList<String> whereClause = new ArrayList<String>();
	protected ArrayList<String> groupByFields = new ArrayList<String>();
	protected ArrayList<String> havingClause = new ArrayList<String>();
	protected ArrayList<String> fields = new ArrayList<String>();
	protected ArrayList<String> joinClause = new ArrayList<String>();
	protected ArrayList<String> orderBys = new ArrayList<String>();
	protected int startRow = 0;
	protected int limit = -1;
	protected boolean SQL_CALC_FOUND_ROWS = false;
	protected boolean distinct = false;

	/**
	 * fullClause allows developers to use the SQLBuilder class as a sql string instead and explicitly describe the SQL
	 * statement in full.
	 */
	String fullClause = "";

	/**
	 * Return the sql clause in this format:
	 * 
	 * SELECT {fields<String>} FROM {fromTable} [{joinClause<String>}] [WHERE {whereClause}] [GROUP BY
	 * {groupByFields<String>] [HAVING {havingClause}] [ORDER BY {orderBys<String>} [LIMIT {limit}|LIMIT {startRow},
	 * {limit}]
	 */
	public String toString() {
		return toString(new ArrayList<SelectSQL>());
	}

	public String toString(List<SelectSQL> unionSql) {
		if (fullClause.length() > 0)
			return fullClause;

		StringBuilder sql = new StringBuilder();

		sql.append("SELECT ");
		if (this.SQL_CALC_FOUND_ROWS) {
			sql.append("SQL_CALC_FOUND_ROWS ");
		}
		if (this.distinct) {
			sql.append("DISTINCT ");
		}
		if (fields.size() > 0)
			sql.append(combineArray(fields));
		else
			sql.append("*");
		
		if(!Strings.isEmpty(fromTable)) {
			sql.append("\nFROM ");
			sql.append(fromTable);
		}
		
		for (String joinSQL : this.joinClause) {
			sql.append("\n");
			sql.append(joinSQL);
		}

		if (whereClause.size() > 0) {
			sql.append("\nWHERE ");
			boolean needAnd = false;
			for (String whereSQL : this.whereClause) {
				if (needAnd)
					sql.append("\n AND ");
				sql.append("(").append(whereSQL).append(") ");
				needAnd = true;
			}
		}
		if (this.groupByFields.size() > 0) {
			sql.append("\nGROUP BY ");
			sql.append(this.combineArray(this.groupByFields));

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
		return sql.toString();
	}

	public SelectSQL() {
	}

	public SelectSQL(String from) {
		this.fromTable = from;
	}

	public SelectSQL(String from, String where) {
		this.fromTable = from;
		this.whereClause.add(where);
	}

	public String combineArray(ArrayList<String> list) {
		StringBuilder sql = new StringBuilder();
		Boolean start = true;
		for (String value : list) {
			if (!start)
				sql.append(", ");
			sql.append(value);
			start = false;
		}
		return sql.toString();
	}

	public boolean hasJoin(String tableName) {
		for (String join : joinClause) {
			if (join.contains(tableName))
				return true;
		}
		return false;
	}

	public void setFromTable(String fromTable) {
		this.fromTable = fromTable;
	}

	public void addWhere(String clause) {
		clause = clause.trim();

		if (Strings.isEmpty(clause))
			return;
		if (clause.equals("1"))
			return;
		if (whereClause.contains(clause))
			return;

		whereClause.add(clause);
	}
	
	public void addGroupBy(String groupBy) {
		if (!Strings.isEmpty(groupBy))
			this.groupByFields.add(groupBy);
	}

	public void addHaving(String havingClause) {
		if (Strings.isEmpty(havingClause))
			return;

		if (havingClause.trim().equals("1"))
			return;
		this.havingClause.add(havingClause);
	}

	/**
	 * @param join
	 *            Example: JOIN user u ON u.user_id = t.user_id
	 */
	public void addJoin(String join) {
		if (join != null && join.length() > 0)
			this.joinClause.add(join);
	}

	public void addField(String field) {
		if (field != null && field.length() > 0)
			this.fields.add(field);
	}

	public void addOrderBy(String field) {
		if (field != null && field.length() > 0)
			this.orderBys.add(field);
	}

	public void setFullClause(String fullClause) {
		this.fullClause = fullClause;
	}

	public String getOrderBy() {
		return this.combineArray(this.orderBys);
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

	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public ArrayList<String> getFields() {
		return fields;
	}

	public static String getAlias(String field) {
		// TODO: Need to write a better alias parser
		String alias = "";
		if (StringUtils.contains(field, " ")) {
			String subField = field.substring(field.indexOf(" ") + 1, field.length()); 
			if (StringUtils.contains(subField, "\"")) {
				alias = subField.substring(subField.lastIndexOf("\"",subField.lastIndexOf("\"")-1)+1, subField.lastIndexOf("\""));
			}
			else if (StringUtils.contains(subField, "`")) {
				alias = subField.substring(subField.lastIndexOf("`",subField.lastIndexOf("`")-1)+1, subField.lastIndexOf("`"));
			}
			else if (StringUtils.contains(subField, "'")) {
				alias = subField.substring(subField.lastIndexOf("'",subField.lastIndexOf("'")-1)+1, subField.lastIndexOf("'"));
			}
			else
				alias = subField.substring(subField.lastIndexOf(" ") + 1, subField.length());
		}
		else if (StringUtils.contains(field, ".")) {
			alias = field.substring(field.lastIndexOf(".") + 1, field.length());
		}
		else
			alias = field;
		return alias;
	}
	
	public void setPageNumber(int rowsPerPage, int pageNumber) {
		if (pageNumber > 1) {
			setStartRow((pageNumber - 1) * rowsPerPage);
		}

		setLimit(rowsPerPage);
		setSQL_CALC_FOUND_ROWS(true);
	}

}
