package com.picsauditing.report.data;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.report.fields.DisplayType;
import com.picsauditing.util.Strings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class HtmlWriter {
    private static final Logger logger = LoggerFactory.getLogger(HtmlWriter.class);

    private static I18nCache i18nCache = I18nCache.getInstance();

    private Locale locale;

    private ReportResults reportResults;
    private StringBuilder html;

    private final int MAX_COLUMNS = 10;
    private final int MAX_ROWS = 20;

    public HtmlWriter(ReportResults reportResults, Locale locale) {
        html = new StringBuilder();

        this.reportResults = reportResults;
        this.locale = locale;
    }

    public String toString() {
        html.append("<html><table border=\"1\">");
        createHeader();
        createRows();
        html.append("</table></html>");

        return html.toString();
    }

    private void createHeader() {
        int countColumns = 0;

        html.append("<tr>");
        for (Column column : reportResults.getColumns()) {
            if (countColumns == MAX_COLUMNS)
                break;

            html.append("<th>");

            String label = getText(column.getField().getText(),locale);
            if (Strings.isEmpty(label)) {
                label = column.getName();
            }
            html.append(label);
            html.append("</th>");

            countColumns++;
        }
        html.append("</tr>");
    }

    private void createRows() {
        int countRows = 0;

        for (ReportRow row : reportResults.getRows()) {
            if (countRows == MAX_ROWS)
                break;

            int countColumns = 0;

            html.append("<tr>");
            for (Column column : reportResults.getColumns()) {
                if (countColumns == MAX_COLUMNS)
                    break;

                html.append("<td>");

                createCells(row.getCellByColumn(column));
                html.append("</td>");

                countColumns++;
            }
            html.append("</tr>");

            countRows++;
        }
    }

    private void createCells(ReportCell cell) {
        switch (cell.getColumn().getDisplayType()) {
            case Number:
                html.append(Double.parseDouble(cell.getValue().toString()));
                break;
            default:
                String prefix = cell.getColumn().getField().getPreTranslation();
                String suffix = cell.getColumn().getField().getPostTranslation();
                String text = "";
                if (cell.getValue() != null)
                    text = cell.getValue().toString();
                html.append(getText(text,locale));
        }
    }

    protected static String getText(String key, Locale locale) {
        return i18nCache.getText(key, locale);
    }
}
