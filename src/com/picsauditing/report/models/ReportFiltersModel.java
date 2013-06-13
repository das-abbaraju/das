package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.ReportFilterTable;

public class ReportFiltersModel extends AbstractModel {

    public ReportFiltersModel(Permissions permissions) {
        super(permissions, new ReportFilterTable());
    }

    public ModelSpec getJoinSpec() {
        ModelSpec column = new ModelSpec(null, "ReportFilter");

        column.join(ReportFilterTable.Report).alias = "Report";

        return column;
    }
}