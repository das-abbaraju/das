package com.picsauditing.report.fields;

import java.util.HashSet;

@SuppressWarnings("serial")
public enum SqlFunctionGroup {
	Aggregate(new HashSet<SqlFunction>(){{
		add(SqlFunction.Count);
		add(SqlFunction.CountDistinct);
		add(SqlFunction.Max);
		add(SqlFunction.Min);
	}}),
	Number(new HashSet<SqlFunction>(){{
		add(SqlFunction.Average);
		add(SqlFunction.Round);
		add(SqlFunction.Sum);
		add(SqlFunction.StdDev);
	}}),
	String(new HashSet<SqlFunction>(){{
		add(SqlFunction.GroupConcat);
		add(SqlFunction.Length);
		add(SqlFunction.LowerCase);
		add(SqlFunction.UpperCase);
	}}),
	Date(new HashSet<SqlFunction>(){{
		add(SqlFunction.Month);
		add(SqlFunction.Year);
		add(SqlFunction.YearMonth);
		add(SqlFunction.WeekDay);
		add(SqlFunction.Hour);
		add(SqlFunction.Date);
	}});

	private HashSet<SqlFunction> sqlFunctions;

	SqlFunctionGroup(HashSet<SqlFunction> sqlFunctions) {
		this.sqlFunctions = sqlFunctions;
	}

	public HashSet<SqlFunction> getSqlFunctions() {
		return sqlFunctions;
	}
}
