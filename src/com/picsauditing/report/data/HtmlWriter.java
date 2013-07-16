package com.picsauditing.report.data;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.report.fields.DisplayType;
import com.picsauditing.util.Strings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class HtmlWriter {
    private static final Logger logger = LoggerFactory.getLogger(HtmlWriter.class);

    private static I18nCache i18nCache = I18nCache.getInstance();

    private Locale locale;

    private ReportResults reportResults;
    private StringBuilder html;

    private final String HTML_HEADER = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
            "<body style=\"width: 100%; margin: 0; padding: 0;\">" +
            "<table style=\"width: 100%; padding: 0; border-spacing: 0; border: 0; width:100%; line-height: 100%;\">" +
            "<tr><td>";
    private final String HTML_FOOTER = "</table></td></tr></table></body></html>";
    private final String TABLE_STYLE = "border-spacing: 0; border-collapse: collapse; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; font-size: 12px;";
    private final String TH_STYLE = "background: #454545; border: #333; color: #ccc; font-weight: bold; padding: 4px 6px;";

    private final String TD_BORDER_STYLE = "border: 1px solid #ccc; padding: 4px 6px; ";
    private final String TD_NO_TOP_BORDER_STYLE = "border-top: none; ";
    private final String TD_NO_BOTTOM_BORDER_STYLE = "border-bottom: none; ";
    private final String TD_COMMON_BORDER_STYLE = TD_BORDER_STYLE + TD_NO_TOP_BORDER_STYLE + TD_NO_BOTTOM_BORDER_STYLE;
    private final String TD_BACKGROUND_STYLE = "background: #f1f1f2; ";
    private final String TD_TEXT_ALIGN_RIGHT_STYLE = "text-align: right; ";
    private final String TD_TEXT_ALIGN_CENTER_STYLE = "text-align: center; ";

    private final int MAX_COLUMNS = 5;
    private final int MAX_ROWS = 100;

    public HtmlWriter(ReportResults reportResults, Locale locale) {
        html = new StringBuilder();

        this.reportResults = reportResults;
        this.locale = locale;
    }

    public String toString() {
        html.append(HTML_HEADER + "<table " + addStyle(TABLE_STYLE) + ">");
        createColumnHeader();
        createRows();
        html.append(HTML_FOOTER);

        return html.toString();
    }

    private void createColumnHeader() {
        int countColumns = 0;

        html.append("<thead><tr>");

        // row number header is blank
        addTH("");

        for (Column column : reportResults.getColumns()) {
            if (countColumns == MAX_COLUMNS) {
                addTH("...");
                break;
            }

            String label = getText(column.getField().getText(),locale);
            if (Strings.isEmpty(label)) {
                label = column.getName();
            }
            addTH(label);

            countColumns++;
        }
        html.append("</tr></thead>");
    }

    private void createRows() {
        int countRows = 0;
        html.append("<tbody>");

        for (ReportRow row : reportResults.getRows()) {
            boolean isOddRow = (countRows) % 2 == 0;
            String endRowStyle = getEndRowStyle(reportResults.getRows().size());
            Integer columnSpan = createColumnSpan(row.getCells().size());

            if (countRows == MAX_ROWS) {
                endRowStyle = StringUtils.remove(endRowStyle,TD_NO_BOTTOM_BORDER_STYLE);
                html.append("<tr>");
                addTD(endRowStyle,columnSpan.toString(),"...");
                addTD(endRowStyle, "");
                html.append("</tr>");
                break;
            }

            int countColumns = 0;

            html.append("<tr>");

            addRowNumber(countRows);

            for (Column column : reportResults.getColumns()) {
                if (countColumns == MAX_COLUMNS) {
                    addTD(getEndColumnStyle(endRowStyle),"");
                    break;
                }

                addTD(createColumnStyle(isOddRow, column.getDisplayType()), createCells(row.getCellByColumn(column)));

                countColumns++;
            }
            html.append("</tr>");

            countRows++;
        }

        html.append("</tbody>");
    }

    private String createColumnStyle(boolean oddRow, DisplayType displayType) {
        if (!displayType.isNumber())
            displayType = DisplayType.String;

        String style = TD_COMMON_BORDER_STYLE;

        if (!oddRow)
            style += TD_BACKGROUND_STYLE;

        if (displayType == DisplayType.Number)
            style += TD_TEXT_ALIGN_RIGHT_STYLE;

        return style;
    }

    private String getEndColumnStyle(String endRowStyle) {
        if (endRowStyle != null)
            return endRowStyle;
        else
            return TD_COMMON_BORDER_STYLE + TD_BACKGROUND_STYLE;
    }

    private String getEndRowStyle(int numberOfRows) {
        String endRowStyle = null;

        if (MAX_ROWS < numberOfRows) {
            endRowStyle = TD_COMMON_BORDER_STYLE + TD_TEXT_ALIGN_CENTER_STYLE;

            boolean oddEndRow = (MAX_ROWS) % 2 == 0;
            if (!oddEndRow)
                endRowStyle += TD_BACKGROUND_STYLE;
        }
        return endRowStyle;
    }

    private void addRowNumber(int currentRow) {
        boolean isOddRow = (currentRow) % 2 == 0;

        String style = TD_COMMON_BORDER_STYLE + TD_TEXT_ALIGN_RIGHT_STYLE;
        if (!isOddRow) {
            style += TD_BACKGROUND_STYLE;
        }

        addTD(style, new Integer(currentRow+1).toString());
    }

    private String createCells(ReportCell cell) {
        switch (cell.getColumn().getDisplayType()) {
            case Number:
                return cell.getValue().toString();
            default:
                String text = "";
                if (cell.getValue() != null)
                    text = cell.getValue().toString();
                return getText(text,locale);
        }
    }

    private void addTH(String text) {
        html.append("<th ");
        html.append(addStyle(TH_STYLE) + ">");
        html.append(text);
        html.append("</th>");
    }

    private void addTD(String style, String text) {
        addTD(style, null, text);
    }

    private void addTD(String style, String span, String text) {
        html.append("<td ");
        if (span != null)
            html.append(addColumnSpan(span) + " ");
        html.append(addStyle(style) + ">");
        html.append(text);
        html.append("</td>");
    }

    private int createColumnSpan(int numberOfColumns) {
        int columnSpan;
        if (MAX_COLUMNS < numberOfColumns) {
            columnSpan = MAX_COLUMNS+1;
        }
        else {
            columnSpan = numberOfColumns+1;
        }
        return columnSpan;
    }

    private String addColumnSpan(String columnSpan) {
        return "colspan=\"" + columnSpan + "\"";
    }

    private String addStyle(String style) {
        return "style=\"" + style + "\"";
    }

    protected static String getText(String key, Locale locale) {
        return i18nCache.getText(key, locale);
    }
}