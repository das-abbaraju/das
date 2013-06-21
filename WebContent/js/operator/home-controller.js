(function ($) {
    PICS.define('home.HomeController', {
        methods: {
            init: function () {
                if ($('#Home__page').length) {
                    var that = this;

                    $('.panel_content[data-widget-type=GoogleChart]').each(function (key, value) {
                        that.loadWidget(this);
                    });
                }
            },

            loadWidget: function (chart_container) {
                var that = this,
                    $chart_container = $(chart_container),
                    url = $chart_container.data('url'),
                    chart_type = $chart_container.data('chart-type'),
                    style_type = $chart_container.data('style-type') || '';

                PICS.ajax({
                    url: url,
                    type: 'GET',
                    dataType: 'json',
                    success: function(data, textStatus, jqXHR) {
                        var config = data,
                            google_data = config.data,
                            chart = that.getChartByType(chart_type, chart_container),
                            data_table = new google.visualization.DataTable(google_data),
                            options = $.extend({}, that.getDefaultChartOptions(), that.getChartOptionsForStyleType(data_table, style_type));

                        chart.draw(data_table, options);
                    }
                });
            },

            getChartByType: function (chart_type, chart_container) {
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
            },

            getDefaultChartOptions: function () {
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
            },

            getChartOptionsForStyleType: function (data_table, style_type) {
                switch (style_type) {
                    case 'Flags':
                        return this.getChartOptionsForStyleTypeFlags(data_table);
                    default:
                        return {};
                }
            },

            getChartOptionsForStyleTypeFlags: function (data_table) {
                var type_color_associations = {
                    'red-flag': '#ac030c',
                    'amber-flag': '#e67e1c',
                    'green-flag': '#229322',
                    'contractor': 'purple'
                };

                var options = {}

                options.colors = this.getColors(data_table, type_color_associations);

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