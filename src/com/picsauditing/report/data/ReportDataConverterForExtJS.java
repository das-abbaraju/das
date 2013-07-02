package com.picsauditing.report.data;

import com.picsauditing.report.fields.FieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimeZone;

public class ReportDataConverterForExtJS extends ReportDataConverter {
    private static final Logger logger = LoggerFactory.getLogger(ReportDataConverterForExtJS.class);

    public ReportDataConverterForExtJS(ReportResults reportResults) {
        super(reportResults);
    }

    public void convert(TimeZone timezone) {
        for (ReportCell cell : reportResults.getCells()) {
            Object value = convertValueForJson(cell, timezone);
            cell.setValue(value);
        }
    }

    private Object convertValueForJson(ReportCell cell, TimeZone timezone) {
        Object value = cell.getValue();
        if (value == null) {
            return null;
        }

        FieldType type = cell.getColumn().getField().getType();

        Object result = convertValueBasedOnCellColumn(cell, false);
        if (result == null) {
            result = convertValueBasedOnType(value, type, timezone);
            if (result == null) {
                result = value.toString();
            }
        }

        return result;
    }

}
