package com.picsauditing.report.data;

import com.picsauditing.jpa.entities.Column;
import com.picsauditing.report.fields.DisplayType;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.Strings;
import org.apache.commons.lang.StringUtils;

import java.util.Locale;

public class HtmlWriter {
    // private static final Logger logger = LoggerFactory.getLogger(HtmlWriter.class);

    // private static I18nCache i18nCache = I18nCache.getInstance();
    private TranslationService translationService = TranslationServiceFactory.getTranslationService();

    private Locale locale;

    private ReportResults reportResults;
    private StringBuilder html;

    private final String HTML_HEADER = "<table style=\"width: 100%; padding: 0px; border-spacing: 0; border: 0; width:100%; line-height: 100%;\">" +
            "<tr><td>";
    private final String HTML_FOOTER = "</table></td></tr></table>";
    private final String TABLE_STYLE = "border-spacing: 0; border-collapse: collapse; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; font-size: 12px;";
    private final String TH_STYLE = "background-color: #454545; border: 1px solid #454545; color: #ccc; font-weight: bold; padding: 4px 0px 0px 5px;";
    private final String TH_END_COLUMN_WIDTH_STYLE = "width: 29px; ";

    private final String TD_BORDER_STYLE = "border: 1px solid #808080; color: #333; padding: 4px 6px; ";
    private final String TD_NO_TOP_BORDER_STYLE = "border-top: none; ";
    private final String TD_NO_BOTTOM_BORDER_STYLE = "border-bottom: none; ";
    private final String TD_BACKGROUND_STYLE = "background-color: #f1f1f2; ";
    private final String TD_END_ROW_BACKGROUND_STYLE = "background-color: #eaeaea; ";
    private final String TD_TEXT_ALIGN_LEFT_STYLE = "text-align: left; ";
    private final String TD_TEXT_ALIGN_RIGHT_STYLE = "text-align: right; ";
    private final String TD_TEXT_ALIGN_CENTER_STYLE = "text-align: center; ";
    private final String TD_COMMON_BORDER_STYLE = TD_BORDER_STYLE + TD_NO_TOP_BORDER_STYLE + TD_NO_BOTTOM_BORDER_STYLE;
    private final String TD_END_STYLE = TD_BORDER_STYLE + TD_TEXT_ALIGN_CENTER_STYLE;
    private final String TD_END_NO_TOP_STYLE = TD_END_STYLE + TD_NO_TOP_BORDER_STYLE;
    private final String TD_END_ROW_STYLE = TD_END_STYLE + TD_END_ROW_BACKGROUND_STYLE;
    private final String TD_END_COLUMN_STYLE = TD_END_NO_TOP_STYLE + TD_NO_BOTTOM_BORDER_STYLE + TD_BACKGROUND_STYLE;
    private final String TD_END_ROW_COLUMN_STYLE = TD_END_NO_TOP_STYLE + TD_BACKGROUND_STYLE;

    public static final int MAX_COLUMNS = 5;
    public static final int MAX_ROWS = 100;

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
        String style = TH_STYLE;
        int countColumns = 0;

        html.append("<thead><tr>");

        // row number header is blank
        addTH("", style);

        for (Column column : reportResults.getColumns()) {
            style = TH_STYLE;
            if (column.getDisplayType() == DisplayType.Number) {
                style += TD_TEXT_ALIGN_RIGHT_STYLE;
            }
            else
                style += TD_TEXT_ALIGN_LEFT_STYLE;

            if (countColumns == MAX_COLUMNS) {
                String endColumnHeaderStyle = TH_STYLE + TD_TEXT_ALIGN_CENTER_STYLE + TH_END_COLUMN_WIDTH_STYLE;
                addTH("...", endColumnHeaderStyle);
                break;
            }

            String label = getText(column.getField().getText(),locale);
            if (Strings.isEmpty(label)) {
                label = column.getName();
            }
            addTH(label, style);

            countColumns++;
        }
        html.append("</tr></thead>");
    }

    private void createRows() {
        int countRows = 0;
        html.append("<tbody>");

        for (ReportRow row : reportResults.getRows()) {
            boolean isOddRow = (countRows) % 2 == 0;
            boolean isLastRow = countRows+1 == reportResults.size();

            Integer columnSpan = createColumnSpan(row.getCells().size());

            if (countRows == MAX_ROWS) {
                html.append("<tr>");

                addTD(TD_END_ROW_STYLE,columnSpan.toString(),"...");

                if (reportResults.getColumns().size() >= MAX_COLUMNS)
                    addTD(TD_END_ROW_COLUMN_STYLE, "");

                html.append("</tr>");
                break;
            }

            int countColumns = 0;

            html.append("<tr>");

            addRowNumber(countRows, isLastRow);

            for (Column column : reportResults.getColumns()) {
                if (countColumns == MAX_COLUMNS) {
                    String style = TD_END_COLUMN_STYLE;
                    if (isLastRow)
                        style = StringUtils.remove(style,TD_NO_BOTTOM_BORDER_STYLE);

                    addTD(style,"");
                    break;
                }

                addTD(createColumnStyle(isOddRow, isLastRow, column.getDisplayType()), createCells(row.getCellByColumn(column)));

                countColumns++;
            }
            html.append("</tr>");

            countRows++;
        }

        html.append("</tbody>");
    }

    private String createColumnStyle(boolean oddRow, boolean lastRow, DisplayType displayType) {
        if (!displayType.isNumber())
            displayType = DisplayType.String;

        String style = TD_COMMON_BORDER_STYLE;

        if (!oddRow)
            style += TD_BACKGROUND_STYLE;

        if (lastRow)
            style = StringUtils.remove(style,TD_NO_BOTTOM_BORDER_STYLE);

        if (displayType == DisplayType.Number)
            style += TD_TEXT_ALIGN_RIGHT_STYLE;
        else
            style += TD_TEXT_ALIGN_LEFT_STYLE;

        return style;
    }

    private void addRowNumber(int currentRow, boolean lastRow) {
        boolean isOddRow = (currentRow) % 2 == 0;

        String style = TD_COMMON_BORDER_STYLE + TD_TEXT_ALIGN_RIGHT_STYLE;
        if (!isOddRow) {
            style += TD_BACKGROUND_STYLE;
        }

        if (lastRow)
            style = StringUtils.remove(style,TD_NO_BOTTOM_BORDER_STYLE);

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

    private void addTH(String text, String style) {
        html.append("<th  ");
        html.append(addStyle(style) + ">");
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

    protected String getText(String key, Locale locale) {
        if (Strings.isEmpty(key) || key.contains(" ")) {
            return key;
        }
        return translationService.getText(key, locale);
    }
}