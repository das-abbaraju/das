package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.tables.ReportTable;

import java.util.List;

public class ReportsModel extends AbstractModel {

    public ReportsModel(Permissions permissions) {
        super(permissions, new ReportTable());
        addUrl("ReportName", "Report.action?report={ReportID}");
        addUrl("ReportOwnerName", "ManageReports!access.action?reportId={ReportID}");
    }

    public ModelSpec getJoinSpec() {
        ModelSpec report = new ModelSpec(null, "Report");
        report.join(ReportTable.Owner);
        return report;
    }

    @Override
    public String getWhereClause(List<Filter> filters) {
        return "Report.deleted = 0";
    }
}