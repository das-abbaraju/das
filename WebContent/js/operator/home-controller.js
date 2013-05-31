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
                            url: 'js/operator/charts.json',
                            data: {
                                widgetID: widget_id
                            },
                            dataType: 'json',
                            success: function(data, textStatus, jqXHR) {
                                var chart_data = data.data,
                                    chart_options = that.getChartOptions(data.type);

                                that.createChart(chart_container, chart_data, chart_options);
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

            getChartOptions: function (type) {
                switch (type) {
                    case 'flags':
                        return {
                            width: 400,
                            height: 300,
                            colors: [
                                '#3F9F0F',
                                '#FFCF3F',
                                '#CF0F0F'
                            ],
                            is3D: true,
                            legend: {
                                position: 'top'
                            }
                        };
                }
            }
        }
    });
}(jQuery));