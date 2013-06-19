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
                            google_data = config.data,
                            style_type = config.style_type || '',
                            options = $.extend({}, that.getOptionsByStyleType(style_type), that.getDefaultOptions());

                        switch (chart_type) {
                            case 'Pie':
                                chart =  new google.visualization.PieChart(chart_container);
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
                        chart.draw(google_data, options);
                    }
                });
            },

            getFlagStyleOptions: function () {
                return {
                    colors: [
                        'red',
                        'green',
                        'yellow'
                    ]
                }
            },

            getOptionsByStyleType: function (style_type) {
                switch (style_type) {
                    case 'Flags':
                        return this.getFlagStyleOptions();
                        break;
                    default:
                        return {};
                }
            },

            getDefaultOptions: function () {
                return {
                    height: 400
                }
            }
        }
    });
}(jQuery));