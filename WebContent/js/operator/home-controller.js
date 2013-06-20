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
                            google_data = config.data;
                        google_data = {
                          cols: [{id: 'A', label: 'NEW A', type: 'string', p: { class_name: 'green-flag'}},
                                 {id: 'B', label: 'B-label', type: 'number', p: { class_name: 'red-flag'}},
                                 {id: 'C', label: 'C-label', type: 'date', p: { class_name: 'yellow-flag'}}
                                ],
                          rows: [{c:[{v: 'a'}, {v: 1.0, f: 'One'}, {v: new Date(2008, 1, 28, 0, 31, 26), f: '2/28/08 12:31 AM'}]},
                                 {c:[{v: 'b'}, {v: 2.0, f: 'Two'}, {v: new Date(2008, 2, 30, 0, 31, 26), f: '3/30/08 12:31 AM'}]},
                                 {c:[{v: 'c'}, {v: 3.0, f: 'Three'}, {v: new Date(2008, 3, 30, 0, 31, 26), f: '4/30/08 12:31 AM'}]}
                                ],
                          p: {foo: 'hello', bar: 'world!'}
                        };


var
                            style_type = config.style_type || 'Flags',
                            chart = that.getChartByType(chart_type, chart_container),
                            data_table = new google.visualization.DataTable(google_data),
                            options = $.extend({}, that.getChartOptionsForStyleType(style_type, data_table), that.getDefaultChartOptions());

                        chart.draw(data_table, options);
                    }
                });
            },

            getChartByType: function (chart_type, chart_container) {
                switch (chart_type) {
                    case 'Pie':
                        return chart = new google.visualization.PieChart(chart_container);
                    case 'Bar':
                        return chart = new google.visualization.BarChart(chart_container);
                    case 'Column':
                        return chart = new google.visualization.ColumnChart(chart_container);
                    default:
                        throw new Error('Invalid chart type');
                }
            },

            getDefaultChartOptions: function () {
                return {
                    height: 400
                }
            },

            getChartOptionsForFlagStyleType: function (data_table) {
                var column_index = 0,
                    num_columns = data_table.getNumberOfColumns(),
                    colors = [];

                var color_options = {
                    'red-flag': 'red',
                    'yellow-flag': 'yellow',
                    'green-flag': 'green'
                };

                for (; column_index < num_columns; column_index += 1) {
                    class_name = data_table.getColumnProperty(column_index, 'class_name');

                    colors.push(color_options[class_name]);
                }

                return {
                    colors: colors
                }
            },

            getChartOptionsForStyleType: function (style_type, data_table) {
                switch (style_type) {
                    case 'Flags':
                        return this.getChartOptionsForFlagStyleType(data_table);
                        break;
                    default:
                        return {};
                }
            }
        }
    });
}(jQuery));