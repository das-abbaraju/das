package com.picsauditing.report.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimeZone;

public class ReportDataConverterForCharts extends ReportDataConverter {
    private static final Logger logger = LoggerFactory.getLogger(ReportDataConverterForCharts.class);

    public ReportDataConverterForCharts(ReportResults reportResults) {
        super(reportResults);
    }

    public void convert(TimeZone timezone) {
        ReportPivotBuilder builder = new ReportPivotBuilder();
        ReportResults pivotedResults = builder.convertToPivot(reportResults);
        reportResults.setColumns(pivotedResults.getColumns());
        reportResults.setRows(pivotedResults.getRows());
    }
}
