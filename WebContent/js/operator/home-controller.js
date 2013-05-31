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
                                    chart_options = data.options;

                                that.createChart(chart_container, chart_data, chart_options);
                            }
                        });
                    });
                }
            },

            createChart: function (chart_container, chart_data, chart_options) {
                var data_table = new google.visualization.DataTable(chart_data),
                    chart = new google.visualization.PieChart(chart_container);

                chart.draw(data_table, chart_options);
            }
        }
    });
})(jQuery);