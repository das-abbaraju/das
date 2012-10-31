package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ReportPermissionUser;

public class ReportPermissionUserTable extends AbstractTable {
	public static final String Report = "Report";
	public static final String User = "User";
	public static final String ReportUser = "ReportUser";

	public ReportPermissionUserTable() {
		super("report_permission_user");
		addFields(ReportPermissionUser.class);
	}

	protected void addJoins() {
		addOptionalKey(new ReportForeignKey(User, new UserTable(), new ReportOnClause("userID")));
		addOptionalKey(new ReportForeignKey(Report, new ReportTable(), new ReportOnClause("reportID")));

		addOptionalKey(new ReportForeignKey(ReportUser, new ReportUserTable(), new ReportOnClause("userID", "userID",
				ReportOnClause.ToAlias + ".reportID = " + ReportOnClause.FromAlias + ".reportID")));
	}
}