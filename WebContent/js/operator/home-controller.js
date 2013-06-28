(function ($) {
    PICS.define('widget.Chart', {
        methods: (function () {
            function loadCharts() {
                var that = this,
                    widget_selector = getWidgetSelectorFromStyleType(that.style_type);

                $(widget_selector).each(function (key, value) {
                    var chart_container = this;

                    loadChart.call(that, chart_container);
                });
            }

            function getWidgetSelectorFromStyleType(style_type) {
                return '.panel_content[data-widget-type=GoogleChart][data-style-type=' + style_type + ']';
            }

            function getChartByType (chart_type, chart_container) {
                switch (chart_type) {
                    case 'Pie':
                        return new google.visualization.PieChart(chart_container);
                    case 'Bar':
                        return new google.visualization.BarChart(chart_container);
                    case 'Column':
                        return new google.visualization.ColumnChart(chart_container);
                    default:
                        $.error('Invalid chart type: ' + chart_type);
                }
            }

            function getDefaultChartOptions() {
                return {
                    height: 400,
                    colors: [
                        '#3498db',
                        '#e74c3c',
                        '#2ecc71',
                        '#f39c12',
                        '#9b59b6',
                        '#f1c40f',
                        '#2980b9',
                        '#c0392b',
                        '#27ae60',
                        '#d35400',
                        '#8e44ad',
                        '#e67e22'
                    ]
                };
            }

            function loadChart(chart_container) {
                var that = this,
                    $chart_container = $(chart_container),
                    url = $chart_container.data('url'),
                    chart_type = $chart_container.data('chart-type');

                PICS.ajax({
                    url: url,
                    type: 'GET',
                    dataType: 'json',
                    success: function(data, textStatus, jqXHR) {
                        var config = data,
                            google_data = config.data,
                            chart = getChartByType(chart_type, chart_container),
                            data_table = new google.visualization.DataTable(google_data),
                            type_specific_options = (typeof that.getChartOptions == 'function') ? that.getChartOptions(data_table) : {},
                            options = $.extend({}, getDefaultChartOptions(), type_specific_options);

                        chart.draw(data_table, options);
                    }
                });
            }

            return {
                loadCharts: loadCharts
            };
        }())
    });
}(jQuery));

(function ($) {
    PICS.define('widget.BasicChart', {
        extend: 'widget.Chart',

        methods: {
            init: function () {
                this.style_type = 'Basic';

                this.loadCharts();
            },

            getStyleType: function () {
                return this.style_type;
            }
        }
    });
}(jQuery));

(function ($) {
    PICS.define('widget.StackedColumnChart', {
        extend: 'widget.Chart',

        methods: {
            init: function () {
                this.style_type = 'StackedColumn';

                this.loadCharts();
            },

            getStyleType: function () {
                return this.style_type;
            },

            getChartOptions: function (data_table) {
                var options = {};

                options.isStacked = true;

                return options;
            }
        }
    });
}(jQuery));

(function ($) {
    PICS.define('widget.FlagsChart', {
        extend: 'widget.Chart',

        methods: {
            init: function () {
                this.style_type = 'Flags';
                this.type_color_associations = {
                    'red-flag': '#ac030c',
                    'amber-flag': '#e67e1c',
                    'green-flag': '#229322'
                }

                this.loadCharts();
            },

            getStyleType: function () {
                return this.style_type;
            },

            getTypeColorAssociations: function () {
                return this.type_color_associations;
            },

            getChartOptions: function (data_table) {
                var options = {};

                options.colors = this.getColors(data_table, this.getTypeColorAssociations());

                return options;
            },

            getColors: function (data_table, type_color_associations) {
                if (this.isSingleSeries(data_table)) {
                    return this.getColorsForSingleSeriesChart(data_table, type_color_associations);
                } else {
                    return this.getColorsForMultiSeriesChart(data_table, type_color_associations);
                }
            },

            getColorsForSingleSeriesChart: function (data_table, color_associations) {
                var row_index = 0,
                    num_rows = data_table.getNumberOfRows(),
                    colors = [];

                for (; row_index < num_rows; row_index += 1) {
                    style_type = data_table.getRowProperty(row_index, 'style_type'),
                    color = color_associations[style_type] || $.error('Invalid row style type: ' + style_type);

                    colors.push(color);
                }

                return colors;
            },

            getColorsForMultiSeriesChart: function (data_table, color_associations) {
                var column_index = 1,
                    num_columns = data_table.getNumberOfColumns(),
                    colors = [];

                for (; column_index < num_columns; column_index += 1) {
                    style_type = data_table.getColumnProperty(column_index, 'style_type'),
                    color = color_associations[style_type] || $.error('Invalid column style type: ' + style_type);

                    colors.push(color);
                }

                return colors;
            },

            isSingleSeries: function (data_table) {
                return data_table.getNumberOfColumns() <= 2;
            }
        }
    });
}(jQuery));