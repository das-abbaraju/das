package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Report;

public class ReportTable extends AbstractTable {
	public static final String User = "User";
	public static final String UserPermission = "PermissionUser";
	public static final String AccountPermission = "PermissionAccount";
	public static final String Permission = "Permission";

	public ReportTable() {
		super("report");
		addPrimaryKey();
		addFields(Report.class);

        addCreationDate();
        addUpdateDate();
	}

	protected void addJoins() {
		addOptionalKey(new ReportForeignKey(User, new ReportUserTable(), new ReportOnClause("id","reportID")));
		addOptionalKey(new ReportForeignKey(UserPermission, new ReportPermissionUserTable(), new ReportOnClause("id","reportID")));
		addOptionalKey(new ReportForeignKey(AccountPermission, new ReportPermissionAccountTable(), new ReportOnClause("id","reportID")));
		addOptionalKey(new ReportForeignKey(Permission, new ReportPermissionView(), new ReportOnClause("id","reportID")));
	}
}