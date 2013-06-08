PICS.Charts = {};

// Chart //

PICS.Charts.Chart = function (config) {
    if (!config) {
        return;
    }

    this.data = config.data,
    this.chart_type = config.chart_type,
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
        case 'Pie':
        default:
            return new google.visualization.PieChart(this.container);
    }
};
PICS.Charts.Chart.prototype.getColorsInOrderOfLabels = function (labels) {
    var colors = [];

    $.each(this.colors, function (label, color) {
        colors[labels.indexOf(label)] = color;
    });

    return colors;
};

// LabelsAlongSideChart //

PICS.Charts.LabelsAlongSideChart = function (config) {
    PICS.Charts.Chart.call(this, config);

    this.colors = {
        'Clear': '#FFF',
        'Red': '#CF0F0F',
        'Amber': '#FFCF3F',
        'Green': '#3F9F0F'
    };
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
        colors = this.getColorsInOrderOfLabels(labels);

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
                                        chart = new PICS.Charts.PieChart(config);
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