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
        addRequiredKey(new ReportForeignKey(User, new UserTable(), new ReportOnClause("userID"))).setMinimumImportance(FieldImportance.Required);
        addRequiredKey(new ReportForeignKey(Report, new ReportTable(), new ReportOnClause("reportID"))).setMinimumImportance(FieldImportance.Average);
	}
}