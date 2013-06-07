PICS.Charts = {};

PICS.Charts.Chart = function (config) {
    if (!config) {
        return;
    }

    this.style_type = config.type,
    this.chart_type = config.chart_type, // FIXME: Should not be hard-coded
    this.data = config.data,
    this.container = config.container;
};
PICS.Charts.Chart.prototype.draw = function () {
    var data_table = new google.visualization.DataTable(this.data),
        options = this.getOptions(),
        google_chart = this.getGoogleChart();

    google_chart.draw(data_table, options);
};
PICS.Charts.Chart.prototype.getOptions = function () {
    // Return generic, default options
};
PICS.Charts.Chart.prototype.getGoogleChart = function () {
    switch (this.chart_type) {
        case 'Bar':
            return new google.visualization.BarChart(this.container);        
        case 'Pie':
        default:
            return new google.visualization.PieChart(this.container);
    }
};
PICS.Charts.Chart.prototype.getColumnNames = function () {
    var column_names = [],
        rows = this.data.rows;

    $.each(rows, function(index, row) {
        cell = row.c,
        cell_column = cell[0],
        column_name = cell_column.v;

        column_names.push(column_name);
    });

    return column_names;
};
PICS.Charts.Chart.prototype.getColorsOrderedLikeColumnNames = function (column_names) {
    var colors = [];

    $.each(this.colors, function (column_name, color) {
        colors[column_names.indexOf(column_name)] = color;
    });

    return colors;
};

PICS.Charts.FlagsChart = function (config) {
    PICS.Charts.Chart.call(this, config);

    this.colors = {
        'Clear': '#FFF',
        'Red': '#CF0F0F',
        'Amber': '#FFCF3F',
        'Green': '#3F9F0F'
    };
};
PICS.Charts.FlagsChart.prototype = new PICS.Charts.Chart();
PICS.Charts.FlagsChart.constructor = PICS.Charts.FlagsChart;
PICS.Charts.FlagsChart.prototype.getOptions = function () {
    var column_names = this.getColumnNames(this.data),
        colors = this.getColorsOrderedLikeColumnNames(column_names);

    return {
        width: 400,
        height: 300,
        colors: colors,
        is3D: true,
        legend: {
            position: 'top',
            alignment: 'center'
        }
    };
};

(function ($) {
    PICS.define('home.HomeController', {
        methods: {
            init: function () {
                if ($('#Home__page').length) {
                    var that = this;

                    $('.panel_content[data-widget-type=Chart][data-widget-id=23]').each(function (key, value) {
                        var chart_container = this,
                            $chart_container = $(chart_container),
                            widget_id = $chart_container.data('widget-id');

                        PICS.ajax({
                            url: 'ReportApi!chart.action',
                            data: {
                                widgetId: widget_id
                            },
                            type: 'GET',
                            dataType: 'json',
                            success: function(data, textStatus, jqXHR) {
                                var config = data,
                                    chart;

                                config.chart_type = 'Pie';
                                config.container = chart_container;

                                switch (config.type) {
                                    case 'Flags':
                                        chart = new PICS.Charts.FlagsChart(config);
                                }

                                chart.draw();
                            }
                        });
                    });
                }
            }
        }
    });
}(jQuery));