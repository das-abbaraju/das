package com.picsauditing.report.tables;

import com.picsauditing.util.Strings;

public class ReportOnClause {
	private String fromKey; // contactID
	private String toKey = "id";
	private String extraClauses;

	public ReportOnClause(String fromKey) {
		this.fromKey = fromKey;
	}

	public ReportOnClause(String fromKey, String toKey) {
		this.fromKey = fromKey;
		this.toKey = toKey;
	}

	public ReportOnClause(String fromKey, String toKey, String extraClauses) {
		this.fromKey = fromKey;
		this.toKey = toKey;
		this.extraClauses = extraClauses;
	}

	// Still questions about this method
	public String toSql(String fromAlias, String toAlias) {
		String onClause = fromAlias + "." + fromKey + " = " + toAlias + "." + toKey;
		if (Strings.isNotEmpty(extraClauses))
			onClause += " AND " + extraClauses;
		return onClause;
	}
}
