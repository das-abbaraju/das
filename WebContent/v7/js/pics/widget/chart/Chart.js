/**
 * Chart: Abstract base class for all chart types.
 *
 * Implements Google Charts: https://developers.google.com/chart/interactive/docs/reference
 * See ChartsDemo.action for implementation examples.
 *
 * @author: Jason Roos
 * @date: 7-1-2013
 * @version: 1
 */
(function ($) {
    PICS.define('widget.chart.Chart', {
        methods: (function () {
            function init() {
                this.$all_containers = getAllContainers();
            }

            function getAllContainers() {
                return $('.panel_content[data-widget-type=GoogleChart]');
            }

            function loadCharts() {
                var child = this,
                    widget_selector = getWidgetSelectorFromStyleType(child.style_type);

                $custom_containers = this.$all_containers.filter(widget_selector);

                $custom_containers.each(function (key, value) {
                    var chart_container = this;

                    loadChart.call(child, chart_container);
                });
            }

            function isSingleSeries(data_table) {
                return data_table.getNumberOfColumns() <= 2;
            }

            function getWidgetSelectorFromStyleType(style_type) {
                return '[data-style-type=' + style_type + ']';
            }

            function getChartByType (chart_type, chart_container) {
                switch (chart_type) {
                    case 'Pie':
                        return new google.visualization.PieChart(chart_container);
                    case 'Bar':
                        return new google.visualization.BarChart(chart_container);
                    case 'Column':
                        return new google.visualization.ColumnChart(chart_container);
                    case 'Line':
                        return new google.visualization.LineChart(chart_container);
                    case 'Area':
                        return new google.visualization.AreaChart(chart_container);
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

            function getUniversalChartOptions(data_table) {
                var options = {
                    vAxis: {
                        title: data_table.getTableProperty('v_axis_title')
                    },
                    hAxis: {
                        title: data_table.getTableProperty('h_axis_title')
                    }
                };

                // Single-series charts with their vertical and horizontal axes labeled do not need a legend.
                if (options.vAxis.title && options.hAxis.title && isSingleSeries(data_table)) {
                    options.legend = {
                        position: 'none'
                    };
                }

                return options;
            }

            function getAllOptions(child, data_table) {
                var parent = child.getParent(),
                    custom_options_from_parent = (typeof parent.getChartOptions == 'function') ? parent.getChartOptions(data_table) : {},
                    custom_options_from_child = (typeof child.getChartOptions == 'function') ? child.getChartOptions(data_table) : {},
                    all_options = $.extend({}, custom_options_from_parent, custom_options_from_child),
                    all_options = $.extend({}, getUniversalChartOptions(data_table), all_options),
                    all_options = $.extend({}, getDefaultChartOptions(), all_options);

                return all_options;
            }

            function loadChart(chart_container) {
                var child = this,
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
                            options = getAllOptions(child, data_table);

                        google.visualization.events.addListener(chart, 'select', function () { redirect(chart, data_table); } );
                        chart.draw(data_table, options);
                    }
                });
            }

            function redirect(chart, data_table) {
                var selection = chart.getSelection()[0],
                    url;

                // We use typeof here because row and column can equal zero.
                if (typeof selection.row != 'undefined' && typeof selection.column != 'undefined') {
                    url = data_table.getProperty(selection.row, selection.column, 'url');
                } else if (typeof selection.row != 'undefined') {
                    url = data_table.getRowProperty(selection.row, 'url');
                } else if (typeof selection.column != 'undefined') {
                    url = data_table.getColumnProperty(selection.column, 'url');
                }

                window.location = url;
            }

            return {
                init: init,
                loadCharts: loadCharts,
                isSingleSeries: isSingleSeries
            };
        }())
    });
}(jQuery));