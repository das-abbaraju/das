package com.picsauditing.report.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimeZone;

public class ReportDataConverterForPrinting extends ReportDataConverter {
    private static final Logger logger = LoggerFactory.getLogger(ReportDataConverterForPrinting.class);

    public ReportDataConverterForPrinting(ReportResults reportResults) {
        super(reportResults);
    }

    public void convert(TimeZone timezone) {
        for (ReportCell cell : reportResults.getCells()) {
            Object value = convertValueForPrinting(cell);
            cell.setValue(value);
        }
    }

    private Object convertValueForPrinting(ReportCell cell) {
        Object value = cell.getValue();
        if (value == null) {
            return null;
        }

        Object result = convertValueBasedOnCellColumn(cell, true);
        if (result == null) {
            result = value;
        }

        return result;
    }

}
