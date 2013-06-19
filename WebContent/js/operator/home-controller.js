PICS.Charts = {};

// Chart //

PICS.Charts.Chart = function (config) {
    if (!config) {
        return;
    }

    this.data = config.data,
    this.style_type = config.type, // Should be "= config.style_type"
    this.chart_type = config.chart_type,
    this.container = config.container;
};

PICS.Charts.Chart.prototype.getColors = function () {
    switch (this.style_type) {
        case 'Flags':
            return {
                'Clear': '#FFF',
                'Red': '#CF0F0F',
                'Amber': '#FFCF3F',
                'Green': '#3F9F0F'
            };
        default:
            return null;
    }
};
PICS.Charts.Chart.prototype.draw = function () {
    var data_table = new google.visualization.DataTable(this.data),
        options = this.getOptions(),
        google_chart = this.getGoogleChart();

    console.log(data_table);
    console.log(options);
    google_chart.draw(data_table, options);
};
PICS.Charts.Chart.prototype.getGoogleChart = function () {
    // return generic, default chart
};
PICS.Charts.Chart.prototype.getOptions = function () {
    // Return generic, default options
};

// LabelsAlongSideChart //

PICS.Charts.LabelsAlongSideChart = function (config) {
    PICS.Charts.Chart.call(this, config);
};
PICS.Charts.LabelsAlongSideChart.prototype = new PICS.Charts.Chart();
PICS.Charts.LabelsAlongSideChart.constructor = PICS.Charts.LabelsAlongSideChart;

PICS.Charts.LabelsAlongSideChart.prototype.getLabels = function () {
    var labels = [],
        rows = this.data.rows;

    $.each(rows, function(index, row) {
        row_cells = row.c,
        label_cell = row_cells[0],
        label = label_cell.v;

        labels.push(label);
    });

    return labels;
};

// PieChart //

PICS.Charts.PieChart = function (config) {
    PICS.Charts.LabelsAlongSideChart.call(this, config);
};
PICS.Charts.PieChart.prototype = new PICS.Charts.LabelsAlongSideChart();
PICS.Charts.PieChart.constructor = PICS.Charts.PieChart;

PICS.Charts.PieChart.prototype.getOptions = function () {
    var labels = this.getLabels(this.data),
        colors = this.getColors();

    return {
        width: 400,
        height: 300,
        is3D: true,
        legend: {
            position: 'top',
            alignment: 'center'
        }
    };
};
PICS.Charts.PieChart.prototype.getGoogleChart = function () {
    return new google.visualization.PieChart(this.container);
};

// BarChart

PICS.Charts.BarChart = function (config) {
    PICS.Charts.LabelsAlongSideChart.call(this, config);
};
PICS.Charts.BarChart.prototype = new PICS.Charts.LabelsAlongSideChart();
PICS.Charts.PieChart.constructor = PICS.Charts.PieChart;

PICS.Charts.BarChart.prototype.getOptions = function () {
    var labels = this.getLabels(this.data),
        colors = this.getColors();

    return {
        width: 400,
        height: 300,
        is3D: true,
        legend: {
            position: 'top',
            alignment: 'center'
        }
    };
};
PICS.Charts.BarChart.prototype.getGoogleChart = function () {
    return new google.visualization.BarChart(this.container);
};

(function ($) {
    PICS.define('home.HomeController', {
        methods: {
            init: function () {
                if ($('#Home__page').length) {
                    var that = this;

                    $('.panel_content[data-widget-type=GoogleChart]').each(function (key, value) {
                        that.refreshWidget(this);
                    });
                }
            },

            refreshWidget: function (chart_container) {
                var $chart_container = $(chart_container),
                    url = $chart_container.data('url'),
                    chart_type = $chart_container.data('chart-type');

                PICS.ajax({
                    url: url,
                    type: 'GET',
                    dataType: 'json',
                    success: function(data, textStatus, jqXHR) {
                        var config = data, chart;
                        var options = {};

                        switch (chart_type) {
                            case 'Pie':
                                chart =  new google.visualization.PieChart(chart_container);
                                options.height = 400;
                                break;
                            case 'Bar':
                                chart = new google.visualization.BarChart(chart_container);
                                break;
                            case 'Column':
                                chart = new google.visualization.ColumnChart(chart_container);
                                options.isStacked = true;
                                break;
                        }

                        var google_data = new google.visualization.DataTable(config.data);
                        // TODO
                        chart.draw(google_data, options);
                    }
                });

            }
        }
    });
}(jQuery));