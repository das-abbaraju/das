package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.ReportColumnTable;
import com.picsauditing.report.tables.ReportUserTable;

public class ReportUsersModel extends AbstractModel {

    public ReportUsersModel(Permissions permissions) {
        super(permissions, new ReportUserTable());
    }

    public ModelSpec getJoinSpec() {
        ModelSpec column = new ModelSpec(null, "ReportUser");

        column.join(ReportUserTable.Report).alias = "Report";
        column.join(ReportUserTable.User);

        return column;
    }
}