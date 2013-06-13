package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Report;

public class ReportTable extends AbstractTable {
    public static final String Owner = "Owner";
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
        addOptionalKey(new ReportForeignKey(Owner, new UserTable(), new ReportOnClause("ownerID"))).setMinimumImportance(FieldImportance.Required);
    }
}