package com.picsauditing.report.data;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.ChartSeries;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.ReportChart;
import com.picsauditing.report.ReportValidationException;
import com.picsauditing.report.fields.DisplayType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.SqlFunction;
import com.picsauditing.util.PicsDateFormat;
import com.picsauditing.util.TimeZoneUtil;
import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.util.*;

public class ReportDataConverter {

    private Locale locale;
    private final ReportResults reportResults;

    private static I18nCache i18nCache = I18nCache.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(ReportDataConverter.class);

    public ReportDataConverter(Collection<Column> columns, List<BasicDynaBean> results) {
        reportResults = new ReportResults(columns, results);
    }

    public void convertForExtJS(TimeZone timezone) {
        for (ReportCell cell : reportResults.getCells()) {
            Object value = convertValueForJson(cell, timezone);
            cell.setValue(value);
        }
    }

    public void convertForPrinting() {
        for (ReportCell cell : reportResults.getCells()) {
            Object value = convertValueForPrinting(cell);
            cell.setValue(value);
        }
    }

    @SuppressWarnings("unchecked")
    public JSONObject convertForChart(ReportChart chart) throws ReportValidationException {
        JSONObject dataJson = new JSONObject();

        List<Column> stringColumns = new ArrayList<Column>();
        List<Column> numericColumns = new ArrayList<Column>();
;
        for (Column col : chart.getReport().getColumns()) {
            DisplayType type = col.getField().getType().getDisplayType();
            if (col.getSqlFunction() != null) {
                type = col.getSqlFunction().getDisplayType();
            }

            if (type.isNumber()) {
                numericColumns.add(col);
            } else {
                stringColumns.add(col);
            }
        }

        if (stringColumns.size() < 0 || numericColumns.size() < 0) {
            throw new ReportValidationException("Report " + chart.getReport().getId() + " is not formatted correctly to become a chart", chart.getReport());
        }
        if (numericColumns.size() > 1 && chart.getSeries() == ChartSeries.Single) {
            throw new ReportValidationException("Report " + chart.getReport().getId() + " is formatted to be a multi-series, not a single-series", chart.getReport());
        }
        if (numericColumns.size() == 1 && chart.getSeries() == ChartSeries.Multi) {
            throw new ReportValidationException("Report " + chart.getReport().getId() + " is formatted to be a single-series, not a multi-series", chart.getReport());
        }

        dataJson.put("cols", createChartColumns(stringColumns, numericColumns));

        dataJson.put("rows", createChartRows(stringColumns, numericColumns));

        return dataJson;
    }

    private JSONArray createChartColumns(List<Column> stringColumns, List<Column> numericColumns) {
        JSONArray columnCollection = new JSONArray();

        JSONObject labelJson = new JSONObject();
        String labelID = "";
        String labelText = "";

        for (Column col : stringColumns) {
            labelID += col.getId() + " ";
            labelText += col.getField().getText() + " ";
        }

        labelJson.put("type", "string");
        labelJson.put("id", labelID.trim());
        labelJson.put("label", labelText.trim());
        labelJson.put("pattern", "");
        columnCollection.add(labelJson);

        for (Column col : numericColumns) {
            JSONObject seriesJson = new JSONObject();

            seriesJson.put("type", "number");
            seriesJson.put("id", col.getId());
            seriesJson.put("label", col.getField().getText());
            seriesJson.put("pattern", "");

            columnCollection.add(seriesJson);
        }

        return columnCollection;
    }

