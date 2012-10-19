package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ReportUser;

public class ReportUserTable extends AbstractTable {
	public static final String Report = "Report";
	public static final String User = "User";

	public ReportUserTable() {
		super("report_user");
		addFields(ReportUser.class);
	}

	protected void addJoins() {
		addOptionalKey(new ReportForeignKey(User, new UserTable(), new ReportOnClause("userID")));
		addOptionalKey(new ReportForeignKey(Report, new ReportTable(), new ReportOnClause("reportID")));
	}
}