package com.picsauditing.report.data;

import com.picsauditing.jpa.entities.Column;
import com.picsauditing.report.fields.DisplayType;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.Strings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.picsauditing.report.ReportJson.FILTER_OPERATOR;
import static com.picsauditing.report.ReportJson.FILTER_VALUE;
import static com.picsauditing.report.ReportJson.REPORT_ELEMENT_FIELD_ID;

public class ChartWriter {
    // private static final Logger logger = LoggerFactory.getLogger(ChartWriter.class);

    private TranslationService translationService = TranslationServiceFactory.getTranslationService();

    private Locale locale;

    private ReportResults reportResults;

    private final int MAX_ROWS = 20;

    public ChartWriter(ReportResults reportResults, Locale locale) {
        this.reportResults = reportResults;
        this.locale = locale;
    }

    public JSONObject toJson() {
        JSONObject dataJson = new JSONObject();

        dataJson.put("cols", createChartColumns());
        dataJson.put("rows", createChartRows());

        return dataJson;
    }

    private JSONArray createChartColumns() {
        JSONArray columnCollection = new JSONArray();

        for (Column col : reportResults.getColumns()) {
            JSONObject seriesJson = createColumnJson(col);
            columnCollection.add(seriesJson);
        }
        return columnCollection;
    }

    private JSONObject createColumnJson(Column column) {
        JSONObject json = new JSONObject();
        DisplayType displayType = column.getDisplayType();

        if (!displayType.isNumber()) {
            displayType = DisplayType.String;
        }
        json.put("id", column.getName());
        json.put("type", displayType.toString().toLowerCase());
        String label = getText(applyPrefixSuffix(column.getField().getPreTranslation(),column.getField().getPostTranslation(),column.getField().getText()),locale);
        if (Strings.isEmpty(label)) {
            label = column.getName();
        }
        json.put("label", label);

        // TODO: Find a better solution than using pre-translation
        JSONObject styleJson = new JSONObject();
        if (column.getField() != null && "FlagColor".equals(column.getField().getPreTranslation())) {
            String p = column.getName() + "-flag";
            styleJson.put("style_type", p.toLowerCase());
        }
        json.put("p", styleJson);

        return json;
    }

    private JSONArray createChartRows() {
        JSONArray rowsJson = new JSONArray();
        int count = 0;

        for (ReportRow row : reportResults.getRows()) {
            if (count > MAX_ROWS)
                break;

            JSONArray cellsJson = new JSONArray();
            JSONObject styleJson = new JSONObject();

            for (Column column : reportResults.getColumns()) {
                ReportCell cell = row.getCellByColumn(column);

                setDrillDownURL(row);
                cellsJson.add(createCellJson(cell, column.getField().getPreTranslation(), column.getField().getPostTranslation()));

                if (cell.getColumn().getDisplayType() == DisplayType.Flag) {
                    String p = cell.getValue() + "-flag";
                    styleJson.put("style_type", p.toLowerCase());
                }

                styleJson.put("url", column.getField().getUrl());
            }
            JSONObject rowJson = new JSONObject();
            rowJson.put("c", cellsJson);
            rowJson.put("p", styleJson);

            rowsJson.add(rowJson);
            count++;
        }

        return rowsJson;
    }

    private JSONObject createCellJson(ReportCell cell, String prefix, String suffix) {
        JSONObject json = new JSONObject();

        switch (cell.getColumn().getDisplayType()) {
            case Number:
                json.put("v", Double.parseDouble(cell.getValue().toString()));
                break;
            default:
                String text = cell.getValue().toString();
                text = applyPrefixSuffix(prefix, suffix, text);
                json.put("v", getText(text,locale));
        }

        if (cell.getColumn().getField().getUrl() != null) {
            JSONObject styleJson = new JSONObject();
            styleJson.put("url", cell.getColumn().getField().getUrl());
            json.put("p", styleJson);
        }

        return json;

    }

    private String applyPrefixSuffix(String prefix, String suffix, String text) {
        if (prefix != null && !prefix.isEmpty()) {
            text = prefix + "." + text;
        }
        if (suffix != null && !suffix.isEmpty()) {
            text = text + "." + suffix;
        }
        return text;
    }

    protected String getText(String key, Locale locale) {
        if (Strings.isEmpty(key) || key.contains(" ")) {
            return key;
        }
        return translationService.getText(key, locale);
    }

    private void setDrillDownURL(ReportRow row) {
        List <Column> aggregateColumns = new ArrayList<Column>();
        JSONArray dynamicParameters = new JSONArray();

        for (Column column : reportResults.getColumns()) {
            ReportCell cell = row.getCellByColumn(column);
            if (column.getSqlFunction() != null && column.getSqlFunction().isAggregate()) {
                aggregateColumns.add(column);
            }
            else {
                JSONObject param = new JSONObject();
                param.put(REPORT_ELEMENT_FIELD_ID,column.getName());
                param.put(FILTER_OPERATOR,column.getField().getFilterType().defaultOperator.toString());
                param.put(FILTER_VALUE,cell.getValue());
                dynamicParameters.add(param);
            }
        }

        String drillDownURL = "Report.action?report=" + reportResults.getReportId() + "&removeAggregates=true&dynamicParameters=" + dynamicParameters;

        for (Column aggregateColumn : aggregateColumns) {
            aggregateColumn.getField().setUrl(drillDownURL);
        }
    }
}
