(function ($) {
    PICS.define('home.HomeController', {
        methods: {
            init: function () {
                if ($('#Home__page').length) {
                    var that = this;

                    $('.panel_content[data-widget-type=Chart]').each(function (key, value) {
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
                            success: function(response, textStatus, jqXHR) {
                                var chart_data = response.data,
                                    chart_options;

                                if (chart_data) {
                                    chart_options = that.getChartOptions(response);
                                    that.createChart(chart_container, chart_data, chart_options);
                                }
                            }
                        });
                    });
                }
            },

            createChart: function (container, data, options) {
                var data_table = new google.visualization.DataTable(data),
                    chart = new google.visualization.PieChart(container);

                chart.draw(data_table, options);
            },

            getChartOptions: function (response) {
                var chart_type = response.type,
                    chart_data = response.data;

                switch (chart_type) {
                    case 'Flags':
                        return this.getFlagsOptions(chart_data);
                }
            },

            getFlagsOptions: function(chart_data) {
                var column_names = [],
                    colors = [],
                    rows = chart_data.rows,
                    cell;

                // Extract the column names out of the data
                $.each(rows, function(index, row) {
                    cell = row.c,
                    cell_column = cell[0],
                    column_name = cell_column.v;

                    column_names.push(column_name);
                });

                // Assign colors to their corresponding column names
                colors[column_names.indexOf('Clear')] = '#FFF';
                colors[column_names.indexOf('Red')] = '#CF0F0F';
                colors[column_names.indexOf('Amber')] = '#FFCF3F';
                colors[column_names.indexOf('Green')] = '#3F9F0F';

                return {
                    width: 400,
                    height: 300,
                    colors: colors,
                    is3D: true,
                    legend: {
                        position: 'top'
                    }
                };
            }
        }
    });
}(jQuery));