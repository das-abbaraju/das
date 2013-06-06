var convertForChart = (function ($) {

    // PRIVATE //
    
    function createGoogleColumns(report_columns) {
        var google_column;

        $.each(report_columns, function (report_column, index) {
            google_column = {};
            
            google_column.id = '';
            google_column.label = '????';
            google_column.pattern = '';
            google_column.type = column.getField().getType();
            
            google_columns.push(google_column);
        });
        
        return google_columns;
    }

    function createGoogleRows(report_rows, report_columns) {
        var report_row = report_rows[0],
            google_row;

        $.each(report_rows, function (report_row, index) {
            google_row = {
                c: []
            };

            $.each(report_columns, function (report_column, index) {					
                google_row.c.push({
                    v: report_row.getCellByColumn(column),
                    f: null
                });
            });
            
            google_rows.push(google_row);
        });
        
        return google_rows;
    }

    function getChartType() {
        return 'flags';
    }

    function createGoogleDataObj(chart_type, google_columns, google_rows) {
        return google_data {
            type: chart_type,
            data: {
                cols: google_columns,
                rows: google_rows
            }
        }
    }

    // PUBLIC //

    function convertForChart(reportResults) {
        var report_rows = reportResults.getRows(),
            report_columns = report_rows[0].getMap().getKeyValues(),
            google_columns = createGoogleColumns(report_rows),
            google_rows = createGoogleRows(report_rows, report_columns),
            chart_type = getChartType(),
            google_data = createGoogleDataObj(chart_type, google_columns, google_rows);

        return JSON.stringify(google_data);
    }

    return convertForChart;
}(jQuery));