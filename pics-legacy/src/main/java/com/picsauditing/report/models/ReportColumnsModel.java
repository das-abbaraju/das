package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.ReportColumnTable;

public class ReportColumnsModel extends AbstractModel {

    public ReportColumnsModel(Permissions permissions) {
        super(permissions, new ReportColumnTable());
    }

    public ModelSpec getJoinSpec() {
        ModelSpec column = new ModelSpec(null, "ReportColumn");

        column.join(ReportColumnTable.Report).alias = "Report";

        return column;
    }
}