    private JSONArray createChartRows(List<Column> stringColumns, List<Column> numericColumns) {
        JSONArray rowsJson = new JSONArray();

        for (ReportRow row : reportResults.getRows()) {
            JSONObject totalRowJson = new JSONObject();
            JSONArray rowColumnJson = new JSONArray();

            JSONObject labelCellJson = new JSONObject();
            String labelValue = "";
            String labelFormattedValue = "";

            for (Column col : stringColumns) {
                ReportCell cell = row.getCellByColumn(col);
                String cellValue = cell.getValue().toString();

                if (cell.getColumn().getField().getFieldClass().equals(FlagColor.class)) {
                    String value = null;
                    try {
                        FlagColor flagColor = FlagColor.valueOf(cellValue);

                        value = getText(flagColor.getI18nKey(), locale);
                    } catch (Exception e) {
                        logger.error("Error translating flag color.");
                    }

                    labelFormattedValue += value + " ";
                }

                labelValue += cellValue + " ";
            }

            labelCellJson.put("v", labelValue.trim());
            labelCellJson.put("f", labelFormattedValue.trim());
            rowColumnJson.add(labelCellJson);

            for (Column col : numericColumns) {
                ReportCell cell = row.getCellByColumn(col);

                JSONObject cellJson = new JSONObject();
                cellJson.put("v", cell.getValue());
                cellJson.put("f", null);

                rowColumnJson.add(cellJson);
            }

            totalRowJson.put("c", rowColumnJson);
            rowsJson.add(totalRowJson);
        }

        return rowsJson;
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

    private Object convertValueBasedOnCellColumn(ReportCell cell, boolean forPrint) {
        Column column = cell.getColumn();
        Object value = cell.getValue();
        Object result = null;

        if (column == null) {
            return result;
        }

        logger.info("Attempting to convert {}, value: {}", column.getName(), value);

        SqlFunction sqlFunction = column.getSqlFunction();
        if (sqlFunction != null && sqlFunction == SqlFunction.Month) {
            result = convertValueAsMonth(value);
        }

        if (column.getName().contains("StatusSubstatus")) {
            result = convertValueAsTranslatedStatus((String) value);
        }

        Field field = column.getField();
        if (field == null) {
            result = column.getName() + ": Field not available";
            return result;
        }

        if (field.isTranslated() && column.hasNoSqlFunction()) {
            String key = field.getI18nKey(value.toString());
            result = getText(key, locale);
        }

        DisplayType displayType = field.getType().getDisplayType();
        if (displayType == DisplayType.Number) {
            result = value;
        }

        if (displayType == DisplayType.Boolean) {
            if (forPrint) {
                long numericValue = safeConversionToLong(value);
                if (numericValue == 1) {
                    result = "Y";
                } else {
                    result = "N";
                }
            } else {
                result = value;
            }
        }

        return result;
    }

    private long safeConversionToLong(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        } else if (value instanceof Long) {
            return ((Long) value).longValue();
        }

        return 0;
    }

    private String convertValueAsMonth(Object value) {
        int month = Integer.parseInt(value.toString());
        return new DateFormatSymbols(locale).getMonths()[month - 1];
    }

    private String convertValueAsTranslatedStatus(String value) {
        String[] valueString = value.split(":");

        String statusI18nKey = "AuditStatus." + valueString[0];
        String statusTranslation = getText(statusI18nKey, locale);
        String valueTranslated = statusTranslation;

        if (valueString.length > 1) {
            String subStatusI18nKey = "AuditSubStatus." + valueString[1];
            String subStatusTranslation = getText(subStatusI18nKey, locale);
            valueTranslated += ": " + subStatusTranslation;
        }

        return valueTranslated;
    }

    private String convertValueBasedOnType(Object value, FieldType type, TimeZone timezone) {
        String result = null;

        Date date = null;

        if (value instanceof Timestamp) {
            date = new Date(((Timestamp) value).getTime());
        } else if (value instanceof java.sql.Date) {
            date = new Date(((java.sql.Date) value).getTime());
        } else {
            return null;
        }

        if (type == FieldType.Date) {
            result = PicsDateFormat.formatDateIsoOrBlank(date);
        }

        if (type == FieldType.DateTime) {
            result = TimeZoneUtil.getFormattedTimeStringWithNewTimeZone(timezone, PicsDateFormat.DateAndTimeNoTimezone,
                    date);
        }

        return result;
    }

    private static String getText(String key, Locale locale) {
        return i18nCache.getText(key, locale);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public ReportResults getReportResults() {
        return reportResults;
    }
}
