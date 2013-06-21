package com.picsauditing.report.data;

import com.picsauditing.jpa.entities.Column;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.report.fields.DisplayType;
import com.picsauditing.util.Strings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimeZone;

public class ChartWriter {
    private static final Logger logger = LoggerFactory.getLogger(ChartWriter.class);

    private ReportResults reportResults;

    public ChartWriter(ReportResults reportResults) {
        this.reportResults = reportResults;
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
        DisplayType displayType = column.getField().getType().getDisplayType();
        if (!displayType.isNumber()) {
            displayType = DisplayType.String;
        }
        json.put("id", column.getName());
        json.put("type", displayType.toString().toLowerCase());
        String label = column.getField().getText();
        if (Strings.isEmpty(label)) {
            label = column.getName();
        }
        json.put("label", label);
        // json.put("pattern", "");

        return json;
    }

    private JSONArray createChartRows() {
        JSONArray rowsJson = new JSONArray();

        for (ReportRow row : reportResults.getRows()) {
            JSONArray cellsJson = new JSONArray();
            JSONObject styleJson = new JSONObject();

            for (Column column : reportResults.getColumns()) {
                ReportCell cell = row.getCellByColumn(column);
                cellsJson.add(createCellJson(cell));

                if (cell.getColumn().getDisplayType() == DisplayType.Flag) {
                    String p = cell.getValue() + "-flag";
                    styleJson.put("style_type", p.toLowerCase());
                }
            }
            JSONObject rowJson = new JSONObject();
            rowJson.put("c", cellsJson);
            rowJson.put("p", styleJson);

            rowsJson.add(rowJson);
        }

        return rowsJson;
    }

    private JSONObject createCellJson(ReportCell cell) {
        JSONObject json = new JSONObject();
        switch (cell.getColumn().getDisplayType()) {
            case Number:
                json.put("v", Double.parseDouble(cell.getValue().toString()));
                break;
            default:
                json.put("v", cell.getValue());
        }

        return json;

    }

}
