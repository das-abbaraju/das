package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Report;

public class ReportTable extends AbstractTable {
    public static final String Owner = "Owner";

    public ReportTable() {
        super("report");
        addPrimaryKey();
        addFields(Report.class);

        addCreationDate();
        addUpdateDate();
    }

    protected void addJoins() {
        addOptionalKey(new ReportForeignKey(Owner, new UserTable(), new ReportOnClause("ownerID"))).setMinimumImportance(FieldImportance.Required);
        // Remove these since they violate the "No One-to-Many Joins" rule
//		addOptionalKey(new ReportForeignKey(User, new ReportUserTable(), new ReportOnClause("id","reportID")));
//		addOptionalKey(new ReportForeignKey(UserPermission, new ReportPermissionUserTable(), new ReportOnClause("id","reportID")));
//		addOptionalKey(new ReportForeignKey(AccountPermission, new ReportPermissionAccountTable(), new ReportOnClause("id","reportID")));
//		addOptionalKey(new ReportForeignKey(Permission, new ReportPermissionView(), new ReportOnClause("id","reportID")));
    }
}