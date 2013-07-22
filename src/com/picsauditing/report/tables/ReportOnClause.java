package com.picsauditing.report.tables;

import com.picsauditing.access.Permissions;
import com.picsauditing.util.Strings;

public class ReportOnClause {
	public static final String VisibleAccountIDs = "{CURRENT_VISIBLE_ACCOUNTIDS}";
	public static final String AccountID = "{CURRENT_ACCOUNTID}";
	public static final String UserID = "{CURRENT_USERID}";
	public static final String Alias = "_ALIAS}";
    public static final String ThirdAlias = "{THIRD" + Alias;
	public static final String FromAlias = "{FROM" + Alias;
	public static final String ToAlias = "{TO" + Alias;
	
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

	public String getExtraClauses() {
		return extraClauses;
	}

	public void setExtraClauses(String extraClauses) {
		this.extraClauses = extraClauses;
	}

	public String toSql(String fromAlias, String toAlias, String thirdAlias, Permissions permissions) {
        String onClause = "1";
        if (fromKey != null && toKey != null)
		    onClause = fromAlias + "." + fromKey + " = " + toAlias + "." + toKey;

		if (Strings.isNotEmpty(extraClauses))
			onClause += " AND " + extraClauses;

		onClause = onClause.replace(VisibleAccountIDs, Strings.implodeForDB(permissions.getVisibleAccounts()));
		onClause = onClause.replace(AccountID, permissions.getAccountIdString());
		onClause = onClause.replace(UserID, permissions.getUserIdString());
		onClause = onClause.replace(FromAlias, fromAlias);
		onClause = onClause.replace(ToAlias, toAlias);

        if (thirdAlias != null)
            onClause = onClause.replace(ThirdAlias, thirdAlias);

		return onClause;
	}
}
