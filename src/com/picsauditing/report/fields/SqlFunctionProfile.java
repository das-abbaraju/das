package com.picsauditing.report.fields;

import java.util.HashSet;

public enum SqlFunctionProfile {
	Boolean(new HashSet<SqlFunctionGroup>(){{
		add(SqlFunctionGroup.Aggregate);
	}}),
	String(new HashSet<SqlFunctionGroup>(){{
		add(SqlFunctionGroup.Aggregate);
		add(SqlFunctionGroup.String);
	}}),
	Date(new HashSet<SqlFunctionGroup>(){{
		add(SqlFunctionGroup.Aggregate);
		add(SqlFunctionGroup.String);
		add(SqlFunctionGroup.Date);
	}}),
	Number(new HashSet<SqlFunctionGroup>(){{
		add(SqlFunctionGroup.Aggregate);
		add(SqlFunctionGroup.Number);
	}});

	private HashSet<SqlFunctionGroup> sqlFunctionGroups;

	SqlFunctionProfile(HashSet<SqlFunctionGroup> sqlFunctionGroups) {
		this.sqlFunctionGroups = sqlFunctionGroups;
	}

	public HashSet<SqlFunctionGroup> getSqlFunctionGroups() {
		return sqlFunctionGroups;
	}
}
